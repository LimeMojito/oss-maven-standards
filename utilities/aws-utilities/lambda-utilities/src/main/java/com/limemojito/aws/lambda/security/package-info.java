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

/**
 * <p>This package provides support for Spring Security PreMethod invocations using AWS HTTPEvent where APIGateway has a
 * configured JWT authorizer and roles are mapped from Cognito groups.</p>
 *
 * @see <a href="https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html#method-security-architecture">Spring Security Method Level Architecture</a>
 * @see <a href="https://aws.amazon.com/api-gateway/">API Gateway</a>
 * @see <a href="https://docs.aws.amazon.com/apigateway/latest/developerguide/http-api-jwt-authorizer.html">API Gateway with JWT Authorizers</a>
 * @see com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
 */
package com.limemojito.aws.lambda.security;
