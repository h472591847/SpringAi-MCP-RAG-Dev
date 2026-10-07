[English](README.md) | [中文](README_CN.md)

# Spring AI MCP RAG Dev

> A practical Java/Spring Boot reference project for building AI-powered backend applications with **Spring AI, RAG, MCP tools, and web search**.

This project shows how a traditional Java backend can be extended with modern LLM capabilities without leaving the Spring ecosystem.

It combines:

- **LLM integration** through an OpenAI-compatible API
- **RAG** (Retrieval-Augmented Generation) for document ingestion and vector search
- **MCP** (Model Context Protocol) to expose backend capabilities as AI-callable tools
- **Web search** through SearXNG
- **SSE** for MCP client/server communication
- **Redis Stack** as the vector store
- **MySQL + MyBatis-Plus** for application data
- A separated **MCP Client / MCP Server** architecture

This is not a generic chatbot demo. The focus is the **integration layer between LLMs and traditional backend systems**: databases, knowledge bases, search, and business tools.

---

## What Can This Project Do?

### 1. Build AI-Powered Backend APIs

Integrate an LLM into a Spring Boot application and expose AI capabilities through REST APIs. Typical uses: internal AI assistants, AI-enabled business APIs, customer support backends.

### 2. Build a RAG Knowledge Base

Upload documents, parse and chunk them, embed them, store them in a vector database, and use the retrieved context in an AI conversation.

```text
Document → Parsing → Text Processing → Embedding
        → Redis Vector Store → Similarity Search
        → Relevant Context → LLM → Grounded Answer
```

### 3. Give AI Access to Backend Tools

Backend operations are exposed through MCP, so the model can discover and call tools instead of only generating text.

Example tools included:

- **ProductTool**: product CRUD operations
- **EmailTool**: email operations
- **DateTool**: current date/time operations

```text
User Request → LLM → Tool Selection → MCP Client
            → MCP Server → Backend Tool → Tool Result
            → LLM → Final Response
```

### 4. Combine RAG + MCP + Web Search

```text
                    User
                     │
                     ▼
          Spring Boot / Spring AI
                     │
      ┌──────────────┼──────────────┐
      ▼              ▼              ▼
     RAG        Web Search      MCP Client
      │              │              │
      ▼              ▼              ▼
 Redis Vector     SearXNG       MCP Server
    Store                           │
                         ┌──────────┼──────────┐
                         ▼          ▼          ▼
                   ProductTool  EmailTool  DateTool
```

---

## Why This Project?

For most software teams, applying AI is less about training models and more about this question:

> How do we connect LLMs with existing backend services, databases, knowledge bases, APIs, and business tools?

This repository focuses on that integration layer, using familiar Spring Boot patterns so Java developers can add AI capabilities to existing systems step by step.

It is relevant if you work with:

- Java / Spring Boot backends
- Existing enterprise systems and REST APIs
- Internal knowledge bases
- AI assistants and agentic workflows
- AI automation

---

## Tech Stack

| Category           | Technology             |
| ------------------ | ---------------------- |
| Language           | Java 21                |
| Backend            | Spring Boot 3.5.x      |
| AI Framework       | Spring AI 1.0.0        |
| LLM API            | OpenAI-compatible API  |
| RAG / Vector Store | Redis Stack            |
| Database           | MySQL                  |
| Persistence        | MyBatis-Plus           |
| Tool Protocol      | MCP (SSE transport)    |
| Web Search         | SearXNG                |
| Document Parsing   | Apache Tika / Markdown |
| HTTP Client        | OkHttp                 |
| Infrastructure     | Docker Compose         |
| Build              | Maven                  |

---

## Architecture

The repository contains two Spring Boot applications.

### MCP Client

Handles AI orchestration and exposes REST APIs to external applications:

- LLM communication and chat requests
- RAG document processing and vector search
- Web search
- Connection to MCP servers

### MCP Server

Exposes backend capabilities as MCP tools:

| Tool        | Purpose                      |
| ----------- | ---------------------------- |
| ProductTool | Product CRUD operations      |
| EmailTool   | Email operations             |
| DateTool    | Current time / timezone time |

Keeping the client and server separate lets AI orchestration and business capabilities evolve independently. The server can be extended with tools such as `OrderTool`, `CustomerTool`, `InventoryTool`, or `ReportTool`.

---

