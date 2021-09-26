package com.hanzec.P2PFileSyncServer.model.data.manage.authenticate;

import com.google.gson.annotations.Expose;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "USER_TOKEN")
public class UserToken implements Serializable {
    static final long serialVersionUID = 42L;

    @Getter
    @GeneratedValue()
    @Id @Column(name = "token_id")
    private UUID tokenID;

    @Getter
    @GeneratedValue()
    @Column(name = "token_secret")
    private UUID secret;

    @Getter
    @GeneratedValue()
    @Column(name = "refresh_key")
    private UUID refreshKey;

    @Getter
    @Expose
    private final ZonedDateTime expireTime;

    @Getter
    @ManyToOne(
            optional = false,
            cascade = {CascadeType.MERGE,CascadeType.REFRESH}
    )
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount owner;

    public UserToken(){
        this(null,null);
    }

    public UserToken(UserAccount owner, ZonedDateTime expireTime){
        this.owner = owner;
        this.expireTime = expireTime;
    }
}
