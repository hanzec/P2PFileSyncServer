package com.hanzec.P2PFileSyncServer.model.data.manage;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;

@Entity
@Table(name = "clinet_account")
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Client implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    @Id @NotNull
    @Column(name = "client_id")
    @GeneratedValue(generator = "jpa-uuid")
    private String client_id;

    @Column(name = "locked")
    public boolean locked = false;

    @Column(name = "expired")
    public boolean expired = false;

    @Column(name = "enabled")
    public boolean enabled = true;

    @Column(name = "credential_expired")
    public boolean credential_expired = false;

    @Column(name = "create_time", columnDefinition = "TIMESTAMP")
    private ZonedDateTime createTime;

    @Column(name = "update_time", columnDefinition = "TIMESTAMP")
    private ZonedDateTime updateTime;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credential_expired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
