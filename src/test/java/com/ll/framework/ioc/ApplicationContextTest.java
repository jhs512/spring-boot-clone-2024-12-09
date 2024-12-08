package com.ll.framework.ioc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ApplicationContextTest {
    @Test
    @DisplayName("ApplicationContext")
    public void t1() {
        ApplicationContext applicationContext = new ApplicationContext("com.ll");
        System.out.println(applicationContext);
    }
}
