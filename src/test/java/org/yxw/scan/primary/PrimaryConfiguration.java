package org.yxw.scan.primary;

import org.yxw.annotation.Bean;
import org.yxw.annotation.Configuration;
import org.yxw.annotation.Primary;

@Configuration
public class PrimaryConfiguration {

    @Primary
    @Bean
    DogBean husky() {
        return new DogBean("Husky");
    }

    @Bean
    DogBean teddy() {
        return new DogBean("Teddy");
    }
}
