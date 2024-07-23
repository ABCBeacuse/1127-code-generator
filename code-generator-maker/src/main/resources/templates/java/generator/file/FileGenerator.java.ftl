package ${basePackage}.generator.file;

import freemarker.template.TemplateException;

import ${basePackage}.model.DataModel;
import java.io.File;
import java.io.IOException;

/**
 * 动静结合的 代码生成器
 */
public class FileGenerator {

    public static void doGenerator(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
<#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
</#list>
<#list fileConfig.files as fileInfo>
    <#if fileInfo.condition??>
        if(${fileInfo.condition}) {
            inputPath = inputRootPath + File.separator + "${fileInfo.inputPath}";
            outputPath = outputRootPath + File.separator + "${fileInfo.outputPath}";
        <#if fileInfo.generateType == "static">
            StaticFileGenerator.copyFilesWithHutool(inputPath, outputPath);
        <#else>
            DynamicFileGenerator.doGenerator(inputPath, outputPath, model);
        </#if>
        }
    <#else>
        inputPath = inputRootPath + File.separator + "${fileInfo.inputPath}";
        outputPath = outputRootPath + File.separator + "${fileInfo.outputPath}";
        <#if fileInfo.generateType == "static">
        StaticFileGenerator.copyFilesWithHutool(inputPath, outputPath);
        <#else>
        DynamicFileGenerator.doGenerator(inputPath, outputPath, model);
        </#if>
    </#if>
</#list>
    }
}
