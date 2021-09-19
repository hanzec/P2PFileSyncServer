package com.hanzec.P2PFileSyncServer.config.datasouce;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        transactionManagerRef="transactionManagerFile",
        entityManagerFactoryRef="entityManagerFactoryFile",
        basePackages= { "com.hanzec.P2PFileSyncServer.repository.file" })
public class FileDataSourceConfig {
    private final DataSource fileDataSource;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    FileDataSourceConfig(JpaProperties jpaProperties,
                         HibernateProperties hibernateProperties,
                         @Qualifier("fileDataSource") DataSource fileDataSource){
        this.jpaProperties = jpaProperties;
        this.fileDataSource = fileDataSource;
        this.hibernateProperties = hibernateProperties;
    }

    @Primary
    @Bean(name = "entityManagerFile" )
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactoryFile(builder).getObject()).createEntityManager();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryFile (EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());

        return builder
                .dataSource(fileDataSource)
                .properties(properties)
                .packages("com.hanzec.P2PFileSyncServer.repository.file")
                .persistenceUnit("filePersistenceUnit")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManagerFile(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryFile(builder).getObject()));
    }

}
