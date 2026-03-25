from fastapi import FastAPI, Depends
from database import SessionLocal, engine
import models
import schemas
from auth.routes import router as auth_router
from ai_engine import generate_workout_plan
from pydantic import BaseModel
from typing import List



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
@app.get("/profile/{user_id}", response_model=schemas.UserResponse)
def get_profile(user_id: int, db: Session = Depends(get_db)):

    user = db.query(models.User).filter(models.User.id == user_id).first()

    return user



# UPDATE PROFILE
@app.post("/profile/update")
def update_profile(profile: schemas.ProfileUpdate, db: Session = Depends(get_db)):

    user = db.query(models.User).filter(models.User.id == profile.user_id).first()

    if not user:
        return {"error": "User not found"}

    user.age = profile.age
    user.schedule = profile.schedule

    db.commit()

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
def ai_plan(request: AIRequest):

    plan = generate_workout_plan(
        request.age,
        request.days,
        request.duration,
        request.goal
    )

    return {"plan": plan}
