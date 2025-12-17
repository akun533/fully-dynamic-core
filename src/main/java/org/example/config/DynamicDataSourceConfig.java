package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DynamicDataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;
    
    @Value("${spring.datasource.username}")
    private String username;
    
    @Value("${spring.datasource.password}")
    private String password;
    
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;
    
    @Bean
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        
        // 创建默认数据源
        DataSource defaultDataSource = createDataSource(url, username, password, driverClassName);
        
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("default", defaultDataSource);
        
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        dynamicDataSource.afterPropertiesSet();
        
        return dynamicDataSource;
    }
    
    private DataSource createDataSource(String url, String username, String password, String driverClassName) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }
    
    public static class DynamicDataSource extends AbstractRoutingDataSource {
        @Override
        protected Object determineCurrentLookupKey() {
            // 默认返回"default"，在实际应用中可以根据上下文动态决定
            return "default";
        }
    }
}