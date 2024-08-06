package com.lab.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model 数据模型的分组配置
 */
@Data
public class TemplateMakerModelConfig {

    private List<ModelConfig> models;

    private GroupConfig groupConfig;

    @NoArgsConstructor
    @Data
    public static class ModelConfig {
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;

        // 用于替换哪些文本
        private String replaceText;
    }

    @NoArgsConstructor
    @Data
    public static class GroupConfig {
        private String groupKey;
        private String groupName;
        private String condition;
        private String description;
        private String type;
    }

}
