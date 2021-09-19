package com.hanzec.P2PFileSyncServer.repository.manage;

import com.hanzec.P2PFileSyncServer.model.data.manage.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, String> {
}
