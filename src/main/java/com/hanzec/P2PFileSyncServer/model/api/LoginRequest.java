package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class LoginRequest {

    @NotBlank
    public String password;

    @NotBlank
    public String email;
}
