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
        transactionManagerRef="transactionManagerCertificate",
        entityManagerFactoryRef="entityManagerFactoryCertificate",
        basePackages= { "com.hanzec.P2PFileSyncServer.repository.certificate" })
public class CertificateDataSourceConfig {
    private final DataSource dataSource;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    CertificateDataSourceConfig(JpaProperties jpaProperties,
                         @Qualifier("certificateDataSource") DataSource dataSource){
        this.dataSource = dataSource;
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = new HibernateProperties();
    }

    @Primary
    @Bean(name = "entityManagerCertificate" )
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactoryCertificate(builder).getObject()).createEntityManager();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryCertificate (EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());
        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.hanzec.P2PFileSyncServer.model.data.certificate")
                .persistenceUnit("certificatePersistenceUnit")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManagerCertificate(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryCertificate(builder).getObject()));
    }
}
