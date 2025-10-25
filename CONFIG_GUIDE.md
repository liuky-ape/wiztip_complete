# 配置指南

## 🔐 本地配置说明

为了安全起见，真实的配置密钥已从代码中移除。请按照以下步骤配置您的本地环境。

## 📝 配置步骤

### 1. 复制配置模板

```bash
# 如果src/main/resources/application.yml中的密钥是占位符
# 请直接修改该文件，填入真实密钥
```

或者创建本地配置文件（不会被提交到Git）：

```bash
cp application.yml.example src/main/resources/application-local.yml
```

### 2. 编辑配置文件

打开 `src/main/resources/application.yml` 并填入真实配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wiztip?useSSL=false&serverTimezone=UTC
    username: root
    password: YOUR_MYSQL_PASSWORD  # ← 填入您的MySQL密码

wiztip:
  aliyun:
    oss:
      endpoint: "oss-cn-shanghai.aliyuncs.com"
      bucket: "your-bucket-name"                 # ← 填入您的OSS bucket名称
      accessKeyId: "YOUR_ACCESS_KEY_ID"          # ← 填入您的AccessKey ID
      accessKeySecret: "YOUR_ACCESS_KEY_SECRET"  # ← 填入您的AccessKey Secret
  asr:
    appKey: "YOUR_ASR_APP_KEY"                   # ← 填入您的NLS AppKey
    accessKeyId: "YOUR_ACCESS_KEY_ID"            # ← 填入您的AccessKey ID
    accessKeySecret: "YOUR_ACCESS_KEY_SECRET"    # ← 填入您的AccessKey Secret
  llm:
    apiKey: "YOUR_QWEN_API_KEY"                  # ← 填入您的通义千问API Key
    endpoint: "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation"
```

### 3. 获取阿里云密钥

#### 3.1 获取AccessKey

1. 登录 [阿里云控制台](https://ram.console.aliyun.com/manage/ak)
2. 创建AccessKey
3. 记录 `AccessKeyId` 和 `AccessKeySecret`

#### 3.2 获取OSS配置

1. 登录 [OSS控制台](https://oss.console.aliyun.com/)
2. 创建Bucket或使用现有Bucket
3. 记录Bucket名称和endpoint

#### 3.3 获取NLS AppKey

1. 登录 [NLS控制台](https://nls-portal.console.aliyun.com/)
2. 创建项目和应用
3. 记录AppKey

#### 3.4 获取通义千问API Key

1. 登录 [DashScope控制台](https://dashscope.console.aliyun.com/)
2. 创建API Key
3. 记录API Key

### 4. 创建数据库

```bash
mysql -u root -p
```

```sql
CREATE DATABASE wiztip CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE wiztip;
source src/main/resources/schema.sql;
```

### 5. 启动应用

```bash
mvn spring-boot:run
```

## 🔒 安全建议

### ✅ 要做的事

1. **使用环境变量**（生产环境推荐）
   ```bash
   export WIZTIP_OSS_ACCESS_KEY_ID="your-key"
   export WIZTIP_OSS_ACCESS_KEY_SECRET="your-secret"
   ```
   
   在application.yml中使用：
   ```yaml
   accessKeyId: ${WIZTIP_OSS_ACCESS_KEY_ID}
   accessKeySecret: ${WIZTIP_OSS_ACCESS_KEY_SECRET}
   ```

2. **使用配置加密**
   - Spring Boot支持Jasypt加密配置
   - 或使用阿里云KMS密钥管理服务

3. **限制AccessKey权限**
   - 使用RAM子账号
   - 只授予必要的权限（OSS、NLS）

### ❌ 不要做的事

1. ❌ 不要将真实密钥提交到Git
2. ❌ 不要在公开文档中包含密钥
3. ❌ 不要使用主账号AccessKey（使用RAM子账号）
4. ❌ 不要在前端代码中包含密钥

## 📋 配置检查清单

启动前请确认：

- [ ] MySQL已安装并启动
- [ ] 创建了wiztip数据库
- [ ] 执行了schema.sql建表脚本
- [ ] application.yml中配置了正确的MySQL密码
- [ ] 获取了阿里云AccessKey
- [ ] 获取了NLS AppKey
- [ ] 获取了OSS Bucket信息
- [ ] 配置了所有必要的密钥
- [ ] target/目录已添加到.gitignore

## 🆘 常见问题

### Q: 如何验证配置是否正确？

**A**: 启动应用，查看日志：

```bash
mvn spring-boot:run
```

如果看到以下信息说明配置正确：
```
✅ NLS Token获取成功！
Started WiztipApplication in X.XXX seconds
```

### Q: 如果AccessKey泄露了怎么办？

**A**: 立即采取以下措施：
1. 登录阿里云控制台删除或禁用该AccessKey
2. 创建新的AccessKey
3. 更新本地配置
4. 检查是否有异常使用（账单、日志）

### Q: 可以使用不同的密钥吗？

**A**: 可以！OSS和NLS可以使用不同的AccessKey：
```yaml
wiztip:
  aliyun:
    oss:
      accessKeyId: "OSS专用的AccessKey"
      accessKeySecret: "OSS专用的Secret"
  asr:
    accessKeyId: "NLS专用的AccessKey"  
    accessKeySecret: "NLS专用的Secret"
```

## 📚 相关文档

- [阿里云AccessKey管理](https://help.aliyun.com/document_detail/116401.html)
- [OSS快速入门](https://help.aliyun.com/document_detail/31883.html)
- [NLS快速入门](https://help.aliyun.com/document_detail/120575.html)
- [Spring Boot外部化配置](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)

---

**最后更新**: 2025-10-22

