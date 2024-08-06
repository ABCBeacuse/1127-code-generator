package com.lab.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lab.maker.meta.Meta;
import com.lab.maker.meta.enums.FileGenerateTypeEnums;
import com.lab.maker.meta.enums.FileTypeEnum;
import com.lab.maker.template.model.TemplateFileRetainRule;
import com.lab.maker.template.model.TemplateMakerConfig;
import com.lab.maker.template.model.TemplateMakerFilterConfig;
import com.lab.maker.template.model.TemplateMakerModelConfig;
import com.lab.maker.template.utils.TemplateMakerUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基本的模板制作流程
 */
public class TemplateMaker {

    /**
     * makeTemplate 的重载方法
     *
     * @param makerConfig 模板制作的配置对象
     * @return 工作空间 id
     */
    public static Long makeTemplate(TemplateMakerConfig makerConfig) {
        // 工作空间 id
        Long id = makerConfig.getId();
        // 项目 元信息
        Meta meta = makerConfig.getMeta();
        // 项目源码的路径
        String originProjectPath = makerConfig.getOriginProjectPath();
        // 文件信息 配置
        TemplateMakerFilterConfig filterConfig = makerConfig.getFilterConfig();
        // 模型信息 配置
        TemplateMakerModelConfig modelConfig = makerConfig.getModelConfig();

        TemplateFileRetainRule repeatRule = makerConfig.getRepeatRule();

        return makeTemplate(meta, id, originProjectPath, filterConfig, modelConfig, repeatRule);
    }

    /**
     * 分步制作 代码模板
     *
     * @param newMeta           基本元信息
     * @param id                工作空间 id
     * @param originProjectPath 从哪个路径下 复制 代码源码 到 temp 工作空间
     * @param filterConfig      文件过滤配置
     * @param modelConfig       最新的需要向 ftl 模板文件 添加的 modelInfo 信息, 包含 想使用 model 替换的 String 字符串内容
     * @return
     */
    public static Long makeTemplate(Meta newMeta, Long id, String originProjectPath, TemplateMakerFilterConfig filterConfig, TemplateMakerModelConfig modelConfig, TemplateFileRetainRule repeatRule) {
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // F:\code\code-generator
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            // 只有第一次会复制 源码文件 到目标路径下
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 输入文件信息(找到指定目录下, 第一个文件夹的名称)
        String fileRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream().filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();

        // 获取 模型数据信息
        List<Meta.ModelConfig.ModelInfo> modelInfos = getModelInfos(modelConfig);

        List<Meta.FileConfig.FileInfo> fileInfos = getFileInfos(filterConfig, modelConfig, fileRootPath);

        // 更新 meta.json 文件内容, 如果已有 meta.json 文件, 则在此基础上 额外添加 model 相关信息
        String metaOutPutPath = templatePath + File.separator + "meta.json";
        if (FileUtil.exist(metaOutPutPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutPutPath), Meta.class);

            // 新旧 Meta 合并
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            List<Meta.FileConfig.FileInfo> files = newMeta.getFileConfig().getFiles();
            files.addAll(fileInfos);
            List<Meta.ModelConfig.ModelInfo> models = newMeta.getModelConfig().getModels();
            models.addAll(modelInfos);
            // 去重
            newMeta.getFileConfig().setFiles(distinctFiles(files));
            newMeta.getModelConfig().setModels(distinctModels(models));
        } else {
            // 不存在 meta.json 文件, 则使用传入的 Meta 对象, 将信息封装到该对象中即可
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(fileRootPath.replace("\\", "/"));
            List<Meta.FileConfig.FileInfo> files = new ArrayList<>();
            fileConfig.setFiles(files);
            files.addAll(fileInfos);

            Meta.ModelConfig mConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(mConfig);
            List<Meta.ModelConfig.ModelInfo> models = new ArrayList<>();
            mConfig.setModels(models);
            models.addAll(modelInfos);
        }

