from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from database import engine, get_db
import models
import schemas
from routes import router as auth_router
from ai_engine import generate_workout_plan
from pydantic import BaseModel
from typing import List
from dependencies import get_current_user


#Made changes

# this creates the tables 
models.Base.metadata.create_all(bind=engine)
app = FastAPI()
app.include_router(auth_router)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


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

    db.commit()
    db.refresh(user)

    return {"message": "Profile updated"}


# Uses the AI engine to generate a workout plan based on user input
class AIRequest(BaseModel):
    age: int
    days: List[str]
    duration: int
    goal: str


@app.get("/")
def home():
    return {"message": "Backend is running"}


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