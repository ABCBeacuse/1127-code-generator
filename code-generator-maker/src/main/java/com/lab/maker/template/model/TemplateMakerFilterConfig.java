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

    @Data
    @NoArgsConstructor
    public static class FilterConfig {
        private String path;

        private List<FileFilterConfig> filters;
    }

}
