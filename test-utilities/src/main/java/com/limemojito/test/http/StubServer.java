/*
 * Copyright 2011-2023 Lime Mojito Pty Ltd
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

package com.limemojito.test.http;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static com.github.tomakehurst.wiremock.stubbing.StubMapping.buildFrom;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

public class StubServer extends WireMockServer {

    public StubServer(int port) {
        super(port);
    }

    @Override
    public void start() {
        resetAll();
        super.start();
    }

    public void addMapping(String mapping) {
        addStubMapping(loadMapping(format("/wiremock-mappings/%s.json", mapping)));
    }

    @SneakyThrows
    private StubMapping loadMapping(String path) {
        final int initialOutputSize = 8096;
        final InputStream resourceAsStream = getClass().getResourceAsStream(path);
        if (resourceAsStream == null) {
            throw new RuntimeException(format("Could not load mapping %s from classpath", path));
        }
        try (InputStreamReader reader = new InputStreamReader(resourceAsStream, UTF_8);
             StringWriter output = new StringWriter(initialOutputSize)) {
            int read = reader.read();
            while (read != -1) {
                output.write(read);
                read = reader.read();
            }
            return buildFrom(output.toString());
        }
    }
}
