package com.hanzec.P2PFileSyncServer.config.params;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("truststore")
public class TrueStoreConfigParams {
    public String trustStorePath = "config/truststore.bks";
    public String trustStorePassword = "wso2carbon";
    public String keyStorePath = "config/wso2carbon.jks";
    public String keyStorePassword = "wso2carbon";
    public String defaultType = "BJS";
}
