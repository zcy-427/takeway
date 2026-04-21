package com.example.takeway.firstspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.takeway.firstspringboot.entity.User;
import com.example.takeway.firstspringboot.service.UserService;
import com.example.takeway.firstspringboot.mapper.UserMapper;
import com.example.takeway.firstspringboot.utils.JwtUtil;
import lombok.Data;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
* @author zcy
* @description 针对表【user】的数据库操作Service实现
* @createDate 2026-04-14 20:17:39
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String register(String username, String rawPassword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        if (userMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("该用户名已被注册，换一个吧！");
        }

        // 2. 🚨 极其关键：启动粉碎机！对明文密码进行 BCrypt 加盐哈希加密
        // 哪怕两个人都设置了 123456，加密后的密文也完全不同！
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // 3. 存入数据库
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword); // 存入的是极其安全的乱码！
//        user.setPhone();
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userMapper.insert(user);

        return "注册成功！赶紧去登录吧！";
    }

    @Override
    public String login(String username, String rawPassword) {
        // 1. 根据用户名从冷库捞出这个用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 2. 如果捞不到，说明账号不存在
        if (user == null) {
            throw new RuntimeException("账号不存在！");
        }

        // 3. 🚨 极其关键：密码比对！
        // 因为我们连原始密码都不知道，所以只能把用户现在输入的密码扔进机器，看能不能匹配上数据库里的乱码
        if (!BCrypt.checkpw(rawPassword, user.getPassword())) {
            throw new RuntimeException("密码错误！保安：抓起来！");
        }

        // 4. 账号密码完全正确，调用工具类，为这位真实的 VIP 颁发手环！
        System.out.println("====== 真实用户登录成功！ID: " + user.getId() + " ======");
        return JwtUtil.createToken(user.getId());
    }

}




