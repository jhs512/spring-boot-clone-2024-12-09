package com.ll.framework.ioc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationContextTest {
    private static ApplicationContext applicationContext;

    @BeforeAll
    public static void beforeAll() {
        applicationContext = new ApplicationContext("com.ll");
    }

    @Test
    @DisplayName("ApplicationContext")
    public void t1() {
        System.out.println(applicationContext);
    }

    @Test
    @DisplayName("applicationContext.findComponentBy(TestPostService.class);")
    public void t2() {
        Class<TestPostService> cls = applicationContext.findComponentBy(TestPostService.class);

        assertThat(cls).isNotNull();
    }
}
