package com.lab.maker.generator;

import com.lab.maker.meta.Meta;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * 模板方法模式
 * <p>
 * 可以自定义一些 Generator 代码生成类, 去重写 GeneratorTemplate 中的代码生成过程的方法实现, 从而实现自定义代码生成过程
 */
public class MainGenerator extends GeneratorTemplate {

    @Override
    protected void generatorSimpleDist(Meta meta, String generatorProjectPath, String sourceDestPath) {
        // 重写 generatorSimpleDist, 可以不生成 dist 简易版 “代码生成器”
        System.out.println("不生成 dist 精简版 代码生成器~");
    }

    public static void main(String[] args) throws IOException, TemplateException, InterruptedException {
        new MainGenerator().doGenerator();
    }
}
