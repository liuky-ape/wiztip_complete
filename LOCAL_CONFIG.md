# 🔐 本地配置说明

## ⚠️ 重要安全提醒

**真实密钥已保存在 `application-local.yml` 文件中，该文件已被添加到 `.gitignore`，不会被提交到Git。**

## 📝 配置步骤

### 1. 使用本地配置文件（推荐）

项目已为您创建了 `application-local.yml` 文件，包含您的真实配置：

```bash
# 启动应用时会自动加载此文件
mvn spring-boot:run
```

### 2. 或者直接修改主配置文件

如果您想直接修改 `src/main/resources/application.yml`：

```yaml
wiztip:
  aliyun:
    oss:
      endpoint: "cn-beijing.oss.aliyuncs.com"
      bucket: "wiztip-test"
      accessKeyId: "YOUR_ACCESS_KEY_ID"
      accessKeySecret: "YOUR_ACCESS_KEY_SECRET"
  asr:
    appKey: "YOUR_ASR_APP_KEY"
    accessKeyId: "YOUR_ACCESS_KEY_ID"
    accessKeySecret: "YOUR_ACCESS_KEY_SECRET"
```

**⚠️ 注意：如果直接修改主配置文件，请确保在提交前将其恢复为占位符，否则GitHub会拒绝推送。**

### 3. 启动应用

```bash
# 方式1：使用Maven启动
mvn spring-boot:run

# 方式2：使用脚本启动
./run.sh
```

## 🔒 安全最佳实践

### ✅ 推荐做法

1. **使用本地配置文件**
   - 真实密钥保存在 `application-local.yml`
   - 该文件已被 `.gitignore` 忽略
   - 不会意外提交到Git

2. **使用环境变量**（生产环境）
   ```bash
   export WIZTIP_OSS_ACCESS_KEY_ID="YOUR_ACCESS_KEY_ID"
   export WIZTIP_OSS_ACCESS_KEY_SECRET="YOUR_ACCESS_KEY_SECRET"
   ```

3. **定期轮换密钥**
   - 定期更新AccessKey
   - 监控密钥使用情况

### ❌ 避免的做法

1. ❌ 不要将真实密钥提交到Git
2. ❌ 不要在公开文档中包含密钥
3. ❌ 不要在前端代码中包含密钥
4. ❌ 不要使用主账号AccessKey（建议使用RAM子账号）

## 🚀 快速启动

1. **确保MySQL运行**
   ```bash
   mysql -u root -p
   CREATE DATABASE wiztip CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE wiztip;
   source src/main/resources/schema.sql;
   ```

2. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

3. **访问应用**
   - 打开浏览器访问：http://localhost:8080
   - 说"你好小笔"开始录音
   - 等待5秒静音或点击保存按钮

## 🔍 验证配置

启动后查看日志，如果看到以下信息说明配置正确：

```
✅ NLS Token获取成功！
Started WiztipApplication in X.XXX seconds
```

## 📞 技术支持

如果遇到问题，请检查：

1. MySQL是否正常运行
2. 数据库 `wiztip` 是否已创建
3. 阿里云密钥是否正确
4. 网络连接是否正常

---

**配置完成！现在可以安全地推送代码到GitHub了。**
