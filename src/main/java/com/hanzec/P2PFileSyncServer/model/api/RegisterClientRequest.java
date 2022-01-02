package com.hanzec.P2PFileSyncServer.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Schema(description = "Request to register a client")
public class RegisterClientRequest {
    @Expose
    @SerializedName("ip")
    @NotBlank(message = "ip address is empty")
    private String ip;

    @Expose
    @SerializedName("machineID")
    @NotBlank(message = "ip address")
    private String machineID;

    @Expose
    @SerializedName("group")
    private String groupName = "DEFAULT_GROUP";
}
