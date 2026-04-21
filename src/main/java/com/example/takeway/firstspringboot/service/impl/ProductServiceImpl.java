package com.example.takeway.firstspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.takeway.firstspringboot.entity.Product;
import com.example.takeway.firstspringboot.service.ProductService;
import com.example.takeway.firstspringboot.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
* @author zcy
* @description 针对表【product】的数据库操作Service实现
* @createDate 2026-04-14 20:17:39
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

}




