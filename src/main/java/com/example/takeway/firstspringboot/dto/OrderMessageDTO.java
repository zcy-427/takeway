package com.example.takeway.firstspringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageDTO implements Serializable {
    private Long userId;
    private Long productId;
    private String address;
    private String remark;
}
