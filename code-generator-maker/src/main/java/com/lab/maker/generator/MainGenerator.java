package com.lab.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lab.maker.generator.file.DynamicFileGenerator;
import com.lab.maker.meta.Meta;
import com.lab.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMetaInstance();

        /**
         * 将 DataModel.java 生成 到 code-generator-maker/generator/ 下对应的 package 下
         */
        // code-generator-maker 的项目根路径
        String projectRoot = System.getProperty("user.dir") + File.separator + "code-generator-maker";
        String generatorProjectPath = projectRoot + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(generatorProjectPath)) {
            FileUtil.mkdir(generatorProjectPath);
        }

        String generatorBasePackage = StrUtil.join("/", StrUtil.split(meta.getBasePackage(), "."));
        String outputBaseJavaPackagePath = generatorProjectPath + File.separator + "src/main/java/" + generatorBasePackage;

        String dataModelTemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/model/DataModel.java.ftl";
        String outputDataModelPath = outputBaseJavaPackagePath + "/model/DataModel.java";
        DynamicFileGenerator.doGenerator(dataModelTemplatePath, outputDataModelPath, meta);

        String TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/cli/command/ConfigCommand.java.ftl";
        String outputPath = outputBaseJavaPackagePath + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/cli/command/GeneratorCommand.java.ftl";
        outputPath = outputBaseJavaPackagePath + "/cli/command/GeneratorCommand.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/cli/command/ListCommand.java.ftl";
        outputPath = outputBaseJavaPackagePath + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/cli/CommonExecutor.java.ftl";
        outputPath = outputBaseJavaPackagePath + "/cli/CommonExecutor.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/Main.java.ftl";
        outputPath = outputBaseJavaPackagePath + "/Main.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/generator/file/DynamicFileGenerator.java.ftl";
        outputPath = outputBaseJavaPackagePath + "/generator/file/DynamicFileGenerator.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/generator/file/FileGenerator.java.ftl";
        outputPath = outputBaseJavaPackagePath + "/generator/file/FileGenerator.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/generator/file/StaticFileGenerator.java.ftl";
        outputPath = outputBaseJavaPackagePath + "/generator/file/StaticFileGenerator.java";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);
    }
}
