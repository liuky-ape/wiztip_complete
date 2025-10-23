package com.wiztip.repository;

import com.wiztip.entity.VoiceTranscript;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 语音转写记录数据访问层
 * 
 * 提供对voice_transcript表的CRUD操作和自定义查询
 * 支持查询所有用户ID和指定用户当日的转写文本
 * 
 * @author Wiztip Team
 */
@Repository
public interface VoiceTranscriptRepository extends JpaRepository<VoiceTranscript, Long> {

    /**
     * 查询所有不重复的用户ID
     * 用于定时任务遍历所有用户生成总结
     * 
     * @return 用户ID列表
     */
    @Query("SELECT DISTINCT v.userId FROM VoiceTranscript v")
    List<String> findAllUserIds();

    /**
     * 查询指定用户当天的所有转写文本
     * 用于生成每日总结时获取当天的所有语音内容
     * 
     * @param userId 用户ID
     * @return 当天的转写文本列表
     */
    @Query("SELECT v.transcriptText FROM VoiceTranscript v WHERE v.userId = :userId AND DATE(v.createTime) = CURRENT_DATE")
    List<String> findTodayTextsByUser(@Param("userId") String userId);
}
