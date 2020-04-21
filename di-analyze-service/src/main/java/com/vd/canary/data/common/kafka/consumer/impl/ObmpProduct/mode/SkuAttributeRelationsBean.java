package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct.mode;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SkuAttributeRelationsBean {
    private String attributeType;
    private String attributeId;
    private String attributeName;
    private SkuAttributeRelationsSubBean attributeValue;
}
