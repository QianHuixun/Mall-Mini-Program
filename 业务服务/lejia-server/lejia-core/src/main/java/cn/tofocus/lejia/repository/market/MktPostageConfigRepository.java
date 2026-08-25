package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktPostageConfig;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
*  快递费
* @author lai 2020-06-15
*/

@Repository
public interface MktPostageConfigRepository extends JpaRepository<MktPostageConfig,Integer>,  JpaSpecificationExecutor<MktPostageConfig> 
{
	@Query(value ="select l.name,SUM(p.postage) s,COUNT(p.pkey) from mkt_postage_config p, mkt_logistics l " 
			+ "where logistics is not null and p.logistics = l.pkey "  
			+ "and if(:startTime != '', p.created_time between :startTime and :endTime, 1=1) "  
			+ "GROUP BY l.name order by s desc limit :page, :pagesize", nativeQuery = true)
	List<List<Object>> getPostageCount(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("page") int page,  @Param("pagesize") int pagesize);
	
}
