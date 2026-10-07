# Spring AI MCP RAG Dev

> A practical Java/Spring Boot reference project for building AI-powered backend applications with **Spring AI, RAG, MCP tools, and web search**.

This project demonstrates how a traditional Java backend can be extended with modern LLM capabilities without abandoning the Spring ecosystem.

It combines:

- **LLM integration** through an OpenAI-compatible API
- **RAG** for document ingestion and vector search
- **MCP (Model Context Protocol)** for exposing backend capabilities as AI-callable tools
- **Web search** through SearXNG
- **Streaming communication** with SSE
- **Redis Stack** as the vector store
- **MySQL + MyBatis-Plus** for application data
- A separated **MCP Client / MCP Server** architecture

The goal is not to provide a generic chatbot demo, but to demonstrate a backend architecture that can be adapted to real AI application and automation scenarios.

---

## What Can This Project Do?

### 1. Build AI-Powered Backend APIs

Integrate an LLM into an existing Spring Boot application and expose AI capabilities through REST APIs.

### 2. Build a RAG Knowledge Base

Upload documents, process their content, index them into a vector store, retrieve relevant knowledge, and use the retrieved context in an AI conversation.

### 3. Give AI Access to Backend Tools

Expose backend operations through MCP so the model can discover and invoke tools instead of only generating text.

Example tools included in this project:

- Product CRUD
- Email operations
- Date/time operations

### 4. Combine RAG + MCP + Web Search

The project provides the building blocks for an AI application that can combine knowledge retrieval, external web search, and backend tool calling.

```text
User
  │
  ▼
Spring Boot / Spring AI
  │
  ├── LLM
  │
  ├── RAG ──────► Redis Vector Store
  │
  ├── Web Search ► SearXNG
  │
  └── MCP Client ───► MCP Server
                         │
                         ├── Product Tool
                         ├── Email Tool
                         └── Date Tool
