package com.wiztip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Wiztip应用配置属性类
 * 用于绑定application.yml中的wiztip配置项
 */
@Component
@ConfigurationProperties(prefix = "wiztip")
public class WiztipProperties {
    
    private Aliyun aliyun = new Aliyun();
    private Llm llm = new Llm();
    
    public Aliyun getAliyun() {
        return aliyun;
    }
    
    public void setAliyun(Aliyun aliyun) {
        this.aliyun = aliyun;
    }
    
    public Llm getLlm() {
        return llm;
    }
    
    public void setLlm(Llm llm) {
        this.llm = llm;
    }
    
    public static class Aliyun {
        private Oss oss = new Oss();
        private Asr asr = new Asr();
        
        public Oss getOss() {
            return oss;
        }
        
        public void setOss(Oss oss) {
            this.oss = oss;
        }
        
        public Asr getAsr() {
            return asr;
        }
        
        public void setAsr(Asr asr) {
            this.asr = asr;
        }
    }
    
    public static class Oss {
        private String endpoint;
        private String bucket;
        private String accessKeyId;
        private String accessKeySecret;
        
        // Getters and setters
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public String getBucket() { return bucket; }
        public void setBucket(String bucket) { this.bucket = bucket; }
        public String getAccessKeyId() { return accessKeyId; }
        public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }
        public String getAccessKeySecret() { return accessKeySecret; }
        public void setAccessKeySecret(String accessKeySecret) { this.accessKeySecret = accessKeySecret; }
    }
    
    public static class Asr {
        private String appKey;
        private String accessKeyId;
        private String accessKeySecret;
        
        // Getters and setters
        public String getAppKey() { return appKey; }
        public void setAppKey(String appKey) { this.appKey = appKey; }
        public String getAccessKeyId() { return accessKeyId; }
        public void setAccessKeyId(String accessKeyId) { this.accessKeyId = accessKeyId; }
        public String getAccessKeySecret() { return accessKeySecret; }
        public void setAccessKeySecret(String accessKeySecret) { this.accessKeySecret = accessKeySecret; }
    }
    
    public static class Llm {
        private String apiKey;
        private String endpoint;
        
        // Getters and setters
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    }
}
