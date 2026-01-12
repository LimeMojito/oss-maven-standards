/*
 * Copyright 2011-2026 Lime Mojito Pty Ltd
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.limemojito.aws.lambda.sql;

import com.limemojito.aws.lambda.SnapStartOptimizer;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>This configuration manages the proxy of a java.sql.Connection instance through AWS SnapStart lifecycle.</p>
 * <ol>
 * <li>When preSnapshot occurs, LambdaSqlConnection closes the Connection instance.</li>
 * <li>When postRestore occurs, LambdaSqlConnection reconnects the Connection instance.</li>
 * </ol>
 * <p>Because LambdaSqlConnection creating a dynamic proxy as the Connection instance, it can manage the delegated
 * connection "behind" the proxy without your injected Connection instance changing.</p>
 */
@Configuration
@Slf4j
public class LambdaSqlConnection extends SnapStartOptimizer {

    private ProxyFactory proxyFactory;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;
    private Driver driver;
    @Getter
    private volatile boolean connected;

    /**
     * Establishes and returns a database connection based on the provided parameters.
     *
     * @param dbDriveClassName the class name of the JDBC driver
     * @param url              the URL of the database
     * @param username         the username for the database connection
     * @param password         the password for the database connection
     * @return a proxy database connection
     */
    @Bean
    @SneakyThrows
    public Connection connection(@Value("${lime.jdbc.driver.classname}") String dbDriveClassName,
                                 @Value("${lime.jdbc.url}") String url,
                                 @Value("${lime.jdbc.username}") String username,
                                 @Value("${lime.jdbc.password}") String password) {
        log.info("Loading DB driver {}", dbDriveClassName);
        this.driver = (Driver) Class.forName(dbDriveClassName)
                                    .getDeclaredConstructor()
                                    .newInstance();
        this.dbUrl = url;
        this.dbUsername = username;
        this.dbPassword = password;
        proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(Connection.class);
        resetConnection();
        return (Connection) proxyFactory.getProxy();
    }

    @Override
    protected void performBeforeCheckpoint() {
        dropConnection();
    }

    protected void performAfterRestore() throws SQLException {
        resetConnection();
    }

    /**
     * Close connection on application shutdown event.
     */
    @PreDestroy
    public void destroy() {
        dropConnection();
    }

    private void dropConnection() {
        try {
            log.info("Closing connection");
            final Connection connection = getConnection();
            connection.close();
        } catch (SQLException e) {
            log.warn("Error closing connection", e);
        } finally {
            connected = false;
        }
    }

    private Connection getConnection() {
        SingletonTargetSource target = (SingletonTargetSource) proxyFactory.getTargetSource();
        return (Connection) target.getTarget();
    }

    private void resetConnection() throws SQLException {
        log.info("Connecting to {}", dbUrl);
        Connection connect = driver.connect(dbUrl, dbProps());
        if (connect == null) {
            throw new SQLException("Could not connect to %s with driver %s".formatted(dbUrl,
                                                                                      driver.getClass().getName()));
        }
        proxyFactory.setTarget(connect);
        connected = true;
        log.debug("Connected to {}", dbUrl);
    }

    private Properties dbProps() {
        final Properties info = new Properties();
        info.put("user", dbUsername);
        info.put("password", dbPassword);
        return info;
    }
}
