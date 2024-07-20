package com.lab.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lab.maker.generator.file.DynamicFileGenerator;
import com.lab.maker.generator.file.FileGenerator;
import com.lab.maker.meta.Meta;
import com.lab.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMetaInstance();
        System.out.println(meta);

        /**
         * 将 DataModel.java 生成 到 code-generator-maker/generator/ 下对应的 package 下
         */
        // code-generator-maker 的项目根路径
        String projectRoot = System.getProperty("user.dir") + File.separator + "code-generator-maker";
        String generatorProjectPath = projectRoot + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(generatorProjectPath)) {
            FileUtil.mkdir(generatorProjectPath);
        }

        String dataModelTemplatePath = projectRoot + File.separator + "src/main/resources/templates/java/model/DataModel.java.ftl";

        String generatorBasePackage = StrUtil.join("/", StrUtil.split(meta.getBasePackage(), "."));
        String outputBaseJavaPackagePath = generatorProjectPath + File.separator + "src/main/java/" + generatorBasePackage;
        String outputDataModelPath = outputBaseJavaPackagePath + "/model/DataModel.java";

        DynamicFileGenerator.doGenerator(dataModelTemplatePath, outputDataModelPath, meta);
    }
}
