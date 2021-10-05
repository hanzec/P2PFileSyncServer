package com.hanzec.P2PFileSyncServer.config.params;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("certificate.ca")
public class CACertificateConfigParams {
    private int expireDays = 0;
    private int expireYears = 10;
    private int expireMonths = 0;
    private int privateKeySize = 4096;
    private String algorithm = "RSA";
    private String singedAlgorithm = "SHA256withRSA";
    private String rootCertificateSubject = "ROOT_CERTIFICATE";
    private String urlSignCertificateSubject = "URL_CERTIFICATE";
    private String clientSignCertificateSubject = "CLIENT_CERTIFICATE";
}
