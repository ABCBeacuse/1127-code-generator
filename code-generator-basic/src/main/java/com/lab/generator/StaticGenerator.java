package com.lab.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 静态文件生成器
 */
public class StaticGenerator {

    public static void main(String[] args) {
        // 获取项目运行根目录 F:\code\code-generator
        String projectPath = System.getProperty("user.dir");
        String originPath = projectPath + File.separator + "demo-projects" + File.separator + "acm-template";
        String targetPath = projectPath;
        copyFilesWithHutool(originPath, targetPath);
    }

    /**
     * 拷贝文件（ Hutool, 会将输入目录完整拷贝到输出目录下 ）
     * @param originPath 输入目录
     * @param targetPath 输出目录
     */
    public static void copyFilesWithHutool(String originPath, String targetPath) {
        FileUtil.copy(originPath, targetPath, false);
    }

}
