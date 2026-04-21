package com.example.takeway.firstspringboot.mapper;

import com.example.takeway.firstspringboot.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
* @author zcy
* @description 针对表【product】的数据库操作Mapper
* @createDate 2026-04-14 20:17:39
* @Entity generator.domain.Product
*/
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    // 自定义高并发安全扣库存 SQL
    @Update("update product set stock = stock - #{num} where id = #{id} and stock >= #{num}")
    int reduceStock(@Param("id") Long id, @Param("num") Integer num);
}




