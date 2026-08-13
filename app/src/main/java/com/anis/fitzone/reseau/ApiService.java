package com.anis.fitzone.reseau;

import com.anis.fitzone.modeles.NutritionTip;
import com.anis.fitzone.modeles.Program;
import com.anis.fitzone.modeles.Quiz;
import com.anis.fitzone.modeles.Seance;
import com.anis.fitzone.modeles.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Contrat REST vers le serveur json-server local (voir server/fitzone.json).
 */
public interface ApiService {

    @GET("users")
    Call<List<User>> getUserByEmail(@Query("email") String email);

    @GET("users")
    Call<List<User>> getAllUsers();

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id);

    @POST("users")
    Call<User> createUser(@Body User user);

    @PATCH("users/{id}")
    Call<User> updateUser(@Path("id") String id, @Body Map<String, Object> fields);

    @GET("programs")
    Call<List<Program>> getPrograms();

    @GET("programs/{id}")
    Call<Program> getProgram(@Path("id") String id);

    @GET("seances")
    Call<List<Seance>> getSeancesByProgram(@Query("programId") String programId);

    @GET("seances/{id}")
    Call<Seance> getSeance(@Path("id") String id);

    @PATCH("seances/{id}")
    Call<Seance> updateSeance(@Path("id") String id, @Body Map<String, Object> fields);

    @GET("quizzes")
    Call<List<Quiz>> getQuizzesByProgram(@Query("programId") String programId);

    @GET("quizzes/{id}")
    Call<Quiz> getQuiz(@Path("id") String id);

    @GET("nutritionTips")
    Call<List<NutritionTip>> getNutritionTips();
}
