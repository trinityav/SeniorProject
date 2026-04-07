import os
from openai import OpenAI
from dotenv import load_dotenv
from vector_store import search_workouts
from vector_store import load_workouts
load_workouts()


load_dotenv()


client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

SYSTEM_PROMPT = """
You are a strict fitness-only assistant.

Rules:
- ONLY answer fitness-related questions
- If the question is not fitness-related, say: "I can only answer fitness questions."
- If unsure, say: "I am not sure."
- Do NOT provide medical advice
- Always end with: General fitness info only
"""

def answer_fitness_question(question: str) -> str:
    try:
        
        context = search_workouts(question)
        formatted_context = "\n".join(context)
        response = client.responses.create(
            model="gpt-4o-mini",
            input=[
                {"role": "system", "content": SYSTEM_PROMPT},

                {"role": "system", "content": f"Relevant workout data:\n{formatted_context}"},

                {"role": "user", "content": question}
            ]
        )

        return response.output_text.strip()

    except Exception as e:
        return f"Error: {str(e)}"


def run_chatbot():
    print("Fitness Chatbot (type 'exit' to quit)\n")

    while True:
        user_input = input("You: ")

        if user_input.lower() in ["exit", "quit"]:
            print("Goodbye!")
            break

        answer = answer_fitness_question(user_input)
        print(f"Bot: {answer}\n")


if __name__ == "__main__":
    run_chatbot()
