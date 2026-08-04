package com.itzixi.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName SearchResult
 * @Description
 * @Author oliver
 * @Date 2026/7/30 12:54
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {
    private String title;
    private String url;
    private String content;
    private double score;

}
