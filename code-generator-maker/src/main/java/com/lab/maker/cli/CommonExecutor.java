package com.lab.maker.cli;


import com.lab.maker.cli.command.ConfigCommand;
import com.lab.maker.cli.command.GeneratorCommand;
import com.lab.maker.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "code_generator", version = {"code_generator v1.0"}, mixinStandardHelpOptions = true)
public class CommonExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GeneratorCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new ConfigCommand());
    }

    @Override
    public void run() {
        // 不输入子命令时, 给出友好提示
        System.out.println("请输入具体命令, 或者输入 --help 查看命令提示");
    }

    public void doExecutor(String[] args) {
        commandLine.execute(args);
    }
}
