package com.example.takeway.firstspringboot.mapper;

import com.example.takeway.firstspringboot.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zcy
* @description 针对表【orders】的数据库操作Mapper
* @createDate 2026-04-14 20:17:39
* @Entity generator.domain.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




