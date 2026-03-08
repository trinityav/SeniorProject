from fastapi import FastAPI, Depends
from sqlalchemy.orm import Session
import models
import schemas
from database import SessionLocal, engine
from models import Base

models.Base.metadata.create_all(bind=engine)

app = FastAPI()


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