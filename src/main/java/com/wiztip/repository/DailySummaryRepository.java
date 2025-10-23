package com.wiztip.repository;

import com.wiztip.entity.DailySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 每日总结数据访问层
 * 
 * 提供对daily_summary表的CRUD操作
 * 继承JpaRepository获得基础的数据库操作方法
 * 
 * @author Wiztip Team
 */
@Repository
public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {
}
