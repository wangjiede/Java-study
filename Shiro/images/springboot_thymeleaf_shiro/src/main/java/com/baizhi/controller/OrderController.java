package com.baizhi.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("order")
public class OrderController {


    @RequiresRoles(value={"admin","user"})//用来判断角色  同时具有 admin user
    @RequiresPermissions("user:update:01") //用来判断权限字符串
    @RequestMapping("save")
    public String save(){
        System.out.println("进入方法");
        return "redirect:/index.jsp";
    }

}
