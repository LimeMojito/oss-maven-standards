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

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.limemojito.aws.lambda.ApiGatewayResponseDecoratorFactory;
import com.limemojito.json.ObjectMapperPrototype;
import com.limemojito.lambda.poc.Application;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationUnitTest {

    private final ObjectMapper mapper = ObjectMapperPrototype.buildBootLikeMapper();
    private final ApiGatewayResponseDecoratorFactory factory = new ApiGatewayResponseDecoratorFactory(mapper);
    private final Function<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> hello = new Application().hello(factory);

    @Test
    public void shouldTestCall() {
        final APIGatewayV2HTTPEvent inputEvent = APIGatewayV2HTTPEvent.builder()
                                                                      .build();
        final APIGatewayV2HTTPResponse outputEvent = hello.apply(inputEvent);

        final String stringJson = "\"world\"";
        assertThat(outputEvent.getBody()).isEqualTo(stringJson);
    }
}