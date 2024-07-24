package ${basePackage}.generator.file;

import freemarker.template.TemplateException;

import ${basePackage}.model.DataModel;
import java.io.File;
import java.io.IOException;

<#macro generator retract fileInfo>
${retract}inputPath = inputRootPath + File.separator + "${fileInfo.inputPath}";
${retract}outputPath = outputRootPath + File.separator + "${fileInfo.outputPath}";
<#if fileInfo.generateType == "static">
${retract}StaticFileGenerator.copyFilesWithHutool(inputPath, outputPath);
<#else>
${retract}DynamicFileGenerator.doGenerator(inputPath, outputPath, model);
</#if>
</#macro>

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
    <#if fileInfo.groupKey??>
        <#if fileInfo.condition??>
        if(${fileInfo.condition}) {
            <#list fileInfo.files as file>
                <@generator retract="            " fileInfo = file />
            </#list>
        }
        <#else>
            <#list fileInfo.files as file>
                <@generator retract="        " fileInfo = file />
            </#list>
        </#if>
    <#else>
    <#if fileInfo.condition??>
        if(${fileInfo.condition}) {
        <@generator retract="            " fileInfo = fileInfo />
        }
    <#else>
        <@generator retract="        " fileInfo = fileInfo />
    </#if>
    </#if>
</#list>
    }
}
