package com.lab.generator;

import com.lab.model.MainTemplateModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 结合 FreeMarker 的 动态代码生成
 */
public class DynamicGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        // 获取项目的根路径 F:\code\code-generator
        String projectPath = System.getProperty("user.dir") + File.separator + "code-generator-basic";
        String templatePath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";

        String outputPath = projectPath + File.separator + "MainTemplate.java";

        // 创建符合 MainTemplate.java.ftl 的数据对象
        MainTemplateModel model = new MainTemplateModel();
        model.setLoop(false);
        model.setAuthor("1127 研究室");
        model.setOutputContext("自定义输出信息：");
        doGenerator(templatePath, outputPath, model);

    }

    public static void doGenerator(String templatePath, String outputFilePath, MainTemplateModel dataModel) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setDefaultEncoding("UTF-8");

        // 获取 FTL 模板的所在位置, 模板文件 的 父目录 作为 Configuration 对象设置的 模板文件夹
        String templateDirectory = new File(templatePath).getParent();
        configuration.setDirectoryForTemplateLoading(new File(templateDirectory));

        // 获取模板信息
        String templateName = new File(templatePath).getName();
        Template template = configuration.getTemplate(templateName);
        // 将 model 数据 和 template 模板结合, 生成代码 到 指定目录
        FileWriter target = new FileWriter(outputFilePath);
        template.process(dataModel, target);
    }

}
