package com.wiztip.service;

import com.wiztip.entity.VoiceRecord;
import com.wiztip.entity.VoiceTranscript;
import com.wiztip.repository.VoiceTranscriptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import com.alibaba.nls.client.AccessToken;

/**
 * 阿里云语音识别（ASR）服务
 * 
 * 提供语音转文字功能，支持从OSS URL进行录音文件识别
 * 使用阿里云智能语音交互服务（NLS）
 * 
 * 认证方式：
 * - 使用AccessKeyId和AccessKeySecret动态获取Token
 * - Token自动缓存和刷新机制，避免频繁请求
 * 
 * @author Wiztip Team
 */
@Service
public class AsrService {

    /** ASR应用AppKey */
    @Value("${wiztip.asr.appKey}")
    private String appKey;
    
    /** 阿里云AccessKey ID */
    @Value("${wiztip.asr.accessKeyId}")
    private String accessKeyId;
    
    /** 阿里云AccessKey Secret */
    @Value("${wiztip.asr.accessKeySecret}")
    private String accessKeySecret;

    @Autowired
    private VoiceTranscriptRepository transcriptRepo;

    private final ObjectMapper mapper = new ObjectMapper();
    
    /** 阿里云NLS Token客户端 */
    private AccessToken accessTokenClient = null;
    
    /** 缓存的NLS Token */
    private String cachedToken = null;
    
    /** Token过期时间（毫秒） */
    private long tokenExpireTime = 0;

