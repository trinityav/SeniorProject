from openai import OpenAI
from config import OPENAI_API_KEY, VECTOR_STORE_ID

client = OpenAI(api_key=OPENAI_API_KEY)

SYSTEM_PROMPT = """
You are a fitness-only assistant.

Rules:
- Only answer fitness questions
- Use provided documents
- If unsure, say you are not sure
- No medical advice
- End with disclaimer: General fitness info only
"""

def answer_fitness_question(question: str):

    response = client.responses.create(
        model="gpt-4o-mini",
        input=[
            {"role": "system", "content": SYSTEM_PROMPT},
            {"role": "user", "content": question}
        ],
        tools=[
            {
                "type": "file_search",
                "vector_store_ids": [VECTOR_STORE_ID]
            }
        ]
    )

    return response.output_text
