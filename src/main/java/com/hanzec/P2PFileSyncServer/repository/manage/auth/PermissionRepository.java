package com.hanzec.P2PFileSyncServer.repository.manage.auth;
import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

    @Query("from Permission")
    Set<GrantedAuthority> getAll();
}
