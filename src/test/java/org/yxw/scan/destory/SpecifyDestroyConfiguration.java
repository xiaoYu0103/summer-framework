package org.yxw.scan.destory;

import org.yxw.annotation.Bean;
import org.yxw.annotation.Configuration;
import org.yxw.annotation.Value;

@Configuration
public class SpecifyDestroyConfiguration {
    @Bean(destroyMethod = "destroy")
    SpecifyDestroyBean createSpecifyDestroyBean(@Value("${app.title}") String appTitle) {
        return new SpecifyDestroyBean(appTitle);
    }
}
