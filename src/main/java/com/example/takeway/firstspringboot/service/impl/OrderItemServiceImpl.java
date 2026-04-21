package com.example.takeway.firstspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.takeway.firstspringboot.entity.OrderItem;
import com.example.takeway.firstspringboot.service.OrderItemService;
import com.example.takeway.firstspringboot.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author zcy
* @description 针对表【order_item】的数据库操作Service实现
* @createDate 2026-04-14 20:17:39
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{

}




