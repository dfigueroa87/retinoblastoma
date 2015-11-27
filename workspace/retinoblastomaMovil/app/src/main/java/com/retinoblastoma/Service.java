package com.retinoblastoma;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface Service {

    @POST("retinoblastoma/rest/imagen/upload_image")
    Call<DTOJsonResponse> getResponse(@Body DTOJsonRequest params);

}
