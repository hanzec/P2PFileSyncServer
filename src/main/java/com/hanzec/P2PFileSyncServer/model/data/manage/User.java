package com.hanzec.P2PFileSyncServer.model.data.manage;

import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Role;
import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Token;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@Setter
@Getter
@Entity
@Table(name = "user_account")
public class User implements UserDetails, Serializable{

    @Email
    @NotNull
    @Id @Column(name = "user_email")
    private String email;

    @GeneratedValue
    @Column(name = "user_id")
    private UUID userID;

    @Column(name = "user_name")
    private String username;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Column(name = "register_time")
    private ZonedDateTime registerTime;

    @NotNull
    @Column(name = "last_password_update_time")
    private ZonedDateTime passwordUpdateTime;

    @Column(name = "jwt_key")
    private String jwtKey;

    @Column(name = "password")
    private String password;

    @ManyToOne(targetEntity=Role.class)
    private Role role;

    @ManyToMany
    private List<Group> userGroup;

    @OneToMany(
            mappedBy = "owner",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    private Set<Token> userTokens;

    public User(){
        this.registerTime = ZonedDateTime.now();
    }

    public void setPassword(String password) {
        this.password = password;
        this.passwordUpdateTime = ZonedDateTime.now();
    }

    @Override
    public String getUsername() { return userID.toString(); }

    @Override
    public String getPassword() { return password; }

    @Override
    public boolean isEnabled() {
        return password != null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        var result_list = role.getPermissions();
        result_list.add(role);
        return result_list;
    }
}
