package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@ApiModel
public class ChangePasswordRequest {
    @NotEmpty(message = "Request should contains a new password")
    String newPassword;

    @NotEmpty(message = "Request should provide old password")
    String oldPassword;
}
