package com.wiztip.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiztip.repository.VoiceTranscriptRepository;
import com.wiztip.repository.DailySummaryRepository;
import com.wiztip.entity.DailySummary;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

/**
 * LLM智能总结服务
 * 
 * 使用大语言模型（如通义千问）生成用户每日语音内容的智能总结
 * 由定时任务每天23点自动触发
 * 
 * @author Wiztip Team
 */
@Service
public class LlmSummaryService {

    /** LLM API密钥 */
    @Value("${wiztip.llm.apiKey}")
    private String apiKey;
    
    /** LLM API端点地址 */
    @Value("${wiztip.llm.endpoint}")
    private String endpoint;

    @Autowired
    private VoiceTranscriptRepository transcriptRepo;
    
    @Autowired
    private DailySummaryRepository summaryRepo;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 为所有用户生成每日总结
     * 
     * 执行流程：
     * 1. 查询所有有语音记录的用户
     * 2. 逐个用户获取当天的转写文本
     * 3. 调用LLM生成总结
     * 4. 保存总结到数据库
     * 
     * 该方法由定时任务SummaryJob每天23点调用
     */
    public void generateDailySummaries() {
        // 1. 获取所有用户ID
        List<String> users = transcriptRepo.findAllUserIds();
        
        // 2. 遍历每个用户
        for (String userId : users) {
            // 2.1 获取该用户今天的所有转写文本
            List<String> texts = transcriptRepo.findTodayTextsByUser(userId);
            if (texts.isEmpty()) {
                continue; // 如果当天没有语音记录，跳过
            }
            
            // 2.2 将所有文本合并
            String joined = String.join("\n", texts);
            
            // 2.3 调用LLM生成总结
            String summary = callLlmForSummary(joined);
            
            // 2.4 保存总结到数据库
            DailySummary ds = new DailySummary();
            ds.setUserId(userId);
            ds.setSummaryText(summary);
            ds.setDate(LocalDate.now());
            ds.setPushStatus(0); // 0-未推送
            summaryRepo.save(ds);
        }
    }

    /**
     * 调用LLM API生成内容总结
     * 
     * 使用通义千问等大语言模型对文本内容进行智能总结
     * 根据实际使用的LLM服务调整请求格式和响应解析
     * 
     * @param content 待总结的文本内容
     * @return 生成的总结文本，失败时返回错误信息
     */
    private String callLlmForSummary(String content) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // 构造HTTP POST请求
            HttpPost post = new HttpPost(endpoint);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + apiKey);
            
            // 构造Prompt提示词
            String prompt = "请基于以下内容生成精炼总结：\n" + content;
            
            // 构造请求体（根据实际LLM API格式调整）
            String payload = mapper.writeValueAsString(java.util.Map.of(
                    "model", "qwen", 
                    "input", java.util.Map.of(
                        "messages", java.util.List.of(
                            java.util.Map.of("role", "user", "content", prompt)
                        )
                    )
            ));
            post.setEntity(new StringEntity(payload, StandardCharsets.UTF_8));
            
            // 发送请求并处理响应
            try (CloseableHttpResponse resp = client.execute(post)) {
                String json = new String(resp.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                JsonNode node = mapper.readTree(json);
                
                // 解析响应（根据实际LLM API返回格式调整）
                if (node.has("output")) {
                    return node.get("output").toString();
                }
                return json;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "LLM_ERROR: " + e.getMessage();
        }
    }
}
