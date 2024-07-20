package ${basePackage}.maker.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine.Command;

import java.io.File;
import java.util.List;

@Command(name = "list", mixinStandardHelpOptions = true, description = "查看生成的项目目录")
public class ListCommand implements Runnable {

    @Override
    public void run() {
        // 查看项目路径
        String projectPath = System.getProperty("user.dir");
        // 生成的项目文件 目录
        String targetPath = projectPath + File.separator + "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(targetPath);
        for (File file : files) {
            System.out.println(file);
        }
    }
}
