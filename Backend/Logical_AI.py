def generate_workout_plan(age, days, duration, goal):
    plan = []

    for day in days:

        if goal == "muscle_gain":
            workout = "Strength"
        elif goal == "weight_loss":
            workout = "Cardio"
        else:
            workout = "Full Body"

        if duration <= 30:
            intensity = "Light"
        elif duration <= 45:
            intensity = "Moderate"
        else:
            intensity = "High"

        plan.append({
            "day": day,
            "workout": workout,
            "intensity": intensity,
            "duration": duration
        })

    return plan
