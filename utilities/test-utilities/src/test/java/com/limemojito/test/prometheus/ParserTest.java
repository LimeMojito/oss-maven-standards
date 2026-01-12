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

package com.limemojito.test.prometheus;

import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void shouldParseTags() {
        final Metric metric = parser.parse(
                "trade_event_account_info_seconds_count{class=\"com.limemojito.cloud.tradeeventprocessor.AccountInfoEventHandler\",exception=\"none\",method=\"process\",} 2.0");

        assertThat(metric.getName()).isEqualTo("trade_event_account_info_seconds_count");
        assertThat(metric.getValue()).isEqualByComparingTo("2.0");

        assertThat(metric.getTags()).containsEntry("class",
                                                   "com.limemojito.cloud.tradeeventprocessor.AccountInfoEventHandler");
        assertThat(metric.getTags()).containsEntry("exception", "none");
        assertThat(metric.getTags()).containsEntry("method", "process");
    }

    @Test
    public void shouldParseSimpleMetric() {
        final Metric metric = parser.parse("jvm_classes_loaded_classes 11909.0");

        assertThat(metric.getName()).isEqualTo("jvm_classes_loaded_classes");
        assertThat(metric.getValue()).isEqualByComparingTo("11909.0");
        assertThat(metric.hasTags()).isFalse();
    }

    @Test
    public void shouldBeComment() {
        assertThat(parser.isComment("# a comment")).isTrue();
    }

    @Test
    public void shouldLoadAllData() throws Exception {
        String fileData = IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/prometheus.txt")),
                                           UTF_8);
        final Map<String, List<Metric>> metricMap = parser.parsePrometheusData(fileData);
        final int expectedSize = 65;
        Assertions.assertThat(metricMap).hasSize(expectedSize);

        assertThat(metricMap.get("jvm_classes_loaded_classes").get(0).getValue()).isEqualByComparingTo("11909.0");

        assertThat(metricMap.get("trade_event_account_info_seconds_count")
                            .get(0)
                            .getValue()).isEqualByComparingTo("2.0");
        assertThat(metricMap.get("trade_event_account_info_seconds_count").get(0).getTags()).hasSize(3);
        assertThat(metricMap.get("trade_event_account_info_seconds_count").get(0).getTags().get("method")).isEqualTo(
                "process");
    }

    @Test
    public void shouldLoadMultipleTagsForSameName() throws Exception {
        String fileData = IOUtils.toString(Objects.requireNonNull(getClass().getResourceAsStream("/prometheus.txt")),
                                           UTF_8);
        final Map<String, List<Metric>> metricMap = parser.parsePrometheusData(fileData);

        assertThat(metricMap.get("jvm_memory_committed_bytes")).hasSize(7);
    }
}
