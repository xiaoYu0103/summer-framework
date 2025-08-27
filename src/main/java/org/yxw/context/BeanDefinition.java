package org.yxw.context;

import jakarta.annotation.Nullable;
import org.yxw.exception.BeanCreationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class BeanDefinition implements Comparable<BeanDefinition> {

    // 全局唯一的Bean Name
    private final String name;
    // Bean的声明类型
    private final Class<?> beanClass;
    // Bean的实例
    private Object instance = null;
    // 构造方法/null
    private final Constructor<?> constructor;
    // 工厂方法名称/null
    private final String factoryName;
    // 工厂方法/null
    private final Method factoryMethod;
    // Bean的顺序
    private final int order;
    // 是否标识了@Primary
    private final boolean primary;

    private String initMethodName;
    private String destroyMethodName;

    private Method initMethod;
    private Method destroyMethod;

    public BeanDefinition(String name, Class<?> beanClass, Constructor<?> constructor, int order, boolean primary,
        String initMethodName, String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = constructor;
        this.factoryName = null;
        this.factoryMethod = null;
        this.order = order;
        this.primary = primary;
        // 这条语句可以将private等本来不可访问的构造方法设置为可访问
        constructor.setAccessible(true);
        setInitAndDestroyMethod(initMethodName, destroyMethodName, initMethod, destroyMethod);
    }

    public BeanDefinition(String name, Class<?> beanClass, String factoryName, Method factoryMethod, int order, boolean primary,
                          String initMethodName, String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.name = name;
        this.beanClass = beanClass;
        this.constructor = null;
        this.factoryName = factoryName;
        this.factoryMethod = factoryMethod;
        this.order = order;
        this.primary = primary;
        factoryMethod.setAccessible(true);
        setInitAndDestroyMethod(initMethodName, destroyMethodName, initMethod, destroyMethod);
    }

    private void setInitAndDestroyMethod(String initMethodName, String destroyMethodName, Method initMethod, Method destroyMethod) {
        this.initMethodName = initMethodName;
        this.destroyMethodName = destroyMethodName;
        if (initMethodName != null) {
            initMethod.setAccessible(true);
        }
        if (destroyMethodName != null) {
            destroyMethod.setAccessible(true);
        }
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }

    @Nullable
    public Constructor<?> getConstructor() {
        return constructor;
    }

    @Nullable
    public String getFactoryName() {
        return factoryName;
    }

    @Nullable
    public Method getFactoryMethod() {
        return factoryMethod;
    }

    @Nullable
    public String getInitMethodName() {
        return initMethodName;
    }

    @Nullable
    public Method getInitMethod() {
        return initMethod;
    }

    @Nullable
    public String getDestroyMethodName() {
        return destroyMethodName;
    }

    @Nullable
    public Method getDestroyMethod() {
        return destroyMethod;
    }

    public String getName() {
        return name;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Nullable
    public Object getInstance() {
        return instance;
    }

    public Object getRequiredInstance() {
        if (this.instance == null) {
            throw new BeanCreationException(String.format("Instance of bean with name '%s' and type '%s' is not instantiated during current stage.",
                    this.getName(), this.getBeanClass().getName()));
        }
        return this.instance;
    }

    public void setInstance(Object instance) {
        Objects.requireNonNull(instance, "Bean instance is null.");
        if (!this.beanClass.isAssignableFrom(instance.getClass())){
            throw new BeanCreationException(String.format("Bean with name '%s' and type '%s' is not assignable from the given instance of type '%s'.",
                    this.getName(), this.getBeanClass().getName(), instance.getClass().getName()));
        }
    }

    public boolean isPrimary() {
        return this.primary;
    }

    @Override
    public String toString() {
        return "BeanDefinition [name=" + name + ", beanClass=" + beanClass.getName() + ", factory=" + getCreateDetail() + ", init-method="
                + (initMethod == null ? "null" : initMethod.getName()) + ", destroy-method=" + (destroyMethod == null ? "null" : destroyMethod.getName())
                + ", primary=" + primary + ", instance=" + instance + "]";
    }

    String getCreateDetail() {
        if (this.factoryMethod != null) {
            String params = String.join(", ", Arrays.stream(this.factoryMethod.getParameterTypes()).map(t -> t.getSimpleName()).toArray(String[]::new));
            return this.factoryMethod.getDeclaringClass().getSimpleName() + "." + this.factoryMethod.getName() + "(" + params + ")";
        }
        return null;
    }

    @Override
    public int compareTo(BeanDefinition other) {
        int cmp = Integer.compare(this.order, other.order);
        if (cmp != 0) {
            return cmp;
        }
        return this.name.compareTo(other.name);
    }



}
