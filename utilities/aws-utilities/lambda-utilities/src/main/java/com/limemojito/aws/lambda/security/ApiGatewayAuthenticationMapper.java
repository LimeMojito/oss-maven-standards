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

package com.limemojito.aws.lambda.security;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent.RequestContext.Authorizer.JWT;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Convert an HTTP API Event from a JWT authorizer to an authentication object by reading the event request context.
 * Claim key for groups is configurable.  Anonymous string keys also configurable.
 *
 * @see APIGatewayV2HTTPEvent
 * @see ApiGatewayAuthenticationMapper#ApiGatewayAuthenticationMapper(String, String, String, String)
 */
@Component
@Slf4j
public class ApiGatewayAuthenticationMapper {

    private final String claimsKey;
    @Getter
    private final AnonymousAuthenticationToken anonymousAuthentication;

    /**
     * Create a new mapper using the supplied claims key to extract group information.  Assumes string value embedded as
     * an array (ie [GROUP1,GROUP2]).
     *
     * @param claimsKey          Claims key to use to extract authorities for mapping. (cognito:groups)
     * @param anonymousSub       Sub (subject) value for anonymous user (compatible with cognito)  (ANONYMOUS)
     * @param anonymousUserName  UserName for anonymous user (anonymous)
     * @param anonymousAuthority Authority name set by default for anonymous users (ANONYMOUS).
     */
    public ApiGatewayAuthenticationMapper(@Value("${com.limemojito.aws.lambda.security.claimsKey:cognito:groups}") String claimsKey,
                                          @Value("${com.limemojito.aws.lambda.security.anonymous.sub:ANONYMOUS}") String anonymousSub,
                                          @Value("${com.limemojito.aws.lambda.security.anonymous.userName:anonymous}") String anonymousUserName,
                                          @Value("${com.limemojito.aws.lambda.security.anonymous.authority:ANONYMOUS}") String anonymousAuthority) {
        this.claimsKey = claimsKey;
        final ApiGatewayAuthentication.ApiGatewayPrincipal anonPrincipal = new ApiGatewayAuthentication.ApiGatewayPrincipal(
                anonymousSub,
                anonymousUserName,
                Set.of(anonymousAuthority));
        final ApiGatewayAuthentication anonApi = new ApiGatewayAuthentication(anonymousSub,
                                                                              anonymousUserName,
                                                                              Set.of(anonymousAuthority));
        this.anonymousAuthentication = new AnonymousAuthenticationToken(this.getClass().getName(),
                                                                        anonApi.getPrincipal(),
                                                                        anonApi.getAuthorities()) {
            /**
             * Block setting authenticated to true.
             * @return always false
             */
            @Override
            public boolean isAuthenticated() {
                return false;
            }
        };
        log.info("ApiGatewayAuthenticationMapper claimsKey set to {}, anon authority set to {}:{}:{}",
                  claimsKey,
                  anonymousUserName,
                  anonymousSub,
                  anonymousAuthority);
    }

    /**
     * Convert input HTTP event to an Authentication object suitable for use with method level security.  Found users
     * will be allocated Authorities to match the groups claim values.
     *
     * @param input HTTP Event
     * @return The anonymous Authentication if there is no JWT context details OR the extracted user Authentication.
     */
    public Authentication convertToAuthentication(APIGatewayV2HTTPEvent input) {
        log.debug("User from HttpEvent");
        final Optional<JWT> jwt = findJwtContext(input);
        if (jwt.isEmpty()) {
            log.debug("No JWT found in httpEvent");
            log.info("Anonymous user");
            return anonymousAuthentication;
        } else {
            final Map<String, String> claimsMap = fetchClaimsMap(jwt.get());
            final String userName = fetchClaimValue(claimsMap, "username");
            final String sub = fetchClaimValue(claimsMap, "sub");
            final Set<String> groups = authoritiesFromOptionalGroupsClaim(claimsMap);
            log.info("Found user {} with groups {} in httpEvent", userName, groups);
            final ApiGatewayAuthentication authentication = new ApiGatewayAuthentication(sub, userName, groups);
            authentication.setAuthenticated(true);
            return authentication;
        }
    }

    private Set<String> authoritiesFromOptionalGroupsClaim(Map<String, String> claims) {
        Optional<String> groupsValue = Optional.ofNullable(claims.get(claimsKey));
        if (groupsValue.isEmpty() || "[]".equals(groupsValue.get())) {
            return Set.of();
        }
        final String value = groupsValue.get().substring(1, groupsValue.get().length() - 1);
        log.debug("received groups value of {} ", value);
        final Set<String> groups = Set.of(value.split(","));
        log.debug("converted to groups {}", groups);
        return groups;
    }

    private static String fetchClaimValue(Map<String, String> claimsMap, String claimKey) {
        return Optional.ofNullable(claimsMap.get(claimKey))
                       .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(
                               "No %s in httpEvent".formatted(claimKey)));
    }

    private static Optional<JWT> findJwtContext(APIGatewayV2HTTPEvent event) {
        if (event == null) {
            throw new AuthenticationCredentialsNotFoundException("Http event is null");
        }
        final APIGatewayV2HTTPEvent.RequestContext context = event.getRequestContext();
        if (context == null) {
            throw new AuthenticationCredentialsNotFoundException("Request context is null");
        }
        final APIGatewayV2HTTPEvent.RequestContext.Authorizer authorizer = context.getAuthorizer();
        if (authorizer != null) {
            final JWT jwt = authorizer.getJwt();
            return Optional.ofNullable(jwt);
        }
        return Optional.empty();
    }

    private static Map<String, String> fetchClaimsMap(JWT jwt) {
        final Map<String, String> claims = jwt.getClaims();
        if (claims == null) {
            throw new AuthenticationCredentialsNotFoundException("Claims are null");
        }
        return claims;
    }
}
