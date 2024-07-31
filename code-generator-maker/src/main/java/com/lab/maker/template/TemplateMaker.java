package com.lab.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lab.maker.meta.Meta;
import com.lab.maker.meta.enums.FieldTypeEnums;
import com.lab.maker.meta.enums.FileGenerateTypeEnums;
import com.lab.maker.meta.enums.FileTypeEnum;
import com.lab.maker.template.enums.FileFilterRangeEnum;
import com.lab.maker.template.enums.FileFilterRuleEnum;
import com.lab.maker.template.model.FileFilterConfig;
import com.lab.maker.template.model.TemplateMakerFilterConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基本的模板制作流程
 */
public class TemplateMaker {

    /**
     * 分步制作 代码模板
     *
     * @param newMeta           基本元信息
     * @param id                工作空间 id
     * @param originProjectPath 从哪个路径下 复制 代码源码 到 temp 工作空间
     * @param filterConfig      文件过滤配置
     * @param model             最新的 需要向 ftl 模板文件 添加的 modelInfo 信息
     * @param replace           想使用 model 替换的 String 字符串内容
     * @return
     */
    public static Long makeTemplate(Meta newMeta, Long id, String originProjectPath, TemplateMakerFilterConfig filterConfig, Meta.ModelConfig.ModelInfo model, String replace) {
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

        // 输入文件信息
        String fileRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();

        List<Meta.FileConfig.FileInfo> fileInfos = new ArrayList<>();

        // 允许指定多个文件 或者 文件目录 的过滤配置信息, 只会针对这些指定目录下的文件 并且满足过滤配置信息的 file 文件 生成 ftl 模板
        List<TemplateMakerFilterConfig.FilterConfig> fileFilterConfig = filterConfig.getFiles();
        for (TemplateMakerFilterConfig.FilterConfig config : fileFilterConfig) {
            String fileInputPath = config.getPath();
            String inputFileAbsolutePath = fileRootPath + File.separator + fileInputPath;
            // 需要进行 ftl 模板生成的 file 文件 。同时也遍历了输入路径下 所有的子文件信息（包含输入路径 以及 该输入路径的子目录）, 针对这些文件也进行了 filter 过滤器过滤
            List<File> attendFtlFiles = FileFilter.doFilter(config.getFilters(), inputFileAbsolutePath);
            for (File file : attendFtlFiles) {
                Meta.FileConfig.FileInfo fileInfo = makeSingleFileTemplate(model, replace, fileRootPath, file);
                fileInfos.add(fileInfo);
            }
        }

        // 更新 meta.json 文件内容, 如果已有 meta.json 文件, 则在此基础上 额外添加 model 相关信息
        String metaOutPutPath = fileRootPath + File.separator + "meta.json";
        if (FileUtil.exist(metaOutPutPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutPutPath), Meta.class);

            // 新旧 Meta 合并
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            List<Meta.FileConfig.FileInfo> files = newMeta.getFileConfig().getFiles();
            files.addAll(fileInfos);
            List<Meta.ModelConfig.ModelInfo> models = newMeta.getModelConfig().getModels();
            models.add(model);
            // 去重
            newMeta.getFileConfig().setFiles(distinctFiles(files));
            newMeta.getModelConfig().setModels(distinctModels(models));
        } else {
            // 不存在 meta.json 文件, 则使用传入的 Meta 对象, 将信息封装到该对象中即可
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(originProjectPath);
            List<Meta.FileConfig.FileInfo> files = new ArrayList<>();
            fileConfig.setFiles(files);
            files.addAll(fileInfos);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> models = new ArrayList<>();
            modelConfig.setModels(models);
            models.add(model);
        }

        // 写到 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutPutPath);
        return id;
    }

    private static Meta.FileConfig.FileInfo makeSingleFileTemplate(Meta.ModelConfig.ModelInfo model, String replace, String fileRootPath, File inputFile) {
        String fileInputAbsolutePath = inputFile.getAbsolutePath();
        // 存储到 FileInfo 的 相对路径
        String fileInputPath = fileInputAbsolutePath.replace(fileRootPath + "\\", "").replace("\\", "/");
        String fileOutPutPath = fileInputPath + ".ftl";
        String fileOutputAbsolutePath = inputFile.getAbsolutePath() + ".ftl";

        String fileContent = null;

        // 如果已有模板文件, 就在这个已有的模板文件上进行修改
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            // 没有模板文件, 说明这是第一次操作。 ftl 模板文件还未生成
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 根据 model 信息, 使用 字符串替换方法 fileContent 中的内容
        String newFileContent = StrUtil.replace(fileContent, replace, String.format("${%s}", model.getFieldName()));

        FileGenerateTypeEnums fileType = FileGenerateTypeEnums.DYNAMIC;
        // 有一些文件, 当中没有需要 修改的部分, 所以不应该产生 ftl 模板, 就算产生, ftl 模板中的内容 和 原文件 的内容也是一致的
        if (newFileContent.equals(fileContent)) {
            // 修改后的文件内容 与 原文件一致, 所以应该是 静态文件, 直接复制即可, 不需要产生 ftl 代码模板文件
            fileOutPutPath = fileInputPath;
            fileType = FileGenerateTypeEnums.STATIC;
        } else {
            // 替换完毕后, 将内容重新写到 ftl 模板中
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }
        // 追加配置参数
        Meta.FileConfig.FileInfo file = new Meta.FileConfig.FileInfo();
        file.setInputPath(fileInputPath);
        file.setOutputPath(fileOutPutPath);
        file.setType(FileTypeEnum.FILE.getValue());
        file.setGenerateType(fileType.getValue());
        return file;
    }

    public static void main(String[] args) {
        // 输入项目基本信息
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("baseResponse");
        // 分布测试通过 modelInfo.setFieldName("outputText");
        modelInfo.setType(FieldTypeEnums.STRING.getType());

        String projectPath = System.getProperty("user.dir");
        String sourceProjectPath = projectPath + File.separator + "demo-projects/springboot-init-master";
        sourceProjectPath = sourceProjectPath.replace("\\", "/");

        // 过滤器配置 1
        TemplateMakerFilterConfig.FilterConfig filterConfig = new TemplateMakerFilterConfig.FilterConfig();
        filterConfig.setPath("src/main/java/com/yupi/springbootinit/common");
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        filterConfig.setFilters(Collections.singletonList(fileFilterConfig));

        // 过滤器配置 2
        TemplateMakerFilterConfig.FilterConfig filterConfig1 = new TemplateMakerFilterConfig.FilterConfig();
        filterConfig1.setPath("src/main/java/com/yupi/springbootinit/controller");

        TemplateMakerFilterConfig templateMakerFilterConfig = new TemplateMakerFilterConfig();
        templateMakerFilterConfig.setFiles(Arrays.asList(filterConfig, filterConfig1));

        makeTemplate(meta, 1818120284805251072L, sourceProjectPath, templateMakerFilterConfig, modelInfo, "BaseResponse");
        // 分布测试通过 makeTemplate(meta, 1818120284805251072L, sourceProjectPath, "src/com/lab/acm/MainTemplate.java", modelInfo, "Sum: ");
    }

    /**
     * 根据 inputPath 去重 files 中的 重复 fileInfo 信息
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 借助 Map<k, v> 来去重, k 相同时, 新值 覆盖 旧值
        return new ArrayList<>(fileInfoList.stream().collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o, (e, r) -> r)).values());
    }

    /**
     * 根据 fieldName 去重 models 中的 重复 modelInfo 信息
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        return new ArrayList<>(modelInfoList.stream().collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());
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