## Project Structure

```text
SpringAi-MCP-RAG-Dev
├── mcp-client
│   ├── controller      # REST APIs
│   ├── service         # AI / RAG / search services
│   ├── entity          # DTOs and entities
│   ├── config          # Application configuration
│   └── utils           # SSE and utility components
│
├── mcp-server
│   ├── mcp/tool        # MCP tools
│   ├── mcp/config      # MCP registration / configuration
│   ├── entity          # Entities and request objects
│   └── mapper          # MyBatis-Plus mappers
│
└── scripts/docker
    ├── docker-compose.yml
    └── .env
```

---

## Main APIs

| Method | Endpoint            | Description                                          |
| ------ | ------------------- | ---------------------------------------------------- |
| POST   | `/chat/doChat`      | Basic LLM conversation                               |
| POST   | `/rag/uploadRagDoc` | Upload a document for RAG processing                 |
| GET    | `/rag/doSearch`     | Vector search in the knowledge base                  |
| POST   | `/rag/search`       | AI conversation using retrieved knowledge as context |
| POST   | `/internet/search`  | AI-assisted web search via SearXNG                   |
| GET    | `/sse/connect`      | SSE connection used by the MCP integration           |

---

## Quick Start

### Requirements

- JDK 21+
- Maven 3.8+
- Docker and Docker Compose
- MySQL
- An OpenAI-compatible LLM API

### 1. Start Infrastructure

```bash
cd scripts/docker
docker compose up -d
```

This starts supporting services such as Redis Stack and SearXNG. See `scripts/docker/.env` for the infrastructure configuration.

### 2. Configure the MCP Client

```bash
OPENAI_API_KEY=your_api_key
OPENAI_BASE_URL=your_base_url
OPENAI_MODEL=your_model_name
OPENAI_EMBEDDING_MODEL=your_embedding_model

REDIS_HOST=localhost
REDIS_PORT=9379
REDIS_PASSWORD=your_password
```

Any provider with an OpenAI-compatible API can be used by changing the configuration.

> Never commit real API keys or passwords to GitHub.

### 3. Configure the MCP Server

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

### 4. Start the MCP Server

```bash
cd mcp-server
mvn spring-boot:run
```

### 5. Start the MCP Client

In another terminal:

```bash
cd mcp-client
mvn spring-boot:run
```

---

## Example Application Scenarios

**AI Knowledge Assistant**

```text
Question → LLM → Vector Search → Relevant Documents
        → Context Injection → LLM → Grounded Answer
```

**AI Business Assistant**

```text
Request → LLM ─┬─ Search Knowledge Base
               ├─ Search the Web
               └─ Call MCP Tool → Business System
        → Final Response
```

Possible adaptations:

- Internal enterprise knowledge bases
- AI customer support
- AI-powered CRM or e-commerce assistants
- Automated reporting and workflow automation
- Adding AI features to existing Java/Spring applications

---

## What This Repository Demonstrates

The focus is the application layer of AI engineering, not model training:

- LLM API integration
- RAG document ingestion and retrieval
- Vector database integration
- MCP client/server communication
- Tool calling
- SSE-based communication
- Web search integration
- AI + traditional backend integration

---

## Project Status

This is a **working technical reference and learning project**, not a finished SaaS product. It demonstrates architecture and implementation patterns that can be adapted for production systems.

Before production use, consider adding:

- [ ] Authentication and authorization
- [ ] Input validation and rate limiting
- [ ] Global error handling, timeouts, and retries for LLM calls
- [ ] Token usage monitoring and AI request tracing
- [ ] Structured logging, metrics, and monitoring
- [ ] Secrets management
- [ ] Production-grade persistence and deployment
- [ ] Automated tests and CI/CD

---

## Roadmap

- [ ] Authentication / authorization
- [ ] More reusable MCP tools
- [ ] Improved agent orchestration
- [ ] Conversation memory
- [ ] Evaluation and tracing
- [ ] Docker-based application deployment
- [ ] Web UI
- [ ] More practical business integrations

---

## About

This project comes from a backend engineer with 15 years of Java/Spring experience moving into AI application engineering. The core idea:

> Use proven backend engineering practices to build useful AI applications.

If you want to add AI capabilities to an existing Java/Spring Boot application, feel free to explore the code and adapt it to your use case.

## License

For learning, research, and technical reference purposes.
