package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "Login request")
public class LoginRequest {

    @NotBlank
    public String password;

    @NotBlank
    public String email;
}
