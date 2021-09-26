package com.hanzec.P2PFileSyncServer.model.data.manage.account;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.model.data.manage.AbstractAccount;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serial;
import java.util.Collection;

@Entity
@Table(name = "CLIENT_ACCOUNT")
@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator" )
public class ClientAccount extends AbstractAccount {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id @Column(name = "ID")
    @GeneratedValue(generator = "uuid2")
    private String id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount register;

    public ClientAccount() {
        super(IAccountType.CLIENT_ACCOUNT);
    }

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
        return id;
    }


}
