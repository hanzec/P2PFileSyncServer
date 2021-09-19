package com.hanzec.P2PFileSyncServer.model.api;

import lombok.Data;
import java.util.Map;
import java.util.HashMap;
import java.time.ZonedDateTime;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;


@Data
@ApiModel
public class Response {
    @Expose
    String message;

    @Expose
    Boolean success;

    @Expose
    ZonedDateTime timestamp;

    @Expose
    private Map<String,Object> responseBody = new HashMap<>();

    public Response(){
        this.timestamp = ZonedDateTime.now();
    }

    public Response addResponse(String key, Object object){
        this.responseBody.put(key,object);
        return this;
    }
}
