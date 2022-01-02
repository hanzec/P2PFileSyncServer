package com.hanzec.P2PFileSyncServer.model.security;

import com.google.gson.annotations.Expose;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
public class JwtPayload {
    public JwtPayload(String clientID, Date exp){
        this.exp = exp;
        this.clientID = clientID;
    }

    @Expose
    @Schema(description = "expire time")
    private Date exp;

    @Expose
    @Schema(description = "client id")
    private String clientID;

    @Expose
    @Schema(description = "issue time")
    private Date iat = new Date();

    @Expose
    @Schema(description = "subject")
    private String sub = "SIGNED FOR CLIENT LOGIN";

    @Expose
    @Schema(description = "JWT id")
    private String jti = UUID.randomUUID().toString();
}
