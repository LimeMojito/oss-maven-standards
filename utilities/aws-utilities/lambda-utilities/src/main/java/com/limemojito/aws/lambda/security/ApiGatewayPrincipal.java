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

package com.limemojito.aws.lambda.security;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.security.Principal;
import java.util.Set;

/**
 * Record representing the ApiGateway authenticated user. Note that sub is treated as the unique identifier.
 *
 * @param sub      Identity pool primary key
 * @param userName username (optionally unfixed in Cognito)
 * @param groups   groups/authorities allocated to user.
 */
public record ApiGatewayPrincipal(@NotEmpty String sub, @NotEmpty String userName, @NotNull Set<String> groups)
        implements Principal {
    /**
     * Note this principal return SUB for the key rather than userName as cognito's can be altered.
     *
     * @return primaryKey of user.
     */
    @Override
    public String getName() {
        return sub;
    }
}
