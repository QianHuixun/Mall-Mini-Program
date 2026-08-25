package cn.tofocus.lejia.repository.market;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktOrderLine;

/**
*  订单明细
* @author lai 2020-06-15
*/

@Repository
public interface MktOrderLineRepository extends JpaRepository<MktOrderLine,Integer>,  JpaSpecificationExecutor<MktOrderLine> 
{
	
	
	@Query(value = "select g.m_type,SUM(l.pricen*l.num) s,count(g.m_type) from mkt_order_line l, mkt_goods g, mkt_order o where "
			+ "l.goods = g.pkey and l.order_pkey = o.pkey  and o.status in (1,2,3,4)  and l.ascription = :ascription "
			+ "and if(:marketPkey != '', l.farmer = :marketPkey, 1=1) "
			+ "and if(:companyPkey != '', l.company = :companyPkey, 1=1) "
			+ "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1) GROUP BY g.m_type order by s desc limit :page, :pagesize", nativeQuery = true)
	List<List<Object>> getMtypeSales(@Param("marketPkey") String marketPkey, 
				@Param("companyPkey") String companyPkey, @Param("startTime") String startTime, @Param("endTime") String endTime,
				@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
	
	@Query(value = "select l.goods_name,SUM(l.pricen*l.num) s,count(l.goods),l.goods from mkt_order_line l, mkt_order o where "
			+ "l.order_pkey = o.pkey  and o.status in (1,2,3,4)  and l.ascription = :ascription "
			+ "and if(:marketPkey != '', l.farmer = :marketPkey, 1=1) "
			+ "and if(:companyPkey != '', l.company = :companyPkey, 1=1) "
			+ "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1) GROUP BY l.goods_name,l.goods order by s desc limit :page, :pagesize", nativeQuery = true)
	List<List<Object>> getGoodsSales(@Param("marketPkey") String marketPkey, 
				@Param("companyPkey") String companyPkey, @Param("startTime") String startTime, @Param("endTime") String endTime,
				@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
	
	@Query(value = "select SUM(l.pricen*l.num) s,DATE_FORMAT(o.created_time,'%Y-%m-%d') from mkt_order_line l, mkt_order o where "
			+ "if(:goodsPkey != '', l.goods = :goodsPkey, 1=1) and l.order_pkey = o.pkey  and o.status in (1,2,3,4)   and l.ascription = :ascription "
			+ "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1) GROUP BY DATE_FORMAT(o.created_time,'%Y-%m-%d') order by DATE_FORMAT(o.created_time,'%Y-%m-%d') ", nativeQuery = true)
	List<List<Object>> getGoodsAnalysis(@Param("goodsPkey") Integer goodsPkey, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("ascription") Integer ascription);
	
	@Query(value = "select l.goods_name,SUM(l.pricen*l.num) s,count(l.goods),l.goods from mkt_order_line l, mkt_order o where "
			+ "l.order_pkey = o.pkey and o.status in (1,2,3,4)  "
			+ "and l.farmer = :marketPkey "
			+ "and DATE_SUB(CURDATE(), INTERVAL 30 DAY) <= date(o.created_time)"
			+ "GROUP BY l.goods_name,l.goods HAVING COUNT(l.goods_name) <= :abnormalNum order by s desc limit :page, :pagesize", nativeQuery = true)
	public List<List<Object>> getGoodsAbnormal(@Param("marketPkey") String marketPkey, @Param("abnormalNum") Integer abnormalNum,
			@Param("page") int page,  @Param("pagesize") int pagesize);
	
	@Query(value = "select DATE_FORMAT(o.created_time,'%H'),SUM(l.pricen*l.num)  from mkt_order_line l,  mkt_order o "  
			+ "where if(:goodsPkey != '', l.goods = :goodsPkey, 1=1) and l.order_pkey = o.pkey and o.status in (1,2,3,4)  and l.ascription = :ascription "
			+ "and o.created_time  between :startTime and :endTime  "  
			+ "GROUP BY DATE_FORMAT(o.created_time,'%H') ", nativeQuery = true)
	List<List<Object>> getgoodsHourAnalysis(@Param("goodsPkey") Integer goodsPkey, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("ascription") Integer ascription);
	
	@Query(value = "select l.goods_name,l.num,l.pricen,o.kc_code,o.created_time from mkt_order_line l,  mkt_order o " 
			+ "where if(:goodsPkey != '', l.goods = :goodsPkey, 1=1) and l.order_pkey = o.pkey  and o.status in (1,2,3,4) and l.ascription = :ascription  "
			+ "and o.created_time  between :startTime and :endTime  limit :page, :pagesize ", nativeQuery = true)
	List<List<Object>> getgoodsHourDetail(@Param("goodsPkey") Integer goodsPkey, @Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
	
	@Query(value = "select l.goods_name,SUM(l.pricen*l.num) s,COUNT(l.goods),SUM(o.pointn),l.goods from mkt_order_line l, mkt_goods g, mkt_order o "
			+ "where  g.m_type in (0,9,10) and l.goods = g.pkey and l.order_pkey = o.pkey and o.status in (1,2,3,4)  and l.ascription = :ascription "
			+ "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1)  "  
			+ "group BY l.goods_name,l.goods ORDER BY s desc limit :page, :pagesize ", nativeQuery = true)
	List<List<Object>> getgoodsIntegralSales(@Param("startTime") String startTime, @Param("endTime") String endTime, 
			@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
	
	
	@Query(value = "select l.goods_name,SUM(l.pricen*l.num) s,count(l.goods),l.goods from mkt_order_line l, mkt_order o where "
			+ "l.order_pkey = o.pkey  and o.status in (1,2,3,4) "
			+ "and  o.member_key in (:memberPkeys) "
			+ "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1) GROUP BY l.goods_name,l.goods order by s desc limit :page, :pagesize ", nativeQuery = true)
	List<List<Object>> getMemberGoodsSales(@Param("memberPkeys") List<Integer> memberPkeys,
			@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("page") int page, @Param("pagesize") int pagesize);
	
	@Query(value = "select mg.name,SUM(l.pricen*l.num) s,COUNT(l.pkey) from mkt_order_line l, mkt_goods g,mkt_order o, mkt_gtype mg  " 
			+ "where  l.order_pkey = o.pkey  and l.goods = g.pkey and g.gtype = mg.pkey and o.status in (1,2,3,4)  and l.ascription = :ascription  " 
			+ "and if(:marketPkey != '', l.farmer = :marketPkey, 1=1) "
			+ "and if(:companyPkey != '', l.company = :companyPkey, 1=1) "
			+ "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1)"
			+ "GROUP BY mg.name ORDER BY s desc limit :page, :pagesize ", nativeQuery = true)
	List<List<Object>> getGoodsTypeSales(@Param("marketPkey") String marketPkey, 
				@Param("companyPkey") String companyPkey, @Param("startTime") String startTime, @Param("endTime") String endTime,
				@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
	
   @Query(value ="SELECT f.name fname,c.name cname, SUM(o.amto) s, count(o.pkey)  from  sys_farmer f, sys_company c,mkt_order o where " 
           + "o.farmer = f.pkey and o.company = c.pkey and o.status in (1,2,3,4)  and f.ascription = :ascription "  
           + "and if(:marketPkey != '', o.farmer = :marketPkey, 1=1) "
           + "and if(:companyPkey != '', o.company = :companyPkey, 1=1) "
           + "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1) "
           + "and f.id_del = 0 and c.id_del = 0 "
           + "and f.pkey != 1 and c.pkey != 1 "
           + "GROUP BY f.name,c.name order by s desc limit :page, :pagesize", nativeQuery = true)
   List<List<Object>> getFarmerSales(@Param("marketPkey") String marketPkey, 
           @Param("companyPkey") String companyPkey, @Param("startTime") String startTime, @Param("endTime") String endTime,
           @Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
	
	@Query(value ="SELECT c.name cname, SUM(o.amto) s, count(o.pkey)  from  sys_company c,mkt_order o where " 
			+ "o.status in (1,2,3,4) and c.pkey = o.company  and c.ascription = :ascription "  
			+ "and if(:companyPkey != '', o.company = :companyPkey, 1=1) "
			+ "and if(:startTime != '', o.created_time between :startTime and :endTime, 1=1) "  
			+ "and c.id_del = 0 "
			+ "and c.pkey != 1 "
			+ "GROUP BY c.name order by s desc limit :page, :pagesize", nativeQuery = true)
	List<List<Object>> getCompanySales(
			@Param("companyPkey") String companyPkey, @Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("page") int page,  @Param("pagesize") int pagesize, @Param("ascription") Integer ascription);
	
	@Query(value = "select o.company,o.farmer,SUM(l.pricen*l.num) s,COUNT(g.m_type),g.m_type from mkt_order_line l, mkt_goods g, mkt_order o "
			+ "where  l.goods = g.pkey and l.order_pkey = o.pkey  and o.status in (1,2,3,4)  and l.ascription = :ascription  " 
			+ "and o.created_time between :startTime and :endTime "
			+ "GROUP BY o.company,o.farmer,g.m_type ", nativeQuery = true)
	List<List<Object>> getLastWeekMtypeSales( @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("ascription") Integer ascription);
	
	@Query(value = "select o.company,o.farmer,l.goods_name,SUM(l.pricen*l.num) s,COUNT(l.goods) sa,l.goods from mkt_order_line l, mkt_order o  " + 
			"where  l.order_pkey = o.pkey  and o.status in (1,2,3,4) and o.created_time < :time  and l.ascription = :ascription " + 
			"GROUP BY o.company,o.farmer,l.goods_name,l.goods ORDER BY sa desc", nativeQuery = true)
	List<List<Object>> getYesterdayGoodsSales(@Param("time") String time, @Param("ascription") Integer ascription);
	
	@Query(value = "select gtype,SUM(pricen * num) s from mkt_order_line WHERE " 
       + "status <> 0 and status <> 99 "  
       + "and created_time BETWEEN :startTime and :endTime  and ascription = :ascription "
       + "GROUP BY gtype  " 
       + "ORDER BY s asc LIMIT 10 ", nativeQuery = true)
	List<List<Object>> listTypeSales(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("ascription") Integer ascription);
 
	@Query(value = "select goods,SUM(pricen * num) s from mkt_order_line WHERE " 
	    + "status <> 0 and status <> 99 "  
	    + "and created_time BETWEEN :startTime and :endTime and ascription = :ascription "
	    + "GROUP BY goods  " 
	    + "ORDER BY s desc LIMIT 20 ", nativeQuery = true)
	List<List<Object>> listGoodsSales(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("ascription") Integer ascription);
	
}




