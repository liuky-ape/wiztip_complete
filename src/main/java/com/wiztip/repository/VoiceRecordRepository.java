package com.wiztip.repository;

import com.wiztip.entity.VoiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 语音记录数据访问层
 * 
 * 提供对voice_record表的CRUD操作
 * 继承JpaRepository获得基础的数据库操作方法
 * 
 * @author Wiztip Team
 */
@Repository
public interface VoiceRecordRepository extends JpaRepository<VoiceRecord, Long> {
}
