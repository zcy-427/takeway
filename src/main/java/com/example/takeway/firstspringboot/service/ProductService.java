package com.example.takeway.firstspringboot.service;

import com.example.takeway.firstspringboot.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author zcy
* @description 针对表【product】的数据库操作Service
* @createDate 2026-04-14 20:17:39
*/
public interface ProductService extends IService<Product> {
    public List<Product> getMenuList();
}
