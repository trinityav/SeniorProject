from __future__ import annotations

from typing import Dict, List, Optional

from Backend.workout_data import workouts

DAY_ORDER = [
    "monday",
    "tuesday",
    "wednesday",
    "thursday",
    "friday",
    "saturday",
    "sunday",
]

LEVEL_ORDER = {
    "beginner": 1,
    "beginner-intermediate": 2,
    "intermediate": 3,
    "intermediate-advanced": 4,
    "advanced": 5,
}

FOCUS_KEYWORDS = {
    "full_body": ["full body", "core", "quads", "glutes", "back", "chest", "shoulders"],
    "upper": ["chest", "back", "biceps", "triceps", "shoulders"],
    "lower": ["quads", "glute", "hamstring", "calves", "posterior chain"],
    "push": ["chest", "triceps", "shoulders"],
    "pull": ["back", "biceps", "forearms", "lower back"],
    "legs": ["quads", "glute", "hamstring", "calves", "posterior chain"],
}


def normalize_level(level: Optional[str]) -> str:
    if not level:
        return "beginner"
    value = level.strip().lower()
    if value in {"intermediate", "advanced", "beginner"}:
        return value
    if "advanced" in value and "intermediate" in value:
        return "intermediate"
    if "intermediate" in value:
        return "intermediate"
    return "beginner"


def minutes_between(start_time: str, end_time: str) -> int:
    start_hour, start_min = [int(x) for x in start_time.split(":")]
    end_hour, end_min = [int(x) for x in end_time.split(":")]
    return max(((end_hour * 60) + end_min) - ((start_hour * 60) + start_min), 20)


def sort_slots(slots: List[Dict]) -> List[Dict]:
    def key(slot: Dict):
        day_index = DAY_ORDER.index(slot["day"].lower()) if slot["day"].lower() in DAY_ORDER else 99
        return day_index, slot["start_time"]

    return sorted(slots, key=key)


def choose_split(slot_count: int, level: str) -> List[str]:
    if slot_count <= 2:
        return ["full_body"] * slot_count
    if slot_count == 3:
        return ["full_body", "full_body", "full_body"]
    if slot_count == 4:
        return ["upper", "lower", "upper", "lower"]
    if level == "advanced":
        return ["push", "pull", "legs", "push", "pull", "legs"][:slot_count]
    return ["push", "pull", "legs", "upper", "lower"][:slot_count]


def allowed_level_values(level: str) -> List[str]:
    max_rank = LEVEL_ORDER[level]
    return [name for name, rank in LEVEL_ORDER.items() if rank <= max_rank]


def matches_focus(target_group: str, focus: str) -> bool:
    target_lower = target_group.lower()
    for keyword in FOCUS_KEYWORDS[focus]:
        if keyword in target_lower:
            return True
    return False


def build_exercise_pool(level: str, focus: str) -> List[Dict]:
    allowed = set(allowed_level_values(level))
    pool: List[Dict] = []
    for item in workouts:
        difficulty = item.get("difficulty", "Beginner").strip().lower()
        target = item.get("targeted_muscle_group") or item.get("targeted muscle group") or ""
        if difficulty not in allowed:
            continue
        if not matches_focus(target, focus):
            continue
        pool.append(
            {
                "name": item["name"],
                "targeted_muscle_group": target,
                "equipment": item.get("equipment", "Unknown"),
                "difficulty": item.get("difficulty", "Beginner"),
            }
        )
    return pool


def reps_for_level(level: str, focus: str) -> tuple[str, str]:
    if focus in {"full_body", "legs", "lower"}:
        if level == "advanced":
            return "4", "6-10"
        if level == "intermediate":
            return "3-4", "8-12"
        return "2-3", "10-15"
    if level == "advanced":
        return "4", "6-10"
    if level == "intermediate":
        return "3-4", "8-12"
    return "2-3", "10-15"


def exercise_count_for_duration(duration: int, level: str) -> int:
    base = 3
    if duration >= 45:
        base = 4
    if duration >= 60:
        base = 5
    if level == "advanced" and duration >= 60:
        base = 6
    return base


def estimate_intensity(age: Optional[int], level: str, weight: Optional[int], height_feet: Optional[int], height_inches: Optional[int]) -> str:
    score = {"beginner": 1, "intermediate": 2, "advanced": 3}[level]
    if age and age < 30:
        score += 1
    if age and age >= 45:
        score -= 1
    if weight and height_feet is not None and height_inches is not None:
        total_inches = (height_feet * 12) + height_inches
        if total_inches > 0:
            bmi_like = (weight / (total_inches * total_inches)) * 703
            if bmi_like >= 30:
                score -= 1
    if score <= 1:
        return "Light"
    if score == 2:
        return "Moderate"
    return "High"


def generate_workout_plan_for_user(user, availability_slots: List[Dict], goal_override: Optional[str] = None) -> Dict:
    slots = sort_slots(availability_slots)
    if not slots:
        raise ValueError("At least one availability slot is required.")

    level = normalize_level(user.fitness_level)
    goal = goal_override or user.fitness_goal or "general_fitness"
    split = choose_split(len(slots), level)
    intensity = estimate_intensity(user.age, level, user.weight, user.height_feet, user.height_inches)

    plan_days: List[Dict] = []
    used_names: List[str] = []

    for index, slot in enumerate(slots):
        focus = split[index % len(split)]
        duration = minutes_between(slot["start_time"], slot["end_time"])
        pool = build_exercise_pool(level, focus)
        if len(pool) < 3:
            pool = build_exercise_pool(level, "full_body")

        count = min(exercise_count_for_duration(duration, level), len(pool))
        if count == 0:
            raise ValueError(f"No workout data found for focus: {focus}")

        chosen: List[Dict] = []
        for item in pool:
            if item["name"] in used_names:
                continue
            chosen.append(item)
            used_names.append(item["name"])
            if len(chosen) == count:
                break

        if len(chosen) < count:
            for item in pool:
                if item["name"] not in [x["name"] for x in chosen]:
                    chosen.append(item)
                if len(chosen) == count:
                    break

        sets, reps = reps_for_level(level, focus)
        per_exercise_minutes = max(int(duration / max(len(chosen), 1)), 8)
        exercises = []
        total_minutes = 0
        for exercise in chosen:
            exercises.append(
                {
                    "exercise_name": exercise["name"],
                    "targeted_muscle_group": exercise["targeted_muscle_group"],
                    "equipment": exercise["equipment"],
                    "difficulty": exercise["difficulty"],
                    "sets": sets,
                    "reps": reps,
                    "estimated_minutes": per_exercise_minutes,
                }
            )
            total_minutes += per_exercise_minutes

        plan_days.append(
            {
                "day": slot["day"],
                "start_time": slot["start_time"],
                "end_time": slot["end_time"],
                "focus": focus.replace("_", " ").title(),
                "estimated_total_minutes": total_minutes,
                "intensity": intensity,
                "goal": goal,
                "exercises": exercises,
            }
        )

    return {
        "user_id": user.id,
        "fitness_goal": goal,
        "fitness_level": level,
        "plan": plan_days,
    }
