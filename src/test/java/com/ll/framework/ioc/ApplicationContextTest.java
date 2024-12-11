package com.ll.framework.ioc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ll.framework.web.annotations.Controller;
import com.ll.testDomain.post.controller.TestPostController;
import com.ll.testDomain.post.service.TestFacadePostService;
import com.ll.testDomain.post.service.TestPostService;
import com.ll.testDomain.sms.controller.TestSmsLogController;
import com.ll.testDomain.sms.service.TestSmsSenderService;
import com.ll.testGlobal.initData.TestBaseInitData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationContextTest {
    private static ApplicationContext applicationContext;

    @BeforeAll
    public static void beforeAll() {
        applicationContext = new ApplicationContext("com.ll");
        applicationContext.init();
    }

    @Test
    @DisplayName("ApplicationContext 객체 생성")
    public void t1() {
        System.out.println(applicationContext);
    }

    @Test
    @DisplayName("@Service 어노테이션을 가진 TestPostService 클래스를 찾아서 반환")
    public void t2() {
        Class<?> testSmsSenderServiceCls = applicationContext.findComponentClasses()
                .stream()
                .filter(cls -> cls == TestSmsSenderService.class)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("TestSmsSenderService 클래스를 찾을 수 없습니다."));

        assertThat(testSmsSenderServiceCls).isNotNull();
    }

    @Test
    @DisplayName("@RestController 어노테이션을 가진 TestPostController 클래스를 찾아서 반환")
    public void t3() {
        Class<?> testPostController = applicationContext.findComponentClasses()
                .stream()
                .filter(cls -> cls == TestPostController.class)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("TestSmsSenderService 클래스를 찾을 수 없습니다."));

        assertThat(testPostController).isNotNull();
    }

    @Test
    @DisplayName("testPostService 빈의 BeanDefinition을 찾아서 반환")
    public void t4() {
        Optional<BeanDefinition> opBeanDefinition = applicationContext
                .findBeanDefinition("testPostService");

        assertThat(opBeanDefinition).isPresent();
    }

    @Test
    @DisplayName("아무런 의존관계가 없는 단순한 빈인 testSmsSenderService 을 생성")
    public void t5() {
        TestSmsSenderService testSmsSenderService = applicationContext
                .genBean("testSmsSenderService");

        assertThat(testSmsSenderService).isNotNull();
    }

    @Test
    @DisplayName("testPostRepository 라는 의존성을 가지는 testPostService 빈을 생성")
    public void t6() {
        TestPostService testPostService = applicationContext.genBean("testPostService");

        assertThat(testPostService).isNotNull();
        assertThat(testPostService.getTestPostRepository()).isNotNull();
    }

    @Test
    @DisplayName("testServiceRepository 라는 의존성을 가지는 testFacadePostService 빈을 생성")
    public void t7() {
        TestFacadePostService testFacadePostService = applicationContext.genBean("testFacadePostService");

        assertThat(testFacadePostService).isNotNull();
        assertThat(testFacadePostService.getTestPostService()).isNotNull();
    }

    @Test
    @DisplayName("testBaseInitDataApplicationRunner 빈 생성 메서드를 찾아서 반환")
    public void t8() {
        applicationContext.findBeanMethods()
                .stream()
                .filter(method -> {
                    try {
                        Method foundMethod = TestBaseInitData.class.getMethod("testBaseInitDataApplicationRunner");
                        return method.equals(foundMethod);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("testBaseInitDataApplicationRunner 메서드를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("testBaseInitDataApplicationRunner 빈의 BeanDefinition 을 찾아서 반환")
    public void t9() {
        Optional<BeanDefinition> opBeanDefinition = applicationContext
                .findBeanDefinition("testBaseInitDataApplicationRunner");

        assertThat(opBeanDefinition).isPresent();
    }

    @Test
    @DisplayName("testBaseInitDataApplicationRunner 빈은 커스텀 팩토리 메서드로 만들어지도록 설계됨")
    public void t10() {
        BeanDefinition beanDefinition = applicationContext
                .findBeanDefinition("testBaseInitDataApplicationRunner").get();

        assertThat(beanDefinition.hasCustomFactoryMethod()).isTrue();
    }

    @Test
    @DisplayName("아무런 의존관계가 없는 단순한 빈인 testBaseInitDataApplicationRunner 을 생성")
    public void t11() {
        ApplicationRunner testBaseInitDataApplicationRunner = applicationContext
                .genBean("testBaseInitDataApplicationRunner");

        assertThat(testBaseInitDataApplicationRunner).isNotNull();
    }

    @Test
    @DisplayName("아무런 의존관계가 없는 단순한 빈인 testBaseJavaTimeModule 를 생성")
    public void t12() {
        JavaTimeModule testBaseJavaTimeModule = applicationContext.genBean("testBaseJavaTimeModule");

        assertThat(testBaseJavaTimeModule).isNotNull();
    }

    @Test
    @DisplayName("testBaseJavaTimeModule 빈에 의존하는 testBaseObjectMapper 빈을 생성")
    public void t13() {
        ObjectMapper testBaseObjectMapper = applicationContext.genBean("testBaseObjectMapper");

        assertThat(testBaseObjectMapper).isNotNull();
    }

    @Test
    @DisplayName("Controller 혹은 RestController 어노테이션을 가진 빈을 찾아서 반환")
    public void t14() {
        Set<Class<?>> annotations = applicationContext.findComponentClassesBy(Controller.class);

        assertThat(annotations).contains(TestPostController.class);
        assertThat(annotations).contains(TestSmsLogController.class);
    }
}
