package com.example.takeway.firstspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * orders
 */
@TableName(value ="orders")
@Data
public class Orders {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态机 (1:待支付, 2:待接单, 3:已接单, 6:已取消)
     */
    private Integer status;

    /**
     * 配送地址
     */
    private String address;

    /**
     * 下单时间
     */
    private Date createTime;

    /**
     * 状态更新时间
     */
    private Date updateTime;

    /**
     * 业务订单号 (展示给用户看的，如 ORD2023...)
     */
    private String orderNo;

    /**
     * 备注
     */
    private String remark;
}
