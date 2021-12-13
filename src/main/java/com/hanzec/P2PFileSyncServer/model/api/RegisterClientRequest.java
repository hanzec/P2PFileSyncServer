package com.hanzec.P2PFileSyncServer.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@ApiModel
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
