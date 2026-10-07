[English](README.md) | [中文](README_CN.md)

# Spring AI MCP RAG Dev

> 一个基于 Java / Spring Boot 的 AI 后端实践项目，集成 **Spring AI、RAG、MCP 工具调用和联网搜索**。

本项目展示如何在不离开 Spring 生态的前提下，为传统 Java 后端引入现代 LLM 能力。

主要集成：

- 基于 OpenAI 兼容接口的 **LLM 调用**
- **RAG**（检索增强生成）：文档入库与向量检索
- **MCP**（Model Context Protocol）：把后端能力暴露为 AI 可调用的工具
- 基于 SearXNG 的**联网搜索**
- 用于 MCP 客户端/服务端通信的 **SSE**
- **Redis Stack** 作为向量库
- **MySQL + MyBatis-Plus** 存储业务数据
- MCP Client / MCP Server 分离架构

这不是一个普通的聊天机器人 Demo。重点是 **LLM 与传统后端系统之间的集成层**：数据库、知识库、搜索引擎和业务工具。

---

## 项目能做什么？

### 1. 构建 AI 后端 API

把 LLM 集成到 Spring Boot 应用中，通过 REST API 对外提供 AI 能力。典型用途：企业内部 AI 助手、AI 业务接口、AI 客服后端。

### 2. 构建 RAG 知识库

上传文档，经过解析、切分、Embedding 后存入向量库，再把检索到的内容作为上下文参与 AI 对话。

```text
文档 → 解析 → 文本处理 → Embedding
    → Redis 向量库 → 相似度检索
    → 相关上下文 → LLM → 基于知识的回答
```

### 3. 让 AI 调用后端业务能力

后端操作通过 MCP 暴露，模型可以发现并调用工具，而不只是生成文本。

当前示例工具：

- **ProductTool**：商品增删改查
- **EmailTool**：邮件相关操作
- **DateTool**：当前日期 / 时间相关操作

```text
用户请求 → LLM → 选择工具 → MCP Client
        → MCP Server → 业务工具 → 返回结果
        → LLM → 最终回答
```

### 4. RAG + MCP + 联网搜索组合

```text
                    用户
                     │
                     ▼
          Spring Boot / Spring AI
                     │
      ┌──────────────┼──────────────┐
      ▼              ▼              ▼
     RAG          联网搜索       MCP Client
      │              │              │
      ▼              ▼              ▼
 Redis 向量库      SearXNG       MCP Server
                                    │
                         ┌──────────┼──────────┐
                         ▼          ▼          ▼
                   ProductTool  EmailTool  DateTool
```

---

## 为什么做这个项目？

对大多数软件团队来说，应用 AI 的关键往往不是训练模型，而是这个问题：

> 如何把 LLM 与现有的后端服务、数据库、知识库、API 和业务工具连接起来？

本项目聚焦这一层，使用熟悉的 Spring Boot 开发方式，让 Java 开发者可以逐步为现有系统加入 AI 能力。

适合以下场景的开发者：

- Java / Spring Boot 后端
- 已有企业系统和 REST API
- 企业内部知识库
- AI 助手与 Agent 工作流
- AI 自动化

---

## 技术栈

| 类别         | 技术                   |
| ------------ | ---------------------- |
| 语言         | Java 21                |
| 后端         | Spring Boot 3.5.x      |
| AI 框架      | Spring AI 1.0.0        |
| LLM 接口     | OpenAI 兼容 API        |
| RAG / 向量库 | Redis Stack            |
| 数据库       | MySQL                  |
| 持久层       | MyBatis-Plus           |
| 工具协议     | MCP（SSE 传输）        |
| 联网搜索     | SearXNG                |
| 文档解析     | Apache Tika / Markdown |
| HTTP 客户端  | OkHttp                 |
| 基础设施     | Docker Compose         |
| 构建         | Maven                  |

---

## 系统架构

仓库包含两个 Spring Boot 应用。

### MCP Client

负责 AI 编排，并对外提供 REST API：

- LLM 调用与对话请求
- RAG 文档处理与向量检索
- 联网搜索
- 连接 MCP Server

### MCP Server

把后端能力暴露为 MCP 工具：

| 工具        | 作用                |
| ----------- | ------------------- |
| ProductTool | 商品增删改查        |
| EmailTool   | 邮件相关操作        |
| DateTool    | 当前时间 / 时区时间 |

Client 与 Server 分离，让 AI 编排和业务能力可以独立演进。Server 端可以继续扩展 `OrderTool`、`CustomerTool`、`InventoryTool`、`ReportTool` 等工具。

---

## 项目结构

