package org.yxw.scan.destory;

import org.yxw.annotation.Component;
import org.yxw.annotation.Value;

import javax.annotation.PreDestroy;

@Component
public class AnnotationDestroyBean {

    @Value("${app.title}")
    public String appTitle;

    @PreDestroy
    void destroy() {
        this.appTitle = null;
    }
}
