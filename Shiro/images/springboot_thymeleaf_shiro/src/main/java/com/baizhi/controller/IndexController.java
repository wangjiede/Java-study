package com.baizhi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

    @RequestMapping("index")
    public String index(){
        System.out.println("index主页 springboot shiro thymeleaf");
        return "index";
    }

}
