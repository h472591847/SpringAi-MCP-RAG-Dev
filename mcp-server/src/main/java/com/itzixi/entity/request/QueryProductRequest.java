package com.itzixi.entity.request;


import com.itzixi.enums.ListSortEnum;
import com.itzixi.enums.PriceCompareEnum;
import lombok.Data;
import lombok.ToString;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @ClassName QueryProductRequest
 * @Description
 * @Author oliver
 * @Date 2026/8/3 15:48
 */
@ToString
@Data
public class QueryProductRequest {

    // required=true 默认会自动填充数据, 所以查询时建议使用false
    @ToolParam(description = "商品编号", required = false)
    private String productId;

    @ToolParam(description = "商品名称", required = false)
    private String productName;

    @ToolParam(description = "商品品牌", required = false)
    private String brand;

    @ToolParam(description = "具体商品价格大小", required = false)
    private Integer price;

    @ToolParam(description = "商品状态(下架为0，上架为1，预售为2)", required = false)
    private Integer status;
    // --- 排序条件 ---
    @ToolParam(description = "排序字段 (可选值: 'price', 'create_time')", required = false)
    private String sortBy;

    @ToolParam(description = "查询列表的排序", required = false)
    private ListSortEnum sortEnum;

    @ToolParam(description = "比较价格的大小", required = false)
    private PriceCompareEnum priceCompareEnum;

}
