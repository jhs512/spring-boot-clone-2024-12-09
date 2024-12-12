package com.ll.framework.web;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ll.framework.ioc.ApplicationContext;

@DisplayName("ActionMapper 테스트")
public class ActionMapperTest {
    private static ActionMapper actionMapper;
    private static HttpRequest request;
    private static HttpResponse response;

    @BeforeAll
    public static void beforeAll() {
        ApplicationContext applicationContext = new ApplicationContext("com.ll");
        applicationContext.init();

        actionMapper = new ActionMapper(applicationContext);
        actionMapper.init();
    }

    @BeforeEach
    public void setUp() {
        request = new HttpRequest();
        response = new HttpResponse();
    }

    @Test
    @DisplayName("GET /testSmsLogs 요청 처리")
    public void handleGetSmsLogs() {
        // given
        request.setMethod("GET");
        request.setRequestURI("/testSmsLogs");

        // when
        Optional<ActionMethodDefinition> opActionMethodDefinition =
                actionMapper.findActionMethodDefinitionByActionPath(request.getRequestURI());

        // then
        assertThat(opActionMethodDefinition).isPresent();

        // when
        actionMapper.doAction(opActionMethodDefinition.get(), request, response);

        // then
        assertThat(response.getBody().toString()).isEqualTo("getItems");
    }

    @Test
    @DisplayName("GET /testSmsLogs/{id} 요청 처리")
    public void handleGetSmsLogById() {
        // given
        request.setMethod("GET");
        request.setRequestURI("/testSmsLogs/1");

        // when
        Optional<ActionMethodDefinition> opActionMethodDefinition =
                actionMapper.findActionMethodDefinitionByActionPath(request.getRequestURI());

        // then
        assertThat(opActionMethodDefinition).isPresent();

        // when
        actionMapper.doAction(opActionMethodDefinition.get(), request, response);

        // then
        assertThat(response.getBody().toString()).isEqualTo("getItem 1");
    }

    @Test
    @DisplayName("GET /testSmsLogs/{groupCode}/{id} 요청 처리")
    public void handleGetSmsLogByGroupAndId() {
        // given
        request.setMethod("GET");
        request.setRequestURI("/testSmsLogs/emergency/11");

        // when
        Optional<ActionMethodDefinition> opActionMethodDefinition =
                actionMapper.findActionMethodDefinitionByActionPath(request.getRequestURI());

        // then
        assertThat(opActionMethodDefinition).isPresent();

        // when
        actionMapper.doAction(opActionMethodDefinition.get(), request, response);

        // then
        assertThat(response.getBody().toString()).isEqualTo("group : emergency, getItem 11");
    }

    @Test
    @DisplayName("존재하지 않는 경로 요청 처리")
    public void handleNonExistentPath() {
        // given
        request.setMethod("GET");
        request.setRequestURI("/non-existent");

        // when
        Optional<ActionMethodDefinition> opActionMethodDefinition =
                actionMapper.findActionMethodDefinitionByActionPath(request.getRequestURI());

        // then
        assertThat(opActionMethodDefinition).isEmpty();
    }
}
