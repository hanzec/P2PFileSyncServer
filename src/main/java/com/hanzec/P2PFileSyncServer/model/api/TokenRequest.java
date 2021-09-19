package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class TokenRequest {

    @NotNull(message = "Secret should not be empty")
    String secret;

    @NotNull(message = "tokenID should not be empty")
    String tokenID;

    @NotNull(message = "refreshKey should not be empty")
    String refreshKey;
}
