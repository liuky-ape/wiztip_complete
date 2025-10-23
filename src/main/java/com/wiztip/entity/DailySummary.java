package com.wiztip.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * 每日总结实体类
 * 
 * 用于存储用户每日语音内容的智能总结
 * 系统会在每天指定时间（默认23:00）自动生成当日总结
 * 
 * @author Wiztip Team
 */
@Entity
@Table(name = "daily_summary")
public class DailySummary {
    
    /** 主键ID */
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 用户ID */
    private String userId;
    
    /** 总结文本内容（使用TEXT类型支持长文本） */
    @Column(columnDefinition = "TEXT")
    private String summaryText;
    
    /** 关键词（逗号分隔） */
    private String keywords;
    
    /** 总结对应的日期 */
    private LocalDate date;
    
    /** 推送状态：0-未推送，1-已推送 */
    private Integer pushStatus;
    
    /** 创建时间 */
    private java.time.LocalDateTime createdAt;

    // ========== Getters and Setters ==========
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}
    public String getSummaryText(){return summaryText;}
    public void setSummaryText(String summaryText){this.summaryText=summaryText;}
    public String getKeywords(){return keywords;}
    public void setKeywords(String keywords){this.keywords=keywords;}
    public LocalDate getDate(){return date;}
    public void setDate(LocalDate date){this.date=date;}
    public Integer getPushStatus(){return pushStatus;}
    public void setPushStatus(Integer pushStatus){this.pushStatus=pushStatus;}
    public java.time.LocalDateTime getCreatedAt(){return createdAt;}
    public void setCreatedAt(java.time.LocalDateTime createdAt){this.createdAt=createdAt;}
}
