package ${basePackage}.maker.generator.file;

import ${basePackage}.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 动静结合的 代码生成器
 */
public class FileGenerator {

    public static void doGenerator(Object model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
<#list fileConfig.files as fileInfo>
        inputPath = inputRootPath + File.separator + "${fileInfo.inputPath}";
        outputPath = outputRootPath + File.separator + "${fileInfo.outputPath}";
    <#if fileInfo.generateType == "static">
        StaticFileGenerator.copyFilesWithHutool(inputPath, outputPath);
    <#else>
        DynamicFileGenerator.doGenerator(inputPath, outputPath, (DataModel) model);
    </#if>
</#list>
    }
}
