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

LEVEL_RANK = {
    "beginner": 1,
    "intermediate": 2,
    "advanced": 3,
}

FOCUS_PATTERNS = {
    "full_body_a": ["chest", "back", "quads", "core", "glutes"],
    "full_body_b": ["shoulders", "back", "quads", "biceps", "triceps"],
    "full_body_c": ["chest", "glutes", "hamstring", "core", "shoulders"],
    "upper": ["chest", "back", "shoulders", "biceps", "triceps"],
    "lower": ["quads", "glute", "hamstring", "calves", "posterior chain"],
    "push": ["chest", "shoulders", "triceps"],
    "pull": ["back", "biceps", "lower back"],
    "legs": ["quads", "glute", "hamstring", "calves", "posterior chain"],
}

FOCUS_DISPLAY = {
    "full_body_a": "Full Body A",
    "full_body_b": "Full Body B",
    "full_body_c": "Full Body C",
    "upper": "Upper Body",
    "lower": "Lower Body",
    "push": "Push Day",
    "pull": "Pull Day",
    "legs": "Leg Day",
}


def normalize_level(level: Optional[str]) -> str:
    if not level:
        return "beginner"
    value = level.strip().lower()
    if "advanced" in value:
        return "advanced"
    if "intermediate" in value:
        return "intermediate"
    return "beginner"


def normalize_difficulty_label(label: Optional[str]) -> str:
    if not label:
        return "beginner"
    value = label.strip().lower()
    if "advanced" in value and "intermediate" in value:
        return "advanced"
    if "advanced" in value:
        return "advanced"
    if "intermediate" in value:
        return "intermediate"
    return "beginner"


def can_use_for_level(exercise_level: str, user_level: str) -> bool:
    exercise_rank = LEVEL_RANK[normalize_difficulty_label(exercise_level)]
    user_rank = LEVEL_RANK[user_level]
    return exercise_rank <= user_rank


def sort_slots(slots: List[Dict]) -> List[Dict]:
    def key(slot: Dict):
        day = slot["day"].lower()
        return DAY_ORDER.index(day) if day in DAY_ORDER else 99, slot["start_time"]

    return sorted(slots, key=key)


def minutes_between(start_time: str, end_time: str) -> int:
    start_hour, start_min = [int(x) for x in start_time.split(":")]
    end_hour, end_min = [int(x) for x in end_time.split(":")]
    duration = ((end_hour * 60) + end_min) - ((start_hour * 60) + start_min)
    return max(duration, 10)


def choose_split(slot_count: int, level: str) -> List[str]:
    if slot_count == 1:
        return ["full_body_a"]

    if slot_count == 2:
        if level == "beginner":
            return ["full_body_a", "full_body_b"]
        return ["upper", "lower"]

    if slot_count == 3:
        if level == "beginner":
            return ["full_body_a", "full_body_b", "full_body_c"]
        return ["push", "pull", "legs"]

    if slot_count == 4:
        return ["upper", "lower", "upper", "lower"]

    if level == "advanced":
        return ["push", "pull", "legs", "upper", "lower", "full_body_a"][:slot_count]

    return ["push", "pull", "legs", "upper", "lower"][:slot_count]


def target_matches_focus(target_group: str, focus_key: str) -> bool:
    target = target_group.lower()
    for keyword in FOCUS_PATTERNS[focus_key]:
        if keyword in target:
            return True
    return False


def get_target_group(item: Dict) -> str:
    return (
        item.get("targeted_muscle_group")
        or item.get("targeted muscle group")
        or item.get("targetedmuscle_group")
        or ""
    )


def build_pool(user_level: str, focus_key: str) -> List[Dict]:
    pool = []
    for item in workouts:
        target = get_target_group(item)
        if not target:
            continue
        if not can_use_for_level(item.get("difficulty"), user_level):
            continue
        if not target_matches_focus(target, focus_key):
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


def exercise_count_for_duration(duration_minutes: int) -> int:
    if duration_minutes <= 15:
        return 3
    if duration_minutes <= 30:
        return 5
    if duration_minutes <= 45:
        return 6
    if duration_minutes <= 60:
        return 7
    return 8


def sets_reps_for_level(level: str, focus_key: str) -> tuple[str, str]:
    if level == "beginner":
        if focus_key in {"legs", "lower"}:
            return "2-3", "10-12"
        return "2-3", "10-15"

    if level == "intermediate":
        if focus_key in {"push", "pull", "legs", "upper", "lower"}:
            return "3-4", "8-12"
        return "3", "10-12"

    if focus_key in {"push", "pull", "legs", "upper", "lower"}:
        return "4", "6-10"
    return "3-4", "8-12"


def estimate_intensity(level: str, duration_minutes: int) -> str:
    if level == "advanced":
        return "High"
    if level == "intermediate":
        return "Moderate" if duration_minutes < 45 else "High"
    return "Light" if duration_minutes < 30 else "Moderate"


def pick_exercises(pool: List[Dict], count: int, used_names: set[str]) -> List[Dict]:
    chosen = []

    for item in pool:
        if item["name"] in used_names:
            continue
        chosen.append(item)
        used_names.add(item["name"])
        if len(chosen) == count:
            return chosen

    for item in pool:
        if item["name"] not in [x["name"] for x in chosen]:
            chosen.append(item)
            if len(chosen) == count:
                return chosen

    return chosen


def generate_workout_plan_for_user(user, availability_slots: List[Dict], goal_override: Optional[str] = None) -> Dict:
    slots = sort_slots(availability_slots)
    if not slots:
        raise ValueError("At least one availability slot is required.")

    level = normalize_level(user.fitness_level)
    goal = goal_override or user.fitness_goal or "general_fitness"
    split = choose_split(len(slots), level)

    plan_days: List[Dict] = []
    used_names: set[str] = set()

    for index, slot in enumerate(slots):
        focus_key = split[index % len(split)]
        duration_minutes = minutes_between(slot["start_time"], slot["end_time"])

        pool = build_pool(level, focus_key)
        if len(pool) < 3:
            pool = build_pool(level, "full_body_a")

        if not pool:
            raise ValueError(f"No workout data found for {focus_key}")

        exercise_count = min(exercise_count_for_duration(duration_minutes), len(pool))
        chosen = pick_exercises(pool, exercise_count, used_names)

        sets, reps = sets_reps_for_level(level, focus_key)
        per_exercise_minutes = max(5, round(duration_minutes / max(len(chosen), 1)))

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
                "focus": FOCUS_DISPLAY[focus_key],
                "estimated_total_minutes": total_minutes,
                "intensity": estimate_intensity(level, duration_minutes),
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