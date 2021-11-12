package com.hanzec.P2PFileSyncServer.model.security;

import com.google.gson.annotations.Expose;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.time.ZonedDateTime;
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
    @ApiModelProperty("expire time")
    private Date exp;

    @Expose
    @ApiModelProperty("client id")
    private String clientID;

    @Expose
    @ApiModelProperty("issue time")
    private Date iat = new Date();

    @Expose
    @ApiModelProperty("subject")
    private String sub = "SIGNED FOR CLIENT LOGIN";

    @Expose
    @ApiModelProperty("JWT id")
    private String jti = UUID.randomUUID().toString();
}
