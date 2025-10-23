# Wiztip 启动指南

## 问题: ClassNotFoundException: WiztipApplication

如果遇到 `java.lang.ClassNotFoundException: WiztipApplication` 错误，请按照以下步骤解决。

## 解决方案

### ✅ 已修复的配置

我们已经在 `pom.xml` 中添加了正确的主类配置：

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <configuration>
        <mainClass>com.wiztip.WiztipApplication</mainClass>
      </configuration>
    </plugin>
  </plugins>
</build>
```

## 推荐的启动方式

### 方式1: 使用 Maven Spring Boot Plugin（推荐）

```bash
cd /Users/bytedance/Documents/wiztip_complete
mvn spring-boot:run
```

**优点**: 
- 最简单直接
- 自动处理类路径
- 适合开发环境

### 方式2: 先清理编译，再运行

```bash
cd /Users/bytedance/Documents/wiztip_complete
mvn clean compile
mvn spring-boot:run
```

**适用场景**: 
- 代码有变更
- 编译缓存可能有问题

### 方式3: 打包成JAR后运行（生产环境）

```bash
cd /Users/bytedance/Documents/wiztip_complete
mvn clean package -DskipTests
java -jar target/wiztip-1.0.0.jar
```

**优点**: 
- 生成独立可运行的JAR
- 适合生产部署

### 方式4: 使用提供的启动脚本

```bash
cd /Users/bytedance/Documents/wiztip_complete
./run.sh
```

## IDE 中运行

### IntelliJ IDEA

1. **方式A: 直接运行主类**
   - 打开 `src/main/java/com/wiztip/WiztipApplication.java`
   - 右键点击文件 → "Run 'WiztipApplication.main()'"

2. **方式B: 使用Maven插件**
   - 打开 Maven 面板
   - 展开 `Plugins` → `spring-boot`
   - 双击 `spring-boot:run`

3. **方式C: 创建运行配置**
   - Run → Edit Configurations
   - 点击 "+" → Spring Boot
   - Main class: `com.wiztip.WiztipApplication`
   - 点击 OK，然后运行

### VS Code

1. 安装扩展:
   - Spring Boot Extension Pack
   - Java Extension Pack

2. 打开 `WiztipApplication.java`

3. 点击 `Run` 按钮（在 main 方法上方）

### Eclipse

1. 右键项目 → Run As → Spring Boot App

## 常见问题排查

### 问题1: 端口被占用

**错误信息**: 
```
Port 8080 was already in use
```

**解决方案**:
```bash
# 查找占用8080端口的进程
lsof -i :8080

# 杀死进程（替换PID为实际进程号）
kill -9 <PID>

# 或者修改端口，编辑 application.yml:
server:
  port: 8081
```

### 问题2: 数据库连接失败

**错误信息**: 
```
Cannot create PoolableConnectionFactory
```

**解决方案**:
```bash
# 1. 确保MySQL已启动
mysql -u root -p

# 2. 创建数据库
CREATE DATABASE wiztip CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 执行建表脚本
mysql -u root -p wiztip < src/main/resources/schema.sql

# 4. 检查 application.yml 中的数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wiztip?useSSL=false&serverTimezone=UTC
    username: root
    password: 你的密码
```

### 问题3: 依赖下载失败

**错误信息**: 
```
Could not resolve dependencies
```

**解决方案**:
```bash
# 1. 清理Maven缓存
rm -rf ~/.m2/repository

# 2. 重新下载依赖
mvn clean install -U

# 3. 或使用阿里云镜像（编辑 ~/.m2/settings.xml）
```

### 问题4: 编译错误

**错误信息**: 
```
Compilation failure
```

**解决方案**:
```bash
# 1. 清理项目
mvn clean

# 2. 重新编译
mvn compile

# 3. 查看详细错误信息
mvn compile -X
```

## 验证启动成功

### 1. 查看日志输出

启动成功后会看到类似输出：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.1.5)

...
Started WiztipApplication in 3.456 seconds
```

### 2. 测试健康检查接口

```bash
# 访问健康检查端点（如果有配置 actuator）
curl http://localhost:8080/actuator/health

# 测试音频上传接口
curl -X POST http://localhost:8080/api/audio/upload \
  -F "file=@test.wav" \
  -F "userId=test123"
```

### 3. 查看日志文件

```bash
# 如果配置了日志文件
tail -f logs/wiztip.log
```

## 启动前检查清单

- [ ] JDK 17+ 已安装
- [ ] Maven 3.6+ 已安装
- [ ] MySQL 8.0+ 已启动
- [ ] 数据库 `wiztip` 已创建
- [ ] 建表脚本已执行
- [ ] `application.yml` 配置已更新（数据库密码、阿里云密钥等）
- [ ] 端口 8080 未被占用
- [ ] 代码已编译（`target/classes` 目录存在）

## 快速启动命令

```bash
# 一键启动（推荐）
cd /Users/bytedance/Documents/wiztip_complete && mvn spring-boot:run

# 或使用脚本
./run.sh
```

## 停止应用

```bash
# 如果在前台运行，按 Ctrl+C

# 如果在后台运行
ps aux | grep wiztip
kill <PID>
```

## 需要帮助？

如果以上方法都无法解决问题，请：

1. 查看完整的错误堆栈信息
2. 检查 `application.yml` 配置
3. 确认所有依赖都已正确下载
4. 查看 Maven 编译输出

## 配置文件位置

- 主配置: `src/main/resources/application.yml`
- 数据库脚本: `src/main/resources/schema.sql`
- Maven配置: `pom.xml`

---

**版本**: 1.0.0  
**最后更新**: 2025-10-20

