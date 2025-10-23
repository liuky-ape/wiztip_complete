#!/bin/bash
# Wiztip 启动脚本

echo "正在启动 Wiztip 应用..."

# 方式1: 使用 Spring Boot Maven Plugin
mvn spring-boot:run

# 如果上面的命令失败，可以尝试下面的方式
# 方式2: 先编译，再运行
# mvn clean compile
# mvn spring-boot:run

# 方式3: 打包后运行（如果有完整的jar）
# mvn clean package -DskipTests
# java -jar target/wiztip-1.0.0.jar
