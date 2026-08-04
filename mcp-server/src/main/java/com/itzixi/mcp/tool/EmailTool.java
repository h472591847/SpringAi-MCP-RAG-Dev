package com.itzixi.mcp.tool;


import com.itzixi.entity.request.EmailRequest;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @ClassName EmailTool
 * @Description
 * @Author oliver
 * @Date 2026/7/31 14:07
 */
@Component
@Slf4j
public class EmailTool {

    private final JavaMailSender mailSender;
    private final String from;

    @Autowired
    public EmailTool(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }


    @Tool(name = "sendMailMessage", description = "为指定邮箱发送邮件信息, email为收件人邮箱, subject为邮件标题, message为邮件内容")
    public void sendMailMessage(EmailRequest emailRequest) {
        log.info("====== 调用MCP工具:sendMailMessage() ======");
        log.info("====== 参数emailRequest:{} ======", emailRequest.toString());

        Integer contentType = emailRequest.getContentType();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom(from);
            helper.setTo(emailRequest.getEmail());
            helper.setSubject(emailRequest.getSubject());
            //细节打磨,  根据内容类型来判断邮件内容是html格式还是markdown格式
            if (contentType == 1) {
                helper.setText(covert(emailRequest.getMessage()), true);
            } else if (contentType == 2) {
                helper.setText(emailRequest.getMessage(), true);
            } else {
                helper.setText(emailRequest.getMessage());
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("发送邮件失败, 报错信息:{}", e.getMessage());
        }

    }


    @Tool(description = "查询我的邮件/邮箱地址")
    public String getMyEmailAddress() {
        log.info("====== 调用MCP工具：getMyEmailAddress() ======");

        return "472591847@qq.com";
    }


    /**
     * MarkDown转HTML
     *
     * @param markdown
     * @return String
     */
    public static String covert(String markdown) {
        // flexmark 的配置对象，用于设置 Markdown 解析和渲染的选项。这里创建一个空配置，表示使用默认规则
        MutableDataSet options = new MutableDataSet();

        // 根据传入的 options 配置构建一个 Markdown 解析器   Markdown → AST（抽象语法树） 的解析器。
        Parser parser = Parser.builder(options).build();
        // HtmlRenderer 是 AST → HTML 的渲染器   读取 Node（语法树），并输出标准 HTML。
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // 把 Markdown 文本传给 parser.parse()，解析成一个 语法树结构（Node）
        Node document = parser.parse(markdown);

        // 把语法树交给渲染器 renderer，返回HTML字符串
        return renderer.render(document);
    }
}
