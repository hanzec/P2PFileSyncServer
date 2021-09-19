package com.hanzec.P2PFileSyncServer.model.data.manage.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name ="user_permission")
public class Permission implements GrantedAuthority {

    @Id @Column(name = "permission_name")
    private String permissionName;

    private String description;

    @Override
    public String getAuthority() {
        return permissionName;
    }

    @Override
    public int hashCode(){
        return permissionName.hashCode();
    }

    @Override
    public String toString(){ return "[" + permissionName + "]:" + description;}
}
