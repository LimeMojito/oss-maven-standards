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

package com.limemojito.json;

import com.limemojito.json.JsonLoader;
import com.limemojito.json.JsonMapperPrototype;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.io.*;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JsonLoaderTest {

    private final JsonLoader jsonLoader = new JsonLoader(JsonMapperPrototype.buildBootLikeMapper());

    @Test
    public void shouldThrowExceptionOnNoResource() {
        assertThatThrownBy(() -> jsonLoader.loadFrom("bogus.json", TestRecord.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowExceptionOnNoResourceWithTypeReference() {
        assertThatThrownBy(() -> jsonLoader.loadFrom("bogus.json", new TypeReference<TestRecord>() {
        }))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldLoadJson() {
        TestRecord record = jsonLoader.loadFrom("/test.json", TestRecord.class);
        assertThat(record).isEqualTo(new TestRecord("test", 42));
    }

    @Test
    public void shouldLoadJsonAsList() {
        List<TestRecord> record = jsonLoader.loadFrom("/testCollection.json", new TypeReference<>() {
        });
        assertThat(record).isEqualTo(List.of(new TestRecord("test", 42)));
    }

    @Test
    public void shouldConvertString() {
        TestRecord record = jsonLoader.convert("{\"string\":\"test\",\"number\":42}", TestRecord.class);
        assertThat(record).isEqualTo(new TestRecord("test", 42));
    }

    @Test
    public void shouldConvertBytes() {
        TestRecord record = jsonLoader.convert("{\"string\":\"test\",\"number\":42}".getBytes(), TestRecord.class);
        assertThat(record).isEqualTo(new TestRecord("test", 42));
    }

    @Test
    public void shouldConvertInputStream() {
        InputStream stream = new ByteArrayInputStream("{\"string\":\"test\",\"number\":42}".getBytes());
        TestRecord record = jsonLoader.convert(stream, TestRecord.class);
        assertThat(record).isEqualTo(new TestRecord("test", 42));
    }

    @Test
    public void shouldConvertStringWithTypeReference() {
        List<TestRecord> record = jsonLoader.convert("[{\"string\":\"test\",\"number\":42}]", new TypeReference<>() {
        });
        assertThat(record).isEqualTo(List.of(new TestRecord("test", 42)));
    }

    @Test
    public void shouldConvertInputStreamWithTypeReference() {
        InputStream stream = new ByteArrayInputStream("[{\"string\":\"test\",\"number\":42}]".getBytes());
        List<TestRecord> record = jsonLoader.convert(stream, new TypeReference<>() {
        });
        assertThat(record).isEqualTo(List.of(new TestRecord("test", 42)));
    }

    @Test
    public void shouldConvertBytesWithTypeReference() {
        List<TestRecord> record = jsonLoader.convert("[{\"string\":\"test\",\"number\":42}]".getBytes(), new TypeReference<>() {
        });
        assertThat(record).isEqualTo(List.of(new TestRecord("test", 42)));
    }

    @Test
    public void shouldConvertToMap() {
        Map<String, Object> map = jsonLoader.convertToMap("{\"string\":\"test\",\"number\":42}");
        assertThat(map).containsEntry("string", "test").containsEntry("number", 42);
    }

    @Test
    public void shouldConvertObjectToMap() {
        Map<String, Object> map = jsonLoader.convertToMap(new TestRecord("test", 42));
        assertThat(map).containsEntry("string", "test").containsEntry("number", 42);
    }

    @Test
    public void shouldConvertToJson() {
        String json = jsonLoader.toJson(new TestRecord("test", 42));
        assertThat(json).contains("\"string\":\"test\"").contains("\"number\":42");
    }

    @Test
    public void shouldWriteToJsonOutputStream() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        jsonLoader.toJson(out, new TestRecord("test", 42));
        assertThat(out.toString()).contains("\"string\":\"test\"").contains("\"number\":42");
    }

    @Test
    public void shouldWriteToJsonWriter() {
        StringWriter writer = new StringWriter();
        jsonLoader.toJson(writer, new TestRecord("test", 42));
        assertThat(writer.toString()).contains("\"string\":\"test\"").contains("\"number\":42");
    }

    @Test
    public void shouldThrowExceptionOnWriteError() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("forced error");
            }
        };
        assertThatThrownBy(() -> jsonLoader.toJson(out, new TestRecord("test", 42)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void shouldThrowExceptionOnWriteErrorWithWriter() {
        Writer writer = new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("forced error");
            }

            @Override
            public void flush() throws IOException {
            }

            @Override
            public void close() throws IOException {
            }
        };
        assertThatThrownBy(() -> jsonLoader.toJson(writer, new TestRecord("test", 42)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJson() {
        assertThatThrownBy(() -> jsonLoader.convert("invalid", TestRecord.class))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJsonBytes() {
        assertThatThrownBy(() -> jsonLoader.convert("invalid".getBytes(), TestRecord.class))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJsonInputStream() {
        assertThatThrownBy(() -> jsonLoader.convert(new ByteArrayInputStream("invalid".getBytes()), TestRecord.class))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJsonWithTypeReference() {
        assertThatThrownBy(() -> jsonLoader.convert("invalid", new TypeReference<TestRecord>() {
        }))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJsonBytesWithTypeReference() {
        assertThatThrownBy(() -> jsonLoader.convert("invalid".getBytes(), new TypeReference<TestRecord>() {
        }))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJsonInputStreamWithTypeReference() {
        assertThatThrownBy(() -> jsonLoader.convert(new ByteArrayInputStream("invalid".getBytes()), new TypeReference<TestRecord>() {
        }))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJsonFromResource() {
        assertThatThrownBy(() -> jsonLoader.loadFrom("/invalid.json", TestRecord.class))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldThrowExceptionOnInvalidJsonFromResourceWithTypeReference() {
        assertThatThrownBy(() -> jsonLoader.loadFrom("/invalid.json", new TypeReference<TestRecord>() {
        }))
                .isInstanceOf(Exception.class);
    }

    @Test
    public void shouldHandleLoadFromIOException() {
        JsonLoader loader = new JsonLoader(JsonMapperPrototype.buildBootLikeMapper()) {
            @Override
            protected InputStream getResourceAsStream(String resourcePath) {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return -1;
                    }

                    @Override
                    public void close() throws IOException {
                        throw new IOException("forced close");
                    }
                };
            }
        };
        // This will trigger the catch block in the private loadFrom method during close()
        assertThatThrownBy(() -> loader.loadFrom("/test.json", TestRecord.class))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    public void shouldHandleLoadFromIOExceptionWithTypeReference() {
        JsonLoader loader = new JsonLoader(JsonMapperPrototype.buildBootLikeMapper()) {
            @Override
            protected InputStream getResourceAsStream(String resourcePath) {
                return new InputStream() {
                    @Override
                    public int read() throws IOException {
                        return -1;
                    }

                    @Override
                    public void close() throws IOException {
                        throw new IOException("forced close");
                    }
                };
            }
        };
        assertThatThrownBy(() -> loader.loadFrom("/test.json", new TypeReference<TestRecord>() {
        }))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class);
    }
}
