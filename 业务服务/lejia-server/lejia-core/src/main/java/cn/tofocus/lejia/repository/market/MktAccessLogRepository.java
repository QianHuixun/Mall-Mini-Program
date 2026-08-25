package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktAccessLog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
*  app访问记录
* @author zdw 2020-08-17
*/

@Repository
public interface MktAccessLogRepository extends JpaRepository<MktAccessLog,Integer>,  JpaSpecificationExecutor<MktAccessLog> 
{
	
	@Query(value = "select DATE_FORMAT(access_time, '%Y-%m-%d'),COUNT(pkey) from mkt_access_log "  
			+ "where if(:startTime != '', access_time between :startTime and :endTime, 1=1) and ascription = :ascription "  
			+ "GROUP BY DATE_FORMAT(access_time, '%Y-%m-%d') ", nativeQuery = true)
	List<List<Object>> mallAccessNum(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("ascription")Integer ascription);
}
