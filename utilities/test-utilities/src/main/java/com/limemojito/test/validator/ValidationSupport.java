/*
 * Copyright 2011-2025 Lime Mojito Pty Ltd
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

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Test-time helper around {@link jakarta.validation.Validator} to perform bean validation
 * and fail fast with a {@link jakarta.validation.ConstraintViolationException} when
 * constraints are violated.
 * <p>
 * This component is registered by Spring (see {@code ValidationSupportConfiguration}) so it can
 * be injected into tests or utility classes. It is especially useful in integration and
 * contract tests to assert that DTOs, commands, and events satisfy their Jakarta Bean Validation
 * annotations (e.g. {@code @NotNull}, {@code @Size}, etc.).
 * </p>
 *
 * # Typical usage
 * <pre>{@code
 * @Autowired
 * private ValidationSupport validationSupport;
 *
 * @Test
 * void dtoIsValid() {
 *     MyDto dto = new MyDto("abc");
 *     // Returns the same instance if valid; throws if invalid.
 *     MyDto validated = validationSupport.hardValidate(dto);
 * }
 *
 * @Test
 * void dtoIsInvalid() {
 *     MyDto dto = new MyDto(null);
 *     assertThatThrownBy(() -> validationSupport.hardValidate(dto))
 *         .isInstanceOf(ConstraintViolationException.class);
 * }
 * }</pre>
 * <p>
 * The helper does not mutate the instance; it simply validates and returns it for convenient
 * inline use such as {@code repository.save(validationSupport.hardValidate(dto))}.
 *
 * @see jakarta.validation.Validator
 * @see jakarta.validation.ConstraintViolationException
 */
@Component
@RequiredArgsConstructor
public class ValidationSupport {
    private final Validator validator;

    /**
     * Validates the provided value using the injected {@link Validator}. If any
     * constraint violations are found, a {@link ConstraintViolationException} is thrown
     * containing all violations. If valid, the same instance is returned to enable
     * fluent/inline usage.
     *
     * @param value the object to validate (must have Jakarta Bean Validation annotations)
     * @param <T>   the type of the object being validated
     * @return the same {@code value} instance when no violations are found
     * @throws ConstraintViolationException if one or more constraints are violated
     */
    public <T> T hardValidate(T value) {
        Set<ConstraintViolation<T>> violations = validator.validate(value);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return value;
    }

}
