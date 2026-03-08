from database import SessionLocal, engine
from models import Base, Exercise, Routine, RoutineExercise, Schedule
from workout_data import workouts
from workout_routines import routines
from workout_schedule import schedule

# Create all tables
Base.metadata.create_all(bind=engine)



# INSERT EXERCISES
def insert_exercises(db):
    for w in workouts:
        exercise = Exercise(
            name=w["name"],
            targeted_muscle_group=w["targeted_muscle_group"],
            equipment=w["equipment"],
            difficulty=w["difficulty"]
        )
        db.add(exercise)
    db.commit()



# INSERT ROUTINES + ROUTINE EXERCISES
def insert_routines(db):
    for routine_name, exercises in routines.items():
        # Create routine row
        routine = Routine(routine_name=routine_name)
        db.add(routine)
        db.commit()
        db.refresh(routine)

        # Insert exercises for this routine
        for ex in exercises:
            routine_ex = RoutineExercise(
                routine_id=routine.id,
                exercise_name=ex["exercise"],
                sets=ex["sets"],
                reps=ex["reps"]
            )
            db.add(routine_ex)

    db.commit()


# INSERT WEEKLY SCHEDULE
def insert_schedule(db):
    for program, days in schedule.items():
        for day, exercises in days.items():
            for ex in exercises:
                entry = Schedule(
                    program=program,
                    day=day,
                    exercise_name=ex["exercise"],
                    sets=ex["sets"],
                    reps=ex["reps"]
                )
                db.add(entry)

    db.commit()


# RUN ALL INSERT FUNCTIONS
def run_insertion():
    db = SessionLocal()
    insert_exercises(db)
    insert_routines(db)
    insert_schedule(db)
    db.close()


if __name__ == "__main__":
    run_insertion()