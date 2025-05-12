# AmiCode

基于 Spring Boot 3 构建的现代化后端服务，提供用户认证、文件存储等核心功能，为 AmiCode 平台提供稳定高效的 API 支持。

## 📚 技术栈

- **核心框架**: Spring Boot 3.4.x
- **安全认证**: Sa-Token 1.42.0
- **数据访问**: MyBatis-Plus 3.5.12 + MySQL
- **缓存系统**: Redis + Caffeine
- **对象存储**: MinIO 8.5.14
- **工具库**: Hutool 5.8.37
- **接口文档**: Knife4j 4.4.0 (Swagger 增强)
- **Java 版本**: JDK 21

## 🔍 项目特性

- 完善的用户认证系统 (登录、注册、注销)
- 基于 Sa-Token 的权限控制
- 对象存储服务集成
- RESTful API 设计
- Swagger 接口文档自动生成
- 多环境配置支持

## ⚙️ 环境要求

- JDK 21+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.9+

## 🚀 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/tenyon/amicode-backend.git
cd amicode-backend
```

### 2. 配置环境

创建或修改 `src/main/resources/application-local.properties` 文件，配置数据库、Redis 和对象存储信息。

### 3. 构建并启动

```bash
mvn clean package
java -jar target/amicode-backend-1.0.0-SNAPSHOT.jar
```

### 4. 访问接口文档

```
http://localhost:8072/doc.html
```

## 📋 API 接口说明

- 用户授权: `/api/auth/**`

  - 登录: `/api/auth/login`
  - 注册: `/api/auth/register`
  - 注销: `/api/auth/logout`
  - 获取当前用户: `/api/auth/getLoginUser`

- 用户管理: `/api/user/**`

## 🔒 安全配置

项目使用 Sa-Token 进行身份验证和授权，支持:

- 基于 Token 的认证
- 会话管理
- 权限控制
- 并发登录控制

## 📝 许可证

[MIT License](LICENSE)