        // 对文件信息 未分组部分 与 分组部分 重合 的文件进行过滤
        if (repeatRule != null) {
            if (repeatRule.isRetainFilesInGroup()) {
                List<Meta.FileConfig.FileInfo> files = newMeta.getFileConfig().getFiles();
                // 分组内的文件信息优先
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.filterRepeatFileInfo(files));
            }
        }

        // 写到 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPutPath);
        return id;
    }

    private static List<Meta.FileConfig.FileInfo> getFileInfos(TemplateMakerFilterConfig filterConfig, TemplateMakerModelConfig modelConfig, String fileRootPath) {
        List<Meta.FileConfig.FileInfo> fileInfos = new ArrayList<>();
        if (filterConfig == null) {
            return fileInfos;
        }

        // 允许指定多个文件 或者 文件目录 的过滤配置信息, 只会针对这些指定目录下的文件 并且满足过滤配置信息的 file 文件 生成 ftl 模板
        List<TemplateMakerFilterConfig.FilterConfig> fileFilterConfig = filterConfig.getFiles();
        for (TemplateMakerFilterConfig.FilterConfig config : fileFilterConfig) {
            String fileInputPath = config.getPath();
            String inputFileAbsolutePath = fileRootPath + File.separator + fileInputPath;
            // 需要进行 ftl 模板生成的 file 文件 。同时也遍历了输入路径下 所有的子文件信息（包含输入路径 以及 该输入路径的子目录）, 针对这些文件也进行了 filter 过滤器过滤
            List<File> attendFtlFiles = FileFilter.doFilter(config.getFilters(), inputFileAbsolutePath);

            // 过滤掉已经生成的 ftl 文件
            attendFtlFiles = attendFtlFiles.stream().filter(file -> !file.getName().endsWith(".ftl")).collect(Collectors.toList());
            for (File file : attendFtlFiles) {
                Meta.FileConfig.FileInfo fileInfo = makeSingleFileTemplate(modelConfig, fileRootPath, file, config);
                fileInfos.add(fileInfo);
            }
        }

        // 将在一次操作中的所有的文件 归类为一组 groupKey 相同
        TemplateMakerFilterConfig.GroupConfig groupConfig = filterConfig.getGroupConfig();
        if (groupConfig != null) {
            // 说明要将此次操作的文件分组
            Meta.FileConfig.FileInfo groupInfo = new Meta.FileConfig.FileInfo();
            groupInfo.setGroupKey(groupConfig.getGroupKey());
            groupInfo.setGroupName(groupConfig.getGroupName());
            groupInfo.setCondition(groupConfig.getCondition());
            groupInfo.setType(FileTypeEnum.GROUP.getValue());

            // 文件全部放到一个分组内
            groupInfo.setFiles(fileInfos);
            fileInfos = new ArrayList<>();
            fileInfos.add(groupInfo);
        }
        return fileInfos;
    }

    private static List<Meta.ModelConfig.ModelInfo> getModelInfos(TemplateMakerModelConfig modelConfig) {
        List<Meta.ModelConfig.ModelInfo> modelInfos = new ArrayList<>();

        if (modelConfig == null) {
            return modelInfos;
        }

        // Model 数据模型信息处理
        List<TemplateMakerModelConfig.ModelConfig> modelsList = modelConfig.getModels();

        List<Meta.ModelConfig.ModelInfo> infos = modelsList.stream().map(modelInfo -> {
            Meta.ModelConfig.ModelInfo info = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelInfo, info);
            return info;
        }).collect(Collectors.toList());
        TemplateMakerModelConfig.GroupConfig modelGroupConfig = modelConfig.getGroupConfig();
        if (modelGroupConfig != null) {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, modelInfo);

            modelInfo.setModels(infos);
            modelInfos.add(modelInfo);
        } else {
            modelInfos.addAll(infos);
        }
        return modelInfos;
    }

    private static Meta.FileConfig.FileInfo makeSingleFileTemplate(TemplateMakerModelConfig modelConfig, String fileRootPath, File inputFile, TemplateMakerFilterConfig.FilterConfig fileInfoConfig) {
        if (modelConfig == null) {
            return new Meta.FileConfig.FileInfo();
        }
        String fileInputAbsolutePath = inputFile.getAbsolutePath();
        // 存储到 FileInfo 的 相对路径
        String fileInputPath = fileInputAbsolutePath.replace(fileRootPath + "\\", "").replace("\\", "/");
        String fileOutPutPath = fileInputPath + ".ftl";
        String fileOutputAbsolutePath = inputFile.getAbsolutePath() + ".ftl";

        String fileContent = null;

        boolean hasTemplate = FileUtil.exist(fileOutputAbsolutePath);
        // 如果已有模板文件, 就在这个已有的模板文件上进行修改
        if (hasTemplate) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            // 没有模板文件, 说明这是第一次操作。 ftl 模板文件还未生成
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }
        TemplateMakerModelConfig.GroupConfig groupConfig = modelConfig.getGroupConfig();
        List<TemplateMakerModelConfig.ModelConfig> models = modelConfig.getModels();
        String newFileContent = fileContent;
        // 根据 model 信息, 使用 字符串替换方法 fileContent 中的内容
        if (groupConfig != null) {
            for (TemplateMakerModelConfig.ModelConfig model : models) {
                newFileContent = StrUtil.replace(newFileContent, model.getReplaceText(), String.format("${%s.%s}", groupConfig.getGroupKey(), model.getFieldName()));
            }
        } else {
            for (TemplateMakerModelConfig.ModelConfig model : models) {
                newFileContent = StrUtil.replace(newFileContent, model.getReplaceText(), String.format("${%s}", model.getFieldName()));
            }
        }
        FileGenerateTypeEnums fileType = FileGenerateTypeEnums.DYNAMIC;
        // 有一些文件, 当中没有需要 修改的部分, 所以不应该产生 ftl 模板, 就算产生, ftl 模板中的内容 和 原文件 的内容也是一致的
        boolean contentEqualBefore = newFileContent.equals(fileContent);
        if (!hasTemplate && contentEqualBefore) {
            // 没有 ftl 模板的前提下
            // 修改后的文件内容 与 原文件一致, 所以应该是 静态文件, 直接复制即可, 不需要产生 ftl 代码模板文件
            fileOutPutPath = fileInputPath;
            fileType = FileGenerateTypeEnums.STATIC;
        } else if (!contentEqualBefore) {
            // 替换完毕后, 将内容重新写到 ftl 模板中
            // 有 .ftl 模板, 则只更新 ftl 模板的内容, fileType 仍然为初始的 dynamic
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        // 追加配置参数
        Meta.FileConfig.FileInfo file = new Meta.FileConfig.FileInfo();
        file.setInputPath(fileOutPutPath);
        file.setOutputPath(fileInputPath);
        file.setCondition(fileInfoConfig.getCondition());
        file.setType(FileTypeEnum.FILE.getValue());
        file.setGenerateType(fileType.getValue());
        return file;
    }

    /**
     * 根据 inputPath 去重 files 中的 重复 fileInfo 信息
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 1. 先将 分组和普通文件 分开
        Map<String, List<Meta.FileConfig.FileInfo>> tempFileConfigFiles = fileInfoList.stream().filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));
        // {"a": [{groupKey:"a", files:[{}, {}]}, {groupKey:"a", files:[{}, {}]} ]} => {"a": [{groupKey:"a", files:[{}, {}, {}]}]}
        // 2. 同组内文件合并
        Map<String, Meta.FileConfig.FileInfo> groupList = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : tempFileConfigFiles.entrySet()) {
            List<Meta.FileConfig.FileInfo> infoList = entry.getValue();
            ArrayList<Meta.FileConfig.FileInfo> distinctFileInfos = new ArrayList<>(infoList.stream().flatMap(fileInfo -> fileInfo.getFiles().stream()).collect(
                    Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
            ).values());
            // 最新的 group 配置, 只是 groupKey 与之前相同
            Meta.FileConfig.FileInfo latest = CollUtil.getLast(infoList);
            latest.setFiles(distinctFileInfos);
            groupList.put(entry.getKey(), latest);
        }
        // 3. 将文件分组添加到 结果列表
        List<Meta.FileConfig.FileInfo> resultList = new ArrayList<>(groupList.values());
        // 4. 对普通文件去重, 将未分组的文件添加到文件列表
        resultList.addAll(new ArrayList<>(fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(
                        // 借助 Map<k, v> 来去重, k 相同时, 新值 覆盖 旧值
                        Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o, (e, r) -> r)
                ).values()));

        return resultList;
    }

    /**
     * 根据 fieldName 去重 models 中的 重复 modelInfo 信息
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 1. 先处理 有分组的
        Map<String, List<Meta.ModelConfig.ModelInfo>> tempMapList = modelInfoList.stream().filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));
        List<Meta.ModelConfig.ModelInfo> result = new ArrayList<>();
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : tempMapList.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> modelList = entry.getValue();
            List<Meta.ModelConfig.ModelInfo> distinctModelInfo = new ArrayList<>(modelList.stream().flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());
            Meta.ModelConfig.ModelInfo last = CollUtil.getLast(modelList);
            last.setModels(distinctModelInfo);
            result.add(last);
        }
        // 2. 再处理没有分组的
        Collection<Meta.ModelConfig.ModelInfo> noGroupDistinct = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values();
        result.addAll(noGroupDistinct);
        return result;
    }

    /**
     private static void old() {
     // 1. 获取需要 挖坑 的源代码路径 (源代码项目路径)
     String sourceProjectPath = projectPath + File.separator + "demo-projects/acm-template";
     sourceProjectPath = sourceProjectPath.replace("\\", "/");

     // 工作空间隔离 (项目根目录的 .temp 文件夹下)
     String workSpacePath = projectPath + File.separator + ".temp";
     long tempProjectId = IdUtil.getSnowflakeNextId();
     String tempProjectPath = workSpacePath + File.separator + tempProjectId;
     if (!FileUtil.exist(tempProjectPath)) {
     FileUtil.mkdir(tempProjectPath);
     // 只有第一次运行时, 才会创建, 之后运行 是 在第一次结果的基础上添加修改内容
     FileUtil.copy(sourceProjectPath, tempProjectPath, false);
     }

     String tempRootPath = tempProjectPath + File.separator + FileUtil.getLastPathEle(Paths.get(sourceProjectPath)).toString();
     tempRootPath = tempRootPath.replace("\\", "/");
     // 2. meta.json 中的信息配置, fileConfig 中 files 的 inputPath, 即生成 ftl 模板的基本 java 代码路径
     String templateBaseCodePath = tempRootPath + "/src/com/lab/acm/MainTemplate.java";

     // 3. meta.json 中的信息配置, fileConfig 中 files 的 outputPath, 即 生成的 ftl 模板路径
     String templatePath = templateBaseCodePath + ".ftl";

     // 4. 输入模型参数信息
     Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
     modelInfo.setFieldName("outputText");
     modelInfo.setType("String");
     modelInfo.setDefaultValue("sum = ");

     String templateBaseCodePathStr;
     String templateStr;

     if (FileUtil.exist(templatePath)) {
     // 如果 ftl 模板已经存在, 则直接在 其基础上 进行补充
     templateBaseCodePathStr = FileUtil.readUtf8String(templatePath);
     templateStr = templateBaseCodePathStr.replace("MainTemplate", String.format("${%s}", modelInfo.getFieldName()));
     FileUtil.writeUtf8String(templateStr, new File(templatePath));
     } else {
     // 5. 字符串替换 templateBaseCodePath 中的指定部分 为 ${FieldName}
     templateBaseCodePathStr = FileUtil.readUtf8String(templateBaseCodePath);
     templateStr = templateBaseCodePathStr.replace("Sum: ", String.format("${%s}", modelInfo.getFieldName()));
     // 创建 templatePath 文件
     FileUtil.touch(templatePath);
     FileUtil.writeUtf8String(templateStr, new File(templatePath));
     }

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
     FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(meta), tempRootPath + File.separator + "meta.json");
     }
     */
}
