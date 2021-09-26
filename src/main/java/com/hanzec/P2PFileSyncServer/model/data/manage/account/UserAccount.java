package com.hanzec.P2PFileSyncServer.model.data.manage.account;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.model.data.manage.AbstractAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.Group;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserRole;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.*;

@Setter
@Getter
@Entity
@Table(
        name = "USER_ACCOUNT",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "username")
        }
)
public class UserAccount extends AbstractAccount {
    @Id @Column(name = "ID")
    @GeneratedValue(generator = "uuid2")
    private String id;

    @Email
    @NotNull
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "USER_ROLE_ID")
    private UserRole role;

    @ManyToMany
    private Set<Group> groups = new HashSet<>();

    @OneToMany(
            mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<UserToken> tokens = new HashSet<>();

    @OneToMany(
            mappedBy = "register",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private Set<ClientAccount> clients = new HashSet<>();

    public UserAccount(){
        super(IAccountType.USER_ACCOUNT);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        var result_list = role.getPermissions();
        result_list.add(role);
        return result_list;
    }
}
