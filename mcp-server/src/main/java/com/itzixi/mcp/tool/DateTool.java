package com.itzixi.mcp.tool;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName DateTool
 * @Description
 * @Author oliver
 * @Date 2026/7/31 14:07
 */
@Component
@Slf4j
public class DateTool {

    @Tool(name = "getCurrentTimeByZoneId", description = "根据城市所在的时区id来获取当前的时间")
    public String getCurrentTimeByZoneId(String cityName, String zoneId) {
        log.info("====== 调用MCP工具:getCurrentTimeByZoneId() ======");
        log.info("====== 参数cityName:{} ======", cityName);
        log.info("====== 参数zoneId:{} ======", zoneId);

        ZoneId zone = ZoneId.of(zoneId);
        // 获取该时区对应的当前时间
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);

        return String.format("当前的时间是 %s",
                zonedDateTime.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }


    @Tool(name = "getCurrentTime", description = "获取当前时间")
    public String getCurrentTime() {
        log.info("====== 调用MCP工具:getCurrentTime() ======");
        return String.format("当前的时间是 %s",
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
