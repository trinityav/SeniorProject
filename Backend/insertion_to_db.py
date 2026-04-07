from Backend.database import SessionLocal, engine
from Backend.models import Base, Exercise, Routine, RoutineExercise, Schedule
from Backend.workout_data import workouts
from Backend.workout_routines import routines
from Backend.workout_schedule import schedule

# Create all tables
Base.metadata.create_all(bind=engine)

def get_first_existing(data, keys, default=None):
    for key in keys:
        if key in data and data[key] not in [None, ""]:
            return data[key]
    return default

# INSERT EXERCISES
def insert_exercises(db):
    for w in workouts:
        existing = db.query(Exercise).filter(Exercise.name == w["name"]).first()
        if existing:
            continue

        targeted_muscle_group = get_first_existing(
            w,
            ["targeted_muscle_group", "targeted muscle group", "targetedmuscle_group"]
        )

        if not targeted_muscle_group:
            print(f"Skipping exercise '{w.get('name', 'Unknown')}' because targeted muscle group is missing")
            continue

        exercise = Exercise(
            name=w["name"],
            targeted_muscle_group=targeted_muscle_group,
            equipment=w["equipment"],
            difficulty=w["difficulty"]
        )
        db.add(exercise)

    db.commit()
    


# INSERT ROUTINES + ROUTINE EXERCISES
def insert_routines(db):
    for routine_name, exercises in routines.items():
        # Create routine row
        existing_routine = db.query(Routine).filter(Routine.routine_name == routine_name).first()
        if existing_routine:
            routine = existing_routine
        else:
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
            existing_ex = (
                db.query(RoutineExercise)
                .filter(
                    RoutineExercise.routine_id == routine.id,
                    RoutineExercise.exercise_name == ex["exercise"],
                    RoutineExercise.sets == ex["sets"],
                    RoutineExercise.reps == ex["reps"]
                )
                .first()
            )

            if existing_ex:
                continue
            
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
                
                existing_entry = (
                    db.query(Schedule)
                    .filter(
                        Schedule.program == program,
                        Schedule.day == day,
                        Schedule.exercise_name == ex["exercise"],
                        Schedule.sets == ex["sets"],
                        Schedule.reps == ex["reps"]
                    )
                    .first()
                )

                if existing_entry:
                    continue    
            
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