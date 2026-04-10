from datetime import datetime

from sqlalchemy import Column, DateTime, Float, ForeignKey, Integer, String, Text
from sqlalchemy.orm import relationship

from Backend.database import Base


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, nullable=False, index=True)
    hashed_password = Column(String, nullable=False)

    age = Column(Integer, nullable=True)
    gender = Column(String, nullable=True)
    weight = Column(Integer, nullable=True)
    height_feet = Column(Integer, nullable=True)
    height_inches = Column(Integer, nullable=True)
    fitness_level = Column(String, nullable=True)
    fitness_goal = Column(String, nullable=True)
    schedule = Column(Text, nullable=True)

    availability_slots = relationship(
        "AvailabilitySlot",
        back_populates="user",
        cascade="all, delete-orphan",
    )
    workout_plans = relationship(
        "WorkoutPlan",
        back_populates="user",
        cascade="all, delete-orphan",
    )
    workout_logs = relationship(
        "WorkoutLog",
        back_populates="user",
        cascade="all, delete-orphan",
    )
    feedback_entries = relationship(
        "Feedback",
        back_populates="user",
        cascade="all, delete-orphan",
    )
    chat_history = relationship(
        "ChatHistory",
        back_populates="user",
        cascade="all, delete-orphan",
    )


class AvailabilitySlot(Base):
    __tablename__ = "availability_slots"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    day = Column(String, nullable=False)
    start_time = Column(String, nullable=False)
    end_time = Column(String, nullable=False)

    user = relationship("User", back_populates="availability_slots")


class WorkoutPlan(Base):
    __tablename__ = "workout_plans"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)
    fitness_goal = Column(String, nullable=True)
    fitness_level = Column(String, nullable=True)
    status = Column(String, default="active", nullable=False)

    user = relationship("User", back_populates="workout_plans")
    items = relationship(
        "WorkoutPlanItem",
        back_populates="plan",
        cascade="all, delete-orphan",
    )


class WorkoutPlanItem(Base):
    __tablename__ = "workout_plan_items"

    id = Column(Integer, primary_key=True, index=True)
    plan_id = Column(Integer, ForeignKey("workout_plans.id"), nullable=False, index=True)
    day = Column(String, nullable=False)
    start_time = Column(String, nullable=True)
    end_time = Column(String, nullable=True)
    focus = Column(String, nullable=False)
    exercise_name = Column(String, nullable=False)
    targeted_muscle_group = Column(String, nullable=True)
    equipment = Column(String, nullable=True)
    difficulty = Column(String, nullable=True)
    sets = Column(String, nullable=False)
    reps = Column(String, nullable=False)
    estimated_minutes = Column(Integer, nullable=False, default=0)
    sort_order = Column(Integer, nullable=False, default=0)

    plan = relationship("WorkoutPlan", back_populates="items")


class WorkoutLog(Base):
    __tablename__ = "workout_logs"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    plan_item_id = Column(Integer, ForeignKey("workout_plan_items.id"), nullable=True)
    performed_at = Column(String, nullable=False)
    day = Column(String, nullable=True)
    workout_name = Column(String, nullable=False)
    duration_minutes = Column(Integer, nullable=False)
    intensity = Column(String, nullable=True)
    notes = Column(Text, nullable=True)

    user = relationship("User", back_populates="workout_logs")


class Feedback(Base):
    __tablename__ = "feedback"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    plan_id = Column(Integer, ForeignKey("workout_plans.id"), nullable=True)
    day = Column(String, nullable=True)
    rating = Column(String, nullable=False)
    comments = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)

    user = relationship("User", back_populates="feedback_entries")


class ChatHistory(Base):
    __tablename__ = "chat_history"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("users.id"), nullable=False, index=True)
    question = Column(Text, nullable=False)
    answer = Column(Text, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)

    user = relationship("User", back_populates="chat_history")


class Exercise(Base):
    __tablename__ = "exercises"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, unique=True, index=True, nullable=False)
    targeted_muscle_group = Column(String, nullable=False)
    equipment = Column(String, nullable=True)
    difficulty = Column(String, nullable=True)


class Routine(Base):
    __tablename__ = "routines"

    id = Column(Integer, primary_key=True, index=True)
    routine_name = Column(String, index=True, nullable=False)

    exercises = relationship(
        "RoutineExercise",
        back_populates="routine",
        cascade="all, delete-orphan",
    )


class RoutineExercise(Base):
    __tablename__ = "routine_exercises"

    id = Column(Integer, primary_key=True, index=True)
    routine_id = Column(Integer, ForeignKey("routines.id"), nullable=False)
    exercise_name = Column(String, nullable=False)
    sets = Column(String, nullable=False)
    reps = Column(String, nullable=False)

    routine = relationship("Routine", back_populates="exercises")


class Schedule(Base):
    __tablename__ = "schedule"

    id = Column(Integer, primary_key=True, index=True)
    program = Column(String, nullable=False)
    day = Column(String, nullable=False)
    exercise_name = Column(String, nullable=False)
    sets = Column(String, nullable=False)
    reps = Column(String, nullable=False)
