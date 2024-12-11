package com.ll.domain.home.controller;

import com.ll.framework.web.annotations.Controller;
import com.ll.framework.web.annotations.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "안녕하세요.";
    }
}
