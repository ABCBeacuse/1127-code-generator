package com.lab.maker.model;

import lombok.Data;

/**
 * 代码模板 的 数据模型
 */
@Data
public class DataModel {

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
