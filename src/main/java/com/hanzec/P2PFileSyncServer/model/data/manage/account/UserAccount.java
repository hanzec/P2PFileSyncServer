package com.hanzec.P2PFileSyncServer.model.data.manage.account;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.model.data.manage.AbstractAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.UserToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.*;


@Entity
@Table(
        name = "USER_ACCOUNT",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "EMAIL"),
                @UniqueConstraint(columnNames = "USERNAME")
        })
public class UserAccount extends AbstractAccount {
    @Setter
    @Getter
    @Column(name = "USERNAME")
    private String name;

    @Email
    @Setter
    @Getter
    @NotNull
    @Column(name = "EMAIL")
    private String email;

    @Setter
    @Getter
    @Column(name = "PASSWORD")
    private String password;

    @Getter
    @ManyToMany
    private final Set<UserGroup> userGroups = new HashSet<>();

    @Getter
    @OneToMany(
            mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private final Set<UserToken> tokens = new HashSet<>();

    @Getter
    @OneToMany(
            mappedBy = "register",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private final Set<ClientAccount> clients = new HashSet<>();

    @Getter
    @ManyToMany
    private final Set<Permission> permissions = new HashSet<>();

    public UserAccount(){
        super(IAccountType.USER_ACCOUNT);
    }

    public UserAccount(String email, String name, String password){
        this();
        this.name = name;
        this.email = email;
        this.password = password;
        this.enableAccount(); // by default user account is automatic active when register
    }

    public UserAccount(String email, String name, String password, UserGroup userGroup){
        this(email, name, password);
        this.userGroups.add(userGroup);
    }

    public UserAccount(String email, String name, String password, UserGroup userGroup, Permission permission){
        this(email, name, password);
        this.userGroups.add(userGroup);
        this.permissions.add(permission);
    }

    public UserAccount(String email, String name, String password,Set<UserGroup> userGroups){
        this(email, name, password);
        this.userGroups.addAll(userGroups);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> result_permission = new HashSet<>(permissions);

        userGroups.parallelStream().forEach(userGroup -> {
            result_permission.addAll(userGroup.getPermissions());
        });
        return result_permission;
    }
}
