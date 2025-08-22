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

    /**
     * Creates a new prometheus client using the supplied WebClient instance to communicate.  This webClient should be
     * configured with a base path pointing to the prometheus aggregator.  The service will attach /actuator/prometheus
     * to the base url.
     *
     * @param serviceClient WebClient, preconfigured, to use against prometheus.
     */
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
     * Retrieves a single metric by its name. If there is exactly one metric with the given name, it is returned.
     * If no metrics are found with the provided name, null is returned. If multiple metrics are found, an
     * IllegalStateException is thrown.
     *
     * @param metricName The name of the metric to retrieve.
     * @return The metric with the specified name, or null if no metric is found. Throws IllegalStateException
     * if multiple metrics are found with the same name.
     * @throws IllegalStateException If the metric name corresponds to multiple metrics.
     */
    public Metric getMetric(String metricName) {
        final List<Metric> metrics = getMetricsFor(metricName);
        if (metrics == null) {
            return null;
        }
        if (metrics.size() == 1) {
            return metrics.getFirst();
        }
        throw new IllegalStateException(format("Metrics name %s has multiple metrics %s", metricName, metrics));
    }

    /**
     * Retrieves the value of a specific metric using its name.
     * If the metric is found, its value is returned. If the metric does not exist,
     * a default value representing "value not found" is returned.
     *
     * @param metricName The name of the metric whose value is to be retrieved.
     * @return The value of the specified metric if found, or a default "value not found"
     * constant if the metric is missing.
     * @throws IllegalStateException If multiple metrics with the specified name are found.
     */
    public BigDecimal getValue(String metricName) {
        return metricToValue(metricName, getMetric(metricName));
    }

    /**
     * Retrieves a metric by its name and a set of tags to match. The method searches for a metric
     * with the specified name and ensures that all provided tags match. If such a metric is found,
     * it is returned. If no matching metric is found, an IllegalStateException is thrown.
     *
     * @param metricName  the name of the metric to locate
     * @param tagsToMatch a map of tags to match; all tags must be matched for the metric to be returned
     * @return the Metric object that matches the provided name and tags
     * @throws IllegalStateException if no matching metric is found
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
     * Retrieves the numerical value of a metric specified by its name and a map of tags to match.
     * The metric is identified by its name, and only metrics with all the specified tags will be considered.
     * If the metric is found, its value is returned. If no matching metric is found, a default
     * value indicating "value not found" is returned.
     *
     * @param metricName  the name of the metric whose value is to be retrieved
     * @param tagsToMatch a map of tags that must match completely on the metric
     * @return the numerical value of the matching metric if found; otherwise, a default "value not found" value
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
