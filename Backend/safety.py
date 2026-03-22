FITNESS_KEYWORDS = [
    "workout", "exercise", "fitness", "gym", "cardio", "strength",
    "muscle", "sets", "reps", "training", "warm up", "cool down",
    "form", "stretch", "protein", "recovery"
]

MEDICAL_KEYWORDS = [
    "diagnose", "diagnosis", "injury", "treatment", "chest pain",
    "difficulty breathing", "severe pain", "urgent", "medical emergency"
]

DANGEROUS_KEYWORDS = [
    "starve", "extreme dieting", "harm myself", "dangerous weight loss",
    "how to not eat", "overdose", "self harm"
]

def validate_question(question: str):
    q = question.strip()
    if not q:
        return "Please enter a fitness question."
    if len(q) > 500:
        return "Your question is too long. Please keep it under 500 characters."
    return None

def should_refuse(question: str):
    q = question.lower()

    for word in MEDICAL_KEYWORDS:
        if word in q:
            return (
                "I can only provide general fitness information, not medical advice. "
                "For diagnosis, treatment, injury concerns, or urgent symptoms, please seek a qualified medical professional."
            )

    for word in DANGEROUS_KEYWORDS:
        if word in q:
            return (
                "I can’t help with harmful or dangerous behavior. "
                "I can help with safe fitness, training, nutrition basics, and healthy workout habits."
            )

    if not any(word in q for word in FITNESS_KEYWORDS):
        return (
            "I can only answer fitness-related questions. "
            "Please ask about workouts, exercises, training, recovery, or general fitness basics."
        )

    return None
