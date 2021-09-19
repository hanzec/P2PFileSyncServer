package com.hanzec.P2PFileSyncServer;

import com.hanzec.P2PFileSyncServer.config.params.TrueStoreConfigParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


import javax.annotation.PostConstruct;
import java.io.File;

@SpringBootApplication
@EnableRedisHttpSession
public class Application {

    @Autowired
    TrueStoreConfigParams config;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @PostConstruct
    void TrueStoreConfig() {
        // set TrustStoreParams
        File trustStoreFilePath = new File(config.trustStorePath);
        String tsp = trustStoreFilePath.getAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", tsp);
        System.setProperty("javax.net.ssl.trustStorePassword", config.trustStorePassword);
        System.setProperty("javax.net.ssl.keyStoreType", config.defaultType);

        // set KeyStoreParams
        File keyStoreFilePath = new File(config.keyStorePath);
        String ksp = keyStoreFilePath.getAbsolutePath();
        System.setProperty("Security.KeyStore.Location", ksp);
        System.setProperty("Security.KeyStore.Password", config.keyStorePassword);
    }
}
