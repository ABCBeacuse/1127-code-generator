package com.lab.maker.template.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码文件 是否需要生成 ftl 模板的 过滤规则
 */
@Data
public class TemplateMakerFilterConfig {

    private List<FilterConfig> files;

    private GroupConfig groupConfig;

    @Data
    @NoArgsConstructor
    public static class FilterConfig {
        private String path;

        private String condition;

        private List<FileFilterConfig> filters;
    }

    @Data
    @NoArgsConstructor
    public static class GroupConfig {
        private String groupKey;
        private String groupName;
        private String condition;
    }

}
