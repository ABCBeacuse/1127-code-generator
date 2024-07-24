package ${basePackage}.model;

import lombok.Data;

<#macro propertyPart retract modelInfo>
${retract}<#if modelInfo.description??>
${retract}/**
${retract}* ${modelInfo.description}
${retract}*/
${retract}</#if>
${retract}public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c} </#if>;
</#macro>

/**
* 数据模型
*/
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>

    <#--  有分组  -->
    <#if modelInfo.groupKey??>
    /**
    * ${modelInfo.groupName}
    */
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    /**
    * ${modelInfo.description}
    */
    @Data
    public static class ${modelInfo.type} {
        <#list modelInfo.models as model>
            <@propertyPart retract="        " modelInfo=model />
        </#list>
    }

    <#else>
        <@propertyPart retract="    " modelInfo=modelInfo />
    </#if>
</#list>
}
