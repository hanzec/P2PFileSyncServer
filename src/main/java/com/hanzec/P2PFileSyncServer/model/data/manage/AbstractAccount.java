package com.hanzec.P2PFileSyncServer.model.data.manage;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.model.data.converter.AccountTypeConverter;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
public abstract class AbstractAccount implements UserDetails, Serializable {

    @NotNull
    @Column(name = "account_type")
    @Convert(converter = AccountTypeConverter.class)
    private IAccountType accountType;

    @Column(name = "locked")
    public boolean locked = false;

    @Column(name = "expired")
    public boolean expired = false;

    @Column(name = "enabled")
    public boolean enabled = true;

    @Column(name = "credential_expired")
    public boolean credential_expired = false;

    @CreationTimestamp
    @Column(name = "create_time", columnDefinition="TIMESTAMP")
    private ZonedDateTime createTime;

    @UpdateTimestamp
    @Column(name = "last_modify_time", columnDefinition="TIMESTAMP")
    private ZonedDateTime lastModifyTime;

    public AbstractAccount(IAccountType accountType){
        this.accountType = accountType;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credential_expired;
    }
}
