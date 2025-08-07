package org.yxw;

import org.yxw.io.ResourceResolver;

import java.util.List;

public class App {
    public static void main(String[] args) {
        // 创建ResourceResolver实例，指定要扫描的基础包
        ResourceResolver resolver = new ResourceResolver("org.yxw");

        // 使用scan方法扫描资源，并打印结果
        List<String> resources = resolver.scan(resource -> {
            // 或者处理所有文件
             System.out.println("Found resource: " + resource.getName() + " at " + resource.getName());
             return resource.getName();
        });

        // 打印扫描结果
        System.out.println("\n--- Scan Results ---");
        System.out.println("Total resources found: " + resources.size());
        for (String resource : resources) {
            System.out.println(resource);
        }
    }
}