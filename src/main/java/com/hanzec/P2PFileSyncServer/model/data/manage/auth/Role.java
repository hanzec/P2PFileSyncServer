package com.hanzec.P2PFileSyncServer.model.data.manage.auth;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name ="user_role")
public class Role implements GrantedAuthority {

    @Id @Column(name = "role_name")
    private String roleName;

    private String description;

    @JoinTable()
    @ManyToMany(targetEntity = Permission.class)
    private Set<GrantedAuthority> permissions;

    @Override
    public int hashCode(){
        return roleName.hashCode();
    }

    @Override
    public String getAuthority() { return "ROLE_" + roleName; }

    @Override
    public String toString(){
        return "[" + roleName + "]:" + description;
    }
}
