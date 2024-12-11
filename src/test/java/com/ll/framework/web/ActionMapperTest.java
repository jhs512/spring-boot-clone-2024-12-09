package com.ll.framework.web;

import com.ll.framework.ioc.ApplicationContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ActionMapperTest {
    private static ActionMapper actionMapper;

    @BeforeAll
    public static void beforeAll() {
        ApplicationContext applicationContext = new ApplicationContext("com.ll");
        applicationContext.init();

        actionMapper = new ActionMapper(applicationContext);
        actionMapper.init();
    }

    @Test
    @DisplayName("ActionMapper 객체 생성")
    public void t1() {
        System.out.println(actionMapper);
    }

    @Test
    @DisplayName("actionMapper.findActionMethodDefinition(\"/testSmsLogs\")")
    public void t2() {
        Optional<ActionMethodDefinition> opActionMethodDefinition = actionMapper.findActionMethodDefinition("/testSmsLogs");

        assertThat(opActionMethodDefinition).isPresent();
    }

    @Test
    @DisplayName("Optional<ActionMethodDefinition> opActionMethodDefinition = actionMapper.findActionMethodDefinitionByActionPath(\"/testSmsLogs\");")
    public void t3() {
        Optional<ActionMethodDefinition> opActionMethodDefinition = actionMapper.findActionMethodDefinitionByActionPath("/testSmsLogs");

        assertThat(opActionMethodDefinition).isPresent();
    }

    @Test
    @DisplayName("")
    public void t4() {
        Optional<ActionMethodDefinition> opActionMethodDefinition = actionMapper.findActionMethodDefinitionByActionPath("/testSmsLogs");

        ActionMethodDefinition actionMethodDefinition = opActionMethodDefinition.get();

        String rs = actionMapper.doAction(actionMethodDefinition, "/testSmsLogs");

        assertThat(rs).isEqualTo("getItems");
    }

    @Test
    @DisplayName("")
    public void t5() {
        Optional<ActionMethodDefinition> opActionMethodDefinition = actionMapper.findActionMethodDefinitionByActionPath("/testSmsLogs/1");

        ActionMethodDefinition actionMethodDefinition = opActionMethodDefinition.get();

        String rs = actionMapper.doAction(actionMethodDefinition, "/testSmsLogs/1");

        assertThat(rs).isEqualTo("getItem 1");
    }

    @Test
    @DisplayName("")
    public void t6() {
        Optional<ActionMethodDefinition> opActionMethodDefinition = actionMapper.findActionMethodDefinitionByActionPath("/testSmsLogs/1");

        ActionMethodDefinition actionMethodDefinition = opActionMethodDefinition.get();

        String rs = actionMapper.doAction(actionMethodDefinition, "/testSmsLogs/6");

        assertThat(rs).isEqualTo("getItem 6");
    }

    @Test
    @DisplayName("")
    public void t7() {
        Optional<ActionMethodDefinition> opActionMethodDefinition = actionMapper.findActionMethodDefinitionByActionPath("/testSmsLogs/emergency/1");

        ActionMethodDefinition actionMethodDefinition = opActionMethodDefinition.get();

        String rs = actionMapper.doAction(actionMethodDefinition, "/testSmsLogs/emergency/11");

        assertThat(rs).isEqualTo("group : emergency, getItem 11");
    }
}
