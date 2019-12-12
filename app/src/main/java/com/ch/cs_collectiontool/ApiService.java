package com.ch.cs_collectiontool;

import com.ch.cs_collectiontool.bean.RequestResult;
import com.ch.cs_collectiontool.bean.VillageResult;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("saveUser")
    Observable<RequestResult> requestLogin(@Body RequestBody body);

    @POST("saveVillage")
    Observable<RequestResult> saveVillage(@Body RequestBody body);

    @POST("saveGroup")
    Observable<RequestResult> saveGroup(@Body RequestBody body);

    @POST("deleteGroup")
    Observable<RequestResult> deleteGroup(@Body RequestBody body);

    @POST("saveRoom")
    Observable<RequestResult> saveRoom(@Body RequestBody body);

    @POST("getInfo")
    Observable<RequestResult> getInfo(@Body RequestBody body);

}
