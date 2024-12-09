package com.ll.framework.ioc.testDomain.post.service;

import com.ll.framework.ioc.annotations.Service;
import com.ll.framework.ioc.testDomain.post.repository.TestPostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestPostService {
    @Getter
    private final TestPostRepository testPostRepository;
}
