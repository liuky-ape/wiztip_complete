package com.wiztip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Wiztip语音助手应用主入口
 * 
 * 这是一个基于Spring Boot的语音处理应用，提供以下核心功能：
 * 1. 语音录音上传到阿里云OSS
 * 2. 使用阿里云ASR服务进行语音转文字
 * 3. 生成文本向量嵌入
 * 4. 定时生成每日总结
 * 
 * @author Wiztip Team
 * @version 1.0.0
 */
@SpringBootApplication
public class WiztipApplication {
    /**
     * 应用程序启动入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(WiztipApplication.class, args);
    }
}
