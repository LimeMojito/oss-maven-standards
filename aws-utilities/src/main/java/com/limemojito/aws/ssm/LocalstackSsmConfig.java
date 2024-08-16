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

package com.limemojito.aws.ssm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.net.URI;
import java.util.List;
import java.util.StringTokenizer;

@Profile("integration-test")
@Configuration
@Slf4j
public class LocalstackSsmConfig {

    /**
     * Returns an instance of the Amazon SMS client.
     *
     * @param localStackSnsUrl The URI of the Localstack service.
     * @param pairList         The list of pairs for creating SSM parameters.
     * @return An instance of the SmsClient that is configured for the Localstack SNS service.
     */
    @Primary
    @Bean(destroyMethod = "close")
    public SsmClient amazonSmsClient(@Value("${localstack.url}") URI localStackSnsUrl,
                                     @Value("#{'${localstack.sms.pairs:}'.split(',')}") List<String> pairList) {
        SsmClient ssm = SsmClient.builder()
                                 .endpointOverride(localStackSnsUrl)
                                 .build();

        log.info("SSM Parameters to create: {}", pairList);
        pairList.stream()
                .filter(StringUtils::hasLength)
                .map(String::strip)
                .forEach(pair -> createSsmParameter(ssm, pair));
        return ssm;
    }

    private void createSsmParameter(SsmClient ssm, String pair) {
        StringTokenizer tokenizer = new StringTokenizer(pair, "=");
        String name = tokenizer.nextToken();
        String value = tokenizer.nextToken();
        ssmParameter(ssm, name, value);
    }

    private void ssmParameter(SsmClient ssm, String name, String value) {
        ssm.putParameter(builder -> builder.name(name)
                                           .type("String")
                                           .value(value));
    }
}
