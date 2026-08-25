package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
*  订单说明
* @author zdw 2020-07-16
*/

@Repository
public interface MktOrderDescRepository extends JpaRepository<MktOrderDesc,Integer>,  JpaSpecificationExecutor<MktOrderDesc> 
{
	@Query(value = "select d.logistics,sum(postage) from mkt_order o, mkt_order_desc d "  
			+ "where  d.fh_time is not null and o.pkey = d.pkey and o.ascription = :ascription "
			+ "and if(:startTime != '', left(d.fh_time,10) BETWEEN :startTime and :endTime , 1=1) "
			+ "GROUP BY d.logistics ", nativeQuery = true)
	List<List<Object>> aggreLogisticeSum(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("ascription") Integer ascription);
}
