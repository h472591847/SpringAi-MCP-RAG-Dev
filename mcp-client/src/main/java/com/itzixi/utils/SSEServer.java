package com.itzixi.utils;


import com.itzixi.enums.SSEMsgType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @ClassName SSEServer
 * @Description
 * @Author oliver
 * @Date 2026/7/20 15:00
 */
@Slf4j
public class SSEServer {

    // 存放所有用户
    private static final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    /**
     * 创建sse连接
     *
     * @param userId
     * @return SseEmitter
     */
    public static SseEmitter connect(String userId) {
        // 设置超时时间, 0L表示不超时(永不过期) , 默认值是30秒, 超时未完成会抛出异常
        SseEmitter sseEmitter = new SseEmitter(0L);
        // 注册回调方法
        sseEmitter.onTimeout(timeoutCallBack(userId));
        // 完成回调方法
        sseEmitter.onCompletion(completionCallBack(userId));
        // 错误回调方法
        sseEmitter.onError(errorCallback(userId));
        // 存储用户连接
        sseClients.put(userId, sseEmitter);

        log.info("SSE连接创建成功, 用户ID为: {}", userId);

        return sseEmitter;
    }

    public static Runnable timeoutCallBack(String userId) {
        return () -> {
            log.info("SSE超时, 超时用户ID为: {}", userId);
            // 移除用户连接
            remove(userId);
        };
    }

    public static Runnable completionCallBack(String userId) {
        return () -> {
            log.info("SSE完成, 完成用户ID为: {}", userId);
            // 移除用户连接
            remove(userId);
        };
    }

    public static Consumer<Throwable> errorCallback(String userId) {
        return (Throwable throwable) -> {
            log.info("SSE错误, 错误用户ID为: {}", userId);
            // 移除用户连接
            remove(userId);
        };
    }

    public static void remove(String userId) {
        // 删除用户
        sseClients.remove(userId);
        log.info("SSE连接被移除, 移除用户ID为: {}", userId);
    }

    /**
     * 发送消息(功能实现)
     *
     * @param sseEmitter
     * @param userId
     * @param message
     * @param msgType
     */
    private static void sendEmitterMsg(SseEmitter sseEmitter, String userId, String message, SSEMsgType msgType) {
        SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                .data(message)
                .id(userId)
                .name(msgType.getType());
        try {
            sseEmitter.send(eventBuilder);
        } catch (Exception e) {
            log.error("SSE发送消息异常, 异常用户ID为: {}, 异常信息:{}, 异常堆栈:{}", userId, e.getMessage(), ExceptionUtils.getStackTrace(e));            // 移除用户连接
            remove(userId);
        }
    }

    /**
     * SSE发送单个消息
     *
     * @param userId
     * @param message
     * @param msgType
     */
    public static void sendMsg(String userId, String message, SSEMsgType msgType) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }
        if (!sseClients.containsKey(userId)) {
            return;
        }
        SseEmitter sseEmitter = sseClients.get(userId);
        if (ObjectUtils.isEmpty(sseEmitter)) {
            return;
        }
        sendEmitterMsg(sseEmitter, userId, message, msgType);
    }


    /**
     * SSE发送群消息
     *
     * @param message
     */
    public static void sendMsgToAllUsers(String message) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }
        //批量发送消息
        sseClients.forEach((userId, sseEmitter) -> {
            sendEmitterMsg(sseEmitter, userId, message, SSEMsgType.MESSAGE);
        });
    }
}
