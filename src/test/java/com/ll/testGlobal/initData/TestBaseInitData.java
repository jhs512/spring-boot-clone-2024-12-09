package com.ll.testGlobal.initData;

import com.ll.framework.ioc.ApplicationRunner;
import com.ll.framework.ioc.annotations.Bean;
import com.ll.framework.ioc.annotations.Configuration;

@Configuration
public class TestBaseInitData {
    @Bean
    public ApplicationRunner testBaseInitDataApplicationRunner() {
        return args -> {

        };
    }
}
