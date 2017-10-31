package com.apps.juncode.pruebawham.Endpoints;

import com.apps.juncode.pruebawham.Model.UID;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public interface Endpoint {

    @FormUrlEncoded
    @POST(ConstantesRestAPI.KEY_POST_EMAIL)
    Call<UID> enviarMail(@Field("email") String email);
}

