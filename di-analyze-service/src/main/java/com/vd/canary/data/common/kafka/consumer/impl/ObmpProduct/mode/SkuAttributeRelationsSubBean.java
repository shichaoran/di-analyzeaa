package com.vd.canary.data.common.kafka.consumer.impl.ObmpProduct.mode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SkuAttributeRelationsSubBean {
    private String attributeValueId;
    private String attributeValueName;
}
