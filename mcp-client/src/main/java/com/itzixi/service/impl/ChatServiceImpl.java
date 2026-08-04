package com.itzixi.service.impl;


import cn.hutool.json.JSONUtil;
import com.itzixi.entity.ChatEntity;
import com.itzixi.entity.ChatResponseEntity;
import com.itzixi.entity.SearchResult;
import com.itzixi.enums.SSEMsgType;
import com.itzixi.service.ChatService;
import com.itzixi.service.SearXngService;
import com.itzixi.utils.SSEServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ChatServiceImpl
 * @Description
 * @Author oliver
 * @Date 2026/7/14 12:39
 */
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private ChatClient chatClient;

    @Resource
    private SearXngService searXngService;

    private ChatMemory chatMemory;


    private String systemPrompt = """                   
            你是一个非常聪明的人工智能助手,可以帮我解决很多问题,我为你取一个名字,你的名字叫'小柒'
            """;

    // 构造器注入, 自动配置方式(推荐)
    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                // 添加mcp工具功能
                .defaultToolCallbacks(tools)
                // 添加记忆功能
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
//                .defaultSystem(systemPrompt)
                .build();
    }

    @Override
    public String chatTest(String prompt) {
        return chatClient.prompt(prompt).call().content();
    }

    @Override
    public Flux<ChatResponse> streamResponse(String prompt) {
        return chatClient.prompt(prompt).stream().chatResponse();
    }

    @Override
    public Flux<String> streamStr(String prompt) {
        return chatClient.prompt(prompt).stream().content();
    }

    @Override
    public void doChat(ChatEntity chatEntity) {
        String userId = chatEntity.getCurrentUserName();
        String prompt = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();
        String modelName = chatEntity.getModelName();

        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        List<String> list = stringFlux.toStream().map(chatResponse -> {
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId, content, SSEMsgType.ADD);
            log.info("content:{}", content);
            return content;
        }).toList();

        String fullCollect = list.stream().collect(Collectors.joining());
        //FIXME: 可将fullCollect保存到数据库或文件中，供后续查询

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullCollect, botMsgId);
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.FINISH);
    }

    // Dify 智能体引擎构建平台
    private final String ragPrompt = """ 
            基于上下文的知识库内容回答问题:
            [上下文]
            {context}
            
            [问题]
            {question}
            
            [输出]
            如果没有查到, 请回复: 不知道.
            如果查到, 请回复具体的内容. 不相关的近似内容不必提到.
            
            """;

    @Override
    public void doChatRagSearch(ChatEntity chatEntity, List<Document> ragContext) {
        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();
        String modelName = chatEntity.getModelName();
        if (CollectionUtils.isEmpty(ragContext)) {
            log.error("未提供rag知识库的上下文");
            return;
        }
        // 构建提示词
        String context = null;
        context = ragContext.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));
        // 组装提示词
        Prompt prompt = new Prompt(ragPrompt
                .replace("{context}", context)
                .replace("{question}", question));
        log.info("组装后的提示词:{}", prompt);

        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        List<String> list = stringFlux.toStream().map(chatResponse -> {
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId, content, SSEMsgType.ADD);
            log.info("content:{}", content);
            return content;
        }).toList();

        String fullCollect = list.stream().collect(Collectors.joining());
        //FIXME: 可将fullCollect保存到数据库或文件中，供后续查询

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullCollect, botMsgId);
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.FINISH);

    }


    // Dify 智能体引擎构建平台
    private static final String searXngPrompt = """ 
            你是一个互联网搜索大师,请基于以下互联网返回的结果作为上下文,根据你的理解结合用户的提问综合后,生成并且输出专业的回答:
            [上下文]
            {context}
            
            [问题]
            {question}
            
            [输出]
            如果没有查到, 请回复: 不知道.
            如果查到, 请回复具体的内容.
            
            """;

    @Override
    public void doInternetSearch(ChatEntity chatEntity) {
        String userId = chatEntity.getCurrentUserName();
        String question = chatEntity.getMessage();
        String botMsgId = chatEntity.getBotMsgId();
        List<SearchResult> searchResults = searXngService.search(question);
        // 构建searXng搜索引擎提示词
        String finalPrompt = buildSearXngPrompt(question, searchResults);
        Prompt prompt = new Prompt(finalPrompt);
        log.info("组装后的提示词:{}", prompt);

        Flux<String> stringFlux = chatClient.prompt(prompt).stream().content();
        List<String> list = stringFlux.toStream().map(chatResponse -> {
            String content = chatResponse.toString();
            SSEServer.sendMsg(userId, content, SSEMsgType.ADD);
            log.info("content:{}", content);
            return content;
        }).toList();

        String fullCollect = list.stream().collect(Collectors.joining());
        //FIXME: 可将fullCollect保存到数据库或文件中，供后续查询

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(fullCollect, botMsgId);
        SSEServer.sendMsg(userId, JSONUtil.toJsonStr(chatResponseEntity), SSEMsgType.FINISH);

    }

    /**
     * 构建searXng搜索引擎提示词
     *
     * @param question
     * @param searchResults
     * @return
     */
    private static String buildSearXngPrompt(String question, List<SearchResult> searchResults) {
        StringBuilder context = new StringBuilder();
        searchResults.forEach(searchResult -> {
            context.append(
                    String.format("<context>\n[来源] %s \n [摘要] %s \n </context>\n",
                            searchResult.getUrl(),
                            searchResult.getContent()));
        });
        return searXngPrompt
                .replace("{context}", context.toString())
                .replace("{question}", question);
    }
}
