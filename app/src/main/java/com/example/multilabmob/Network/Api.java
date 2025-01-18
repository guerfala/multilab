package com.example.multilabmob.Network;


import com.example.multilabmob.Models.Mission;
import com.example.multilabmob.Models.ObjetMission;
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

    @GET("ordres")
    Call<List<Ordre>> getOrders(@Header("user-id") int userId);

    @GET("ordres/by-day")
    Call<List<Ordre>> getOrdersByDay(@Query("date") String date, @Query("userId") int userId);

    @GET("ordres/{ordreId}/missions")
    Call<List<ObjetMission>> getObjetMissions(@Path("ordreId") int ordreId);

    @POST("ordres/{ordreId}/settle")
    Call<Void> settleOrdre(@Path("ordreId") int ordreId, @Body List<ObjetMission> objetMissions);


    @POST("ordres")
    Call<OrdreAdd> createOrdre(@Body OrdreAdd ordreAdd, @Header("user-id") int userId);


    @POST("auth/login")
    Call<JsonObject> login(@Body User user);

    @GET("ordres/admin/orders-by-day")
    Call<List<Ordre>> getOrdersByDayForAdmin(@Query("date") String date);

    @POST("objets/admin/add-objet-predifini")
    Call<ObjetPredifini> addObjetPredifini(@Body ObjetPredifini objetPredifini);

    @POST("auth/addUser")
    Call<JsonObject> addUser(@Body User user);

    @GET("organismes")
    Call<List<Organisme>> getOrganismes();

    @GET("auth/users")
    Call<List<User>> getUsers();

    @POST("missions")
    Call<Map<String, String>> addMission(@Body Mission mission);

    @GET("missions/filter")
    Call<List<Mission>> getMissionsByDateAndUser(@Query("date") String date, @Query("userId") int userId);

    @DELETE("missions/{id}")
    Call<Map<String, String>> deleteMission(@Path("id") int missionId);

    @POST("auth/save-fcm-token")
    Call<String> saveFcmToken(@Query("userId") int userId, @Query("fcmToken") String fcmToken);

    @GET("fcm/get-token")
    Call<ResponseBody> getUserFcmToken(@Query("userId") int userId);

    @POST("fcm/update-token")
    Call<Void> updateUserFcmToken(@Query("userId") int userId, @Query("fcmToken") String fcmToken);

}

