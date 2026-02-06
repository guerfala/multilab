package com.example.multilabmob.Network;


import com.example.multilabmob.Models.Mission;
import com.example.multilabmob.Models.ObjetMission;
import com.example.multilabmob.Models.ObjetMissionUpdateDTO;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Models.Ordre;
import com.example.multilabmob.Models.OrdreAdd;
import com.example.multilabmob.Models.Organisme;
import com.example.multilabmob.Models.User;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {
    @GET("objets")
    Call<List<ObjetPredifini>> getObjets();

    @POST("objets/admin/add-objet-predifini")
    Call<ObjetPredifini> addObjetPredifini(@Body ObjetPredifini objetPredifini);

    @PUT("objets/{id}")
    Call<ObjetPredifini> updateObjetPredifini(@Path("id") int id, @Body ObjetPredifini objetPredifini);

    @DELETE("objets/{id}")
    Call<Void> deleteObjetPredifini(@Path("id") int id);

    @GET("ordres")
    Call<List<Ordre>> getOrders(@Header("user-id") int userId);

    @GET("ordres/by-day")
    Call<List<Ordre>> getOrdersByDay(@Query("date") String date, @Query("userId") int userId);

    @GET("ordres/{ordreId}/missions")
    Call<List<ObjetMission>> getObjetMissions(@Path("ordreId") int ordreId);

    @POST("ordres/{ordreId}/settle")
    Call<Void> settleOrdre(@Path("ordreId") int ordreId, @Body List<ObjetMissionUpdateDTO> updatedMissions);

    @POST("ordres/create")
    Call<Void> createOrdre(@Body OrdreAdd ordreAddDTO);

    @POST("auth/login")
    Call<JsonObject> login(@Body User user);

    @GET("ordres/admin/orders-by-day")
    Call<List<Ordre>> getOrdersByDayForAdmin(@Query("date") String date);

    @POST("auth/addUser")
    Call<JsonObject> addUser(@Body User user);

    @GET("organismes")
    Call<List<Organisme>> getOrganismes();

    @POST("organismes")
    Call<JsonObject> addOrganisme(@Body Organisme organisme);

    @PUT("organismes/{id}")
    Call<JsonObject> updateOrganisme(@Path("id") int id, @Body Organisme organisme);

    @DELETE("organismes/{id}")
    Call<JsonObject> deleteOrganisme(@Path("id") int id);

    @GET("auth/users")
    Call<List<User>> getUsers();

    @POST("missions")
    Call<Map<String, String>> addMission(@Body Mission mission);

    @GET("missions/filter")
    Call<List<Mission>> getMissionsByDateAndUser(@Query("date") String date, @Query("userId") int userId);

    @DELETE("missions/{id}")
    Call<Map<String, String>> deleteMission(@Path("id") int missionId);

    // 3️⃣ Update an existing user
    @PUT("auth/{id}")
    Call<JsonObject> updateUser(@Path("id") int id, @Body User user);

    // 4️⃣ Delete a user
    @DELETE("auth/{id}")
    Call<JsonObject> deleteUser(@Path("id") int id);

    // ✅ Add the missing method to fetch missions assigned to a user
    @GET("missions/user/{userId}")
    Call<List<Mission>> getMissionsByUser(@Path("userId") int userId);

    @POST("kilometers/start")
    Call<Void> submitStartKilometers(@Query("userId") int userId, @Query("kilometers") float kilometers);

    @GET("kilometers/byUserAndDate")
    Call<Float> getKilometrageByUserAndDate(
            @Query("userId") int userId,
            @Query("date") String date
    );

    @GET("ordres/stats/{userId}")
    Call<JsonObject> getOrderStatsForUser(@Path("userId") int userId);


}

