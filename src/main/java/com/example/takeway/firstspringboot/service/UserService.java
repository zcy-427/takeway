package com.example.takeway.firstspringboot.service;

import com.example.takeway.firstspringboot.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.takeway.firstspringboot.mapper.UserMapper;

/**
* @author zcy
* @description 针对表【user】的数据库操作Service
* @createDate 2026-04-14 20:17:39
*/
public interface UserService extends IService<User> {
    public String register(String username, String rawPassword);// 注册方法，返回注册结果
    public String login(String username, String rawPassword);// 登录方法，返回登录结果
}
