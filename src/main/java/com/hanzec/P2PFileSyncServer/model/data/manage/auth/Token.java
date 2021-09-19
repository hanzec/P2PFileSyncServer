package com.hanzec.P2PFileSyncServer.model.data.manage.auth;

import com.google.gson.annotations.Expose;
import com.hanzec.P2PFileSyncServer.model.data.manage.User;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_token")
public class Token implements Serializable {
    static final long serialVersionUID = 42L;

    @Expose
    @GeneratedValue()
    @Id @Column(name = "token_id")
    private UUID tokenID;

    @Expose
    @GeneratedValue()
    @Column(name = "token_secret")
    private UUID secret;

    @Expose
    @GeneratedValue()
    @Column(name = "refresh_key")
    private UUID refreshKey;

    @Expose
    private ZonedDateTime expireTime;

    @ManyToOne(
            optional = false,
            cascade = {CascadeType.MERGE,CascadeType.REFRESH}
    )
    @JoinColumn(name = "email")
    User owner;

    public Token(){}

    public Token(User owner, ZonedDateTime expireTime){
        this.owner = owner;
        this.expireTime = expireTime;
    }
}
