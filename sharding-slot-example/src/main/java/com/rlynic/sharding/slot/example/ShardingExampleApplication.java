/*
  This file created at 2020/6/21.

  Copyright (c) 2002-2020 crisis, Inc. All rights reserved.
 */
package com.rlynic.sharding.slot.example;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * <code>{@link ShardingExampleApplication}</code>
 *
 * @author crisis
 */
@SpringBootApplication(
        scanBasePackages = "com.rlynic.sharding.slot.example",
        exclude = JtaAutoConfiguration.class
)
public class ShardingExampleApplication {


    public static void main(final String[] args) throws SQLException {
        try (ConfigurableApplicationContext applicationContext = SpringApplication.run(ShardingExampleApplication.class, args)) {
            ExampleExecuteTemplate.run(applicationContext.getBean(ExampleService.class));
        }
    }

    @Configuration
    @MapperScan(basePackages = "com.rlynic.sharding.slot.example.repositories", sqlSessionFactoryRef = "shardingSqlSessionFactory")
    public static class MybatisConfiguration{
        private final MybatisProperties properties;

        private final ResourceLoader resourceLoader;

        private final DatabaseIdProvider databaseIdProvider;

        private final List<ConfigurationCustomizer> configurationCustomizers;

        public MybatisConfiguration(
                MybatisProperties properties,
                ResourceLoader resourceLoader,
                ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
            this.properties = properties;
            this.resourceLoader = resourceLoader;
            this.databaseIdProvider = databaseIdProvider.getIfAvailable();
            this.configurationCustomizers = configurationCustomizersProvider
                    .getIfAvailable();
        }

        @Bean(name = "shardingSqlSessionFactory")
        public SqlSessionFactory basicSqlSessionFactory(@Qualifier("shardingDataSource") DataSource basicDataSource) throws Exception {
            SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
            factoryBean.setDataSource(basicDataSource);

            return generateSessionFactory(factoryBean);
        }

        private SqlSessionFactory generateSessionFactory(SqlSessionFactoryBean factory) throws Exception {
            factory.setVfs(SpringBootVFS.class);
            if (StringUtils.hasText(properties.getConfigLocation())) {
                factory.setConfigLocation(resourceLoader
                        .getResource(properties.getConfigLocation()));
            }
            if (properties.getConfigurationProperties() != null) {
                factory.setConfigurationProperties(properties
                        .getConfigurationProperties());
            }
            if (databaseIdProvider != null) {
                factory.setDatabaseIdProvider(databaseIdProvider);
            }
            if (StringUtils.hasLength(properties.getTypeAliasesPackage())) {
                factory.setTypeAliasesPackage(this.properties
                        .getTypeAliasesPackage());
            }
            if (StringUtils.hasLength(properties.getTypeHandlersPackage())) {
                factory.setTypeHandlersPackage(properties
                        .getTypeHandlersPackage());
            }
            if (!ObjectUtils.isEmpty(properties.resolveMapperLocations())) {
                factory.setMapperLocations(properties.resolveMapperLocations());
            }

            factory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);

            return factory.getObject();
        }
    }


}