# Spring AI MCP RAG Dev

基于 Spring Boot 3.5 + Spring AI 1.0 构建的 MCP（Model Context Protocol）智能体应用，集成 RAG 知识库检索、联网搜索、MCP 工具调用等 AI 能力。

## 技术栈

| 类别 | 技术 |
|------|------|
| 语言/JDK | Java 21 |
| 框架 | Spring Boot 3.5.14、Spring AI 1.0.0 |
| AI 模型 | OpenAI 兼容接口（阿里云百炼） |
| 向量存储 | Redis Stack |
| 数据库 | MySQL + MyBatis-Plus |
| MCP 通信 | SSE（Server-Sent Events） |
| 联网搜索 | SearXNG |
| 工具库 | Hutool、OkHttp、Lombok、Apache Commons |

## 项目结构

```
SpringAi-MCP-RAG-Dev
├── mcp-client          # MCP 客户端模块
│   ├── controller      # 接口层（对话、RAG、联网搜索、SSE）
│   ├── service         # 业务层（ChatService、DocumentService、SearXngService）
│   ├── entity          # 实体与响应对象
│   ├── config          # 配置类（CORS、OkHttp）
│   └── utils           # 工具类（SSEServer、文本分割器）
│
├── mcp-server          # MCP 服务端模块
│   ├── mcp/tool        # MCP 工具（ProductTool、EmailTool、DateTool）
│   ├── mcp/config      # MCP 工具注册配置
│   ├── entity          # 数据实体与请求对象
│   └── mapper          # MyBatis-Plus Mapper 层
│
└── scripts/docker      # Docker Compose 编排文件
```

## 模块说明

### mcp-client

MCP 客户端，负责与 AI 模型交互，提供以下核心接口：

| 接口路径 | 功能 |
|----------|------|
| `POST /chat/doChat` | 基础 AI 对话 |
| `POST /rag/uploadRagDoc` | 上传 RAG 知识文档 |
| `GET /rag/doSearch` | 知识库向量检索 |
| `POST /rag/search` | 基于知识库的 RAG 对话 |
| `POST /internet/search` | 基于 SearXNG 的联网搜索对话 |
| `GET /sse/connect` | SSE 流式连接 |

### mcp-server

MCP 服务端，通过 SSE 暴露 AI 可调用的工具集：

| 工具 | 功能 |
|------|------|
| `ProductTool` | 产品 CRUD（创建/查询/修改/删除） |
| `EmailTool` | 发送邮件、查询邮箱地址 |
| `DateTool` | 获取当前时间 / 指定时区时间 |

## 快速开始

### 1. 环境准备

- JDK 21+
- Maven 3.8+
- Docker（用于运行 Redis Stack 和 SearXNG）

### 2. 启动基础设施

```bash
cd scripts/docker
docker-compose up -d
```

将启动以下服务：
- **Redis Stack**：端口 `9379`（RedisInsight 可视化：`8001`）
- **SearXNG**：端口 `6080`

### 3. 配置环境变量

分别编辑两个模块的 `.env` 文件：

**mcp-client**：配置 AI 模型与 Redis 连接
```env
OPENAI_API_KEY=your_api_key
OPENAI_BASE_URL=your_base_url
OPENAI_MODEL=your_model_name
OPENAI_EMBEDDING_MODEL=your_embedding_model
REDIS_HOST=localhost
REDIS_PORT=9379
REDIS_PASSWORD=your_password
```

**mcp-server**：配置邮箱与数据库
```env
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_password
MAIL_HOST=smtp.xxx.com
MAIL_PORT=465
MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_HOST=localhost
MYSQL_PORT=3306
```

### 4. 启动应用

```bash
# 启动 MCP Server（先启动，供 Client 连接）
cd mcp-server
mvn spring-boot:run

# 启动 MCP Client
cd mcp-client
mvn spring-boot:run
```

## 许可证

本项目仅供学习参考使用。
