from pydantic import BaseModel
from typing import Optional
from typing import List

class ProfileUpdate(BaseModel):
    age: Optional[int] = None
    schedule: Optional[str] = None


class UserResponse(BaseModel):
    id: int
    username: str
    age: Optional[int] = None
    schedule: Optional[str] = None

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


## Workout plan models
class WorkoutRoutine(BaseModel):
    day: str
    workout: str
    intensity: str
    duration: int
    
class WorkoutPlan(BaseModel):
    plan: List[WorkoutRoutine]
    
class Exercise(BaseModel):
    exercise_name: str
    targeted_muscle_group: str
    equipment: str
    difficulty: str
    sets: int
    reps: int
    
## Workout schedule models  
class WorkoutDay(BaseModel):
    day: str
    exercises: List[Exercise]
    
class WorkoutSchedule(BaseModel):
    program: str
    days: List[WorkoutDay]
    
class WorkoutPlanRequest(BaseModel):
    age: int
    days: List[str]
    duration: int
    goal: str

## Save workout plan and log models
class SaveWorkoutPlanRequest(BaseModel):
    user_id: int
    plan: List[WorkoutRoutine]
    
class WorkoutLogEntry(BaseModel):
    user_id: int
    date: str
    workout: str
    duration: int
    intensity: str
