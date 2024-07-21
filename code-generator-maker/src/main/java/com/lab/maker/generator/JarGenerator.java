package com.lab.maker.generator;

import java.io.*;

/**
 * 执行 Maven clean package 命令, 打包 generated 下 生成项目
 */
public class JarGenerator {

    public static void doGenerator(String projectPath) throws IOException, InterruptedException {

        // maven 构建 jar 包的命令, 不同系统下会有区别
        String windows = "mvn.cmd clean package -DSkipTests=true";
        String other = "mvn clean package -DSkipTests=true";

        // 相当于开启一个 cmd 命令窗口 (需要使用 " " 分开)
        ProcessBuilder processBuilder = new ProcessBuilder(windows.split(" "));
        // 设置 cmd 窗口的运行路径
        processBuilder.directory(new File(projectPath));
        Process process = processBuilder.start();

        // 读取该 processBuilder 的执行结果
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("程序执行完毕, 程序退出码为：" + exitCode);
    }

}
