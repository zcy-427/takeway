package com.example.takeway.firstspringboot.dto;

import lombok.Data;

@Data // Lombok 注解，自动帮你生成 get/set 方法
public class CreateOrderDTO {
//    private Long userId;
    private Long productId;
    private String address;
    private String remark;
}

