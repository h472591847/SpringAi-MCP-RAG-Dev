package com.itzixi.controller;


import com.itzixi.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @ClassName HelloController
 * @Description
 * @Author oliver
 * @Date 2026/7/14 12:11
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Resource
    private ChatService chatService;

    @GetMapping("/world")
    public String world() {
        return "Hello, World!";
    }

    @GetMapping("/chat")
    public String chat(String msg) {
        return chatService.chatTest(msg);
    }

    @GetMapping("/chat/stream/response")
    public Flux<ChatResponse> chatStreamResponse(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return chatService.streamResponse(msg);
    }

    @GetMapping("/chat/stream/str")
    public Flux<String> chatStreamStr(String msg, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return chatService.streamStr(msg);
    }
}
