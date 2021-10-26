package com.hanzec.P2PFileSyncServer.model.api;

import lombok.Data;
import java.util.Map;
import java.util.HashMap;
import java.time.ZonedDateTime;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel
public class Response {
    @Expose
    private String message;

    @Expose
    private Boolean success = true;

    @Expose
    private ZonedDateTime timestamp;

    @Expose
    private Map<String,Object> responseBody = new HashMap<>();

    public Response setFailure() {
        this.success = false;
        return this;
    }

    public Response(){
        this.timestamp = ZonedDateTime.now();
    }

    public Response addResponse(String key, Object object){
        this.responseBody.put(key,object);
        return this;
    }
}
