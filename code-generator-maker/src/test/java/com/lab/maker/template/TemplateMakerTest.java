package com.lab.maker.template;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.lab.maker.meta.Meta;
import com.lab.maker.meta.enums.FieldTypeEnums;
import com.lab.maker.template.model.TemplateMakerConfig;
import com.lab.maker.template.model.TemplateMakerFilterConfig;
import com.lab.maker.template.model.TemplateMakerModelConfig;
import org.junit.Test;

import java.util.Collections;

public class TemplateMakerTest {

    /**
     * {
     * "inputPath": "src/main/java/com/yupi/springbootinit/common/BaseResponse.java",
     * "outputPath": "src/main/java/com/yupi/springbootinit/common/BaseResponse.java",
     * "type": "file",
     * "generateType": "static"
     * },
     * {
     * "inputPath": "src/main/java/com/yupi/springbootinit/common/BaseResponse.java.ftl",
     * "outputPath": "src/main/java/com/yupi/springbootinit/common/BaseResponse.java.ftl",
     * "type": "file",
     * "generateType": "static"
     * },
     * 多次运行后, 一些已经生成 ftl 模板的 fileConfig 中的 generateType 变为了 static, 正确的结果为 dynamic
     */
    @Test
    public void templateMakerTestBug1() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setCreateTime(DateUtil.now());
        meta.setDescription("springboot-code-generator");

        // 文件配置
        TemplateMakerFilterConfig filterConfig = new TemplateMakerFilterConfig();
        TemplateMakerFilterConfig.FilterConfig filterInfo = new TemplateMakerFilterConfig.FilterConfig();
        filterInfo.setPath("src/main/java/com/yupi/springbootinit/common");
        filterConfig.setFiles(Collections.singletonList(filterInfo));

        // 模型配置信息
        TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

        TemplateMakerModelConfig.ModelConfig modelInfo = new TemplateMakerModelConfig.ModelConfig();
        modelInfo.setFieldName("className");
        modelInfo.setType(FieldTypeEnums.STRING.getType());
        modelInfo.setReplaceText("BaseResponse");

        modelConfig.setModels(Collections.singletonList(modelInfo));

