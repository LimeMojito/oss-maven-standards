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

package com.limemojito.test.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Test support for jakarta validation tests.
 */
@Configuration
@ComponentScan(basePackageClasses = ValidationSupport.class)
@Slf4j
public class ValidationSupportConfiguration {
    /**
     * Create a default validator factory bean if none is provided.
     *
     * @return ValidatorFactory instance.
     */
    @ConditionalOnMissingBean(ValidatorFactory.class)
    @Bean(destroyMethod = "close")
    public ValidatorFactory limeValidationFactory() {
        log.warn("No ValidatorFactory found, creating a default one using Validation.buildDefaultValidatorFactory().");
        return Validation.buildDefaultValidatorFactory();
    }

    /**
     * Create a default validator bean if none is provided.
     *
     * @param validatorFactory Factory for creating validators
     * @return a validator bean
     */
    @ConditionalOnMissingBean(Validator.class)
    @Bean
    public Validator limeValidator(ValidatorFactory validatorFactory) {
        log.warn("No Validator found, creating a default one using validatorFactory.getValidator().");
        return validatorFactory.getValidator();
    }
}
