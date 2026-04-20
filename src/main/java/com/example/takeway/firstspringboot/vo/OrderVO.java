package com.example.takeway.firstspringboot.vo;

import com.example.takeway.firstspringboot.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class OrderVO {
    private String OrderNO;
    private BigDecimal totalAmount;
    private String address;
    private String remark;
    private Integer status;
    private Date createTime;

    private List<OrderItemVO> items;
    @Data
    public static class OrderItemVO {
        private String productName;
        private BigDecimal price;
        private Integer buyNum;
    }

}
