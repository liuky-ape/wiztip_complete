# Wiztip - 智能语音助手后端系统

## 项目简介

Wiztip是一个基于Spring Boot的智能语音助手后端系统，提供完整的语音处理、智能识别和总结功能。

### 核心功能

- 🎤 **音频上传** - 支持音频文件上传到阿里云OSS对象存储
- 🗣️ **语音识别** - 集成阿里云智能语音服务（NLS）进行语音转文字
- 🧠 **文本嵌入** - 文本向量化，支持语义检索（可扩展）
- 💾 **数据持久化** - 使用MySQL存储语音记录、转写文本和向量
- 📊 **智能总结** - 使用大语言模型（LLM）生成每日智能总结
- ⏰ **定时任务** - 基于Quartz的定时任务调度，每日自动生成总结

## 技术栈

- **框架**: Spring Boot 3.1.5
- **数据库**: MySQL + Spring Data JPA
- **对象存储**: 阿里云OSS
- **语音识别**: 阿里云智能语音交互（NLS）
- **大语言模型**: 通义千问（可配置）
- **定时任务**: Quartz Scheduler
- **HTTP客户端**: Apache HttpClient 5
- **构建工具**: Maven

## 项目结构

```
wiztip_complete/
├── src/main/java/com/wiztip/
│   ├── WiztipApplication.java          # 应用主入口
│   ├── controller/
│   │   └── AudioController.java        # 音频处理API控制器
│   ├── entity/                         # 实体类
│   │   ├── VoiceRecord.java           # 语音记录
│   │   ├── VoiceTranscript.java       # 语音转写
│   │   └── DailySummary.java          # 每日总结
│   ├── repository/                     # 数据访问层
│   │   ├── VoiceRecordRepository.java
│   │   ├── VoiceTranscriptRepository.java
│   │   └── DailySummaryRepository.java
│   ├── service/                        # 业务逻辑层
│   │   ├── OssService.java            # OSS上传服务
│   │   ├── AsrService.java            # 语音识别服务
│   │   ├── EmbeddingService.java      # 文本嵌入服务
│   │   └── LlmSummaryService.java     # LLM总结服务
│   └── scheduler/                      # 定时任务
│       ├── QuartzConfig.java          # Quartz配置
│       └── SummaryJob.java            # 每日总结任务
├── src/main/resources/
│   ├── application.yml                # 应用配置文件
│   └── schema.sql                     # 数据库建表脚本
└── pom.xml                            # Maven项目配置
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- 阿里云账号（OSS、NLS服务）

### 配置步骤

1. **克隆项目**
   ```bash
   cd wiztip_complete
   ```

2. **配置数据库**
   ```bash
   # 创建数据库
   mysql -u root -p
   CREATE DATABASE wiztip CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   # 执行建表脚本
   mysql -u root -p wiztip < src/main/resources/schema.sql
   ```

3. **配置阿里云服务**
   
   编辑 `src/main/resources/application.yml`：
   
   ```yaml
   wiztip:
     aliyun:
       oss:
         endpoint: "oss-cn-shanghai.aliyuncs.com"  # OSS服务端点
         bucket: "your-bucket-name"                 # OSS存储桶
         accessKeyId: "YOUR_ACCESS_KEY_ID"          # AccessKey ID
         accessKeySecret: "YOUR_ACCESS_KEY_SECRET"  # AccessKey Secret
     asr:
       appKey: "YOUR_ASR_APP_KEY"                   # NLS应用AppKey
       accessKeyId: "YOUR_ACCESS_KEY_ID"            # AccessKey ID (与OSS相同)
       accessKeySecret: "YOUR_ACCESS_KEY_SECRET"    # AccessKey Secret (与OSS相同)
       # 注意：无需配置token，SDK会自动获取
     llm:
       apiKey: "YOUR_LLM_API_KEY"                   # LLM API密钥
       endpoint: "https://dashscope.aliyuncs.com/..."  # LLM端点
   ```
   
   **重要说明**：
   - NLS Token会通过SDK使用AccessKey自动获取
   - 无需手动配置Token
   - Token会自动缓存和刷新（24小时有效期）

4. **编译项目**
   ```bash
   mvn clean compile
   ```

5. **运行项目**
   ```bash
   mvn spring-boot:run
   ```

6. **访问Web界面**
   ```bash
   # 启动应用后，在浏览器访问
   http://localhost:8080
   ```
   
   或通过命令行测试接口：
   ```bash
   # 上传音频文件
   curl -X POST http://localhost:8080/api/audio/upload \
     -F "file=@test.wav" \
     -F "userId=user123"
   ```

## 🎤 唤醒词功能

### Web界面使用

1. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

2. **打开浏览器**
   访问 `http://localhost:8080`

3. **使用唤醒词**
   - 点击"启动唤醒词检测"
   - 说出唤醒词："**你好小笔**"
   - 系统自动开始录音
   - 录音完成后自动调用阿里云NLS识别
   - 显示识别结果，用户确认后保存到数据库

### 功能特点

