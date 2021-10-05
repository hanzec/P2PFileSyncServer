package com.hanzec.P2PFileSyncServer.utils;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GsonZonedDateTimeConverter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    @Override
    public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
        try {
            if(jsonPrimitive.isString()){
                return ZonedDateTime.parse(jsonPrimitive.getAsString(), DateTimeFormatter.ISO_ZONED_DATE_TIME);
            }

            if(jsonPrimitive.isNumber()){
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsonPrimitive.getAsLong()), ZoneId.systemDefault());
            }

        } catch(RuntimeException e){
            throw new JsonParseException("Unable to parse ZonedDateTime", e);
        }        throw new JsonParseException("Unable to parse ZonedDateTime");
    }

    @Override
    public JsonElement serialize(ZonedDateTime zonedDateTime, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(zonedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
