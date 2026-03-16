# signup endpoint
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from Backend.models import SignupRequest, SignupResponse
from Backend.auth.utils import hash_password
from Backend.database import get_db
from Backend.models import User

router = APIRouter(prefix="/auth", tags=["Auth"])

@router.post("/signup", response_model=SignupResponse)

def signup(request: SignupRequest, db: Session = Depends(get_db)):

    # Check if user already exists
    existing_user = db.query(User).filter(User.email == request.email).first()
    if existing_user:
        raise HTTPException(status_code=400, detail="Email already registered")

    # Hash password
    hashed_pw = hash_password(request.password)

    # Create user
    new_user = User(
        email=request.email,
        hashed_password=hashed_pw
    )

    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    return SignupResponse(message="User created successfully")
