# ${name}

> ${description}
>
> 作者：${author}
>
> 基于 [1127 研究室 lighthouse] 的 [代码生成器项目](https://github.com/ABCBeacuse/1127-code-generator) 制作，感谢您的使用！

可以通过命令行交互式输入的方式动态生成想要的项目代码

## 使用说明

执行项目根目录下的脚本文件：

> generator <命令> <选项参数>

## 参数说明

<#list modelConfig.models as modelInfo>
<#if modelInfo.groupKey??>
<#list modelInfo.models as model>
${model ? index + 1}) ${model.fieldName}
类型：${model.type}

描述：${model.description}

默认值：${model.defaultValue?c}

<#if model.abbr??>缩写：${model.abbr}</#if>
</#list>
<#else>
类型：${modelInfo.type}

描述：${modelInfo.description}

默认值：${modelInfo.defaultValue?c}

<#if modelInfo.abbr??>缩写：${modelInfo.abbr}</#if>
</#if>



</#list>
