package com.xyz.enterprise.kafka.eda.model.rest.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProduceRequestData<T> {
    private String type;
    private T data;

    public ProduceRequestData() {}

    public ProduceRequestData(final ProduceRequestDataType type, final T data) {
        this.type = type.name();
        this.data = data;
    }

    /**
     * Set data type to a value allowed CCloud allows
     * @param type either BINARY, JSON or STRING
     */
    public void setType(String type) {
        try {
            final ProduceRequestDataType typeEnum = ProduceRequestDataType.valueOf(type);
            this.type = typeEnum.name();
        } catch (IllegalArgumentException ignored) {}
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public T getData() {
        return data;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }
}
