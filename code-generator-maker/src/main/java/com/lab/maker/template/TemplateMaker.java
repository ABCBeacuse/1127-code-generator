package com.lab.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.lab.maker.meta.Meta;
import com.lab.maker.meta.enums.FileGenerateTypeEnums;
import com.lab.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 基本的模板制作流程
 */
public class TemplateMaker {
    public static void main(String[] args) {
        // 输入项目基本信息
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";

        // F:\code\code-generator
        String projectPath = System.getProperty("user.dir");
        // 1. 获取需要 挖坑 的源代码路径 (源代码项目路径)
        String sourceProjectPath = projectPath + File.separator + "demo-projects/acm-template";

        // 2. meta.json 中的信息配置, fileConfig 中 files 的 inputPath, 即生成 ftl 模板的基本 java 代码路径
        String templateBaseCodePath = sourceProjectPath + File.separator + "src/com/lab/acm/MainTemplate.java";

        // 3. meta.json 中的信息配置, fileConfig 中 files 的 outputPath, 即 生成的 ftl 模板路径
        String templatePath = templateBaseCodePath + ".ftl";

        // 4. 输入模型参数信息
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum = ");

        // 5. 字符串替换 templateBaseCodePath 中的指定部分 为 ${FieldName}
        String templateBaseCodePathStr = FileUtil.readUtf8String(templateBaseCodePath);
        String templateStr = templateBaseCodePathStr.replace("Sum: ", String.format("${%s}", modelInfo.getFieldName()));
        if(!FileUtil.exist(templatePath)) {
            FileUtil.touch(templatePath);
        }
        FileUtil.writeUtf8String(templateStr, new File(templatePath));

        // 6. 封装 meta.json 文件, 将 meta 对象 直接转换为 Json 字符串
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        // 构建 meta.json 中 modelConfig 的结构
        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        meta.setModelConfig(modelConfig);
        List<Meta.ModelConfig.ModelInfo> models = new ArrayList<>();
        modelConfig.setModels(models);

        models.add(modelInfo);

        // 构建 meta.json 中 fileConfig 的结构
        Meta.FileConfig fileConfig = new Meta.FileConfig();
        meta.setFileConfig(fileConfig);
        fileConfig.setSourceRootPath(sourceProjectPath);
        List<Meta.FileConfig.FileInfo> files = new ArrayList<>();
        fileConfig.setFiles(files);

        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(templateBaseCodePath);
        fileInfo.setOutputPath(templatePath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnums.DYNAMIC.getValue());
        files.add(fileInfo);

        // 7. 输出 meta.json 文件, toJsonPrettyStr 可以输出 有格式的 JSON 字符串
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), sourceProjectPath + File.separator + "meta.json");
    }
}
