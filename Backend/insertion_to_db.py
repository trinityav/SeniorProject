from Backend.database import SessionLocal, engine
from Backend.models import Base, Exercise, Routine, RoutineExercise, Schedule
from Backend.workout_data import workouts
from Backend.workout_routines import routines
from Backend.workout_schedule import schedule

Base.metadata.create_all(bind=engine)


def get_first_existing(data, keys, default=None):
    for key in keys:
        value = data.get(key)
        if value not in [None, ""]:
            return value
    return default


def insert_exercises(db):
    for item in workouts:
        existing = db.query(Exercise).filter(Exercise.name == item["name"]).first()
        if existing:
            continue

        targeted_muscle_group = get_first_existing(
            item,
            ["targeted_muscle_group", "targeted muscle group", "targetedmuscle_group"],
        )
        if not targeted_muscle_group:
            continue

        exercise = Exercise(
            name=item["name"],
            targeted_muscle_group=targeted_muscle_group,
            equipment=item.get("equipment", "Unknown"),
            difficulty=item.get("difficulty", "Beginner"),
        )
        db.add(exercise)

    db.commit()


def insert_routines(db):
    for routine_name, exercises in routines.items():
        routine = db.query(Routine).filter(Routine.routine_name == routine_name).first()
        if not routine:
            routine = Routine(routine_name=routine_name)
            db.add(routine)
            db.commit()
            db.refresh(routine)

        for item in exercises:
            existing = (
                db.query(RoutineExercise)
                .filter(
                    RoutineExercise.routine_id == routine.id,
                    RoutineExercise.exercise_name == item["exercise"],
                    RoutineExercise.sets == str(item["sets"]),
                    RoutineExercise.reps == str(item["reps"]),
                )
                .first()
            )
            if existing:
                continue

            db.add(
                RoutineExercise(
                    routine_id=routine.id,
                    exercise_name=item["exercise"],
                    sets=str(item["sets"]),
                    reps=str(item["reps"]),
                )
            )

    db.commit()


def insert_schedule(db):
    for program, days in schedule.items():
        for day, exercises in days.items():
            for item in exercises:
                existing = (
                    db.query(Schedule)
                    .filter(
                        Schedule.program == program,
                        Schedule.day == day,
                        Schedule.exercise_name == item["exercise"],
                        Schedule.sets == str(item["sets"]),
                        Schedule.reps == str(item["reps"]),
                    )
                    .first()
                )
                if existing:
                    continue

                db.add(
                    Schedule(
                        program=program,
                        day=day,
                        exercise_name=item["exercise"],
                        sets=str(item["sets"]),
                        reps=str(item["reps"]),
                    )
                )

    db.commit()


def run_insertion():
    db = SessionLocal()
    try:
        insert_exercises(db)
        insert_routines(db)
        insert_schedule(db)
    finally:
        db.close()


if __name__ == "__main__":
    run_insertion()
