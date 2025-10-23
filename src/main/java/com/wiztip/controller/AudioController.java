package com.wiztip.controller;

import com.wiztip.service.AsrService;
import com.wiztip.service.OssService;
import com.wiztip.service.EmbeddingService;
import com.wiztip.entity.VoiceRecord;
import com.wiztip.repository.VoiceRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 音频处理控制器
 * 
 * 提供音频文件上传和处理的REST API接口
 * 主要功能包括：
 * 1. 接收客户端上传的音频文件
 * 2. 将音频文件上传到阿里云OSS存储
 * 3. 调用ASR服务进行语音识别
 * 4. 生成文本向量嵌入
 * 5. 保存转写结果到数据库
 * 
 * @author Wiztip Team
 */
@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private OssService ossService;
    
    @Autowired
    private AsrService asrService;
    
    @Autowired
    private EmbeddingService embeddingService;
    
    @Autowired
    private VoiceRecordRepository recordRepository;

    /**
     * 仅识别音频文件，不保存到数据库
     * 
     * 用于预览识别结果，让用户确认后再决定是否保存
     * 直接从本地文件流读取进行识别，无需先上传到OSS
     * 
     * @param file 音频文件（MultipartFile格式）
     * @param userId 用户ID
     * @return ResponseEntity 返回识别结果的JSON响应
     * @throws Exception 识别过程中的异常
     */
    @PostMapping("/recognize")
    public ResponseEntity<?> recognizeOnly(@RequestParam("file") MultipartFile file,
                                           @RequestParam("userId") String userId) throws Exception {
        // 1. 读取音频文件数据
        byte[] fileData = file.getBytes();
        
        // 2. 获取文件格式（从文件名提取）
        String originalFilename = file.getOriginalFilename();
        String format = "wav"; // 默认格式
        if (originalFilename != null && originalFilename.contains(".")) {
            format = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        
        // 3. 直接从本地文件流调用ASR识别（不上传到OSS）
        String transcript = asrService.transcribeFromLocalFile(fileData, format);
        
        // 4. 返回识别结果（不保存到数据库，不上传OSS）
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("transcript", transcript);
        response.put("fileName", originalFilename);
        response.put("fileSize", fileData.length);
        response.put("message", "语音识别成功，请确认是否保存");

        return ResponseEntity.ok().body(response);
    }

    /**
     * 保存已识别的音频记录到数据库
     * 
     * 在用户确认识别结果后调用，将音频上传到OSS并持久化数据
     * 
     * @param file 音频文件（MultipartFile格式）
     * @param transcript 识别文本
     * @param userId 用户ID
     * @return ResponseEntity 返回保存结果
     * @throws Exception 保存过程中的异常
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveTranscript(@RequestParam("file") MultipartFile file,
                                           @RequestParam("transcript") String transcript,
                                           @RequestParam("userId") String userId) throws Exception {
        System.out.println("========================================");
        System.out.println("开始保存语音记录");
        System.out.println("用户ID: " + userId);
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("识别文本: " + transcript);
        System.out.println("========================================");
        
        // 1. 上传文件到OSS（用户确认后才上传）
        System.out.println("步骤1: 上传音频到OSS...");
        String ossUrl = ossService.upload(file, userId);
        System.out.println("✅ OSS上传成功: " + ossUrl);
        
        // 2. 创建语音记录
        System.out.println("步骤2: 创建语音记录...");
        VoiceRecord r = new VoiceRecord();
        r.setUserId(userId);
        r.setFileName(file.getOriginalFilename());
        r.setOssUrl(ossUrl);
        r.setStatus(1); // 状态：1-已完成
        r.setUploadTime(java.time.LocalDateTime.now());
        recordRepository.save(r);
        System.out.println("✅ 语音记录已保存，ID: " + r.getId());

        // 3. 生成文本向量嵌入
        System.out.println("步骤3: 生成文本向量...");
        float[] emb = embeddingService.embed(transcript);
        System.out.println("✅ 向量生成完成，维度: " + emb.length);

        // 4. 保存转写结果和向量到数据库
        System.out.println("步骤4: 保存转写记录...");
        asrService.saveTranscript(r, transcript, emb);
        System.out.println("✅ 转写记录已保存");

        // 5. 打印最终日志
        System.out.println("========================================");
        System.out.println("✅ 保存完成！");
        System.out.println("记录ID: " + r.getId());
        System.out.println("用户: " + userId);
        System.out.println("识别结果: " + transcript);
        System.out.println("OSS URL: " + ossUrl);
        System.out.println("向量维度: " + emb.length);
        System.out.println("保存时间: " + r.getUploadTime());
        System.out.println("========================================");

        // 6. 返回保存结果
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("recordId", r.getId());
        response.put("ossUrl", ossUrl);
        response.put("transcript", transcript);
        response.put("fileName", file.getOriginalFilename());
        response.put("vectorDimension", emb.length);
        response.put("saveTime", r.getUploadTime().toString());
        response.put("message", "数据已保存到数据库");

        return ResponseEntity.ok().body(response);
    }

    /**
     * 上传音频文件并进行处理
     * 
     * 处理流程：
     * 1. 上传音频文件到OSS
     * 2. 创建语音记录到数据库
     * 3. 调用ASR服务进行语音转文字
     * 4. 生成文本向量嵌入
     * 5. 保存转写结果和向量
     * 
     * @param file 音频文件（MultipartFile格式）
     * @param userId 用户ID，用于标识音频归属
     * @return ResponseEntity 返回包含转写文本的JSON响应
     * @throws Exception 文件上传或处理过程中的异常
     */
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam("userId") String userId) throws Exception {
        // 1. 上传文件到OSS存储
        String ossUrl = ossService.upload(file, userId);
        
        // 2. 创建语音记录
        VoiceRecord r = new VoiceRecord();
        r.setUserId(userId);
        r.setFileName(file.getOriginalFilename());
        r.setOssUrl(ossUrl);
        r.setStatus(0); // 状态：0-处理中
        recordRepository.save(r);

        // 3. 调用ASR服务进行语音识别（同步调用，生产环境建议异步处理）
        String transcript = asrService.transcribeFromOssUrl(ossUrl);
        
        // 4. 生成文本向量嵌入
        float[] emb = embeddingService.embed(transcript);

        // 5. 保存转写结果和向量到数据库
        asrService.saveTranscript(r, transcript, emb);

        // 6. 更新记录状态为已完成
        r.setStatus(1); // 状态：1-已完成
        recordRepository.save(r);

        // 7. 返回转写结果
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("ossUrl", ossUrl);
        response.put("transcript", transcript);
        response.put("recordId", r.getId());
        response.put("message", "语音识别成功");

        return ResponseEntity.ok().body(response);
    }
}
