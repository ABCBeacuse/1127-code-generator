package com.lab.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * .bat 和 .sh 脚本生成
 */
public class ScriptGenerator {

    public static void doGenerator(String outputPath, String jarName) {
        // linux 下 sh 命令脚本生成
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("# bin bash").append("\n");

        stringBuffer.append("java -jar target/" + jarName + " \"$@\"");
        FileUtil.writeBytes(stringBuffer.toString().getBytes(StandardCharsets.UTF_8), outputPath);

        // linux 系统下, 一般需要给 sh 命令脚本 赋予 rwx 可执行权限
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
        } catch (Exception e) {
        }

        // windows 下 bat 脚本生成
        stringBuffer = new StringBuffer();
        stringBuffer.append("@echo off").append("\n");
        stringBuffer.append(String.format("java -jar target/%s %%*", jarName)).append("\n");
        FileUtil.writeBytes(stringBuffer.toString().getBytes(StandardCharsets.UTF_8), outputPath + ".bat");
    }

}
