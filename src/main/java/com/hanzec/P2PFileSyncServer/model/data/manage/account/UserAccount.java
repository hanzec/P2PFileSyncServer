package com.hanzec.P2PFileSyncServer.model.data.manage.account;

import com.hanzec.P2PFileSyncServer.constant.IAccountType;
import com.hanzec.P2PFileSyncServer.model.data.manage.AbstractAccount;
import com.hanzec.P2PFileSyncServer.model.data.manage.Group;
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
    private final Set<Group> groups = new HashSet<>();

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

    public UserAccount(String email, String name, String password,Group group){
        this(email, name, password);
        this.groups.add(group);
    }

    public UserAccount(String email, String name, String password,Group group, Permission permission){
        this(email, name, password);
        this.groups.add(group);
        this.permissions.add(permission);
    }

    public UserAccount(String email, String name, String password,Set<Group> groups){
        this(email, name, password);
        this.groups.addAll(groups);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> result_permission = new HashSet<>();

        groups.parallelStream().forEach(group -> {
            result_permission.addAll(group.getPermissions());

        });
        return result_permission;
    }
}
