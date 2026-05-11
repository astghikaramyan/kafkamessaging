package com.xyz.enterprise.kafka.eda.model.rest.v3;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Base64;
import java.util.Objects;

public class ProduceRequestHeader {
    private final String key;
    private final String value;

    public ProduceRequestHeader(final String key, final String value) {
        Objects.requireNonNull(key, "Null header keys are not permitted");
        this.key = key;
        this.value = Base64.getEncoder().encodeToString(value.getBytes());
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }
}