    /**
     * 获取或刷新NLS Token
     * 
     * 使用阿里云NLS SDK自动管理Token：
     * 1. SDK会使用AccessKeyId和AccessKeySecret自动获取Token
     * 2. Token会自动缓存和刷新
     * 3. 无需手动配置Token
     * 
     * @return 有效的NLS Token
     * @throws Exception Token获取失败时抛出异常
     */
    private String getToken() throws Exception {
        long now = System.currentTimeMillis();
        
        // 1. 检查缓存的Token是否仍然有效
        if (cachedToken != null && now < tokenExpireTime) {
            return cachedToken;
        }
        
        // 2. 使用阿里云NLS SDK获取Token
        try {
            // 初始化AccessToken客户端（如果还没有初始化）
            if (accessTokenClient == null) {
                System.out.println("初始化阿里云NLS Token客户端...");
                accessTokenClient = new AccessToken(accessKeyId, accessKeySecret);
                System.out.println("NLS Token客户端初始化成功");
            }
            
            // 获取Token
            System.out.println("正在通过AppKey和AccessKey获取NLS Token...");
            accessTokenClient.apply();
            
            String token = accessTokenClient.getToken();
            long expireTime = accessTokenClient.getExpireTime();
            
            if (token == null || token.isEmpty()) {
                throw new Exception("获取到的Token为空");
            }
            
            // 缓存Token，设置为过期前5分钟刷新
            cachedToken = token;
            tokenExpireTime = expireTime * 1000L - 300000;
            
            System.out.println("✅ NLS Token获取成功！");
            System.out.println("   Token: " + token.substring(0, Math.min(20, token.length())) + "...");
            System.out.println("   过期时间: " + new java.util.Date(expireTime * 1000L));
            
            return cachedToken;
            
        } catch (Exception e) {
            System.err.println("❌ 获取NLS Token失败: " + e.getMessage());
            e.printStackTrace();
            
            // 提供详细的错误提示
            String errorMsg = "\n" +
                "========================================\n" +
                "NLS Token获取失败\n" +
                "========================================\n" +
                "错误信息: " + e.getMessage() + "\n\n" +
                "请检查以下配置：\n" +
                "1. AccessKeyId 是否正确\n" +
                "2. AccessKeySecret 是否正确\n" +
                "3. AccessKey是否有NLS服务权限\n" +
                "4. 网络是否能访问阿里云API\n\n" +
                "当前配置：\n" +
                "  AccessKeyId: " + accessKeyId.substring(0, Math.min(8, accessKeyId.length())) + "...\n" +
                "  Region: cn-shanghai\n" +
                "========================================\n";
            
            System.err.println(errorMsg);
            throw new Exception("NLS Token获取失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从本地文件流进行语音识别
     * 
     * 直接读取音频文件数据进行识别，无需先上传到OSS
     * 使用阿里云NLS一句话识别RESTful API
     * 
     * @param fileData 音频文件的字节数据
     * @param format 音频格式（如：wav、mp3、pcm等）
     * @return 识别结果文本，失败时返回错误信息
     */
    public String transcribeFromLocalFile(byte[] fileData, String format) {
        // 使用阿里云NLS一句话识别接口
        String baseUrl = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/asr";
        
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // 获取有效Token
            String token = getToken();
            
            // 构造完整URL（带查询参数）
            String urlWithParams = baseUrl + String.format(
                "?appkey=%s&format=%s&sample_rate=16000&enable_intermediate_result=false&enable_punctuation_prediction=true&enable_inverse_text_normalization=true",
                appKey, format
            );
            
            System.out.println("调用NLS一句话识别API: " + baseUrl);
            System.out.println("AppKey: " + appKey);
            System.out.println("文件大小: " + fileData.length + " bytes");
            System.out.println("音频格式: " + format);
            
            // 构造HTTP POST请求
            HttpPost post = new HttpPost(urlWithParams);
            post.setHeader("X-NLS-Token", token);
            post.setHeader("Content-Type", "application/octet-stream");
            
            // 设置音频数据为请求体
            post.setEntity(new org.apache.hc.core5.http.io.entity.ByteArrayEntity(
                fileData, org.apache.hc.core5.http.ContentType.APPLICATION_OCTET_STREAM
            ));
            
            // 发送请求并解析响应
            try (CloseableHttpResponse resp = client.execute(post)) {
                int statusCode = resp.getCode();
                String responseBody = new String(resp.getEntity().getContent().readAllBytes());
                
                System.out.println("NLS响应状态码: " + statusCode);
                System.out.println("NLS响应内容: " + responseBody);
                
                if (statusCode != 200) {
                    System.err.println("NLS识别失败，状态码: " + statusCode);
                    return "ASR_ERROR: HTTP " + statusCode + " - " + responseBody;
                }
                
                JsonNode node = mapper.readTree(responseBody);
                
                // 解析一句话识别的响应格式
                // 标准格式: {"status": 20000000, "result": "识别文本"}
                if (node.has("result")) {
                    String result = node.get("result").asText();
                    System.out.println("✅ 识别成功: " + result);
                    return result;
                }
                
                // 检查是否有错误信息
                if (node.has("status")) {
                    int status = node.get("status").asInt();
                    if (status != 20000000) {
                        String message = node.has("message") ? node.get("message").asText() : "未知错误";
                        System.err.println("识别返回错误状态: " + status + ", 消息: " + message);
                        return "ASR_ERROR: " + message;
                    }
                }
                
                // 尝试其他可能的字段
                if (node.has("data")) {
                    return node.get("data").toString();
                }
                
                // 如果无法解析，返回原始响应
                System.err.println("⚠️ 无法解析识别结果，返回原始响应");
                return responseBody;
                
            }
        } catch (Exception e) {
            System.err.println("❌ 本地文件识别异常: " + e.getMessage());
            e.printStackTrace();
            return "ASR_ERROR: " + e.getMessage();
        }
    }

    /**
     * 从OSS URL进行语音识别
     * 
     * 使用阿里云NLS一句话识别接口，支持短音频文件（60秒内）
     * 对于长音频，建议使用录音文件识别接口
     * 
     * @param ossUrl 音频文件的OSS访问URL
     * @return 识别结果文本，失败时返回错误信息
     */
    public String transcribeFromOssUrl(String ossUrl) {
        String asrUrl = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/asr";
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // 获取有效Token
            String token = getToken();
            
            // 构造ASR请求
            HttpPost post = new HttpPost(asrUrl);
            post.setHeader("X-NLS-Token", token);
            post.setHeader("Content-Type", "application/json");
            
            // 构造请求参数：appkey、音频URL、音频格式
            String payload = String.format("{\"appkey\":\"%s\",\"url\":\"%s\",\"format\":\"wav\"}", 
                appKey, ossUrl);
            post.setEntity(new StringEntity(payload));
            
            // 发送请求并解析响应
            try (CloseableHttpResponse resp = client.execute(post)) {
                String json = new String(resp.getEntity().getContent().readAllBytes());
                JsonNode node = mapper.readTree(json);
                
                // 根据ASR响应格式提取识别结果
                if (node.has("result")) {
                    return node.get("result").asText();
                } else if (node.has("data")) {
                    return node.get("data").toString();
                }
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ASR_ERROR: " + e.getMessage();
        }
    }

    /**
     * 保存语音转写结果到数据库
     * 
     * 将ASR识别结果和文本向量嵌入存储到voice_transcript表
     * 
     * @param record 关联的语音记录
     * @param transcript 转写文本
     * @param embedding 文本向量嵌入
     */
    public void saveTranscript(VoiceRecord record, String transcript, float[] embedding) {
        VoiceTranscript t = new VoiceTranscript();
        t.setRecordId(record.getId());
        t.setUserId(record.getUserId());
        t.setTranscriptText(transcript);
        t.setEmbeddingJson(embeddingToJson(embedding));
        transcriptRepo.save(t);
    }

    /**
     * 将浮点数组转换为JSON字符串
     * 
     * 用于将向量嵌入存储为JSON格式
     * 
     * @param emb 向量数组
     * @return JSON字符串，失败时返回空数组"[]"
     */
    private String embeddingToJson(float[] emb) {
        try {
            return mapper.writeValueAsString(emb);
        } catch (Exception e) {
            return "[]";
        }
    }
}
