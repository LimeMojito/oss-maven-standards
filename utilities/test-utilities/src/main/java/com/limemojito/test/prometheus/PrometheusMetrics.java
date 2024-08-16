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

package com.limemojito.test.prometheus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

/**
 * The PrometheusMetrics class is responsible for fetching and retrieving metrics from a Prometheus endpoint.
 * It uses a WebClient to make HTTP requests to the endpoint and a Parser to parse the metrics data.
 * The class provides methods to refresh and retrieve metrics based on metric name and tags.
 * <p>
 * Example Usage:
 * <pre>{@code
 * WebClient webClient = WebClient.create();
 * PrometheusMetrics metrics = new PrometheusMetrics(webClient);
 * metrics.refresh();
 * metrics.getMetric("metric_name");
 * metrics.getValue("metric_name");
 * metrics.getMetric("metric_name", tags);
 * metrics.getValue("metric_name", tags);
 * }</pre>
 */
@Slf4j
public class PrometheusMetrics {
    /**
     * Represents a constant value used to indicate that a particular value was not found.
     *
     * <p>
     * The {@code VALUE_NOT_FOUND} constant is of type {@code BigDecimal} and is set to a value of {@code -1.0}.
     * It is declared as {@code public}, {@code static}, and {@code final}.
     *
     * <p>
     * This constant is typically used in situations where a search operation is performed
     * and the desired value is not found in the data source. Instead of returning {@code null}
     * or throwing an exception, the {@code VALUE_NOT_FOUND} constant can be used to
     * indicate that the value was not found.
     *
     * <p>
     * Example usage:
     * <pre>{@code
     * BigDecimal value = lookupValue(key);
     * if (value.equals(VALUE_NOT_FOUND)) {
     *     System.out.println("Value not found");
     * } else {
     *     System.out.println("Value found: " + value);
     * }
     * }</pre>
     *
     * @since 1.0
     */
    public static final BigDecimal VALUE_NOT_FOUND = new BigDecimal("-1.0");

    private final WebClient serviceClient;
    private final Parser parser;
    private final String uri;
    private Map<String, List<Metric>> metricMap;

    public PrometheusMetrics(WebClient serviceClient) {
        this.serviceClient = serviceClient;
        this.parser = new Parser();
        this.uri = "/actuator/prometheus";
    }

    /**
     * Load all metrics from the endpoint.
     */
    public void refresh() {
        log.info("fetching metrics from request path {}", uri);
        final String metricsData = serviceClient.get()
                                                .uri(uri)
                                                .retrieve()
                                                .bodyToMono(String.class)
                                                .block();
        metricMap = parser.parsePrometheusData(metricsData);
    }

    /**
     * @param metricName Metric name to get
     * @return null if metric not present.
     */
    public Metric getMetric(String metricName) {
        final List<Metric> metrics = getMetricsFor(metricName);
        if (metrics == null) {
            return null;
        }
        if (metrics.size() == 1) {
            return metrics.get(0);
        }
        throw new IllegalStateException(format("Metrics name %s has multiple metrics %s", metricName, metrics));
    }

    /**
     * @param metricName Metric to locate
     * @return VALUE_NOT_FOUND if the metric can not be found.
     */
    public BigDecimal getValue(String metricName) {
        return metricToValue(metricName, getMetric(metricName));
    }

    /**
     * @param metricName  Metric name to get
     * @param tagsToMatch Tag to match to find metric. All tags must be matched.
     */
    public Metric getMetric(String metricName, Map<String, String> tagsToMatch) {
        final List<Metric> metrics = getMetricsFor(metricName);
        if (metrics != null) {
            for (Metric metric : metrics) {
                if (metric.hasTags() && metric.getTags().entrySet().containsAll(tagsToMatch.entrySet())) {
                    return metric;
                }
            }
        }
        throw new IllegalStateException(format("Could not find metric %s with matching tags %s in %s",
                                               metricName,
                                               tagsToMatch,
                                               metrics));
    }

    /**
     * @param metricName  Metric to locate
     * @param tagsToMatch Tag to match to find metric. All tags must be matched.
     * @return VALUE_NOT_FOUND if the metric with matching tags can not be found.
     */
    public BigDecimal getValue(String metricName, Map<String, String> tagsToMatch) {
        try {
            return metricToValue(metricName, getMetric(metricName, tagsToMatch));
        } catch (IllegalStateException e) {
            return VALUE_NOT_FOUND;
        }
    }

    private List<Metric> getMetricsFor(String metricName) {
        if (metricMap == null) {
            refresh();
        }
        return metricMap.get(metricName);
    }

    private BigDecimal metricToValue(String metricName, Metric metric) {
        if (metric == null) {
            log.warn("Could not locate metric {}", metricName);
            return VALUE_NOT_FOUND;
        }
        log.info("Found metric {}", metric);
        return metric.getValue();
    }
}
