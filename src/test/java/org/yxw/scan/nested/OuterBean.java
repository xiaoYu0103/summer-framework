package org.yxw.scan.nested;

import org.yxw.annotation.Component;

@Component
public class OuterBean {

    @Component
    public static class NestedBean {

    }
}