package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class RegisterClientRequest {
    @NotBlank(message = "ip address")
    private String ipAddress;

    @NotBlank(message = "MachineID should not be empty")
    private String machineID;
}
