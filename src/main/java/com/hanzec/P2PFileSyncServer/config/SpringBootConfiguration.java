package com.hanzec.P2PFileSyncServer.config;

import com.google.gson.*;
import com.hanzec.P2PFileSyncServer.utils.SpringfoxJsonToGsonAdapter;
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
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.plugins.Docket;

import javax.sql.DataSource;
import java.time.ZonedDateTime;

@EnableOpenApi
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
    public DataSource  userDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("fileDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.file")
    public DataSource  fileDataSource(){
        return DataSourceBuilder.create().build();
    }

    /**
     * Swagger 3 configuration
     */
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo()).enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hanzec"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Sync Disk")
                .description("Sync Disk Api Document")
                .contact(new Contact("Hanze Chen", "syncdisk.hanzec.com", "me@hanzec.com"))
                .version("0.0.1")
                .build();
    }

    /**
     * PasswordEncoder configurations
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new Pbkdf2PasswordEncoder();
    }

    /**
     * Gson's configuration in order to use gson as serializer
     */
    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        converter.setGson(gson());
        return converter;
    }

    private Gson gson() {
        JsonSerializer<ZonedDateTime> ser = (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(src.toString());

        final GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class,ser);
        builder.registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter());
        return builder.excludeFieldsWithoutExposeAnnotation().create();
    }
}
