package com.ll.framework.ioc;

import com.ll.framework.ioc.testDomain.post.service.TestPostService.TestPostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationContextTest {
    private static ApplicationContext applicationContext;

    @BeforeAll
    public static void beforeAll() {
        applicationContext = new ApplicationContext("com.ll");
        applicationContext.init();
    }

    @Test
    @DisplayName("ApplicationContext")
    public void t1() {
        System.out.println(applicationContext);
    }

    @Test
    @DisplayName("applicationContext.findComponentClassBy(TestPostService.class);")
    public void t2() {
        Class<TestPostService> cls = applicationContext.findComponentClassBy(TestPostService.class);

        assertThat(cls).isNotNull();
    }

    @Test
    @DisplayName("applicationContext.findBeanDefinition(\"testPostService\");")
    public void t3() {
        Optional<BeanDefinition> opBeanDefinition = applicationContext.findBeanDefinition("testPostService");

        assertThat(opBeanDefinition).isPresent();
    }
}
