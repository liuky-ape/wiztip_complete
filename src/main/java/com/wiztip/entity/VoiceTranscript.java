package com.wiztip.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 语音转写记录实体类
 * 
 * 存储语音识别的转写结果和向量嵌入
 * 每条语音记录对应一条转写记录
 * 
 * @author Wiztip Team
 */
@Entity
@Table(name = "voice_transcript")
public class VoiceTranscript {
    
    /** 主键ID */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 关联的语音记录ID */
    private Long recordId;
    
    /** 用户ID */
    private String userId;
    
    /** 转写文本内容（使用TEXT类型支持长文本） */
    @Column(columnDefinition = "TEXT")
    private String transcriptText;
    
    /** 文本向量嵌入的JSON表示（用于语义检索） */
    @Column(columnDefinition = "JSON")
    private String embeddingJson;
    
    /** ASR识别置信度（0-1之间） */
    private Float confidence;
    
    /** 创建时间 */
    private LocalDateTime createTime;

    // ========== Getters and Setters ==========
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Long getRecordId(){return recordId;}
    public void setRecordId(Long recordId){this.recordId=recordId;}
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}
    public String getTranscriptText(){return transcriptText;}
    public void setTranscriptText(String transcriptText){this.transcriptText=transcriptText;}
    public String getEmbeddingJson(){return embeddingJson;}
    public void setEmbeddingJson(String embeddingJson){this.embeddingJson=embeddingJson;}
    public Float getConfidence(){return confidence;}
    public void setConfidence(Float confidence){this.confidence=confidence;}
    public LocalDateTime getCreateTime(){return createTime;}
    public void setCreateTime(LocalDateTime createTime){this.createTime=createTime;}
}
