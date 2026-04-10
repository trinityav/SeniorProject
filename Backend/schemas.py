from typing import List, Optional

from pydantic import BaseModel, Field


class AvailabilitySlotBase(BaseModel):
    day: str
    start_time: str
    end_time: str


class AvailabilitySlotCreate(AvailabilitySlotBase):
    pass


class AvailabilitySlotResponse(AvailabilitySlotBase):
    id: int

    class Config:
        from_attributes = True


class ProfileUpdate(BaseModel):
    age: Optional[int] = None
    gender: Optional[str] = None
    weight: Optional[int] = None
    height_feet: Optional[int] = None
    height_inches: Optional[int] = None
    fitness_level: Optional[str] = None
    fitness_goal: Optional[str] = None
    schedule: Optional[str] = None


class UserResponse(BaseModel):
    id: int
    username: str
    age: Optional[int] = None
    gender: Optional[str] = None
    weight: Optional[int] = None
    height_feet: Optional[int] = None
    height_inches: Optional[int] = None
    fitness_level: Optional[str] = None
    fitness_goal: Optional[str] = None
    schedule: Optional[str] = None
    availability_slots: List[AvailabilitySlotResponse] = []

    class Config:
        from_attributes = True


class SignupRequest(BaseModel):
    username: str
    password: str


class SignupResponse(BaseModel):
    message: str


class LoginRequest(BaseModel):
    username: str
    password: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str


class ExerciseInfo(BaseModel):
    exercise_name: str
    targeted_muscle_group: str
    equipment: str
    difficulty: str
    sets: str
    reps: str
    estimated_minutes: int


class GeneratedWorkoutDay(BaseModel):
    day: str
    start_time: Optional[str] = None
    end_time: Optional[str] = None
    focus: str
    estimated_total_minutes: int
    exercises: List[ExerciseInfo]


class GeneratePlanRequest(BaseModel):
    override_goal: Optional[str] = None


class GeneratedWorkoutPlanResponse(BaseModel):
    user_id: int
    fitness_goal: Optional[str] = None
    fitness_level: Optional[str] = None
    plan: List[GeneratedWorkoutDay]


class WorkoutPlanItemResponse(BaseModel):
    id: int
    day: str
    start_time: Optional[str] = None
    end_time: Optional[str] = None
    focus: str
    exercise_name: str
    targeted_muscle_group: Optional[str] = None
    equipment: Optional[str] = None
    difficulty: Optional[str] = None
    sets: str
    reps: str
    estimated_minutes: int
    sort_order: int

    class Config:
        from_attributes = True


class WorkoutPlanResponse(BaseModel):
    id: int
    user_id: int
    created_at: str
    fitness_goal: Optional[str] = None
    fitness_level: Optional[str] = None
    status: str
    items: List[WorkoutPlanItemResponse]


class WorkoutLogEntry(BaseModel):
    plan_item_id: Optional[int] = None
    performed_at: str
    day: Optional[str] = None
    workout_name: str
    duration_minutes: int
    intensity: Optional[str] = None
    notes: Optional[str] = None


class WorkoutLogResponse(BaseModel):
    id: int
    plan_item_id: Optional[int] = None
    performed_at: str
    day: Optional[str] = None
    workout_name: str
    duration_minutes: int
    intensity: Optional[str] = None
    notes: Optional[str] = None

    class Config:
        from_attributes = True


class ProgressResponse(BaseModel):
    total_logged_workouts: int
    total_minutes: int
    average_minutes: float
    most_recent_workout: Optional[str] = None


class FeedbackCreate(BaseModel):
    plan_id: Optional[int] = None
    day: Optional[str] = None
    rating: str = Field(description="easy, good, hard, liked, disliked")
    comments: Optional[str] = None


class ChatRequest(BaseModel):
    question: str


class ChatResponse(BaseModel):
    answer: str
