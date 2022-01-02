package com.hanzec.P2PFileSyncServer;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


import javax.annotation.PostConstruct;
import java.io.File;
import java.security.Security;

@SpringBootApplication
@EnableRedisHttpSession
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Spring doc
     */
    @Bean
    public OpenAPI springShopOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .info(new Info().title("SpringShop API")
                        .description("Spring shop sample application")
                        .version(appVersion)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }

    @PostConstruct
    void SettingSecurityProvider(){
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    void TrueStoreConfig() {
        // set TrustStoreParams
        File trustStoreFilePath = new File("config/truststore.bks");
        String tsp = trustStoreFilePath.getAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", tsp);
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.keyStoreType", "BJS");

        // set KeyStoreParams
        File keyStoreFilePath = new File("config/wso2carbon.jks");
        String ksp = keyStoreFilePath.getAbsolutePath();
        System.setProperty("Security.KeyStore.Location", ksp);
        System.setProperty("Security.KeyStore.Password", "wso2carbon");
    }
}
