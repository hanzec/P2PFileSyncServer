package com.hanzec.P2PFileSyncServer.config.params;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("certificate.client")
public class ClientCertificateConfigParams {
    private int expireDays = 0;
    private int expireYears = 10;
    private int expireMonths = 0;
    private String algorithm = "ECDSA";
    private int privateKeySize = 4096;
    private String ecCurveTable = "B-571";
    private String singedAlgorithm = "SHA256withECDSA";
    private String subjectPrefix = "CLIENT_CERTIFICATE_";

}
