package com.hanzec.P2PFileSyncServer.model.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "Response for a new token")
public class TokenRequest {

    @NotNull(message = "Secret should not be empty")
    String secret;

    @NotNull(message = "tokenID should not be empty")
    String tokenID;

    @NotNull(message = "refreshKey should not be empty")
    String refreshKey;
}
