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
    @Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$")
    @NotBlank(message = "ip address is empty")
    private String ip;

    @Expose
    @SerializedName("machineID")
    @NotBlank(message = "ip address")
    private String machineID;
}
