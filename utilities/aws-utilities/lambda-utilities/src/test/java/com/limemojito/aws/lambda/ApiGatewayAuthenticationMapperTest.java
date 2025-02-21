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

package com.limemojito.aws.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.limemojito.aws.lambda.security.ApiGatewayAuthentication;
import com.limemojito.aws.lambda.security.ApiGatewayAuthenticationMapper;
import com.limemojito.aws.lambda.security.ApiGatewayPrincipal;
import com.limemojito.json.JsonLoader;
import com.limemojito.json.ObjectMapperPrototype;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiGatewayAuthenticationMapperTest {

    private final JsonLoader json = new JsonLoader(ObjectMapperPrototype.buildBootLikeMapper());
    private final ApiGatewayAuthenticationMapper mapper = new ApiGatewayAuthenticationMapper("cognito:groups",
                                                                                             "ANON",
                                                                                             "AnonymousUser",
                                                                                             "ANON");

    @Test
    public void shouldHaveAnonymousToken() {
        Authentication anon = mapper.getAnonymousAuthentication();

        assertAnonymous(anon);
        assertThat(anon.getAuthorities()).hasSize(1);
        assertThat(anon.getAuthorities().iterator().next().getAuthority()).isEqualTo("ANON");
        assertThat(anon.getName()).isEqualTo("ANON");
        assertThat(anon.getPrincipal()).isInstanceOf(ApiGatewayPrincipal.class);

        ApiGatewayPrincipal principal = (ApiGatewayPrincipal) anon.getPrincipal();
        assertThat(principal.sub()).isEqualTo("ANON");
        assertThat(principal.userName()).isEqualTo("AnonymousUser");
        assertThat(principal.groups()).isEqualTo(Set.of("ANON"));

        assertThat(anon.getDetails()).isNull();
    }

    @Test
    public void shouldMapToAnonymous() {
        Authentication auth = mapper.convertToAuthentication(loadEvent("/event/httpEventAnonymous.json"));

        assertAnonymous(auth);
    }

    @Test
    public void shouldMapToUserNoGroups() {
        final Authentication auth = mapper.convertToAuthentication(loadEvent("/event/httpEvent.json"));

        assertAuthenticated(auth, "sub-bob@example.com", "bob@example.com", Set.of(), "somejwtencodedtoken");
    }

    @Test
    public void shouldMapToUserToCognitoGroups() {
        final Authentication auth = mapper.convertToAuthentication(loadEvent("/event/httpEventWithWrongGroup.json"));

        assertAuthenticated(auth, "sub-bob@example.com", "bob@example.com", Set.of("WRONG", "accounting", "PEABODY"),
                            "someJWTtokenencodedhere");
    }

    private static void assertAuthenticated(Authentication auth,
                                            String sub,
                                            String userName,
                                            Set<String> groups,
                                            String accessToken) {
        assertThat(auth).isInstanceOf(ApiGatewayAuthentication.class);
        ApiGatewayAuthentication apiAuth = (ApiGatewayAuthentication) auth;
        assertThat(apiAuth.isAuthenticated()).isTrue();
        assertThat(apiAuth.getCredentials()).isEqualTo(accessToken);
        assertApiPrincipal(apiAuth.getPrincipal(), sub, userName, groups);
        assertThat(apiAuth.getAuthorities()
                          .stream()
                          .map(GrantedAuthority::getAuthority)).contains(groups.toArray(new String[0]));
    }

    private Collection<GrantedAuthority> authoritiesFrom(Set<String> groups) {
        return groups.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toCollection(HashSet::new));
    }

    private static void assertApiPrincipal(Object principal, String sub, String username, Set<String> groups) {
        assertThat(principal).isInstanceOf(ApiGatewayPrincipal.class);
        ApiGatewayPrincipal apiPrincipal = (ApiGatewayPrincipal) principal;
        assertThat(apiPrincipal.sub()).isEqualTo(sub);
        assertThat(apiPrincipal.getName()).isEqualTo(sub);
        assertThat(apiPrincipal.userName()).isEqualTo(username);
        assertThat(apiPrincipal.groups()).isEqualTo(groups);
    }

    private void assertAnonymous(Authentication auth) {
        assertThat(auth).isEqualTo(mapper.getAnonymousAuthentication());
        assertThat(auth.isAuthenticated()).isFalse();
    }

    private APIGatewayV2HTTPEvent loadEvent(String resourcePath) {
        return json.loadFrom(resourcePath, APIGatewayV2HTTPEvent.class);
    }
}
