package com.example.takeway.firstspringboot.mapper;

import com.example.takeway.firstspringboot.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zcy
* @description 针对表【user】的数据库操作Mapper
* @createDate 2026-04-14 20:17:39
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




