package com.apps.juncode.pruebawham.Endpoints;

import com.apps.juncode.pruebawham.Model.UID;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Produccion 800N on 31/10/2017.
 */

public class ContactoDeserializador implements JsonDeserializer<UID> {
    @Override
    public UID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        Gson gson = new Gson();
        UID uid = gson.fromJson(json, UID.class);

        return uid;
    }
}

