package com.hanzec.P2PFileSyncServer.config.params;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("certificate")
public class GeneralCertificateConfigParams {
    private String countryCode = "CN";
    private String origination = "IASTATE COMS 454 P2P FILE SYNC PROJECT";
}
