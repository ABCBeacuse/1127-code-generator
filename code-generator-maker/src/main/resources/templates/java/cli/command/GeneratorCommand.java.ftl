package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.file.FileGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "generator", mixinStandardHelpOptions = true)
@Data
public class GeneratorCommand implements Callable<Integer> {

    <#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    /**
    * ${modelInfo.groupName}
    */
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    @Command(name = "${modelInfo.groupKey}", mixinStandardHelpOptions = true, description = "${modelInfo.description}")
    @Data
    static class ${modelInfo.type}Command implements Callable<Integer> {
        <#list modelInfo.models as model>
        @Option(names = {<#if model.abbr??>"-${model.abbr}", </#if>"--${model.fieldName}"}, arity = "0..1", <#if model.description??>description = "${model.description}", </#if>interactive = true)
        private ${model.type} ${model.fieldName}<#if model.defaultValue??> = ${model.defaultValue?c} </#if>;
        </#list>

        @Override
        public Integer call() throws Exception {
            <#list modelInfo.models as model>
            ${modelInfo.groupKey}.${model.fieldName} = ${model.fieldName};
            </#list>
            return 0;
        }

    }
    <#else>
    @Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if>"--${modelInfo.fieldName}"}, arity = "0..1", <#if modelInfo.description??>description = "${modelInfo.description}", </#if>interactive = true)
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c} </#if>;
    </#if>
    </#list>

    @Override
    public Integer call() throws Exception {
    <#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
        <#if modelInfo.condition ??>
        if(${modelInfo.condition}) {
          CommandLine commandLine = new CommandLine(${modelInfo.type}Command.class);
          commandLine.execute(${modelInfo.allArgs});
        }
        <#else>
        CommandLine commandLine = new CommandLine(${modelInfo.type}Command.class);
        commandLine.execute(${modelInfo.allArgs});
        </#if>
    </#if>
    </#list>
        DataModel model = new DataModel();
        BeanUtil.copyProperties(this, model);
    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        model.${modelInfo.groupKey} = ${modelInfo.groupKey};
        </#if>
    </#list>
        FileGenerator.doGenerator(model);
        return 0;
    }
}
