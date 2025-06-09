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

- 用户授权: `/api/user/4`

  - 登录: `/api/user/login`
  - 注册: `/api/user/register`
  - 注销: `/api/user/logout`
  - 获取当前用户: `/api/user/getLoginUser`

- : `/api/user/**`

## 🔒 安全配置

项目使用 Sa-Token 进行身份验证和授权，支持:

- 基于 Token 的认证
- 会话管理
- 权限控制
- 并发登录控制

## 📝 许可证

[MIT License](LICENSE)

## RBAC 权限管理说明

### 主要功能

1. 基于 SaToken 实现的权限管理系统，支持：
   - 角色继承（树形结构）
   - 权限注解自动校验 (@SaCheckPermission)
   - 角色注解校验 (@SaCheckRole)
   - 细粒度权限控制

### 核心组件

1. 角色管理：

   - 支持角色的 CRUD 操作
   - 支持角色树形继承（父子角色关系）
   - 支持角色禁用/启用

2. 菜单权限：

   - 菜单(Menu)实体支持权限标识(perms)
   - 支持菜单权限分配到角色
   - 支持权限自动校验

3. 用户角色：
   - 支持用户分配多个角色
   - 自动合并用户所有角色的权限

### 主要 API

1. 角色管理：

   - 获取角色列表：GET /api/rbac/role/list
   - 创建角色：POST /api/rbac/role/add
   - 分配角色菜单权限：POST /api/rbac/role/assignMenu

2. 权限管理：

   - 获取当前用户所有权限：GET /api/rbac/permission/getCurrentUserPermissions
   - 获取当前用户所有角色：GET /api/rbac/role/getCurrentUserRoles

3. 用户角色管理：
   - 给用户分配角色：POST /api/rbac/user/assignRoles
   - 获取用户角色 ID 列表：GET /api/rbac/user/roles/{userId}

### 使用示例

1. 在控制器方法上添加权限验证注解：

```java
// 验证当前用户必须有指定权限才能访问方法
@SaCheckPermission(value = {"system:role:list"}, mode = SaMode.OR)
@GetMapping("/role/list")
public RtnData<List<Role>> listRoles() {
    // 方法实现...
}

// 验证当前用户必须有指定角色才能访问
@SaCheckRole(UserConstant.ADMIN_ROLE_KEY)
@PostMapping("/add")
public RtnData<Long> add() {
    // 方法实现...
}
```

2. 动态获取用户权限：

```java
// 在业务代码中获取当前用户的权限列表
menuService.getUserPermissions(userId);

// 在业务代码中获取当前用户的角色列表
roleService.getUserRoleKeys(userId);
```
