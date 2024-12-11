package com.ll.testDomain.post.controller;

import com.ll.framework.web.annotations.GetMapping;
import com.ll.framework.web.annotations.PathVariable;
import com.ll.framework.web.annotations.RequestMapping;
import com.ll.framework.web.annotations.RestController;

@RestController
@RequestMapping("/testPosts")
public class TestPostController {
    @GetMapping
    public String getItems() {
        return "getItems";
    }

    @GetMapping("/{id}")
    public String getItem(
            @PathVariable long id
    ) {
        return "getItem " + id;
    }
}
