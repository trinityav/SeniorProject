from sqlalchemy import Column, Integer, String, ForeignKey
from sqlalchemy.orm import relationship
from database import Base

class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True)
    hashed_password = Column(String, nullable=False)
    age = Column(Integer)
    schedule = Column(String)
    
class Exercise(Base):
    __tablename__ = "exercises"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, unique=True, index=True)
    targeted_muscle_group = Column(String)
    equipment = Column(String)
    difficulty = Column(String)
    
class Routine(Base):
    __tablename__ = "routines"

    id = Column(Integer, primary_key=True, index=True)
    routine_name = Column(String, index=True)

    exercises = relationship("RoutineExercise", back_populates="routine")
    
class RoutineExercise(Base):
    __tablename__ = "routine_exercises"

    id = Column(Integer, primary_key=True, index=True)
    routine_id = Column(Integer, ForeignKey("routines.id"))
    exercise_name = Column(String)
    sets = Column(String)
    reps = Column(String)

    routine = relationship("Routine", back_populates="exercises")
    
class Schedule(Base):
    __tablename__ = "schedule"

    id = Column(Integer, primary_key=True, index=True)
    program = Column(String)   # beginner_calisthenics, intermediate, etc.
    day = Column(String)       # monday, tuesday, etc.
    exercise_name = Column(String)
    sets = Column(String)
    reps = Column(String)