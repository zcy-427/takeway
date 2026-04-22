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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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

    private final RedissonClient redissonClient;
    private final TransactionTemplate transactionTemplate;

    //构造器注入
    public OrdersServiceImpl(ProductMapper productMapper, OrdersMapper ordersMapper, OrderItemMapper orderItemMapper,RedissonClient redissonClient,
                             TransactionTemplate transactionTemplate) {
        this.productMapper = productMapper;
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
        this.redissonClient=redissonClient;
        this.transactionTemplate=transactionTemplate;
    }


    public void createOrder(Long userId, Long productId,String address,String remark) {
        // 1. 打造专属门锁
        String lockKey = "lock:product:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 2. 尝试拿钥匙：最多等 5 秒，拿到锁后保护 30 秒
            boolean isLocked = lock.tryLock(5, 30, TimeUnit.SECONDS);
            if (!isLocked) {
                // 直接在这里把 9999 个人拦住，绝不让他们碰到 MySQL！
                throw new RuntimeException("抢购太火爆啦，系统排队中，请稍后再试！");
            }

            System.out.println("====== VIP " + userId + " 挤进大门，进入绝对安全的结账区！ ======");

            // 3. 极其关键：拿到了锁之后，再手动开启数据库事务！
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    // 执行核心扣减逻辑
                    executeCoreOrderLogic(userId, productId, address, remark);
                } catch (Exception e) {
                    status.setRollbackOnly(); // 发生异常，回滚这一个人的事务
                    throw e;
                }
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("排队被挤出去了，请重试！");
        } finally {
            // 4. 无论如何，归还钥匙
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                System.out.println("====== VIP " + userId + " 结账完毕，交还大门钥匙！ ======");
            }
        }
    }

    private void executeCoreOrderLogic(Long userId, Long productId, String address, String remark) {
        // 1. 查菜品信息 (这里查的是内存态的快照)
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("抱歉，您点的菜品不存在！");
        }
        if (product.getStock() <= 0) {
            throw new RuntimeException("手慢了，已被抢空！");
        }

        // 2. 🚨 使用你手写的终极 SQL 扣减库存 (双保险)
        int affectedRows = productMapper.reduceStock(productId, 1);
        if (affectedRows == 0) {
            throw new RuntimeException("手慢了，烤鸭已被抢空！");
        }

        // 3. 生成订单主表
        Orders order = new Orders();
        order.setUserId(userId);
        order.setStatus(1); // 待支付
        String orderNo = UUID.randomUUID().toString().replace("-", "");
        order.setOrderNo(orderNo);
        order.setAddress(address);
        order.setRemark(remark);
        order.setTotalAmount(product.getPrice());
        Date now = new Date();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        ordersMapper.insert(order);

        // 4. 生成订单明细快照
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderNo(orderNo);
        orderItem.setProductId(productId);
        orderItem.setProductName(product.getName());
        orderItem.setSnapshotPrice(product.getPrice());
        orderItem.setQuantity(1);
        orderItem.setCreateTime(now);
        orderItem.setUpdateTime(now);
        orderItemMapper.insert(orderItem);
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
