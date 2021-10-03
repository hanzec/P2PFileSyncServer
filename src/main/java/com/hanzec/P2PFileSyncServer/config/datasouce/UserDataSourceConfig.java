package com.hanzec.P2PFileSyncServer.config.datasouce;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        transactionManagerRef="transactionManagerUser",
        entityManagerFactoryRef="entityManagerFactoryUser",
        basePackages= {"com.hanzec.P2PFileSyncServer.repository.manage"})
public class UserDataSourceConfig {
    private final DataSource dataSource;
    private final JpaProperties jpaProperties;
    private final HibernateProperties hibernateProperties;

    UserDataSourceConfig(JpaProperties jpaProperties,
                         @Qualifier("userDataSource") DataSource dataSource){
        this.dataSource = dataSource;
        this.jpaProperties = jpaProperties;
        this. hibernateProperties = new HibernateProperties();
    }

    @Bean(name ="entityManagerFactoryUser" )
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryUser (EntityManagerFactoryBuilder builder) {
        Map<String, Object> properties = hibernateProperties.determineHibernateProperties(
                jpaProperties.getProperties(), new HibernateSettings());
        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("com.hanzec.P2PFileSyncServer.model.data.manage")
                .persistenceUnit("userPersistenceUnit")
                .build();
    }

    @Bean(name = "entityManagerUser" )
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return Objects.requireNonNull(entityManagerFactoryUser(builder).getObject()).createEntityManager();
    }

    @Bean(name = "transactionManagerUser")
    public PlatformTransactionManager transactionManagerUser(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactoryUser(builder).getObject()));
    }

}
