package com.itzixi.controller;


import com.itzixi.enums.SSEMsgType;
import com.itzixi.service.ChatService;
import com.itzixi.utils.SSEServer;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @ClassName SSEController
 * @Description
 * @Author oliver
 * @Date 2026/7/14 12:11
 */
@RestController
@RequestMapping("/sse")
public class SSEController {

    @Resource
    private ChatService chatService;

    /**
     * 前端发送连接的请求, 连接SSE服务
     *
     * @return
     */
    @GetMapping(path = "/connect", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter connect(@RequestParam String userId) {
        return SSEServer.connect(userId);
    }

    /**
     * SSE发送单条消息
     *
     * @return
     */
    @GetMapping(path = "/sendMessage")
    public Object sendMessage(@RequestParam String userId, @RequestParam String message) {
        SSEServer.sendMsg(userId, message, SSEMsgType.MESSAGE);
        return "ok";
    }

    /**
     * SSE发送单条消息 - add
     *
     * @return
     */
    @GetMapping(path = "/sendMessageAdd")
    @SneakyThrows
    public Object sendMessageAdd(@RequestParam String userId, @RequestParam String message) {
        for (int i = 0; i < 10; i++) {
            Thread.sleep(200L);
            SSEServer.sendMsg(userId, message, SSEMsgType.ADD);
        }
        return "ok";
    }

    /**
     * SSE发送多条消息
     *
     * @return
     */
    @GetMapping(path = "/sendMessageAll")
    public Object sendMessageAll(@RequestParam String message) {
        SSEServer.sendMsgToAllUsers(message);
        return "ok";
    }
}

