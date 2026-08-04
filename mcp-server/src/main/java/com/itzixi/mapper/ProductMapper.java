package com.itzixi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzixi.entity.Product;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author oliver
 * @Description: TODO
 * @ClassName ProductMapper
 * @Date 2025/10/8 10:31
 * @Version 1.0
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
