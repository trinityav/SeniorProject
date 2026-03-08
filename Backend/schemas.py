from pydantic import BaseModel

class ProfileUpdate(BaseModel):
    user_id: int
    age: int
    schedule: str


class UserResponse(BaseModel):
    id: int
    email: str
    age: int
    schedule: str

