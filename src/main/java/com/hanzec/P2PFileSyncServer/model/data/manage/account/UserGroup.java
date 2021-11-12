package com.hanzec.P2PFileSyncServer.model.data.manage.account;

import com.hanzec.P2PFileSyncServer.model.data.manage.AbstractGroup;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import lombok.Getter;
import javax.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name ="USER_ROLE",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "NAME")
        })
public class UserGroup extends AbstractGroup implements Serializable {
    @Getter
    @ManyToMany
    private final Set<UserAccount> roleAccount = new HashSet<>();

    @Getter
    @JoinTable()
    @ManyToMany(targetEntity = Permission.class)
    private final Set<GrantedAuthority> permissions = new HashSet<>();

    public UserGroup(){
        this("DEFAULT_NAME");
    }

    public UserGroup(String name){
        this(name,"DEFAULT_DESCRIPTION");
    }

    public UserGroup(String name, String description){
       super(name,description);
    }
}
