package com.example.takeway.firstspringboot.controller; // 注意包名

import com.example.takeway.firstspringboot.common.Result;
import com.example.takeway.firstspringboot.dto.CreateOrderDTO;
import com.example.takeway.firstspringboot.entity.Orders;
import com.example.takeway.firstspringboot.service.OrdersService; // 确保导入你自己的Service
import com.example.takeway.firstspringboot.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController // 告诉 Spring Boot 这是一个专门接客的 Web 接口类
@RequestMapping("/orders") // 挂上 "/orders" 的专属门牌号

public class OrderController {

    private final OrdersService orderService;

    // 构造器注入
    public OrderController(OrdersService orderService) {
        this.orderService = orderService;
    }

    // 专门处理 POST 请求
    @PostMapping
    public Result<String> createOrder(@RequestBody CreateOrderDTO dto, HttpServletRequest request) {
        System.out.println("====== 收到前端发来的新订单！ ======");
//        System.out.println("下单用户ID: " + dto.getUserId());
//        System.out.println("购买的菜品ID: " + dto.getProductId());
//        System.out.println("备注要求: " + dto.getRemark());
        Long realUserId = (Long) request.getAttribute("currentUserId");
        System.out.println("系统已确认真实的下单用户ID: " + realUserId);
        System.out.println("购买的菜品ID: " + dto.getProductId());

        //扣除库存、计算总金额、生成订单号、保存订单到数据库等一系列操作
        orderService.createOrder(realUserId, dto.getProductId(), dto.getAddress(), dto.getRemark());
        return Result.success("订单创建成功！",null) ;
    }

    // 专门处理 GET 请求，查询订单详情
    @GetMapping("/{orderNo}")
    public OrderVO getOrderByNo(@PathVariable("orderNo") String orderNo) {
        System.out.println("====== 收到查询订单请求！订单号: " + orderNo + " ======");

        OrderVO vo = orderService.getOrderByNo(orderNo);

        return vo;
    }
}