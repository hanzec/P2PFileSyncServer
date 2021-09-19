package com.hanzec.P2PFileSyncServer.repository.manage;

import com.hanzec.P2PFileSyncServer.model.data.manage.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}