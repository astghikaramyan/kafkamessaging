package com.xyz.enterprise.kafka.eda.model.rest.v3;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ProduceRequestBody<K,V> {
    /**
     * Optional : set partitionId to target specific shard within a KafkaTopic
     */
    @JsonProperty("partition_id")
    private Integer partitionId;
    /**
     * Optional : additional metadata mapped to KafkaRecord
     */
    private List<ProduceRequestHeader> headers;
    /**
     * Optional when value is set : Defines KafkaRecord key dataType/value
     */
    private ProduceRequestData<K> key;
    /**
     * Optional when key is set : Defines KafkaRecord value dataType/value
     */
    private ProduceRequestData<V> value;
    /**
     * Optional : Set this when aiming to capture app-time of record creation
     */
    private String timestamp;

    public ProduceRequestBody() {}

    public Integer getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(final Integer partitionId) {
        this.partitionId = partitionId;
    }

    public List<ProduceRequestHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(final List<ProduceRequestHeader> headers) {
        this.headers = headers;
    }

    public ProduceRequestData<K> getKey() {
        return key;
    }

    public void setKey(final ProduceRequestData<K> key) {
        this.key = key;
    }

    public ProduceRequestData<V> getValue() {
        return value;
    }

    public void setValue(final ProduceRequestData<V> value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString( this );
    }

}
