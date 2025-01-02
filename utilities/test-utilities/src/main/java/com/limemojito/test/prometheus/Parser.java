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

package com.limemojito.test.prometheus;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * The Parser class is responsible for parsing Prometheus metrics data and converting it into a map of Metric objects.
 * It provides methods for parsing different formats of metrics data and extracting metric names, tags, and values.
 * The class uses regular expressions and string manipulation to extract the necessary information from the metrics.
 * <p>
 * The Parser class has the following public methods:
 * - parsePrometheusData: Parses the given Prometheus metrics data and returns a map of metrics.
 * - isComment: Checks if a given string starts with a comment symbol.
 * - parse: Parses metrics data to a Metric object representation.
 * <p>
 * Example Usage:
 * <pre>{@code
 * Parser parser = new Parser();
 * String metricsData = "metric_name{tag1=value1,tag2=value2} 10.0";
 * Map<String, List<Metric>> metricMap = parser.parsePrometheusData(metricsData);
 * }</pre>
 */
@Slf4j
public class Parser {
    private final Pattern tagExtractor;

    /**
     * Creates a new parser.
     */
    public Parser() {
        tagExtractor = Pattern.compile("(.+?)\\{(.+?)} (.+)");
    }

    /**
     * Parses the given Prometheus metrics data and returns a map of metrics.
     *
     * @param metricsData the Prometheus metrics data to be parsed
     * @return a map with metric names as keys and lists of metrics as values
     */
    @SneakyThrows
    public Map<String, List<Metric>> parsePrometheusData(String metricsData) {
        try (StringReader reader = new StringReader(metricsData); LineNumberReader lineReader = new LineNumberReader(
                reader)) {
            final Map<String, List<Metric>> metricMap = new LinkedHashMap<>();
            String line;
            do {
                line = lineReader.readLine();
                if (line != null) {
                    if (isComment(line)) {
                        log.trace("Read line {} as comment", lineReader.getLineNumber());
                    } else {
                        log.trace("Parsing line {} as metric", lineReader.getLineNumber());
                        Metric metric = parse(line);
                        log.trace("Read line {} as {}", lineReader.getLineNumber(), metric);
                        final List<Metric> metrics = metricMap.computeIfAbsent(metric.getName(),
                                                                               (key) -> new ArrayList<>());
                        metrics.add(metric);
                        metricMap.put(metric.getName(), metrics);
                    }
                }
            } while (line != null);
            log.debug("Loaded {} metrics from prometheus scrape", metricMap.size());
            return metricMap;
        }
    }

    /**
     * Checks if the given string starts with a comment symbol.
     *
     * @param data the string to check
     * @return true if the string starts with a comment symbol, false otherwise
     */
    public boolean isComment(String data) {
        return data.startsWith("#");
    }

    /**
     * Parses metrics to a class representation.
     * <code>trade_event_account_info_seconds_count{class="com.limemojito.cloud.tradeeventprocessor.AccountInfoEventHandler",exception="none",method="process",} 2.0</code>
     *
     * @param metric content to parse: <code>name{tag="tag-value"} value</code>
     * @return a metric object.
     */
    public Metric parse(String metric) {
        try {
            if (metric.contains("{")) {
                return parseTagFormat(metric);
            }
            return parseSimpleFormat(metric);
        } catch (NumberFormatException e) {
            log.warn("Could not parse metric value [{}]", metric);
            throw e;
        }
    }

    private Metric parseSimpleFormat(String metric) {
        final String[] split = metric.split(" ");
        return Metric.builder()
                     .name(split[0])
                     .value(parseValue(split[1]))
                     .build();
    }

    private Metric parseTagFormat(String metric) {
        final Matcher matcher = tagExtractor.matcher(metric);
        if (matcher.matches()) {
            final int valueIndex = 3;
            return Metric.builder()
                         .name(matcher.group(1))
                         .tags(parseTagMap(matcher.group(2)))
                         .value(parseValue(matcher.group(valueIndex)))
                         .build();
        } else {
            throw new IllegalArgumentException(format("Can not parse [%s]", metric));
        }
    }

    private BigDecimal parseValue(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            if ("NaN".equalsIgnoreCase(value)) {
                return new BigDecimal(Double.MAX_VALUE);
            } else {
                throw e;
            }
        }
    }

    private Map<String, String> parseTagMap(String tags) {
        final Map<String, String> tagMap = new LinkedHashMap<>();
        final String[] split = tags.split(",");
        for (String keyValue : split) {
            final String[] keyValueSet = keyValue.split("=");
            final String rawValue = keyValueSet[1];
            tagMap.put(keyValueSet[0], rawValue.substring(1, rawValue.length() - 1));
        }
        return tagMap;
    }
}
