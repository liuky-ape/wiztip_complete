# NLS Token 自动管理说明

## ✅ 已实现自动Token管理

项目现在使用**阿里云NLS SDK**自动管理Token，无需手动配置！

## 工作原理

### 1. SDK自动获取Token

```java
// 使用AccessKey自动获取Token
AccessToken accessTokenClient = new AccessToken(accessKeyId, accessKeySecret);
accessTokenClient.apply();
String token = accessTokenClient.getToken();
```

### 2. Token自动缓存

- Token有效期：24小时
- 自动缓存：避免重复请求
- 自动刷新：过期前5分钟自动更新

### 3. 配置说明

只需在 `application.yml` 中配置：

```yaml
wiztip:
  asr:
    appKey: "YOUR_ASR_APP_KEY"
    accessKeyId: "YOUR_ACCESS_KEY_ID"
    accessKeySecret: "YOUR_ACCESS_KEY_SECRET"
    # 无需配置token字段
```

## 依赖说明

项目已添加阿里云NLS SDK依赖：

```xml
<dependency>
    <groupId>com.alibaba.nls</groupId>
    <artifactId>nls-sdk-common</artifactId>
    <version>2.2.1</version>
</dependency>
```

## 启动日志

应用启动时会看到：

```
初始化阿里云NLS Token客户端...
NLS Token客户端初始化成功
正在通过AppKey和AccessKey获取NLS Token...
✅ NLS Token获取成功！
   Token: ab12cd34ef56gh78ij90...
   过期时间: Wed Oct 22 20:45:00 CST 2025
```

## 优势

### ✅ 相比手动配置Token

| 特性 | 手动配置 | SDK自动管理 |
|------|---------|------------|
| 配置复杂度 | 需要登录控制台获取 | 只需AccessKey |
| Token过期 | 需要每24小时更新 | 自动刷新 |
| 维护成本 | 高 | 低 |
| 生产环境 | 不推荐 | ✅ 推荐 |

## 注意事项

### 1. AccessKey权限

确保AccessKey有以下权限：
- ✅ 智能语音交互（NLS）服务权限
- ✅ AliyunNLSFullAccess 或自定义策略

### 2. 网络要求

- 需要访问：`nls-meta.cn-shanghai.aliyuncs.com`
- 端口：443 (HTTPS)

### 3. 错误排查

如果Token获取失败，会显示详细错误：

```
❌ 获取NLS Token失败: [错误信息]
========================================
NLS Token获取失败
========================================
错误信息: [具体错误]

请检查以下配置：
1. AccessKeyId 是否正确
2. AccessKeySecret 是否正确
3. AccessKey是否有NLS服务权限
4. 网络是否能访问阿里云API

当前配置：
  AccessKeyId: LTAI5t79...
  Region: cn-shanghai
========================================
```

## 与旧方案的对比

### 旧方案（已废弃）

```yaml
wiztip:
  asr:
    token: "手动从控制台获取的token"  # 每24小时过期
```

❌ 问题：
- 需要手动登录控制台
- 每天需要更新
- 不适合生产环境

### 新方案（当前）

```yaml
wiztip:
  asr:
    appKey: "YOUR_APP_KEY"
    accessKeyId: "YOUR_ACCESS_KEY_ID"
    accessKeySecret: "YOUR_ACCESS_KEY_SECRET"
```

✅ 优势：
- SDK自动管理
- Token自动刷新
- 生产环境友好

## 技术细节

### Token缓存机制

```java
// 1. 检查缓存
if (cachedToken != null && now < tokenExpireTime) {
    return cachedToken;  // 直接使用缓存
}

// 2. Token过期或不存在，重新获取
accessTokenClient.apply();
String token = accessTokenClient.getToken();
long expireTime = accessTokenClient.getExpireTime();

// 3. 缓存Token（过期前5分钟刷新）
cachedToken = token;
tokenExpireTime = expireTime * 1000L - 300000;
```

### 线程安全

当前实现为单例，在高并发场景下建议：
1. 添加同步锁
2. 使用双重检查锁定
3. 或使用Spring的@Cacheable

## FAQ

### Q: 还需要手动配置Token吗？

**A**: 不需要！SDK会自动获取。

### Q: Token多久刷新一次？

**A**: Token有效期24小时，系统会在过期前5分钟自动刷新。

### Q: 如果Token获取失败怎么办？

**A**: 
1. 检查AccessKey是否正确
2. 检查AccessKey权限
3. 检查网络连接
4. 查看详细错误日志

### Q: 可以同时配置多个应用的Token吗？

**A**: 可以。每个AsrService实例独立管理Token。

### Q: 生产环境如何保护AccessKey？

**A**: 建议：
1. 使用环境变量
2. 使用密钥管理服务（KMS）
3. 不要提交到Git仓库

## 相关文档

- [阿里云NLS SDK文档](https://help.aliyun.com/document_detail/120727.html)
- [AccessToken API](https://help.aliyun.com/document_detail/450255.html)
- [RAM权限配置](https://help.aliyun.com/document_detail/57445.html)

---

**版本**: 2.0  
**更新时间**: 2025-10-21  
**状态**: ✅ 已生产就绪

