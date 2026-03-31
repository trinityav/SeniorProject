from pydantic import BaseModel
from typing import Optional

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