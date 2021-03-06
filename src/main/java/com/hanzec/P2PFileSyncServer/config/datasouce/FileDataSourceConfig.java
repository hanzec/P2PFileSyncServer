package com.hanzec.P2PFileSyncServer.config.datasouce;


import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
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
    private final DataSource dataSource;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;
    private final ConfigurableListableBeanFactory beanFactory;

    FileDataSourceConfig(JpaProperties jpaProperties,
                         ConfigurableListableBeanFactory beanFactory,
                         @Qualifier("fileDataSource") DataSource dataSource){
        this.dataSource = dataSource;
        this.beanFactory = beanFactory;
        this.jpaProperties = jpaProperties;
        this.hibernateProperties = new HibernateProperties();
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

        // fix spring auto-injection failed
        properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));

        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.hanzec.P2PFileSyncServer.model.data.file")
                .persistenceUnit("filePersistenceUnit")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManagerFile(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryFile(builder).getObject()));
    }
}
