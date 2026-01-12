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

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.limemojito.aws.lambda.security.ApiGatewayAuthentication;
import com.limemojito.aws.lambda.security.ApiGatewayAuthenticationMapper;
import com.limemojito.aws.lambda.security.ApiGatewayPrincipal;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Optional;

/**
 * This context is available for functions decorated so that security information and event information can be examined
 * easily.
 */
public class ApiGatewayContext {

    /**
     * APIAuthentication object (parsed from event).
     */
    private final ApiGatewayAuthentication auth;

    /**
     * API Gateway event
     */
    @Getter
    private final APIGatewayV2HTTPEvent event;

    /**
     * Constructs an ApiGatewayContext object, initializing the current authentication
     * details and the corresponding API Gateway event.
     *
     * @param currentAuthentication the current authentication information encapsulated
     *                              in the ApiGatewayAuthentication object
     * @param event                 the API Gateway V2 HTTP event that this context is
     *                              associated with
     */
    public ApiGatewayContext(ApiGatewayAuthentication currentAuthentication, APIGatewayV2HTTPEvent event) {
        this.auth = currentAuthentication;
        this.event = event;
    }

    /**
     * True if the current context is valid.  This is only false if the decorated input was NOT an {@link APIGatewayV2HTTPEvent}.
     *
     * @return true if there was input as APIGatewayV2HTTPEvent, false otherwise.
     */
    public boolean isValid() {
        return auth != null && event != null;
    }

    /**
     * Support method to extract a principal from the current security context.
     *
     * @return the ApiGateway principal for this event. (not null, may be anonymous).
     * @see ApiGatewayAuthenticationMapper
     */
    public ApiGatewayPrincipal getPrincipal() {
        return (ApiGatewayPrincipal) auth.getPrincipal();
    }

    /**
     * Gets the value of a claim in the claims map
     *
     * @param key To search
     * @return Optional value of claim.
     */
    public Optional<String> getClaim(String key) {
        return auth.getClaim(key);
    }

    /**
     * Fetches the value of a claim in the claims map
     *
     * @param key To search
     * @return value of claim.
     * @throws ClaimNotFound if the claim is missing (400)
     */
    public String fetchClaim(String key) throws ClaimNotFound {
        return getClaim(key).orElseThrow(() -> new ClaimNotFound(key));
    }

    /**
     * Extract a list of values from a claims key where arrays are packed into a single value like "[ONE,TWO]".
     *
     * @param key key to find
     * @return A List of values or an empty list if not found.
     */
    public List<String> getClaimValues(String key) {
        return auth.getClaimValues(key);
    }

    /**
     * Fetches the value of a claim in the claims map
     *
     * @param key To search
     * @return value of claim.
     * @throws ClaimNotFound if the claim is missing (400)
     */
    public List<String> fetchClaimValues(String key) throws ClaimNotFound {
        List<String> values = getClaimValues(key);
        if (values.isEmpty()) {
            throw new ClaimNotFound(key);
        }
        return values;
    }

    /**
     * Returns the encoded JWT access token if present from the current security context.
     *
     * @return an Optional of the token value.
     */
    public Optional<String> getAccessToken() {
        return auth.credentialsToAccessToken();
    }

    /**
     * Returns the encoded JWT access token if present from the current security  context, throwing a 400 bad request if
     * the token is missing.
     *
     * @return an Optional of the token value.
     * @throws TokenNotFound exception if the access token is not present.  This is annotated to produce a 400 error.
     */
    public String fetchAccessToken() throws TokenNotFound {
        return getAccessToken().orElseThrow(TokenNotFound::new);
    }

    /**
     * Thrown when an access token can not be found and is required.  400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static final class TokenNotFound extends RuntimeException {
        private TokenNotFound() {
            super("No access token passed in HTTP event");
        }
    }

    /**
     * Thrown when an access token can not be found and is required.  400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static final class ClaimNotFound extends RuntimeException {
        private ClaimNotFound(String claim) {
            super("Claim %s not found in HTTP event".formatted(claim));
        }
    }
}
