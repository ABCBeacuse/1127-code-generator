package com.lab.generator;

import com.lab.model.MainTemplateModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 动静结合的 代码生成器
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateModel model = new MainTemplateModel();
        model.setLoop(true);
        model.setAuthor("1127 研究室");
        model.setOutputContext("自定义输出：");
        doGenerator(model);
    }

    public static void doGenerator(Object model) throws TemplateException, IOException {
        // 项目运行 根目录 F:\code\code-generator
        String projectPath = System.getProperty("user.dir");

        // 1. 静态代码 生成, 直接复制所有的静态文件 到 指定目录
        String staticOriginPath = projectPath + File.separator + "demo-projects/acm-template";
        String staticTargetPath = projectPath;
        StaticGenerator.copyFilesWithHutool(staticOriginPath, staticTargetPath);

        // 2. 动态代码 生成, 使用生成的动态代码 去 覆盖 生成的静态代码 中的一些文件
        String dynamicOriginPath = projectPath + File.separator + "code-generator-basic/src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicTargetPath = projectPath + File.separator + "acm-template/src/com/lab/acm/MainTemplate.java";
        DynamicGenerator.doGenerator(dynamicOriginPath, dynamicTargetPath, (MainTemplateModel) model);
    }
}
