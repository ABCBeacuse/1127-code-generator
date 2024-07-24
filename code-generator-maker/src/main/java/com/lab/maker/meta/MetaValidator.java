package com.lab.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lab.maker.meta.enums.FieldTypeEnums;
import com.lab.maker.meta.enums.FileGenerateTypeEnums;
import com.lab.maker.meta.enums.FileTypeEnum;

import java.nio.file.Paths;
import java.util.List;

/**
 * 元信息 校验规则
 */
public class MetaValidator {

    public static void doValidator(Meta meta) {
        validatorAndFillMetaRoot(meta);
        validatorAndFillFileConfig(meta);
        validatorAndFillModelConfig(meta);
    }

    private static void validatorAndFillModelConfig(Meta meta) {
        // modelConfig
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        List<Meta.ModelConfig.ModelInfo> models = modelConfig.getModels();
        if (CollUtil.isNotEmpty(models)) {
            for (Meta.ModelConfig.ModelInfo model : models) {
                String fieldName = model.getFieldName();
                if (StrUtil.isBlank(fieldName)) {
                    throw new MetaException("未填写 fieldName");
                }
                String modelType = model.getType();
                if (StrUtil.isBlank(modelType)) {
                    // 字段的默认类型为 String 类型
                    model.setType(FieldTypeEnums.STRING.getType());
                }
            }
        }
    }

    private static void validatorAndFillFileConfig(Meta meta) {
        // fileConfig 配置信息校验
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        // sourceRootPath 必填
        String sourceRootPath = fileConfig.getSourceRootPath();
        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaException("未填写 sourceRootPath");
        }
        // inputRootPath
        String inputRootPath = fileConfig.getInputRootPath();
        String defaultInputRootPath = ".source/" + FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
        if (StrUtil.isEmpty(inputRootPath)) {
            fileConfig.setInputRootPath(defaultInputRootPath);
        }
        // outputRootPath 默认为 当前 代码生成器 的 generated 文件夹下
        String outputRootPath = StrUtil.emptyToDefault(fileConfig.getOutputRootPath(), "generated");
        fileConfig.setOutputRootPath(outputRootPath);

        // type
        String type = StrUtil.emptyToDefault(fileConfig.getType(), FileTypeEnum.FILE.getValue());
        fileConfig.setType(type);

        // files
        List<Meta.FileConfig.FileInfo> fileInfos = fileConfig.getFiles();
        if (CollUtil.isEmpty(fileInfos)) {
            return;
        }
        for (Meta.FileConfig.FileInfo fileInfo : fileInfos) {
            if (FileTypeEnum.GROUP.getValue().equals(fileInfo.getType())) {
                // 暂时未写 文件组 的校验逻辑
                continue;
            }
            String inputPath = fileInfo.getInputPath();
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("未填写 inputPath");
            }
            String outputPath = fileInfo.getOutputPath();
            if (StrUtil.isBlank(outputPath)) {
                int index = inputPath.lastIndexOf(".ftl");
                outputPath = inputPath;
                if (index != -1) {
                    outputPath = inputPath.substring(0, index);
                }
                fileInfo.setOutputPath(outputPath);
            }
            String fileType = fileInfo.getType();
            if (StrUtil.isBlank(fileType)) {
                if (StrUtil.isEmpty(FileUtil.getSuffix(inputPath))) {
                    fileInfo.setType(FileTypeEnum.DIR.getValue());
                } else {
                    fileInfo.setType(FileTypeEnum.FILE.getValue());
                }
            }
            String generateType = fileInfo.getGenerateType();
            if (StrUtil.isBlank(generateType)) {
                if (inputPath.endsWith(".ftl")) {
                    fileInfo.setGenerateType(FileGenerateTypeEnums.DYNAMIC.getValue());
                } else {
                    fileInfo.setGenerateType(FileGenerateTypeEnums.STATIC.getValue());
                }
            }
        }
    }

    private static void validatorAndFillMetaRoot(Meta meta) {
        // 项目基本信息校验
        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        meta.setName(name);

        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的模板代码生成器");
        meta.setDescription(description);

        if (StrUtil.isBlank(meta.getBasePackage())) {
            throw new MetaException("请完善 package 包名相关配置");
        }

        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0");
        meta.setVersion(version);

        String author = StrUtil.emptyToDefault(meta.getAuthor(), "lighthouse");
        meta.setAuthor(author);

        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());
        meta.setCreateTime(createTime);
    }
}