```text
SpringAi-MCP-RAG-Dev
├── mcp-client
│   ├── controller      # REST API
│   ├── service         # AI / RAG / 搜索服务
│   ├── entity          # DTO 与实体
│   ├── config          # 应用配置
│   └── utils           # SSE 与工具类
│
├── mcp-server
│   ├── mcp/tool        # MCP 工具
│   ├── mcp/config      # MCP 注册 / 配置
│   ├── entity          # 实体与请求对象
│   └── mapper          # MyBatis-Plus Mapper
│
└── scripts/docker
    ├── docker-compose.yml
    └── .env
```

---

## 主要接口

| 方法 | 路径                | 说明                               |
| ---- | ------------------- | ---------------------------------- |
| POST | `/chat/doChat`      | 基础 LLM 对话                      |
| POST | `/rag/uploadRagDoc` | 上传文档并进行 RAG 处理            |
| GET  | `/rag/doSearch`     | 知识库向量检索                     |
| POST | `/rag/search`       | 以检索到的知识作为上下文的 AI 对话 |
| POST | `/internet/search`  | 通过 SearXNG 的 AI 联网搜索        |
| GET  | `/sse/connect`      | MCP 集成使用的 SSE 连接            |

---

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- Docker 与 Docker Compose
- MySQL
- 一个 OpenAI 兼容的 LLM API

### 1. 启动基础设施

```bash
cd scripts/docker
docker compose up -d
```

会启动 Redis Stack、SearXNG 等基础服务，配置见 `scripts/docker/.env`。

### 2. 配置 MCP Client

```bash
OPENAI_API_KEY=your_api_key
OPENAI_BASE_URL=your_base_url
OPENAI_MODEL=your_model_name
OPENAI_EMBEDDING_MODEL=your_embedding_model

REDIS_HOST=localhost
REDIS_PORT=9379
REDIS_PASSWORD=your_password
```

只要服务商提供 OpenAI 兼容接口，修改配置即可切换。

> 切勿把真实的 API Key 或密码提交到 GitHub。

### 3. 配置 MCP Server

```bash
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_email_password
MAIL_HOST=smtp.example.com
MAIL_PORT=465

MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_HOST=localhost
MYSQL_PORT=3306
```

### 4. 启动 MCP Server

```bash
cd mcp-server
mvn spring-boot:run
```

### 5. 启动 MCP Client

另开一个终端：

```bash
cd mcp-client
mvn spring-boot:run
```

---

## 应用场景示例

**AI 知识助手**

```text
问题 → LLM → 向量检索 → 相关文档
    → 上下文注入 → LLM → 基于知识的回答
```

**AI 业务助手**

```text
请求 → LLM ─┬─ 检索知识库
            ├─ 联网搜索
            └─ 调用 MCP 工具 → 业务系统
     → 最终回答
```

可扩展方向：

- 企业内部知识库
- AI 客服
- AI 驱动的 CRM / 电商助手
- 自动化报表与工作流自动化
- 为现有 Java/Spring 应用增加 AI 功能

---

## 本项目展示了什么

重点是 AI 工程的应用层，而不是模型训练：

- LLM API 集成
- RAG 文档入库与检索
- 向量数据库集成
- MCP Client / Server 通信
- Tool Calling
- 基于 SSE 的通信
- 联网搜索集成
- AI 与传统后端的集成

---

## 项目状态

这是一个**可运行的技术参考与学习项目**，不是成熟的 SaaS 产品。它展示的是可以进一步适配到生产系统的架构和实现模式。

用于生产环境前，建议补充：

- [ ] 认证与授权
- [ ] 参数校验与限流
- [ ] 全局异常处理，以及 LLM 调用的超时与重试
- [ ] Token 用量监控与 AI 请求链路追踪
- [ ] 结构化日志、指标与监控
- [ ] 密钥管理
- [ ] 生产级持久化与部署
- [ ] 自动化测试与 CI/CD

---

## 路线图

- [ ] 认证 / 授权
- [ ] 更多可复用的 MCP 工具
- [ ] 优化 Agent 编排
- [ ] 会话记忆
- [ ] 评测与追踪
- [ ] 基于 Docker 的应用部署
- [ ] Web UI
- [ ] 更多实际业务集成

---

## 关于

本项目来自一位拥有 15 年 Java/Spring 经验的后端工程师向 AI 应用工程转型的实践。核心理念：

> 用成熟的后端工程能力，构建真正有用的 AI 应用。

如果你想为现有的 Java/Spring Boot 应用加入 AI 能力，欢迎阅读代码并按自己的场景改造。

## 许可

仅用于学习、研究和技术参考。
