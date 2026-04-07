import chromadb
from workoutdata import workouts


client = chromadb.Client()


collection = client.create_collection("fitness")


def workout_to_text(w):
    return f"{w['name']} targets {w['targeted muscle group']} using {w['equipment']} and is {w['difficulty']} level."


def load_workouts():
    documents = []
    ids = []

    for i, w in enumerate(workouts):
        documents.append(workout_to_text(w))
        ids.append(str(i))

    collection.add(
        documents=documents,
        ids=ids
    )
def search_workouts(query):
    results = collection.query(
        query_texts=[query],
        n_results=3
    )
    return results["documents"][0]

if __name__ == "__main__":
    load_workouts()

    query = "back exercises"
    results = search_workouts(query)

    print("Search Results:")
    for r in results:
        print("-", r)
