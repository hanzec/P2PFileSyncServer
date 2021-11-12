package com.hanzec.P2PFileSyncServer.config;

import com.google.gson.*;
import com.hanzec.P2PFileSyncServer.utils.GsonZonedDateTimeConverter;
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
     * Swagger 3 configuration
     */
    @Bean
    public Docket docket() {
        return new Docket(
                // 设置使用 OpenApi 3.0 规范
                DocumentationType.OAS_30)
                // 是否开启 Swagger
                .enable(true)
                // 配置项目基本信息
                .apiInfo(apiInfo())
                // 设置项目组名
                //.groupName("xxx组")
                // 选择那些路径和api会生成document
                .select()
                // 对所有api进行监控
                .apis(RequestHandlerSelectors.any())
                // 如果需要指定对某个包的接口进行监控，则可以配置如下
                //.apis(RequestHandlerSelectors.basePackage("mydlq.swagger.example.controller"))
                // 对所有路径进行监控
                .paths(PathSelectors.any())
                // 忽略以"/error"开头的路径,可以防止显示如404错误接口
                .paths(PathSelectors.regex("/error.*").negate())
                // 忽略以"/actuator"开头的路径
                .paths(PathSelectors.regex("/actuator.*").negate())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Sync Disk")
                .description("Sync Disk Api Document")
                .contact(new Contact("Hanze Chen", "127.0.0.1:8081", "me@hanzec.com"))
                .version("0.0.1")
                .build();
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
