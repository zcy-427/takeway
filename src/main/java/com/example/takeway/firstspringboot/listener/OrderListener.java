package com.example.takeway.firstspringboot.listener;

import com.example.takeway.firstspringboot.config.RabbitMQConfig;
import com.example.takeway.firstspringboot.dto.OrderMessageDTO;
import com.example.takeway.firstspringboot.service.OrdersService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
    private final OrdersService ordersService;

    public OrderListener(OrdersService ordersService) {
        this.ordersService = ordersService;
    }
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderMessage(OrderMessageDTO message){
        System.out.println(">>> 大厨：从信箱拿到了 VIP [" + message.getUserId() + "] 的小票，开始抢锁做菜！ <<<");
        try {
            // 调用我们极其强悍的防超卖 Service（带 Redisson 锁和编程式事务）
            ordersService.createOrder(
                    message.getUserId(),
                    message.getProductId(),
                    message.getAddress(),
                    message.getRemark()
            );
            System.out.println(">>> 大厨：VIP [" + message.getUserId() + "] 的烤鸭做好了，已入库！ <<<");
        } catch (Exception e) {
            // 如果抢不到锁、或者库存没了，这里的异常会被捕获，订单悄无声息地失败。
            // (在真实的生产环境中，这里其实会把失败状态写进数据库或者发一条 WebSocket 告诉前端“很遗憾被抢光了”)
            System.err.println(">>> 大厨：做菜失败，原因：" + e.getMessage() + " <<<");
        }
    }
}


