package com.itzixi.utils;


import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.List;

/**
 * @ClassName CustomTextSplitter
 * @Description
 * @Author oliver
 * @Date 2026/7/27 12:22
 */
public class CustomTextSplitter extends TextSplitter {

    @Override
    protected List<String> splitText(String text) {
        return List.of(split(text));
    }

    private String[] split(String text) {
        return text.split("\\r\\n\\r\\n");
    }
}
