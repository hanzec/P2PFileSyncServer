package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import java.util.HashMap;
import java.time.ZonedDateTime;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Schema(description = "General response object")
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
