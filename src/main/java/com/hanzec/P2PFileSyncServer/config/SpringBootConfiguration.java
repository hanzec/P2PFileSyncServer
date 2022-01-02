package com.hanzec.P2PFileSyncServer.config;

import com.google.gson.*;
import com.hanzec.P2PFileSyncServer.utils.GsonZonedDateTimeConverter;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import javax.sql.DataSource;
import java.time.ZonedDateTime;

@Configuration
@EnableCaching
public class SpringBootConfiguration {

    /**
     * Data Source configuration
     */
    @Bean
    @Primary
    @Qualifier("userDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.user")
    public DataSource userDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("fileDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.file")
    public DataSource fileDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("certificateDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.certificate")
    public DataSource certificateDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * PasswordEncoder configurations
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder();
    }

    @Bean
    public Gson getGsonInstance(){
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(ZonedDateTime.class, new GsonZonedDateTimeConverter())
                .create();
    }

    /**
     * Gson's configuration in order to use gson as serializer
     */
    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson);
        return converter;
    }
}
