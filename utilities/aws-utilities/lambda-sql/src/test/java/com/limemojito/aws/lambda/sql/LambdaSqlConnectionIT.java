/*
 * Copyright 2011-2024 Lime Mojito Pty Ltd
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestPropertySource("classpath:/test-db.properties")
@SpringBootTest(classes = LambdaSqlConnection.class)
@Slf4j
public class LambdaSqlConnectionIT {

    @Autowired
    private Connection connection;

    @Autowired
    private LambdaSqlConnection factory;

    @Test
    public void shouldBeConnectToPostgresOk() throws Exception {
        assertThat(factory.isConnected()).isTrue();
        testConnection();
    }

    @Test
    public void shouldSimulateSnapstartSnapshot() throws Exception {
        factory.performBeforeCheckpoint();
        assertThat(factory.isConnected()).isFalse();

        assertThatThrownBy(this::testConnection).isInstanceOf(SQLException.class);

        factory.performAfterRestore();
        assertThat(factory.isConnected()).isTrue();
        testConnection();
    }


    private void testConnection() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select 1")) {
                resultSet.next();
                log.info("Received from dual: {}", resultSet.getInt(1));
            }
        }
    }
}
