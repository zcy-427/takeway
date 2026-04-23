package com.example.takeway.firstspringboot.controller;

import com.example.takeway.firstspringboot.common.Result;
import com.example.takeway.firstspringboot.entity.Product;
import com.example.takeway.firstspringboot.service.ProductService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final StringRedisTemplate stringRedisTemplate;

    public ProductController(ProductService productService, org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate) {
        this.productService = productService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping("/list")
    public Result<List<Product>> getMenuList() {
        System.out.println("====== 收到用户获取菜单请求！ ======");
        List<Product> list = productService.getMenuList();
        return Result.success(list,"获取菜单成功！");
    }

    @GetMapping("/flush-cache")
    public Result<String> flushCache() {
        stringRedisTemplate.delete("cache:product:list");
        return Result.success("旧缓存已清空！下一个客人来将被迫查 MySQL！", null);
    }
}
