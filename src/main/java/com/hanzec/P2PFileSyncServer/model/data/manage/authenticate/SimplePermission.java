package com.hanzec.P2PFileSyncServer.model.data.manage.authenticate;

import org.springframework.security.core.GrantedAuthority;

public class SimplePermission implements GrantedAuthority {
    private final String permission;

    public SimplePermission(String permission_name){
        this.permission = permission_name;
    }
    @Override
    public String getAuthority() {
        return permission;
    }
}
