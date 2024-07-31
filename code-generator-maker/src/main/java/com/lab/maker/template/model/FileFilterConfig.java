package com.lab.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * 文件过滤规则类
 */
@Data
@Builder
public class FileFilterConfig {

    /**
     * 过滤规则类型
     */
    private String range;

    /**
     * 具体的过滤规则 类别
     */
    private String rule;

    /**
     * 过滤规则内容
     */
    private String value;
}
