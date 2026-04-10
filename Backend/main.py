from __future__ import annotations

from datetime import datetime
from typing import List

from fastapi import Depends, FastAPI, HTTPException
from sqlalchemy.orm import Session

from Backend.ai_engine import generate_workout_plan_for_user
from Backend.chatbot import answer_fitness_question
from Backend.database import engine, get_db
from Backend.dependencies import get_current_user
import Backend.models as models
import Backend.schemas as schemas
from Backend.routes import router as auth_router

models.Base.metadata.create_all(bind=engine)

app = FastAPI(title="Fitness Backend")
app.include_router(auth_router)


@app.get("/")
def home():
    return {"message": "Fitness backend running"}


@app.get("/profile/me", response_model=schemas.UserResponse)
def get_profile(current_user: models.User = Depends(get_current_user)):
    return current_user


@app.put("/profile/update", response_model=schemas.UserResponse)
def update_profile(
    profile: schemas.ProfileUpdate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    user = db.query(models.User).filter(models.User.id == current_user.id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    for field, value in profile.model_dump(exclude_unset=True).items():
        setattr(user, field, value)

    db.commit()
    db.refresh(user)
    return user


@app.get("/availability", response_model=List[schemas.AvailabilitySlotResponse])
def get_availability(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    return (
        db.query(models.AvailabilitySlot)
        .filter(models.AvailabilitySlot.user_id == current_user.id)
        .order_by(models.AvailabilitySlot.day, models.AvailabilitySlot.start_time)
        .all()
    )


@app.post("/availability", response_model=List[schemas.AvailabilitySlotResponse])
def replace_availability(
    slots: List[schemas.AvailabilitySlotCreate],
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    if not slots:
        raise HTTPException(status_code=400, detail="At least one availability slot is required")

    db.query(models.AvailabilitySlot).filter(
        models.AvailabilitySlot.user_id == current_user.id
    ).delete()

    for slot in slots:
        db.add(
            models.AvailabilitySlot(
                user_id=current_user.id,
                day=slot.day.lower(),
                start_time=slot.start_time,
                end_time=slot.end_time,
            )
        )

    db.commit()
    return (
        db.query(models.AvailabilitySlot)
        .filter(models.AvailabilitySlot.user_id == current_user.id)
        .order_by(models.AvailabilitySlot.day, models.AvailabilitySlot.start_time)
        .all()
    )


@app.post("/workout-plan/generate", response_model=schemas.GeneratedWorkoutPlanResponse)
def generate_plan(
    request: schemas.GeneratePlanRequest,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    slots = (
        db.query(models.AvailabilitySlot)
        .filter(models.AvailabilitySlot.user_id == current_user.id)
        .all()
    )
    if not slots:
        raise HTTPException(status_code=400, detail="No availability saved for this user")

    if not current_user.fitness_level:
        raise HTTPException(status_code=400, detail="fitness_level is required in profile")

    slot_payload = [
        {"day": slot.day, "start_time": slot.start_time, "end_time": slot.end_time}
        for slot in slots
    ]

    try:
        generated = generate_workout_plan_for_user(
            current_user,
            slot_payload,
            goal_override=request.override_goal,
        )
    except ValueError as exc:
        raise HTTPException(status_code=400, detail=str(exc))

    db.query(models.WorkoutPlan).filter(models.WorkoutPlan.user_id == current_user.id).update(
        {"status": "archived"}
    )

    plan_row = models.WorkoutPlan(
        user_id=current_user.id,
        fitness_goal=generated["fitness_goal"],
        fitness_level=generated["fitness_level"],
        status="active",
    )
    db.add(plan_row)
    db.flush()

    for day_index, day_plan in enumerate(generated["plan"]):
        for exercise_index, exercise in enumerate(day_plan["exercises"]):
            db.add(
                models.WorkoutPlanItem(
                    plan_id=plan_row.id,
                    day=day_plan["day"],
                    start_time=day_plan.get("start_time"),
                    end_time=day_plan.get("end_time"),
                    focus=day_plan["focus"],
                    exercise_name=exercise["exercise_name"],
                    targeted_muscle_group=exercise["targeted_muscle_group"],
                    equipment=exercise["equipment"],
                    difficulty=exercise["difficulty"],
                    sets=exercise["sets"],
                    reps=exercise["reps"],
                    estimated_minutes=exercise["estimated_minutes"],
                    sort_order=(day_index * 100) + exercise_index,
                )
            )

    db.commit()
    return generated


@app.get("/workout-plan/me")
def get_latest_plan(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    plan = (
        db.query(models.WorkoutPlan)
        .filter(
            models.WorkoutPlan.user_id == current_user.id,
            models.WorkoutPlan.status == "active",
        )
        .order_by(models.WorkoutPlan.created_at.desc())
        .first()
    )
    if not plan:
        raise HTTPException(status_code=404, detail="No active workout plan found")

    items = (
        db.query(models.WorkoutPlanItem)
        .filter(models.WorkoutPlanItem.plan_id == plan.id)
        .order_by(models.WorkoutPlanItem.sort_order.asc())
        .all()
    )

    return {
        "id": plan.id,
        "user_id": plan.user_id,
        "created_at": plan.created_at.isoformat(),
        "fitness_goal": plan.fitness_goal,
        "fitness_level": plan.fitness_level,
        "status": plan.status,
        "items": items,
    }


@app.post("/workout-log", response_model=schemas.WorkoutLogResponse)
def log_workout(
    entry: schemas.WorkoutLogEntry,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    log = models.WorkoutLog(
        user_id=current_user.id,
        plan_item_id=entry.plan_item_id,
        performed_at=entry.performed_at,
        day=entry.day,
        workout_name=entry.workout_name,
        duration_minutes=entry.duration_minutes,
        intensity=entry.intensity,
        notes=entry.notes,
    )
    db.add(log)
    db.commit()
    db.refresh(log)
    return log


@app.get("/workout-logs/me", response_model=List[schemas.WorkoutLogResponse])
def get_workout_logs(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    return (
        db.query(models.WorkoutLog)
        .filter(models.WorkoutLog.user_id == current_user.id)
        .order_by(models.WorkoutLog.id.desc())
        .all()
    )


@app.get("/progress/me", response_model=schemas.ProgressResponse)
def get_progress(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    logs = (
        db.query(models.WorkoutLog)
        .filter(models.WorkoutLog.user_id == current_user.id)
        .all()
    )
    total_logged_workouts = len(logs)
    total_minutes = sum(log.duration_minutes for log in logs)
    average_minutes = round(total_minutes / total_logged_workouts, 2) if total_logged_workouts else 0.0
    most_recent_workout = logs[-1].performed_at if logs else None
    return {
        "total_logged_workouts": total_logged_workouts,
        "total_minutes": total_minutes,
        "average_minutes": average_minutes,
        "most_recent_workout": most_recent_workout,
    }


@app.post("/feedback")
def save_feedback(
    entry: schemas.FeedbackCreate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    feedback = models.Feedback(
        user_id=current_user.id,
        plan_id=entry.plan_id,
        day=entry.day,
        rating=entry.rating,
        comments=entry.comments,
    )
    db.add(feedback)
    db.commit()
    return {"message": "Feedback saved"}


@app.post("/chatbot/ask", response_model=schemas.ChatResponse)
def chatbot_ask(
    request: schemas.ChatRequest,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    profile_text = (
        f"Age: {current_user.age}, Gender: {current_user.gender}, Weight: {current_user.weight}, "
        f"Height: {current_user.height_feet}ft {current_user.height_inches}in, "
        f"Fitness level: {current_user.fitness_level}, Fitness goal: {current_user.fitness_goal}"
    )
    answer = answer_fitness_question(request.question, user_profile=profile_text)
    db.add(models.ChatHistory(user_id=current_user.id, question=request.question, answer=answer))
    db.commit()
    return {"answer": answer}


@app.delete("/account/me")
def delete_account(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
):
    user = db.query(models.User).filter(models.User.id == current_user.id).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    db.delete(user)
    db.commit()
    return {"message": "Account deleted"}


@app.get("/exercises")
def get_exercises(db: Session = Depends(get_db)):
    return db.query(models.Exercise).all()


@app.get("/routines")
def get_routines(db: Session = Depends(get_db)):
    return db.query(models.Routine).all()


@app.get("/routine/{routine_id}")
def get_routine(routine_id: int, db: Session = Depends(get_db)):
    routine = db.query(models.Routine).filter(models.Routine.id == routine_id).first()
    if not routine:
        raise HTTPException(status_code=404, detail="Routine not found")

    exercises = (
        db.query(models.RoutineExercise)
        .filter(models.RoutineExercise.routine_id == routine_id)
        .all()
    )
    return {"routine_name": routine.routine_name, "exercises": exercises}
