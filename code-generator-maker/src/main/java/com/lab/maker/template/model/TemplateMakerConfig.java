package com.lab.maker.template.model;

import com.lab.maker.meta.Meta;
import lombok.Data;

/**
 * 模板生成方法 需要传入的参数
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta;

    private String originProjectPath;

    private TemplateMakerFilterConfig filterConfig;

    private TemplateMakerModelConfig modelConfig;
}
