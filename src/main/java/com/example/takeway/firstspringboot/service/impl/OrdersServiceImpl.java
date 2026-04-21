package com.example.takeway.firstspringboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.takeway.firstspringboot.entity.OrderItem;
import com.example.takeway.firstspringboot.entity.Orders;
import com.example.takeway.firstspringboot.entity.Product;
import com.example.takeway.firstspringboot.mapper.OrderItemMapper;
import com.example.takeway.firstspringboot.mapper.ProductMapper;
import com.example.takeway.firstspringboot.service.OrdersService;
import com.example.takeway.firstspringboot.mapper.OrdersMapper;
import com.example.takeway.firstspringboot.vo.OrderVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
* @author zcy
* @description 针对表【orders】的数据库操作Service实现
* @createDate 2026-04-14 20:17:39
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{
    private final ProductMapper productMapper;
    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;

    //构造器注入
    public OrdersServiceImpl(ProductMapper productMapper, OrdersMapper ordersMapper, OrderItemMapper orderItemMapper) {
        this.productMapper = productMapper;
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
    }

    //开启事务保护
    @Transactional
    public void createOrder(Long userId, Long productId,String address,String remark) {
        //查询点的菜的信息
        Product product=productMapper.selectById(productId);
        if(product==null)
        {
            throw new RuntimeException("抱歉，您点的菜品不存在！");
        }
        // 2.生成订单（此时数据暂存在事务日志中，并未真正持久化）
        Orders order = new Orders();
        order.setUserId(userId);
        order.setStatus(1); // 待支付
        String orderNo = UUID.randomUUID().toString().replace("-", "");
        order.setOrderNo(orderNo);
        order.setAddress(address);
        order.setRemark(remark);
        //获取订单总金额
        order.setTotalAmount(product.getPrice());
        Date now = new Date();
        order.setCreateTime(now);
        order.setUpdateTime(now);

        //3.生成快照订单

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(productId);
        orderItem.setProductName(product.getName());
        orderItem.setSnapshotPrice(product.getPrice());
        orderItem.setQuantity(1);
        orderItem.setCreateTime(now);
        orderItem.setUpdateTime(now);
        orderItemMapper.insert(orderItem);

        //4.订单入库
        ordersMapper.insert(order);

        //5.扣减库存
        int affectedRows = productMapper.reduceStock(productId, 1);

        if (affectedRows == 0) {
            // 极其重要：如果返回 0，说明刚才的 WHERE stock >= 1 条件没满足，库存已经被别人抢光了！
            // 此时必须主动抛出异常，触发 @Transactional 机制，把第 1 步插入的订单回滚掉！
            throw new RuntimeException("手慢了，烤鸭已被抢空！");
        }
    }

    @Override
    public OrderVO getOrderByNo(String orderNo) {
        LambdaQueryWrapper<Orders> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.eq(Orders::getOrderNo, orderNo);
        Orders order = ordersMapper.selectOne(orderWrapper);
        if (order == null) {
            throw new RuntimeException("查无此单！");
        }

        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderNo, orderNo);
        List<OrderItem> itemList = orderItemMapper.selectList(itemWrapper);

        OrderVO vo = new OrderVO();
        vo.setOrderNO(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        vo.setAddress(order.getAddress());
        vo.setRemark(order.getRemark());
        List<OrderVO.OrderItemVO> itemVOList = new ArrayList<>();
        for (OrderItem item : itemList) {
            OrderVO.OrderItemVO itemVO = new OrderVO.OrderItemVO(); // 准备干净的小碟子

            // 只挑前端需要的字段给出去，绝不泄露 id
            itemVO.setProductName(item.getProductName());
            itemVO.setPrice(item.getSnapshotPrice());
            itemVO.setBuyNum(item.getQuantity());


            itemVOList.add(itemVO); // 放进托盘
        }

        vo.setItems(itemVOList);

        return vo;
    }
}
