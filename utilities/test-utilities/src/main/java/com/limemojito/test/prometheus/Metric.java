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

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <code>trade_event_account_info_seconds_count{class="com.limemojito.cloud.tradeeventprocessor.AccountInfoEventHandler",exception="none",method="process",} 2.0</code>
 */
@Value
@Builder
@SuppressWarnings("RedundantModifiersValueLombok")
public class Metric {

    private final String name;
    private final Map<String, String> tags;
    private final BigDecimal value;

    /**
     * Checks if the Metric object has any tags.
     *
     * @return true if the Metric object has tags, false otherwise.
     */
    public boolean hasTags() {
        return tags != null && !tags.isEmpty();
    }
}
