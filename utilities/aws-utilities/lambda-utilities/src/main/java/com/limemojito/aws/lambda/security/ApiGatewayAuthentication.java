/*
 * Copyright (c) Lime Mojito Pty Ltd 2011-2025
 *
 * Except as otherwise permitted by the Copyright Act 1967 (Cth) (as amended from time to time) and/or any other
 * applicable copyright legislation, the material may not be reproduced in any format and in any way whatsoever
 * without the prior written consent of the copyright owner.
 */

package com.limemojito.aws.lambda.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Authentication object that maps a HttpApiEvent to a simple principal object.
 *
 * @see ApiGatewayAuthentication.ApiGatewayPrincipal
 */
public class ApiGatewayAuthentication extends AbstractAuthenticationToken {

    /**
     * Principal used for the authentication.
     */
    private final ApiGatewayPrincipal principal;

    /**
     * Key identifiers (based on Cognito user)
     *
     * @param sub      primary key in identity pool.
     * @param userName optionally unfixed userName.
     * @param groups   groups/authorities granted to user.  Assuming 1:1.
     */
    public ApiGatewayAuthentication(String sub, String userName, Set<String> groups) {
        super(newAuthoritiesFor(groups));
        this.principal = new ApiGatewayPrincipal(sub, userName, groups);
    }

    /**
     * Hard coded to return "HTTP_EVENT" representing the pre-authentication by API Gateway.
     *
     * @return credentials ("HTTP_EVENT").
     */
    @Override
    public Object getCredentials() {
        return "HTTP_EVENT";
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

    /**
     * Record representing the ApiGateway authenticated user. Note that sub is treated as the unique identifier.
     *
     * @param sub      Identity pool primary key
     * @param userName username (optionally unfixed in Cognito)
     * @param groups   groups/authorities allocated to user.
     */
    public record ApiGatewayPrincipal(String sub, String userName, Set<String> groups) implements Principal {
        @Override
        public String getName() {
            return sub;
        }
    }

    private static Collection<? extends GrantedAuthority> newAuthoritiesFor(Set<String> groups) {
        return groups.stream()
                     .map(SimpleGrantedAuthority::new)
                     .collect(toSet());
    }
}
