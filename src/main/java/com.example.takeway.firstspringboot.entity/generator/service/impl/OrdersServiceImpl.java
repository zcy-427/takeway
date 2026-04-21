package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.domain.Orders;
import generator.service.OrdersService;
import generator.mapper.OrdersMapper;
import org.springframework.stereotype.Service;

/**
* @author zcy
* @description 针对表【orders】的数据库操作Service实现
* @createDate 2026-04-17 14:19:54
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

}




