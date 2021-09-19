package com.hanzec.P2PFileSyncServer.repository.manage.auth;

import com.hanzec.P2PFileSyncServer.model.data.manage.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>{
}
