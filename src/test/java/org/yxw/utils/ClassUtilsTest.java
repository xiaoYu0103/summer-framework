package org.yxw.utils;

import org.junit.jupiter.api.Test;
import org.yxw.exception.BeanDefinitionException;

import java.lang.annotation.*;

import static org.junit.jupiter.api.Assertions.*;

public class ClassUtilsTest {

    // 定义一个直接注解
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    public @interface DirectAnnotation {
        String value() default "";
    }

    // 定义一个元注解，它被DirectAnnotation标注
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @DirectAnnotation("nested")
    public @interface MetaAnnotation {
        String name() default "";
    }

    // 定义一个重复元注解的场景
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @DirectAnnotation("path1")
    public @interface DuplicatePath1 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE})
    @DirectAnnotation("path2")
    public @interface DuplicatePath2 {
    }

    // 直接使用DirectAnnotation的类
    @DirectAnnotation("direct")
    static class DirectAnnotatedClass {
    }

    // 使用MetaAnnotation的类，间接使用了DirectAnnotation
    @MetaAnnotation(name = "test")
    static class MetaAnnotatedClass {
    }

    // 会导致重复注解异常的类
    @DuplicatePath1
    @DuplicatePath2
    static class DuplicateAnnotatedClass {
    }

    @Test
    public void testFindDirectAnnotation() {
        // 测试直接标注的注解
        DirectAnnotation annotation = ClassUtils.findAnnotation(DirectAnnotatedClass.class, DirectAnnotation.class);
        System.out.println(annotation);
        assertNotNull(annotation);
        assertEquals("direct", annotation.value());
    }

    @Test
    public void testFindMetaAnnotation() {
        // 测试元注解（嵌套注解）
        DirectAnnotation annotation = ClassUtils.findAnnotation(MetaAnnotatedClass.class, DirectAnnotation.class);
        System.out.println(annotation);
        assertNotNull(annotation);
        assertEquals("nested", annotation.value());
    }

    @Test
    public void testAnnotationNotFound() {
        // 测试找不到注解的情况
        Deprecated annotation = ClassUtils.findAnnotation(DirectAnnotatedClass.class, Deprecated.class);
        System.out.println(annotation);
        assertNull(annotation);
    }

    @Test
    public void testDuplicateAnnotation() {
        // 测试重复注解会抛出异常
        assertThrows(BeanDefinitionException.class, () -> {
            ClassUtils.findAnnotation(DuplicateAnnotatedClass.class, DirectAnnotation.class);
        });
    }
}