        String originProjectPath = "F:/code/code-generator/demo-projects/springboot-init-master";
        TemplateMaker.makeTemplate(meta, 1820367662266433536L, originProjectPath, filterConfig, modelConfig, null);
    }

    /**
     * {
     * "inputPath": "src/main/java/com/yupi/springbootinit/common/ResultUtils.java.ftl",
     * "outputPath": "src/main/java/com/yupi/springbootinit/common/ResultUtils.java.ftl",
     * "type": "file",
     * "generateType": "static"
     * },
     * <p>
     * 多次运行之后, 在 meta.json 配置文件中, 会出现生成的 ftl 模板文件 被 识别为 static 静态文件的问题。
     * 正常情况下, ftl 模板文件为 “生成文件”, 不应该被记录为 static 静态文件
     * <p>
     * <p>
     * 修复完毕后
     * {
     * "inputPath": "src/main/java/com/yupi/springbootinit/common/ResultUtils.java",
     * "outputPath": "src/main/java/com/yupi/springbootinit/common/ResultUtils.java.ftl",
     * "type": "file",
     * "generateType": "dynamic"
     * },
     */
    @Test
    public void templateMakerTestBug2() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setCreateTime(DateUtil.now());
        meta.setDescription("springboot-code-generator");

        // 文件配置
        TemplateMakerFilterConfig filterConfig = new TemplateMakerFilterConfig();
        TemplateMakerFilterConfig.FilterConfig filterInfo = new TemplateMakerFilterConfig.FilterConfig();
        filterInfo.setPath("src/main/java/com/yupi/springbootinit/common");
        filterConfig.setFiles(Collections.singletonList(filterInfo));

        // 模型配置信息
        TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

        TemplateMakerModelConfig.ModelConfig modelInfo = new TemplateMakerModelConfig.ModelConfig();
        modelInfo.setFieldName("className");
        modelInfo.setType(FieldTypeEnums.STRING.getType());
        modelInfo.setReplaceText("BaseResponse");

        modelConfig.setModels(Collections.singletonList(modelInfo));

        String originProjectPath = "F:/code/code-generator/demo-projects/springboot-init-master";
        TemplateMaker.makeTemplate(meta, 1820367662266433536L, originProjectPath, filterConfig, modelConfig, null);
    }

    /**
     * {
     * "inputPath": "src/main/java/com/yupi/springbootinit/common/ResultUtils.java",
     * "outputPath": "src/main/java/com/yupi/springbootinit/common/ResultUtils.java.ftl",
     * "type": "file",
     * "generateType": "dynamic"
     * }
     * <p>
     * 生成的 meta.json 文件中 inputPath 应该是 freemarker 中的 ftl 模板的相对路径
     * outputPath 应该是 生成的 目标代码 的 相对路径
     */
    @Test
    public void templateMakerTestBug3() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setCreateTime(DateUtil.now());
        meta.setDescription("springboot-code-generator");

        // 文件配置
        TemplateMakerFilterConfig filterConfig = new TemplateMakerFilterConfig();
        TemplateMakerFilterConfig.FilterConfig filterInfo = new TemplateMakerFilterConfig.FilterConfig();
        filterInfo.setPath("src/main/java/com/yupi/springbootinit/common");
        filterConfig.setFiles(Collections.singletonList(filterInfo));

        // 模型配置信息
        TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

        TemplateMakerModelConfig.ModelConfig modelInfo = new TemplateMakerModelConfig.ModelConfig();
        modelInfo.setFieldName("className");
        modelInfo.setType(FieldTypeEnums.STRING.getType());
        modelInfo.setReplaceText("BaseResponse");

        modelConfig.setModels(Collections.singletonList(modelInfo));

        String originProjectPath = "F:/code/code-generator/demo-projects/springboot-init-master";
        TemplateMaker.makeTemplate(meta, 1820367662266433536L, originProjectPath, filterConfig, modelConfig, null);
    }

    /**
     * 当 配置的 filterInfo 的 path 属性为 "./" 时, 即扫描的是 当前项目中的所有目录 时,
     * 第二次执行时 meta.json 也会被生成 meta.json.ftl 模板
     * <p>
     * 解决方法：将 meta.json 文件从 生成的代码项目 中移除, 移到外层的 工作目录 中
     */
    @Test
    public void templateMakerTestBug4() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setCreateTime(DateUtil.now());
        meta.setDescription("springboot-code-generator");

        // 文件配置
        TemplateMakerFilterConfig filterConfig = new TemplateMakerFilterConfig();
        TemplateMakerFilterConfig.FilterConfig filterInfo = new TemplateMakerFilterConfig.FilterConfig();
        filterInfo.setPath("./");
        filterConfig.setFiles(Collections.singletonList(filterInfo));

        // 模型配置信息
        TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

        TemplateMakerModelConfig.ModelConfig modelInfo = new TemplateMakerModelConfig.ModelConfig();
        modelInfo.setFieldName("className");
        modelInfo.setType(FieldTypeEnums.STRING.getType());
        modelInfo.setReplaceText("BaseResponse");

        modelConfig.setModels(Collections.singletonList(modelInfo));

        String originProjectPath = "F:/code/code-generator/demo-projects/springboot-init-master";
        TemplateMaker.makeTemplate(meta, 1820367662266433536L, originProjectPath, filterConfig, modelConfig, null);
    }

    @Test
    public void makeTemplateTest() {
        String configStr = ResourceUtil.readUtf8Str("templateMakerConfig.json");
        TemplateMakerConfig config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        Long id = TemplateMaker.makeTemplate(config);
        System.out.println(id);
    }

    @Test
    public void makeSpringBootInitTemplateTest() {
        // 第一步：创建基础的 meta.json 文件
        String rootPath = "example/springboot-init/";
        String configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig.json");
        TemplateMakerConfig config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        Long id = TemplateMaker.makeTemplate(config);

        // 第二步：meta.json 文件中, 可以省略第一步添加的 meta 元信息 和 originProjectPath, 只需要保留 id 值 与 第一步的 json 文件中的 id 值一致即可
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig2.json");
        config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(config);

        // 帖子相关功能
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig3.json");
        config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(config);

        // 控制 Cors 跨域是否开启
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig4.json");
        config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(config);

        // 控制 接口文档 是否开启
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig5.json");
        config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(config);

        configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig6.json");
        config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(config);

        // 配置 mysql 数据库信息
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig7.json");
        config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(config);

        //控制 Redis 是否开启
        // 配置 mysql 数据库信息
        configStr = ResourceUtil.readUtf8Str(rootPath + "templateInitConfig8.json");
        config = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(config);
    }

}