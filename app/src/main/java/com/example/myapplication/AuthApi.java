package com.example.myapplication;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public class AuthApi {

    private static final String BASE_URL = "https://seniorproject-backend.onrender.com/";

    public interface AuthService {
        @POST("auth/signup")
        Call<MessageResponse> signup(@Body SignupRequest request);

        @POST("auth/login")
        Call<TokenResponse> login(@Body LoginRequest request);

        @GET("profile/me")
        Call<UserResponse> getProfile();

        @PUT("profile/update")
        Call<MessageResponse> updateProfile(@Body ProfileUpdateRequest request);

        @GET("availability")
        Call<List<AvailabilityItemResponse>> getAvailability();

        @POST("availability")
        Call<List<AvailabilityItemResponse>> saveAvailability(@Body List<AvailabilityItem> request);

        @POST("workout-plan/generate")
        Call<GeneratedWorkoutPlanResponse> generateWorkoutPlan(@Body GeneratePlanRequest request);

        @GET("workout-plan/me")
        Call<WorkoutPlanResponse> getWorkoutPlan();

        @POST("workout-log")
        Call<MessageResponse> logWorkout(@Body WorkoutLogRequest request);

        @GET("progress/me")
        Call<ProgressResponse> getProgress();

        @POST("chatbot/ask")
        Call<ChatbotResponse> askChatbot(@Body ChatbotRequest request);
    }

    public static AuthService getService(Context context) {
        SessionManager sessionManager = new SessionManager(context);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor authInterceptor = chain -> {
            Request originalRequest = chain.request();
            String token = sessionManager.getAccessToken();

            if (token != null && !token.isEmpty()) {
                Request newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }

            return chain.proceed(originalRequest);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(AuthService.class);
    }

    public static class SignupRequest {
        private final String username;
        private final String password;

        public SignupRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class LoginRequest {
        private final String username;
        private final String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class MessageResponse {
        private String message;

        public String getMessage() {
            return message;
        }
    }

    public static class TokenResponse {
        @SerializedName("access_token")
        private String accessToken;

        @SerializedName("token_type")
        private String tokenType;

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }
    }

    public static class UserResponse {
        private int id;
        private String username;
        private Integer age;
        private String gender;
        private Integer weight;

        @SerializedName("height_feet")
        private Integer heightFeet;

        @SerializedName("height_inches")
        private Integer heightInches;

        @SerializedName("fitness_level")
        private String fitnessLevel;

        private String schedule;

        public int getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public Integer getAge() {
            return age;
        }

        public String getGender() {
            return gender;
        }

        public Integer getWeight() {
            return weight;
        }

        public Integer getHeightFeet() {
            return heightFeet;
        }

        public Integer getHeightInches() {
            return heightInches;
        }

        public String getFitnessLevel() {
            return fitnessLevel;
        }

        public String getSchedule() {
            return schedule;
        }
    }

    public static class ProfileUpdateRequest {
        private final Integer age;
        private final String gender;
        private final Integer weight;

        @SerializedName("height_feet")
        private final Integer heightFeet;

        @SerializedName("height_inches")
        private final Integer heightInches;

        @SerializedName("fitness_level")
        private final String fitnessLevel;

        @SerializedName("fitness_goal")
        private final String fitnessGoal;

        private final String schedule;

        public ProfileUpdateRequest(
                Integer age,
                String gender,
                Integer weight,
                Integer heightFeet,
                Integer heightInches,
                String fitnessLevel,
                String fitnessGoal,
                String schedule
        ) {
            this.age = age;
            this.gender = gender;
            this.weight = weight;
            this.heightFeet = heightFeet;
            this.heightInches = heightInches;
            this.fitnessLevel = fitnessLevel;
            this.fitnessGoal = fitnessGoal;
            this.schedule = schedule;
        }
    }

    public static class AvailabilityItem {
        private final String day;

        @SerializedName("start_time")
        private final String startTime;

        @SerializedName("end_time")
        private final String endTime;

        public AvailabilityItem(String day, String startTime, String endTime) {
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public String getDay() {
            return day;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }
    }

    public static class AvailabilityItemResponse {
        private int id;
        private String day;

        @SerializedName("start_time")
        private String startTime;

        @SerializedName("end_time")
        private String endTime;

        public int getId() {
            return id;
        }

        public String getDay() {
            return day;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }
    }

    public static class GeneratePlanRequest {
        @SerializedName("override_goal")
        private final String overrideGoal;

        public GeneratePlanRequest(String overrideGoal) {
            this.overrideGoal = overrideGoal;
        }
    }

    public static class GeneratedWorkoutPlanResponse {
        @SerializedName("user_id")
        private int userId;

        @SerializedName("fitness_goal")
        private String fitnessGoal;

        @SerializedName("fitness_level")
        private String fitnessLevel;

        private List<WorkoutPlanItem> plan;

        public int getUserId() {
            return userId;
        }

        public String getFitnessGoal() {
            return fitnessGoal;
        }

        public String getFitnessLevel() {
            return fitnessLevel;
        }

        public List<WorkoutPlanItem> getPlan() {
            return plan;
        }
    }

    public static class WorkoutPlanResponse {
        private int id;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("fitness_goal")
        private String fitnessGoal;

        @SerializedName("fitness_level")
        private String fitnessLevel;

        private String status;
        private List<WorkoutPlanItem> items;

        public int getId() {
            return id;
        }

        public int getUserId() {
            return userId;
        }

        public String getFitnessGoal() {
            return fitnessGoal;
        }

        public String getFitnessLevel() {
            return fitnessLevel;
        }

        public String getStatus() {
            return status;
        }

        public List<WorkoutPlanItem> getItems() {
            return items;
        }
    }

    public static class WorkoutPlanItem {
        private String day;

        @SerializedName("start_time")
        private String startTime;

        @SerializedName("end_time")
        private String endTime;

        private String focus;

        @SerializedName("estimated_total_minutes")
        private Integer estimatedTotalMinutes;

        private List<ExerciseItem> exercises;

        public String getDay() {
            return day;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getFocus() {
            return focus;
        }

        public Integer getEstimatedTotalMinutes() {
            return estimatedTotalMinutes;
        }

        public List<ExerciseItem> getExercises() {
            return exercises;
        }
    }

    public static class ExerciseItem {
        @SerializedName(value = "exercise_name", alternate = {"name"})
        private String exerciseName;

        @SerializedName("sets")
        private String sets;

        @SerializedName("reps")
        private String reps;

        public String getExerciseName() {
            return exerciseName;
        }

        public String getSets() {
            return sets;
        }

        public String getReps() {
            return reps;
        }
    }

    public static class WorkoutLogRequest {
        private final String date;
        private final String workout;
        private final Integer duration;
        private final String intensity;

        public WorkoutLogRequest(String date, String workout, Integer duration, String intensity) {
            this.date = date;
            this.workout = workout;
            this.duration = duration;
            this.intensity = intensity;
        }
    }

    public static class ProgressResponse {
        @SerializedName(value = "total_workouts", alternate = {"totalWorkouts", "total_logged_workouts"})
        private Integer totalWorkouts;

        @SerializedName(value = "last_workout", alternate = {"lastWorkout", "most_recent_workout"})
        private String lastWorkout;

        @SerializedName(value = "current_streak", alternate = {"currentStreak"})
        private Integer currentStreak;

        public Integer getTotalWorkouts() {
            return totalWorkouts;
        }

        public String getLastWorkout() {
            return lastWorkout;
        }

        public Integer getCurrentStreak() {
            return currentStreak;
        }
    }

    public static class ChatbotRequest {
        private final String question;

        public ChatbotRequest(String question) {
            this.question = question;
        }
    }

    public static class ChatbotResponse {
        private String answer;

        public String getAnswer() {
            return answer;
        }
    }
}