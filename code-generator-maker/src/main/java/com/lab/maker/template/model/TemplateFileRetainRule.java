package com.lab.maker.template.model;

import lombok.Data;

/**
 * 文件信息保留规则
 */
@Data
public class TemplateFileRetainRule {

    /**
     * 默认如果 分组内 和 分组外 存在相同的文件信息, 则优先保留 group 分组内的文件信息
     */
    private boolean retainFilesInGroup = true;

}
