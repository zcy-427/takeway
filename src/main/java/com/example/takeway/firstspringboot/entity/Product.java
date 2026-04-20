package com.example.takeway.firstspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName product
 */
@TableName(value ="product")
@Data
public class Product {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 菜名
     */
    private String name;

    /**
     * 原价 (重点：绝不能用 FLOAT，必须用 DECIMAL 保证财务精度)
     */
    private BigDecimal price;

    /**
     * 当前库存 (并发扣减的核心)
     */
    private Integer stock;

    /**
     * 状态 (1:上架, 0:下架)
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}