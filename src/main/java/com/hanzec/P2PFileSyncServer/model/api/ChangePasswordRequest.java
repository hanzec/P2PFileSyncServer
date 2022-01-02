package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Schema(description = "Change password request")
public class ChangePasswordRequest {
    @NotEmpty(message = "Request should contains a new password")
    String newPassword;

    @NotEmpty(message = "Request should provide old password")
    String oldPassword;
}
