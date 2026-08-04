package com.itzixi.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName SearXngResponse
 * @Description
 * @Author oliver
 * @Date 2026/7/30 12:54
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearXngResponse {

    /**
     * 搜索关键词
     */
    private String query;

    /**
     * 搜索结果列表
     */
    private List<SearchResult> results;

}
