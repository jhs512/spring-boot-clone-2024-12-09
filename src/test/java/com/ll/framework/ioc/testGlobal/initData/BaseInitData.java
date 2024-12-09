package com.ll.framework.ioc.testGlobal.initData;

import com.ll.framework.ioc.ApplicationRunner;
import com.ll.framework.ioc.annotations.Bean;
import com.ll.framework.ioc.annotations.Configuration;

@Configuration
public class BaseInitData {
    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            System.out.println("BaseInitData");
        };
    }
}
