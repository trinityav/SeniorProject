# Routines for different muscle groups and fitness levels

workouts = [
    # Chest exercises
    {
        "name": "Incline Dumbbell Press",
        "targeted muscle group": "Chest-Shoulders-Triceps",
        "equipment": "Dumbbells Bench",
        "difficulty": "Intermediate"
    },
    {
        "name": "Cable or Pec deck flys",
        "targeted muscle group": "Chest",
        "equipment": "Pec Deck Machine or Cable Machine",
        "difficulty": "Beginner-Intermediate"
    },
    {
        "name": "Machine Chest Press",
        "targeted muscle group": "Chest-Triceps",
        "equipment": "Chest Press Machine",
        "difficulty": "Beginner"
    },
    {
        "name": "Barbell Bench Press", 
        "targeted muscle group": "Chest-Shoulders-Triceps",
        "equipment": "Barbell Bench", 
        "difficulty": "Intermediate"
    },
    
    # Back exercises
    {
        "name": "Lat Pulldown",
        "targeted muscle group": "Back-Biceps",
        "equipment": "Lat Pulldown Machine",
        "difficulty": "Beginner-Intermediate",
    },
    {
        "name": "Seated Cable Row",
        "targeted muscle group": "Back-Biceps",
        "equipment": "Cable Machine",
        "difficulty": "Beginner-Intermediate"
    },
    {
        "name": "Pull-ups",
        "targeted muscle group": "Back-Biceps",
        "equipment": "Pull-up Bar",
        "difficulty": "Intermediate-Advanced"
    },
    {
        "name": "Assisted Pull-ups",
        "targeted muscle group": "Back-Biceps",
        "equipment": "Assisted Pull-up Machine",
        "difficulty": "Beginner-Intermediate"  
    },
    {
        "name": "Chin-Ups",
        "targeted muscle group": "Back-Biceps",
        "equipment": "Pull-up Bar",
        "difficulty": "Intermediate"
    },
    {
        "name": "Weighted Pull-ups",
        "targeted muscle group": "Back-Biceps",
        "equipment": "Back Extension Machine or Roman Chair",
        "difficulty": "Beginner-Intermediate"
    },
    {
        "name": "Inverted Rows",
        "targeted muscle group": "Back-Biceps",
        "equipment": "Bar or TRX",
        "difficulty": "Beginner-Intermediate"  
    },
    
    # Biceps
    {
        "name": "Barbell Curls",
        "targeted muscle group": "Biceps",
        "equipment": "Barbell",
        "difficulty": "Beginner-Intermediate" 
    },
    {
        "name": "Cable Bicep Curls",
        "targeted muscle group": "Biceps",
        "equipment": "Cable Machine",
        "difficulty": "Beginner" 
    },
    {
        "name": "Cross-Body Dumbbell Curls",
        "targeted muscle group": "Biceps",
        "equipment": "Dumbbells",
        "difficulty": "Beginner" 
    },
    {
        "name": "Dumbbell Bicep Curls",
        "targeted muscle group": "Biceps",
        "equipment": "Dumbbells",
        "difficulty": "Beginner" 
    },
    {
        "name": "Incline Dumbbell Curls",
        "targeted muscle group": "Biceps",
        "equipment": "Dumbells Incline Bench",
        "difficulty": "Intermediate" 
    },
    {
        "name": "Hammer Curls",
        "targeted muscle group": "Biceps-Forearms",
        "equipment": "Dumbells",
        "difficulty": "Beginner" 
    },
    
    # Calves
    {
        "name": "Standing Calf Raises",
        "targeted muscle group": "Calves",
        "equipment": "Machine or Bodyweight",
        "difficulty": "Beginner" 
    },
    
    # Core exercises
    {
        "name": "Plank",
        "targeted muscle group": "Core",
        "equipment": "Bodyweight",
        "difficulty": "Beginner" 
    },
    {
        "name": "Mountain Climbers",
        "targeted muscle group": "Core-Cardio",
        "equipment": "Bodyweight",
        "difficulty": "Beginner" 
    },
    {
        "name": "Side Plank",
        "targeted muscle group": "Core-Obliques",
        "equipment": "Bodyweight",
        "difficulty": "Beginner" 
    },
    
    # Full Body exercise
    {
        "name": "Burpees",
        "targeted muscle group": "Full Body",
        "equipment": "Bodyweight",
        "difficulty": "Intermediate" 
    },
    
    # Glutes exercises
    {
        "name": "Glute Bridge",
        "targeted muscle group": "Glutes-Hamstring",
        "equipment": "Bodyweight",
        "difficulty": "Beginner" 
    },
    {
        "name": "Single-Leg Glute Bridge",
        "targeted muscle group": "Glute-Hamstring",
        "equipment": "Bodyweight",
        "difficulty": "Beginner" 
    },
    
    #Lower Abs exercises
    {
        "name": "Leg Raises Lying",
        "targeted muscle group": "Lower Abs",
        "equipment": "Bodyweight",
        "difficulty": "Beginner-Intermediate" 
    },
    {
        "name": "Hanging Leg Raises",
        "targeted muscle group": "Lower Abs-Hip Flexors",
        "equipment": "Pull-up Bar",
        "difficulty": "Intermediate-Advanced" 
    },
    
    # Lower Back exercises
    {
        "name": "Back Extension",
        "targeted muscle group": "Lower Back",
        "equipment": "Back Extension Machine",
        "difficulty": "Beginner" 
    },
    {
        "name": "Superman Hold",
        "targeted muscle group": "Lower Back",
        "equipment": "Bodyweight",
        "difficulty": "Beginner" 
    },

    #Obliques exercises
    {
        "name": "Weighted Abs Twist Machine",
        "targeted muscle group": "Obliques",
        "equipment": "Machine",
        "difficulty": "Beginner" 
    },

    #Posterior Chain exercises
    {
        "name": "Deadlift",
        "targeted muscle group": "Posterior Chain",
        "equipment": "Barbell",
        "difficulty": "Intermediate-Advanced" 
    },

    # Quads exercises
    {
        "name": "Leg Extensions",
        "targeted muscle group": "Quads",
        "equipment": "Leg Extension Machine",
        "difficulty": "Beginner" 
    },
    {
        "name": "Barbell Squats",
        "targeted muscle group": "Quads-Glutes",
        "equipment": "Barbell rack",
        "difficulty": "Intermediate-Advanced" 
    },
    {
        "name": "Bulgarian Split Squat",
        "targeted muscle group": "Quads-Glutes",
        "equipment": "Bench Dumbbells optional",
        "difficulty": "Intermediate" 
    },
    {
        "name": "Dumbell Lunges",
        "targeted muscle group": "Quads-Glutes",
        "equipment": "Dumbbells ",
        "difficulty": "Beginner-Intermediate" 
    },
    {
        "name": "Goblet Squats",
        "targeted muscle group": "Quads-Glutes",
        "equipment": "Dumbbell or Kettleball ",
        "difficulty": "Beginner" 
    },
    {
        "name": "Jump Squats",
        "targeted muscle group": "Quads-Glutes",
        "equipment": "Bodyweight ",
        "difficulty": "Intermediate" 
    },
    {
        "name": "Leg Press",
        "targeted muscle group": "Quads-Glutes",
        "equipment": "Leg Press Machine ",
        "difficulty": "Beginner" 
    },
    {
        "name": "Walking Lunges",
        "targeted muscle group": "Quads-Glutes",
        "equipment": "Bodyweight or Dumbells ",
        "difficulty": "Beginner-Intermediate" 
    },
    # Shoulders exercises
    {
        "name": "Wall Handstand Hold",
        "targeted muscle group": "Shoulders-Core",
        "equipment": "Wall ",
        "difficulty": "Advanced" 
    },
    {
        "name": "Pike Push-ups",
        "targeted muscle group": "Shoulders-Triceps",
        "equipment": "Bodyweight ",
        "difficulty": "Intermediate" 
    },
    #Triceps exercises
    {
        "name": "Cable Triceps Pushaway",
        "targeted muscle group": "Triceps",
        "equipment": "Cable Machine ",
        "difficulty": "Beginner-Intermediate" 
    },
    {
        "name": "Overhead Dumbell Extension",
        "targeted muscle group": "Triceps",
        "equipment": "Dumbell ",
        "difficulty": "Beginner-Intermediate" 
    },
    {
        "name": "Skull Crushers",
        "targeted muscle group": "Triceps",
        "equipment": "EZ Bar or Dumbell ",
        "difficulty": "Intermediate" 
    },
    {
        "name": "Tricep Pushdown",
        "targeted muscle group": "Triceps",
        "equipment": "Cable Machine ",
        "difficulty": "Beginner" 
    },
    {
        "name": "Bench Dips",
        "targeted muscle group": "Triceps-Chest",
        "equipment": "Bench ",
        "difficulty": "Beginner" 
    },
    {
        "name": "Diamond Push-ups",
        "targeted muscle group": "Triceps-Chest",
        "equipment": "Bodyweight",
        "difficulty": "Intermediate" 
    },
    {
        "name": "Weighted Dips",
        "targeted muscle group": "Triceps-Chest",
        "equipment": "Dip Bars Weight Belt ",
        "difficulty": "Advanced" 
    },
    
]
