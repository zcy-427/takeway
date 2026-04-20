package com.example.takeway.firstspringboot.controller;

import com.example.takeway.firstspringboot.service.UserService;
import com.example.takeway.firstspringboot.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password) {
        return userService.register(username, password);
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password) {
        String token = userService.login(username, password);
        return "登录成功！你的专属防伪手环(Token)是：\n" + token;
    }
}