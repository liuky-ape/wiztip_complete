package com.wiztip.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 语音记录实体类
 * 
 * 存储用户上传的语音文件基本信息
 * 每次用户上传语音文件时都会创建一条记录
 * 
 * @author Wiztip Team
 */
@Entity
@Table(name = "voice_record")
public class VoiceRecord {
    
    /** 主键ID */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 用户ID */
    private String userId;
    
    /** 原始文件名 */
    private String fileName;
    
    /** OSS存储的完整URL */
    private String ossUrl;
    
    /** 音频时长（秒） */
    private Double duration;
    
    /** 处理状态：0-处理中，1-已完成，2-失败 */
    private Integer status;
    
    /** 上传时间 */
    private LocalDateTime uploadTime;

    // ========== Getters and Setters ==========
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}
    public String getFileName(){return fileName;}
    public void setFileName(String fileName){this.fileName=fileName;}
    public String getOssUrl(){return ossUrl;}
    public void setOssUrl(String ossUrl){this.ossUrl=ossUrl;}
    public Double getDuration(){return duration;}
    public void setDuration(Double duration){this.duration=duration;}
    public Integer getStatus(){return status;}
    public void setStatus(Integer status){this.status=status;}
    public LocalDateTime getUploadTime(){return uploadTime;}
    public void setUploadTime(LocalDateTime uploadTime){this.uploadTime=uploadTime;}
}
