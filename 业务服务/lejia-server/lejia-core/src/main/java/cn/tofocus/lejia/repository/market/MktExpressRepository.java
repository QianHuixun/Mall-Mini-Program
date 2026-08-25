package cn.tofocus.lejia.repository.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktExpress;

/**
*  跑脚单
* @author zdw 2020-07-21
*/

@Repository
public interface MktExpressRepository extends JpaRepository<MktExpress,Integer>,  JpaSpecificationExecutor<MktExpress> 
{
	
	@Query(value = "select e.* from mkt_express e, mkt_courier c where e.courier = c.pkey  "
			+ "and if(:courierName != '', c.name like CONCAT('%',:courierName,'%'), 1=1 )   "
			+ "and if(:orderId != '', e.kc_code like CONCAT('%', :orderId ,'%') , 1=1) "
			+ "and if(:status != '', e.status = :status, 1=1) "
			+ "and if(:startTime != '', e.created_time >= :startTime, 1=1) "
			+ "and if(:endTime != '', e.created_time <= :endTime, 1=1) "
			+ "and if(:marketPkey != '', e.farmer = :marketPkey, 1=1) "
			+ "and e.ascription = :ascription "
			+ "order by pkey desc  ", nativeQuery = true)
	List<MktExpress> findExpressOrderorName(
			@Param("courierName") String courierName, @Param("orderId") String orderId, @Param("status") Integer status, 
			@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("marketPkey")  String marketPkey,
			@Param("ascription") Integer ascription);
	
	@Query(value = "select COUNT(*) from mkt_express "
	    + "where courier = :courier "
	    + "and status = 3 "
	    + "and if(:qrTime != '', DATE(qr_time) = :qrTime, 1=1) "
	    + "and ascription = :ascription ", nativeQuery = true)
	Long getCountExpress(@Param("courier") Integer courier, @Param("qrTime") String qrTime, @Param("ascription") Integer ascription);
	
	@Query(value = "select c.name, COUNT(e.pkey) s from mkt_express e, mkt_courier c "  
			+ "where e.courier = c.pkey  "
			+ "and if(:status != '', e.status = :status, 1=1)   "  
			+ "and if(:marketPkey != '', e.farmer = :marketPkey, 1=1) "
			+ "and if(:startTime != '', e.created_time between :startTime and :endTime, 1=1) "  
		    + "and e.ascription = :ascription "
			+ "GROUP BY c.name order by s desc limit :page, :pagesize", nativeQuery = true)
	List<List<Object>> getExpressCourierCount(@Param("marketPkey") String marketPkey,@Param("status") Integer status,
			@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
}
