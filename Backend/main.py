from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import List
from Backend.database import engine, get_db
import Backend.models as models
import Backend.schemas as schemas
from Backend.ai_engine import generate_workout_plan
from Backend.auth.routes import router as auth_router
from Backend.auth.dependencies import get_current_user

#Made changes

# this creates the tables 
models.Base.metadata.create_all(bind=engine)
app = FastAPI()
app.include_router(auth_router)

@app.get("/")
def home():
    return {"message": "Fitness backend running"}


# GET USER PROFILE
@app.get("/profile/me", response_model=schemas.UserResponse)
def get_profile(current_user: models.User = Depends(get_current_user)):
    return current_user



# UPDATE PROFILE
@app.put("/profile/update")
def update_profile(
    profile: schemas.ProfileUpdate,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    user = db.query(models.User).filter(models.User.id == current_user.id).first()

    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    user.age = profile.age
    user.schedule = profile.schedule
    user.weight = profile.weight
    user.gender = profile.gender

    db.commit()
    db.refresh(user)

    return {"message": "Profile updated"}


# Uses the AI engine to generate a workout plan based on user input
class AIRequest(BaseModel):
    age: int
    days: List[str]
    duration: int
    goal: str




@app.post("/ai_plan")
def ai_plan(
    request: AIRequest,
    current_user: models.User = Depends(get_current_user)
):

    plan = generate_workout_plan(
        request.age,
        request.days,
        request.duration,
        request.goal
    )

    return {"user": current_user.username, "plan": plan}

# New modifications
@app.post("/save_plan")
def save_plan(request: schemas.SaveWorkoutPlanRequest, db: Session = Depends(get_db)):
    # Implementation for saving workout plan
    return {"message": "Workout plan saved"}

@app.get("/schedule/{program}")
def get_schedule(program: str, db: Session = Depends(get_db)):
    schedule = db.query(models.Schedule).filter(models.Schedule.program == program).all()

    return schedule

@app.get("/routines")
def get_routines(db: Session = Depends(get_db)):
    routines = db.query(models.Routine).all()
    return routines

@app.get("/exercises")
def get_exercises(db: Session = Depends(get_db)):
    exercises = db.query(models.Exercise).all()
    return exercises

@app.get("/routine/{routine_id}")
def get_routine(routine_id: int, db: Session = Depends(get_db)):
    routine = db.query(models.Routine).filter(models.Routine.id == routine_id).first()
    if not routine:
        return {"error": "Routine not found"}

    exercises = db.query(models.RoutineExercise).filter(models.RoutineExercise.routine_id == routine_id).all()

    return {
        "routine_name": routine.routine_name,
        "exercises": exercises
    }
    
@app.post("/log_workout")
def log_workout(entry: schemas.WorkoutLogEntry, db: Session = Depends(get_db)):
    # Implementation for logging workout
    return {"message": "Workout logged"}

@app.get("/workout_logs/{user_id}")
def get_workout_logs(user_id: int, db: Session = Depends(get_db)):
    # Implementation for retrieving workout logs
    return {"logs": []}

@app.get("/progress/{user_id}")
def get_progress(user_id: int, db: Session = Depends(get_db)):
    # Implementation for calculating and returning progress
    return {"progress": "Progress data here"}

# Delete Account
@app.delete("/delete_account/{user_id}")
def delete_account(user_id: int, db: Session = Depends(get_db)):
    user = db.query(models.User).filter(models.User.id == user_id).first()
    if not user:
        return {"error": "User not found"}

    db.delete(user)
    db.commit()

    return {"message": "Account deleted"}

