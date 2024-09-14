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

package com.limemojito.aws.lambda;

import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Function;

@SpringBootApplication
@Import(TestFunction.ValidatingService.class)
public class TestFunction {
    public static void main(String[] args) {
        SpringApplication.run(TestFunction.class, args);
    }

    @Service
    @Validated
    @Slf4j
    public static class ValidatingService {
        public String perform(@Size(min = 5, max = 10) String input) {
            log.info("Validating service on {}", input);
            return input;
        }
    }

    @Bean
    public SnapStartOptimizer snapStartOptimizer(ValidatingService validatingService) {
        return new SnapStartOptimizer() {
            @Override
            protected void performBeforeCheckpoint() {
                checkSpringCloudFunctionDefinitionBean();
                swallowError(() -> validatingService.perform("bng"));
            }
        };
    }

    @Bean
    public Function<String, String> goBangValue() {
        return input -> {
            throw new NotFoundException();
        };
    }

    @Bean
    public Function<String, String> goPassthrough() {
        return input -> input;
    }

    @Bean
    public Function<String, String> goBangCode() {
        return input -> {
            throw new TeapotException();
        };
    }

    @Bean
    public Function<String, String> goBangRaw() {
        return input -> {
            throw new RawException();
        };
    }

    @Bean
    public Function<String, String> goBangConstraintViolation(ValidatingService service) {
        return service::perform;
    }

    public static class RawException extends RuntimeException {
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException() {
            super("I am not found, so I am lost");
        }
    }

    @ResponseStatus(code = HttpStatus.I_AM_A_TEAPOT, reason = "custom reason")
    public static class TeapotException extends RuntimeException {
    }
}
