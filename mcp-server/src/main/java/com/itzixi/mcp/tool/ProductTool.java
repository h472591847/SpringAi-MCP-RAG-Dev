package com.itzixi.mcp.tool;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itzixi.entity.Product;
import com.itzixi.entity.request.CreatProductRequest;
import com.itzixi.entity.request.DeleteProductRequest;
import com.itzixi.entity.request.ModifyProductRequest;
import com.itzixi.entity.request.QueryProductRequest;
import com.itzixi.enums.ListSortEnum;
import com.itzixi.enums.PriceCompareEnum;
import com.itzixi.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

/**
 * @ClassName ProductTool
 * @Description
 * @Author oliver
 * @Date 2026/7/31 14:07
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProductTool {

    private final ProductMapper productMapper;

    @Tool(name = "createProduct", description = "创建/新增产品信息")
    public String createProduct(CreatProductRequest request) {
        log.info("====== 调用MCP工具：createProduct() ======");
        log.info("====== 创建/新增产品参数：{} ======", request);
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        // 生成随机的12位主键
        product.setProductId(RandomStringUtils.randomAlphanumeric(12));

        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());

        productMapper.insert(product);

        return "创建/新增产品成功";
    }

    @Transactional
    @Tool(name = "deleteProduct", description = "删除一个或多个产品。通过名称和品牌精确删除。")
    public String deleteProduct(DeleteProductRequest request) {
        log.info("====== 调用MCP工具：deleteProduct() ======");
        log.info("====== 删除产品参数，productName: {} ======", request);

        // 安全检查：防止没有任何条件时误删全表数据
        if (!StringUtils.isBlank(request.getProductName()) && !StringUtils.isBlank(request.getBrand())) {
            return "操作失败：删除条件不能为空，请至少提供一个删除条件（如产品名称或品牌）。";
        }

        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        StringJoiner description = new StringJoiner("，"); // 用于拼接删除条件的描述

        // 动态构建查询条件
        if (StringUtils.isNotBlank(request.getProductName())) {
            queryWrapper.eq("product_name", request.getProductName());
            description.add("名称为 '" + request.getProductName() + "'");
        }

        if (StringUtils.isNotBlank(request.getBrand())) {
            queryWrapper.eq("brand", request.getBrand());
            description.add("品牌为 '" + request.getBrand() + "'");
        }
        int deletedRows = productMapper.delete(queryWrapper);

        if (deletedRows > 0) {
            String successMsg = String.format("操作成功, 共删除了 %d 个符合条件 [%s] 的产品。", deletedRows, description);
            log.info(successMsg);
            return successMsg;
        } else {
            String failureMsg = String.format("操作失败, 未找到任何符合条件 [%s] 的产品。", description);
            log.warn(failureMsg);
            return failureMsg;
        }
    }


    @Tool(name = "getSortEnum", description = "把排序(正序/倒序)转换为对应的枚举")
    public ListSortEnum getSortEnum(String sort) {
        log.info("====== 调用MCP工具：getSortEnum() ======");
        log.info("====== 转换排序枚举参数，sort: {} ======", sort);
        return ListSortEnum.getTypeByValue(sort);
    }

    @Tool(name = "getPriceCompareEnum", description = "把产品价格的比较(大于,小于,等于,大于等于,小于等于,高于,低于,不高于,不低于)")
    public PriceCompareEnum getPriceCompareEnum(String priceCompare) {
        log.info("====== 调用MCP工具：getPriceCompareEnum() ======");
        log.info("====== 转换产品价格比较的枚举参数，priceCompare: {} ======", priceCompare);
        return PriceCompareEnum.getTypeByValue(priceCompare);
    }

    @Tool(name = "queryProduct", description = "根据条件查询产品(product)信息")
    public List<Product> queryProductListByCondition(QueryProductRequest request) {
        log.info("====== 调用MCP工具：queryProduct() ======");
        log.info("====== 根据条件查询产品参数，QueryProductRequest: {} ======", request.toString());
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        String productId = request.getProductId();
        String productName = request.getProductName();
        String brand = request.getBrand();
        Integer status = request.getStatus();

        Integer price = request.getPrice();
        PriceCompareEnum priceCompareEnum = request.getPriceCompareEnum();

        String sortBy = request.getSortBy();
        ListSortEnum sortEnum = request.getSortEnum();

        queryWrapper.eq(StringUtils.isNotBlank(productId), "product_id", productId);
        queryWrapper.like(StringUtils.isNotBlank(productName), "product_name", productName);
        queryWrapper.like(StringUtils.isNotBlank(brand), "brand", brand);
        queryWrapper.eq(status != null, "status", status);
        if (ObjectUtils.isNotEmpty(price) && ObjectUtils.isNotEmpty(priceCompareEnum)) {
            switch (priceCompareEnum) {
                case GREATER_THAN, HIGHER_THAN -> queryWrapper.gt("price", price);
                case LESS_THAN, LOWER_THAN -> queryWrapper.lt("price", price);
                case EQUAL_TO -> queryWrapper.eq("price", price);
                case GREATER_THAN_OR_EQUAL_TO, NOT_LOWER_THAN -> queryWrapper.ge("price", price);
                case LESS_THAN_OR_EQUAL_TO, NOT_HIGHER_THAN -> queryWrapper.le("price", price);
            }
        }

        if (ObjectUtils.isNotEmpty(sortEnum) && StringUtils.isNotBlank(sortBy)) {

            switch (sortEnum) {
                case ASC -> queryWrapper.orderByAsc(sortBy);
                case DESC -> queryWrapper.orderByDesc(sortBy);
            }
        }

        return productMapper.selectList(queryWrapper);
    }

    @Tool(name = "modifyProduct", description = "根据产品id/编号修改产品信息")
    public String modifyProduct(ModifyProductRequest request) {
        log.info("====== 调用MCP工具：modifyProduct() ======");
        log.info("====== 根据条件查询产品参数，ModifyProductRequest: {} ======", request.toString());
        if(ObjectUtils.isEmpty(request.getProductId())){
            return "产品编号不能为空";
        }
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setUpdateTime(LocalDateTime.now());
        int update = productMapper.updateById(product);
        if (update <= 0) {
            return "产品信息更新失败, 或产品信息不存在";
        }

        return "产品信息更新成功";
    }

}
