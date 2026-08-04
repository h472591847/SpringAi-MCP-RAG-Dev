package com.itzixi.mcp.config;


import com.itzixi.mcp.tool.DateTool;
import com.itzixi.mcp.tool.EmailTool;
import com.itzixi.mcp.tool.ProductTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName ToolConfiguration
 * @Description
 * @Author oliver
 * @Date 2026/7/31 15:23
 */
@Configuration
public class ToolConfiguration {
    /**
     * 注册mcp工具
     *
     * @param dateTool
     * @return
     */
    @Bean
    public ToolCallbackProvider registerToolCallbackProvider(DateTool dateTool, EmailTool emailTool, ProductTool productTool) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(dateTool, emailTool, productTool)
                .build();
    }
}
