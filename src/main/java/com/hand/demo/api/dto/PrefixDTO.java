package com.hand.demo.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PrefixDTO {
    private String bucketName;
    private String directory;
    private String contentType;
    private String storageUnit;
    private String storageSize;
    private String tenantAdminFlag;
}
