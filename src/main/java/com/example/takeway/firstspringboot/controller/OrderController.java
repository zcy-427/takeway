package com.example.takeway.firstspringboot.controller; // 注意包名

import com.example.takeway.firstspringboot.common.Result;
import com.example.takeway.firstspringboot.config.RabbitMQConfig;
import com.example.takeway.firstspringboot.dto.CreateOrderDTO;
import com.example.takeway.firstspringboot.dto.OrderMessageDTO;
import com.example.takeway.firstspringboot.entity.Orders;
import com.example.takeway.firstspringboot.service.OrdersService; // 确保导入你自己的Service
import com.example.takeway.firstspringboot.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import static io.lettuce.core.pubsub.PubSubOutput.Type.message;

@RestController // 告诉 Spring Boot 这是一个专门接客的 Web 接口类
@RequestMapping("/orders") // 挂上 "/orders" 的专属门牌号

public class OrderController {

    private final OrdersService orderService;
    private final RabbitTemplate rabbitTemplate;

    // 构造器注入
    public OrderController(OrdersService orderService,RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate=rabbitTemplate;
        this.orderService = orderService;
    }

    // 专门处理 POST 请求
    @PostMapping
    public Result<String> createOrder(@RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        System.out.println("====== 收到前端发来的新订单！ ======");
//        System.out.println("下单用户ID: " + dto.getUserId());
//        System.out.println("购买的菜品ID: " + dto.getProductId());
//        System.out.println("备注要求: " + dto.getRemark());


        // (如果想正常跑前后端联调，就换回 request.getAttribute("currentUserId"))
//        Long realUserId = 1L;测压号
      Long realUserId = (Long) request.getAttribute("currentUserId");
        System.out.println("系统已确认真实的下单用户ID: " + realUserId);
        System.out.println("购买的菜品ID: " + dto.getProductId());
        OrderMessageDTO message = new OrderMessageDTO(realUserId, dto.getProductId(), dto.getAddress(), dto.getRemark());
        //扣除库存、计算总金额、生成订单号、保存订单到数据库等一系列操作
//        orderService.createOrder(realUserId, dto.getProductId(), dto.getAddress(), dto.getRemark());
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, message);

        System.out.println("====== 服务员：订单小票已扔进信箱！立刻接待下一位客人！ ======");
        return Result.success("您的订单已进入极速排队通道，请稍后在订单列表查看结果！",null) ;
    }

    // 专门处理 GET 请求，查询订单详情
    @GetMapping("/{orderNo}")
    public OrderVO getOrderByNo(@PathVariable("orderNo") String orderNo) {
        System.out.println("====== 收到查询订单请求！订单号: " + orderNo + " ======");

        OrderVO vo = orderService.getOrderByNo(orderNo);

        return vo;
    }
}