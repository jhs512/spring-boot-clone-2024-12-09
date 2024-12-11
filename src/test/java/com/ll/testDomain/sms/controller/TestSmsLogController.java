package com.ll.testDomain.sms.controller;

import com.ll.framework.web.annotations.Controller;
import com.ll.framework.web.annotations.GetMapping;
import com.ll.framework.web.annotations.PathVariable;
import com.ll.framework.web.annotations.RequestMapping;

@Controller
@RequestMapping("/testSmsLogs")
public class TestSmsLogController {
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

    @GetMapping("/{groupCode}/{id}")
    public String getItem(
            @PathVariable String groupCode,
            @PathVariable long id
    ) {
        return "group : " + groupCode + ", getItem " + id;
    }
}
