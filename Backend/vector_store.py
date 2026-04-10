from __future__ import annotations

from typing import Dict, List

from Backend.workout_data import workouts


_SEARCH_INDEX: List[Dict] = []


def workout_to_text(item: Dict) -> str:
    target = item.get("targeted_muscle_group") or item.get("targeted muscle group") or ""
    return f"{item['name']} targets {target}. Equipment: {item.get('equipment', 'Unknown')}. Difficulty: {item.get('difficulty', 'Unknown')}."


def load_workouts() -> None:
    global _SEARCH_INDEX
    if _SEARCH_INDEX:
        return
    _SEARCH_INDEX = []
    for item in workouts:
        _SEARCH_INDEX.append(
            {
                "text": workout_to_text(item),
                "raw": item,
            }
        )


def _score(query: str, text: str) -> int:
    score = 0
    query_words = {word for word in query.lower().split() if word}
    text_lower = text.lower()
    for word in query_words:
        if word in text_lower:
            score += 1
    return score


def search_workouts(query: str, n_results: int = 5) -> List[str]:
    load_workouts()
    scored = sorted(_SEARCH_INDEX, key=lambda item: _score(query, item["text"]), reverse=True)
    return [item["text"] for item in scored[:n_results]]
