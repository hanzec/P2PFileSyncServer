package com.hanzec.P2PFileSyncServer.repository.manage.authenticate;
import com.hanzec.P2PFileSyncServer.model.data.manage.authenticate.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    @Query("from Permission")
    Set<GrantedAuthority> getAll();

    boolean existsByName(String name);

    Permission getPermissionByName(String name);
}
