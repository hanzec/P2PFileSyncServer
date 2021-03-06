package com.hanzec.P2PFileSyncServer.model.data.manage.account;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.model.data.manage.AbstractAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.SimplePermission;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serial;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
@Table(
        name = "CLIENT_ACCOUNT",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "MACHINE_ID"),
                @UniqueConstraint(columnNames = "IP_ADDRESS")
        })
@GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
public class ClientAccount extends AbstractAccount {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    @Column(name = "MACHINE_ID")
    private String machineID = "";

    @Getter
    @Setter
    @Column(name = "IP_ADDRESS")
    private String ipAddress = "";

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ACCOUNT_ID")
    private UserAccount register;

    @Getter
    @ManyToOne
    @JoinColumn(name = "clients")
    private ClientGroup group;

    public ClientAccount() {
        super(IAccountType.CLIENT_ACCOUNT);
    }

    public ClientAccount(String machineID, String ipAddress, ClientGroup group) {
        this();
        this.group = group;
        this.machineID = machineID;
        this.ipAddress = ipAddress;
    }

    public void enableClient(UserAccount operator) {
        register = operator;
        this.enableAccount();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimplePermission("client_operation"));
    }
}
