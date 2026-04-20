package com.example.takeway.firstspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName order_item
 */
@TableName(value ="order_item")
@Data
public class OrderItem {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 关联的订单主表 ID
     */
    private String orderNo;

    /**
     * 关联的菜品 ID
     */
    private Long productId;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 下单时的历史快照单价
     */
    private BigDecimal snapshotPrice;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 菜名
     */
    private String productName;
}