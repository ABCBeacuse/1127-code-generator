package com.lab.model;

import lombok.Data;

/**
 * MainTemplate.java.ftl 代码模板 的 数据模型
 */
@Data
public class MainTemplateModel {

    /**
     * 是否循环
     */
    private boolean loop;

    /**
     * 作者
     */
    private String author = "1127";

    /**
     * 输出提示
     */
    private String outputContext = "默认输出信息：";
}
