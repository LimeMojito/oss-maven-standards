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

package com.limemojito.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;
import java.util.Objects;

public class SimpleModel {

    private String key;
    private Integer attribute;
    private final Instant anotherAttribute;

    public SimpleModel(String key) {
        this(key, null, Instant.parse("2017-01-02T03:04:05.000006Z"));
    }


    @JsonCreator
    public SimpleModel(@JsonProperty("key") String key,
                       @JsonProperty("attribute") Integer attribute,
                       @JsonProperty("anotherAttribute") Instant anotherAttribute) {
        this.key = key;
        this.attribute = attribute;
        this.anotherAttribute = anotherAttribute;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getAttribute() {
        return attribute;
    }

    public void setAttribute(Integer attribute) {
        this.attribute = attribute;
    }

    public Instant getAnotherAttribute() {
        return anotherAttribute;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("key", key)
                .append("attribute", attribute)
                .append("anotherAttribute", anotherAttribute)
                .toString();
    }

    @Override
    public boolean equals(Object requestObject) {
        if (this == requestObject) {
            return true;
        }
        if (!(requestObject instanceof SimpleModel)) {
            return false;
        }
        SimpleModel that = (SimpleModel) requestObject;
        return Objects.equals(key, that.key) &&
                Objects.equals(attribute, that.attribute) &&
                Objects.equals(anotherAttribute, that.anotherAttribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, attribute, anotherAttribute);
    }
}
