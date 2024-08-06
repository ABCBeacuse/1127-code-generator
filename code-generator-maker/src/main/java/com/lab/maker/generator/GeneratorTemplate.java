package com.lab.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lab.maker.generator.file.DynamicFileGenerator;
import com.lab.maker.meta.Meta;
import com.lab.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 代码生成流程 模板方法
 */
public abstract class GeneratorTemplate {


    public void doGenerator() throws TemplateException, IOException, InterruptedException {
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

        // 拷贝包含 ftl 代码模板 的项目 到 .source 目录
        String sourceDestPath = copyCodeTemplate(meta, generatorProjectPath);

        // 根据 maker 项目的 ftl 模板, 生成整个 “代码生成器” 的项目代码
        generatorProjectCode(meta, projectRoot, generatorProjectPath);

        // “代码生成器” 可运行 jar 包的生成
        generatorJarPackage(generatorProjectPath);

        // “代码生成器” 可运行脚本的生成
        generatorScript(meta, generatorProjectPath);

        // 生成简洁版的 “代码生成器”
        generatorSimpleDist(meta, generatorProjectPath, sourceDestPath);
    }

    protected void generatorSimpleDist(Meta meta, String generatorProjectPath, String sourceDestPath) {
        // 生成精简版的 代码生成器, 当中只包含 .source 代码模板文件夹 generator.sh generator.bat 脚本文件 以及 target 目录下的 包含依赖的 jar 包
        String distFilePath = generatorProjectPath + "-dist";
        FileUtil.mkdir(distFilePath);

        // 复制 .source 代码模板文件夹
        FileUtil.copy(sourceDestPath, distFilePath, false);

        // 复制 脚本文件
        FileUtil.copy(generatorProjectPath + File.separator + "generator", distFilePath, true);
        FileUtil.copy(generatorProjectPath + File.separator + "generator.bat", distFilePath, true);

        // 复制 jar 包
        String targetAbsolutePath = distFilePath + File.separator + "target/";
        FileUtil.mkdir(targetAbsolutePath);
        FileUtil.copy(generatorProjectPath + File.separator + "target/" + String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion()), targetAbsolutePath, true);
    }

    protected void generatorScript(Meta meta, String generatorProjectPath) {
        ScriptGenerator.doGenerator(generatorProjectPath + File.separator + "generator", String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion()));
    }

    protected void generatorJarPackage(String generatorProjectPath) throws IOException, InterruptedException {
        JarGenerator.doGenerator(new File(generatorProjectPath).getAbsolutePath());
    }

    protected void generatorProjectCode(Meta meta, String projectRoot, String generatorProjectPath) throws IOException, TemplateException {
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

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/pom.xml.ftl";
        outputPath = generatorProjectPath + "/pom.xml";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);

        TemplatePath = projectRoot + File.separator + "src/main/resources/templates/README.md.ftl";
        outputPath = generatorProjectPath + "/README.md";
        DynamicFileGenerator.doGenerator(TemplatePath, outputPath, meta);
    }

    protected String copyCodeTemplate(Meta meta, String generatorProjectPath) {
        // 复制 代码生成器 将要使用的 “项目模板文件” 到 指定路径 ".source"
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceDestPath = generatorProjectPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceDestPath, false);
        return sourceDestPath;
    }


}
