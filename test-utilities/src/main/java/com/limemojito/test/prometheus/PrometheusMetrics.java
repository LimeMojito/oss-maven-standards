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

package com.limemojito.test.prometheus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

@Slf4j
public class PrometheusMetrics {
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
     *
     * @throws IOException If the metrics can not be read.
     */
    public void refresh() throws IOException {
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
     * @throws IllegalStateException if there are too many metrics matching the name.
     */
    public Metric getMetric(String metricName) throws IOException {
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
     * @throws IOException           When we can not connect to metric endpoint.
     * @throws IllegalStateException if there are too many metrics matching the name.
     */
    public BigDecimal getValue(String metricName) throws IOException {
        return metricToValue(metricName, getMetric(metricName));
    }

    /**
     * @param metricName  Metric name to get
     * @param tagsToMatch Tag to match to find metric. All tags must be matched.
     * @throws IOException           When we can not connect to metric endpoint.
     * @throws IllegalStateException if there are no metrics matching the name and tags.
     */
    public Metric getMetric(String metricName, Map<String, String> tagsToMatch) throws IOException {
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
     * @throws IOException When we can not connect to metric endpoint.
     */
    public BigDecimal getValue(String metricName, Map<String, String> tagsToMatch) throws IOException {
        try {
            return metricToValue(metricName, getMetric(metricName, tagsToMatch));
        } catch (IllegalStateException e) {
            return VALUE_NOT_FOUND;
        }
    }

    private List<Metric> getMetricsFor(String metricName) throws IOException {
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
