package com.hanzec.P2PFileSyncServer.repository.certificate;

import com.hanzec.P2PFileSyncServer.model.data.certificate.ClientCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientCertificateRepository extends JpaRepository<ClientCertificate, Integer> {
}
