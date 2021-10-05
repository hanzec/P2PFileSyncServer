package com.hanzec.P2PFileSyncServer.repository.manage.account;

import com.hanzec.P2PFileSyncServer.model.data.manage.account.ClientAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAccountepository extends JpaRepository<ClientAccount, String> {

    boolean existsClientAccountByMachineIDOrIpAddress(String machineID, String ipAddress);
}
