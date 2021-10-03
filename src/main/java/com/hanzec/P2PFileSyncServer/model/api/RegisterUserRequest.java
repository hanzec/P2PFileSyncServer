package com.hanzec.P2PFileSyncServer.model.api;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel
public class RegisterUserRequest {

    @Email(message = "Not a correct Email")
    @NotBlank(message = "Password should not empty")
    String email;

    @NotBlank(message = "Password should not empty")
    String password;

    @NotBlank(message = "Username should not empty")
    String username;
}
