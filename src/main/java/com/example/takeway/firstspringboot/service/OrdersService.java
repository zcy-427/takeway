package com.example.takeway.firstspringboot.service;

import com.example.takeway.firstspringboot.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.takeway.firstspringboot.vo.OrderVO;

/**
* @author zcy
* @description 针对表【orders】的数据库操作Service
* @createDate 2026-04-14 20:17:39
*/
public interface OrdersService extends IService<Orders> {
    // 创建订单的方法
    public void createOrder(Long userId, Long productId,String address,String remark);
    // 根据订单ID查询订单详情的方法
    public OrderVO getOrderByNo(String orderNo);
}
