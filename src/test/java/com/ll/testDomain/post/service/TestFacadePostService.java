package com.ll.testDomain.post.service;

import com.ll.framework.ioc.annotations.Service;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestFacadePostService {
    @Getter
    private final TestPostService testPostService;
}