- ✅ **语音唤醒** - 说"你好小笔"自动开始录音
- ✅ **持续监听** - 实时检测唤醒词
- ✅ **智能静音检测** - 5秒无声音自动停止
- ✅ **手动保存** - 点击保存按钮立即停止并识别
- ✅ **自动Token管理** - 使用SDK自动获取和刷新Token
- ✅ **实时反馈** - 显示录音和上传状态
- ✅ **操作日志** - 记录所有操作历史

### 浏览器要求

- **推荐**: Chrome 浏览器（完整支持Web Speech API）
- **支持**: Edge、Safari（部分功能）
- **注意**: 需要允许麦克风权限

## API接口说明

### 识别音频（不保存）

**接口**: `POST /api/audio/recognize`

**参数**:
- `file` (MultipartFile): 音频文件
- `userId` (String): 用户ID

**响应**:
```json
{
  "success": true,
  "transcript": "识别的文本内容",
  "ossUrl": "https://bucket.oss-cn-shanghai.aliyuncs.com/...",
  "message": "语音识别成功，请确认是否保存"
}
```

**处理流程**:
1. 上传音频到OSS
2. 调用阿里云NLS识别
3. 返回识别结果（不保存到数据库）

### 保存识别结果

**接口**: `POST /api/audio/save`

**参数**:
- `ossUrl` (String): OSS文件URL
- `transcript` (String): 识别文本
- `userId` (String): 用户ID
- `fileName` (String): 文件名

**响应**:
```json
{
  "success": true,
  "recordId": 123,
  "message": "数据已保存到数据库"
}
```

**处理流程**:
1. 创建语音记录
2. 生成文本向量
3. 保存到数据库

### 上传音频（一步完成）

**接口**: `POST /api/audio/upload`

**参数**:
- `file` (MultipartFile): 音频文件
- `userId` (String): 用户ID

**响应**:
```json
{
  "success": true,
  "transcript": "识别文本",
  "ossUrl": "https://...",
  "recordId": 123,
  "message": "语音识别成功"
}
```

**处理流程**:
1. 上传音频到OSS
2. 创建语音记录
3. 调用ASR服务识别
4. 生成文本向量
5. 保存转写结果（一步完成，适合API调用）

## 定时任务

### 每日总结任务

- **执行时间**: 每天 23:00
- **功能**: 为所有用户生成当日语音内容的智能总结
- **配置**: `QuartzConfig.java` 中的 Cron 表达式

修改执行时间：
```java
// 在 QuartzConfig.java 中修改
CronScheduleBuilder.cronSchedule("0 0 23 * * ?")  // 每天23:00
```

## 最近修复的问题

✅ **修复了Apache HttpClient导入错误**
- 将 `CloseableHttpResponse` 的导入路径从错误的包修正为正确的包
- 修正前: `org.apache.hc.client5.http.classic.CloseableHttpResponse`
- 修正后: `org.apache.hc.client5.http.impl.classic.CloseableHttpResponse`

✅ **完善了POM配置**
- 添加了Spring Boot Parent依赖管理
- 修正了MySQL Connector的groupId和version

✅ **添加了完整的代码注释**
- 所有类和方法都添加了详细的中文注释
- 包含参数说明、返回值说明和功能描述

## 数据库表结构

### voice_record - 语音记录表
- `id`: 主键
- `user_id`: 用户ID
- `file_name`: 文件名
- `oss_url`: OSS存储URL
- `duration`: 音频时长
- `status`: 处理状态（0-处理中，1-已完成，2-失败）
- `upload_time`: 上传时间

### voice_transcript - 语音转写表
- `id`: 主键
- `record_id`: 关联的语音记录ID
- `user_id`: 用户ID
- `transcript_text`: 转写文本
- `embedding_json`: 向量嵌入JSON
- `confidence`: 识别置信度
- `create_time`: 创建时间

### daily_summary - 每日总结表
- `id`: 主键
- `user_id`: 用户ID
- `summary_text`: 总结文本
- `keywords`: 关键词
- `date`: 日期
- `push_status`: 推送状态（0-未推送，1-已推送）
- `created_at`: 创建时间

## 注意事项

### 生产环境建议

1. **安全性**
   - 不要将密钥硬编码在配置文件中
   - 使用环境变量或密钥管理服务
   - 启用HTTPS和API认证

2. **性能优化**
   - ASR识别改为异步处理（使用消息队列）
   - 添加Redis缓存层
   - 实现连接池和超时控制

3. **可靠性**
   - 添加全局异常处理
   - 实现重试机制
   - 添加日志监控和告警

4. **扩展性**
   - 将EmbeddingService改为真实的向量模型调用
   - 集成向量数据库（如Milvus）用于语义检索
   - 实现微服务架构拆分

## 依赖版本

- Spring Boot: 3.1.5
- MySQL Connector: 8.1.0
- Aliyun OSS SDK: 3.16.0
- Apache HttpClient: 5.3
- Jackson: 由Spring Boot管理

## 编译状态

✅ 编译通过 - BUILD SUCCESS  
⚠️ 存在deprecation警告（不影响功能）

## 开发团队

**Author**: Wiztip Team  
**Version**: 1.0.0

## 许可证

本项目仅供学习和演示使用。
