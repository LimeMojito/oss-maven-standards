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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static java.util.stream.Collectors.toSet;
import static software.amazon.awssdk.utils.StringUtils.isBlank;

/**
 * Authentication object that maps a HttpApiEvent to a simple principal object.
 *
 * @see ApiGatewayPrincipal
 */
@Slf4j
public class ApiGatewayAuthentication extends AbstractAuthenticationToken {

    /**
     * Principal used for the authentication.
     */
    private final ApiGatewayPrincipal principal;
    private final String accessToken;
    @Getter
    private final Map<String, String> claims;

    /**
     * Key identifiers (based on Cognito user)
     *
     * @param sub      primary key in identity pool.
     * @param userName optionally unfixed userName.
     * @param groups   groups/authorities granted to user.  Assuming 1:1.
     * @param claims   Claims map from the HttpEvent's jwt authorization
     */
    public ApiGatewayAuthentication(String sub,
                                    String userName,
                                    Set<String> groups,
                                    String accessToken,
                                    Map<String, String> claims) {
        super(newAuthoritiesFor(groups));
        this.claims = claims;
        this.principal = new ApiGatewayPrincipal(sub, userName, groups);
        this.accessToken = accessToken;
    }

    /**
     * Gets the value of a claim in the claims map
     *
     * @param key To search
     * @return Optional value of claim.
     */
    public Optional<String> getClaim(String key) {
        return Optional.ofNullable(claims.get(key));
    }

    /**
     * Extract a list of values from a claims key where arrays are packed into a single value like "[ONE,TWO]".
     *
     * @param key key to find
     * @return A List of values or an empty list if not found.
     */
    public List<String> getClaimValues(String key) {
        return arrayValuesOf(claims, key);
    }

    /**
     * Extract a list of values from a claims key where arrays are packed into a single value like "[ONE,TWO]".
     *
     * @param claims claims map to interrogate
     * @param key    key to find
     * @return A List of values or an empty list if not found.
     */
    static List<String> arrayValuesOf(Map<String, String> claims, String key) {
        Optional<String> groupsValue = Optional.ofNullable(claims.get(key));
        final List<String> arrayValues;
        if (groupsValue.isEmpty() || "[]".equals(groupsValue.get())) {
            arrayValues = Collections.emptyList();
        } else {
            final String packedValue = groupsValue.get();
            if (packedValue.startsWith("[") && packedValue.endsWith("]")) {
                final String value = packedValue.substring(1, groupsValue.get().length() - 1);
                log.debug("received {} values of {} ", key, value);
                arrayValues = List.of(value.split(","));
            } else {
                log.debug("received {} value of {} ", key, packedValue);
                arrayValues = List.of(packedValue);
            }
        }
        return arrayValues;
    }

    /**
     * The access token encoded JWT or null if now JWT present.
     *
     * @return the JWT access token or null if not present.
     */
    @Override
    public Object getCredentials() {
        return accessToken;
    }

    /**
     * An API Gateway Principal representing the user as authorized by API Gateway.
     *
     * @return principal object.
     * @see ApiGatewayPrincipal
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }

    public Optional<String> credentialsToAccessToken() {
        Object credentials = getCredentials();
        // note anonymous returns an empty string for credentials here.
        return credentials instanceof String && !isBlank((String) credentials)
               ? Optional.of((String) credentials)
               : Optional.empty();
    }

    private static Collection<? extends GrantedAuthority> newAuthoritiesFor(Set<String> groups) {
        return groups.stream().map(SimpleGrantedAuthority::new).collect(toSet());
    }
}
