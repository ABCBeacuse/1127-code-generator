package com.lab.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Command(name = "MyOrder", version = {"MyOrder 1.0"}, mixinStandardHelpOptions = true)
public class Example implements Callable<Integer> {

    @Option(names = {"-u", "--user"}, arity = " 0..1 ", description = {"用户名相关信息"}, interactive = true, prompt = "用户名：")
    private String user = "default user name";

    @Option(names = {"-p", "--password"}, arity = " 0..1 ", description = {"密码相关信息"}, interactive = true, prompt = "密码：")
    private String password;

    @Override
    public Integer call() throws Exception {
        System.out.println("用户名：" + user + "\n密码：" + password);
        return 0;
    }

    public static void main(String[] args) {
        // new CommandLine(new Example()).execute("-u", "default", "--password");
        new CommandLine(new Example()).execute("--version");
    }

}
