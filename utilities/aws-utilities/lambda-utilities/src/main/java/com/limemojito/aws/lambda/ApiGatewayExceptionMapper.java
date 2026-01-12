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

package com.limemojito.aws.lambda;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * This can be overridden by a bean definition of this interface.
 */
public interface ApiGatewayExceptionMapper {
    /**
     * INTERNAL_SERVER_ERROR by default.
     * Or @ResponseStatus if he annotation is present on the exception
     * 400 for ConstraintViolationException
     * 403 for AccessDeniedException (Spring Security)
     *
     * @param e Throwable to convert
     * @return a http error code.
     * @see ConstraintViolationException
     * @see AccessDeniedException
     * @see ResponseStatus
     */
    default HttpStatus map(Throwable e) {
        final ResponseStatus responseStatusType = e.getClass().getAnnotation(ResponseStatus.class);
        if (responseStatusType != null) {
            return handleResponseStatusAnnotation(responseStatusType);
        } else {
            if (e instanceof ConstraintViolationException) {
                return BAD_REQUEST;
            } else if (e instanceof AccessDeniedException) {
                return FORBIDDEN;
            }
            return INTERNAL_SERVER_ERROR;
        }
    }

    private static HttpStatus handleResponseStatusAnnotation(ResponseStatus responseStatusType) {
        // these can be set to different values.  We pay attention if not the default (500).
        if (responseStatusType.code() != INTERNAL_SERVER_ERROR) {
            return responseStatusType.code();
        }
        if (responseStatusType.value() != INTERNAL_SERVER_ERROR) {
            return responseStatusType.value();
        } else {
            return INTERNAL_SERVER_ERROR;
        }
    }
}
