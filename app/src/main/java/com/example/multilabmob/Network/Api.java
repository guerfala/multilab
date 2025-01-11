package com.example.multilabmob.Network;


import com.example.multilabmob.Models.ObjetMission;
import com.example.multilabmob.Models.ObjetPredifini;
import com.example.multilabmob.Models.Ordre;
import com.example.multilabmob.Models.OrdreAdd;
import com.example.multilabmob.Models.User;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
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

}

