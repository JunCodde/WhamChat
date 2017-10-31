package com.apps.juncode.pruebawham.Adapter;

import com.apps.juncode.pruebawham.Endpoints.ConstantesRestAPI;
import com.apps.juncode.pruebawham.Endpoints.ContactoDeserializador;
import com.apps.juncode.pruebawham.Endpoints.Endpoint;
import com.apps.juncode.pruebawham.Model.UID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class RestAPIAdapter {

    public Endpoint conexionRestApi(Gson gson){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantesRestAPI.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(Endpoint.class);
    }

    public Gson construyeGSONEmail(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(UID.class, new ContactoDeserializador());

        return gsonBuilder.create();

    }
}
