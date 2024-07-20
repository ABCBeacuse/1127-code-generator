package ${basePackage};

import ${basePackage}.cli.CommonExecutor;

/**
 * 全局调用接口
 */
public class Main {
    public static void main(String[] args) {
        // args = new String[]{"generator", "-l", "-a", "-o"};
        // args = new String[]{"list"};
        new CommonExecutor().doExecutor(args);
    }
}