package com.lab.maker.generator.file;

import com.lab.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 动静结合的 代码生成器
 */
public class FileGenerator {

    public static void doGenerator(Object model) throws TemplateException, IOException {
        // 项目运行 根目录 F:\code\code-generator
        String projectPath = System.getProperty("user.dir");

        // 1. 静态代码 生成, 直接复制所有的静态文件 到 指定目录
        String staticOriginPath = projectPath + File.separator + "demo-projects/acm-template";
        String staticTargetPath = projectPath;
        StaticFileGenerator.copyFilesWithHutool(staticOriginPath, staticTargetPath);

        // 2. 动态代码 生成, 使用生成的动态代码 去 覆盖 生成的静态代码 中的一些文件
        String dynamicOriginPath = projectPath + File.separator + "code-generator-basic/src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicTargetPath = projectPath + File.separator + "acm-template/src/com/lab/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerator(dynamicOriginPath, dynamicTargetPath, (DataModel) model);
    }
}
