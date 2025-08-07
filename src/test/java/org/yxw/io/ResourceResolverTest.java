package org.yxw.io;

import org.junit.jupiter.api.Test;

import java.util.List;


public class ResourceResolverTest {

    @Test
    public void testResourceResolverScanClass() {
        // 创建ResourceResolver实例，指定要扫描的基础包
        ResourceResolver resolver = new ResourceResolver("org.yxw");

        // 使用scan方法扫描资源
        List<String> classFiles = resolver.scan(resource -> {
            String name = resource.getName();
            // 只处理.class文件
            if (name.endsWith(".class")) {
                System.out.println(name);
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        });

        // 打印结果
        System.out.println("\n找到的类文件：");
        for (String classFile : classFiles) {
            System.out.println(classFile);
        }
        System.out.println("\n共找到 " + classFiles.size() + " 个类文件");
    }
}
