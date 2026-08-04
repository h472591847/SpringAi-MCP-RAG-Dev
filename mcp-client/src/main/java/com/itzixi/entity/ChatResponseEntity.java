package com.itzixi.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ChatResponseEntity
 * @Description
 * @Author oliver
 * @Date 2026/7/24 16:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseEntity {
    private String message;
    private String botMsgId;
}
