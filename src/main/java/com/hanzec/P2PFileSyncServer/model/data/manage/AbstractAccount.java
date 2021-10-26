package com.hanzec.P2PFileSyncServer.model.data.manage;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.model.data.converter.AccountTypeConverter;
import com.hanzec.P2PFileSyncServer.model.data.manage.account.UserAccount;
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
    @Getter
    @Id @Column(name = "ID")
    @GeneratedValue(generator = "uuid2")
    private String id;

    @NotNull
    @Column(name = "account_type")
    @Convert(converter = AccountTypeConverter.class)
    private IAccountType accountType;

    @Column(name = "locked")
    private boolean locked = true;

    @Column(name = "expired")
    private boolean expired = true;

    @Column(name = "enabled")
    private boolean enabled = true;

    @Column(name = "credential_expired")
    private boolean credential_expired = true;

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
    public int hashCode(){
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj)
            return true;

        if(obj == null || obj.getClass()!= this.getClass())
            return false;

        // type casting of the argument.
        AbstractAccount geek = (AbstractAccount) obj;

        // comparing the state of argument with
        // the state of 'this' Object.
        return geek.id.equals(this.id);
    }

    protected void enableAccount(){
        this.enabled = true;
    }

    @Override
    public String getUsername() {
        return id;
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
