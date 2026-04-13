from __future__ import annotations

import os
from typing import Optional

from dotenv import load_dotenv
from openai import OpenAI

from Backend.safety import should_refuse, validate_question
from Backend.vector_store import load_workouts, search_workouts

load_dotenv()
load_workouts()

OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=OPENAI_API_KEY)

SYSTEM_PROMPT = """
You are a strict fitness-only assistant.
Rules:
1. Only answer fitness and workout questions.
2. Do not give medical advice or diagnoses.
3. Keep answers short and practical.
4. Use the provided workout context when possible.
5. End with: General fitness info only.
""".strip()


def answer_fitness_question(question: str, user_profile: Optional[str] = None) -> str:
    validation_error = validate_question(question)
    if validation_error:
        return validation_error

    refusal = should_refuse(question)
    if refusal:
        return refusal

    if not OPENAI_API_KEY:
        return "Chatbot error: OPENAI_API_KEY is missing."

    context = "\n".join(search_workouts(question, n_results=5))
    profile_text = user_profile or "No user profile provided."

    try:
        response = client.responses.create(
            model="gpt-4o-mini",
            input=[
                {"role": "system", "content": SYSTEM_PROMPT},
                {"role": "system", "content": f"User profile: {profile_text}"},
                {"role": "system", "content": f"Workout context:\n{context}"},
                {"role": "user", "content": question},
            ],
        )
        return response.output_text.strip()
    except Exception as exc:
        return f"Chatbot error: {exc}"