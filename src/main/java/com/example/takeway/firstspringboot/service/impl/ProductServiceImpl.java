package com.example.takeway.firstspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.takeway.firstspringboot.entity.Product;
import com.example.takeway.firstspringboot.service.ProductService;
import com.example.takeway.firstspringboot.mapper.ProductMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author zcy
* @description 针对表【product】的数据库操作Service实现
* @createDate 2026-04-14 20:17:39
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService{
    // 🚨 注入 Redis 极速操作模板
    private final StringRedisTemplate stringRedisTemplate;
    // 🚨 注入 JSON 转换工具（用来把菜单对象打包成字符串存进 Redis）
    private final ObjectMapper objectMapper;

    public ProductServiceImpl(ObjectMapper objectMapper,StringRedisTemplate stringRedisTemplate)
    {
        this.objectMapper=objectMapper;
        this.stringRedisTemplate=stringRedisTemplate;
    }
    @Override
    public List<Product> getMenuList() {
        String cacheKey = "cache:product:list";
        try {
            // 从Redis中获取缓存数据
            String menuJson = stringRedisTemplate.opsForValue().get(cacheKey);
            if (menuJson != null && !menuJson.isEmpty()) {
                System.out.println("====== 🟢 缓存命中 (Cache Hit)！直接从 Redis 大堂返回菜单，速度起飞！ ======");
                // 把 JSON 字符串拆包成 List 集合返回
                return objectMapper.readValue(menuJson, new TypeReference<List<Product>>() {});
            }

            //缓存未命中，去MySQL查询
            System.out.println("====== 🔴 缓存未命中 (Cache Miss)！正在艰难地开启 MySQL 冷库... ======");
            List<Product> dbMenuList = this.list(); // 这是 MyBatis-Plus 自带的查全表方法
            //回写 Redis
            if (dbMenuList != null && !dbMenuList.isEmpty()) {
                String writeJson = objectMapper.writeValueAsString(dbMenuList);
                stringRedisTemplate.opsForValue().set(cacheKey, writeJson, 1, TimeUnit.HOURS);
                System.out.println("====== 📝 已将最新菜单复印件存入 Redis 大堂！ ======");
            }
            return dbMenuList;
        }catch (Exception e)
        {
            System.err.println("Redis 出现异常，降级走数据库兜底！" + e.getMessage());
            return this.list();
        }
    }
}




