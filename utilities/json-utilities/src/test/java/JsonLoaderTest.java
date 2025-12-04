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

import com.limemojito.json.JsonLoader;
import com.limemojito.json.JsonMapperPrototype;
import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.util.List;

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
}
