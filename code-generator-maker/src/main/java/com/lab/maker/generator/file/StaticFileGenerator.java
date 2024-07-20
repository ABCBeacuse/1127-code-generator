package com.lab.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * 静态文件生成器
 */
public class StaticFileGenerator {

    /**
     * 拷贝文件（ Hutool, 会将输入目录完整拷贝到输出目录下 ）
     * @param originPath 输入目录
     * @param targetPath 输出目录
     */
    public static void copyFilesWithHutool(String originPath, String targetPath) {
        FileUtil.copy(originPath, targetPath, false);
    }

}
