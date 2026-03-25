# signup endpoint
from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from schemas import SignupRequest, SignupResponse
from auth.utils import hash_password
from database import get_db
from models import User

from auth.jwt import create_access_token
from auth.utils import verify_password


router = APIRouter(prefix="/auth", tags=["Auth"])

@router.post("/signup", response_model=SignupResponse)

def signup(request: SignupRequest, db: Session = Depends(get_db)):

    # Check if user already exists
    existing_user = db.query(User).filter(User.username == request.username).first()
    if existing_user:
        raise HTTPException(status_code=400, detail="Email already registered")

    # Hash password
    hashed_pw = hash_password(request.password)

    # Create user
    new_user = User(
        username=request.username,
        hashed_password=hashed_pw
    )

    db.add(new_user)
    db.commit()
    db.refresh(new_user)

    return SignupResponse(message="User created successfully")


# Login flow that uses token to verify 

@router.post("/login")
def login(request: SignupRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.username == request.username).first()

    if not user:
        raise HTTPException(status_code=400, detail="Invalid username or password")

    if not verify_password(request.password, user.hashed_password):
        raise HTTPException(status_code=400, detail="Invalid username or password")

    token = create_access_token({"sub": user.username})

    return {"access_token": token, "token_type": "bearer"}
