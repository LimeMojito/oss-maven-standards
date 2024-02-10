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

package com.limemojito.lambda.poc;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

import static com.limemojito.lambda.poc.JavaLambdaPocStack.LAMBDA_FUNCTION_ID;

public final class Application {
    public static void main(final String... args) {
        final App app = new App();
        final String stackId = LAMBDA_FUNCTION_ID + "-stack";
        new JavaLambdaPocStack(app,
                               stackId,
                               StackProps.builder()
                                           .stackName(stackId)
                                           .description("Proof of a native lambda deployment.")
                                           .build());
        app.synth();
    }
}
