package com.itzixi.service;


import com.itzixi.entity.SearchResult;

import java.util.List;

/**
 * @ClassName SearXngService
 * @Description
 * @Author oliver
 * @Date 2026/7/14 12:39
 */
public interface SearXngService {
    /**
     * 调用本地搜索引擎searXng进行搜索
     * @param query
     * @return
     */
    List<SearchResult> search(String query);

}
