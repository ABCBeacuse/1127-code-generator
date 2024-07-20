package com.lab.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import com.lab.maker.meta.Meta;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 结合 FreeMarker 的 动态代码生成
 */
public class DynamicFileGenerator {

    public static void doGenerator(String templatePath, String outputFilePath, Meta dataModel) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setDefaultEncoding("UTF-8");

        // 获取 FTL 模板的所在位置, 模板文件 的 父目录 作为 Configuration 对象设置的 模板文件夹
        String templateDirectory = new File(templatePath).getParent();
        configuration.setDirectoryForTemplateLoading(new File(templateDirectory));

        // 获取模板信息
        String templateName = new File(templatePath).getName();
        Template template = configuration.getTemplate(templateName);

        // 判断目标文件是否存在
        if(!FileUtil.exist(outputFilePath)) {
            // 如果模板文件不存在, 则创建该文件
            FileUtil.touch(outputFilePath);
        }

        // 将 model 数据 和 template 模板结合, 生成代码 到 指定文件
        Writer target = new FileWriter(outputFilePath);
        template.process(dataModel, target);

        // 关闭文件
        target.close();
    }

}
