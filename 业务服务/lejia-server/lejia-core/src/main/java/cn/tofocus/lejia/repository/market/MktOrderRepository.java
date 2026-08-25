package cn.tofocus.lejia.repository.market;

import java.util.Date;
import java.util.List;

import javax.persistence.TemporalType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktOrder;

/**
*  订单
* @author lai 2020-06-15
*/

@Repository
public interface MktOrderRepository extends JpaRepository<MktOrder, Integer>, JpaSpecificationExecutor<MktOrder>
{
    @Query(value = "SELECT o.kc_code,f.name,(l.pricen * l.num),l.goods_name,o.created_time from mkt_order o, sys_farmer f, mkt_order_line l "
        + "where member_key = :memberPkey and o.status in (1,2,3,4) and o.farmer = f.pkey and o.pkey = l.order_pkey ORDER BY o.created_time desc limit :page, :pagesize", nativeQuery = true)
    List<List<Object>> queryMemberConsumption(@Param("memberPkey") Integer memberPkey, @Param("page") int page,
        @Param("pagesize") int pagesize);
    
    @Query(value = "select COUNT(*) from mkt_order o, sys_farmer f, mkt_order_line l  "
        + "where member_key = :memberPkey and o.status in (1,2,3,4) and o.farmer = f.pkey and o.pkey = l.order_pkey", nativeQuery = true)
    Long queryMemberConsumptionCount(@Param("memberPkey") Integer memberPkey);
    
    @Query(value = "select DATE_FORMAT(created_time,'%H'),SUM(amto),count(pkey) from mkt_order "
        + "where amtn is not null and DATE_FORMAT(created_time, '%Y-%m-%d') = :time " + " and status in (1,2,3,4)  and ascription = :ascription "
        + "GROUP BY DATE_FORMAT(created_time,'%H')  ", nativeQuery = true)
    List<List<Object>> yesterdayHourData(@Param("time") String time, @Param("ascription") Integer ascription);
    
    @Query(value = "select farmer,DATE_FORMAT(created_time,'%H'),SUM(amto),count(pkey) from mkt_order "
        + "where amtn is not null and DATE_FORMAT(created_time, '%Y-%m-%d') = :time " + " and status in (1,2,3,4)  and ascription = :ascription "
        + "group by farmer,DATE_FORMAT(created_time,'%H') ", nativeQuery = true)
    List<List<Object>> yesterdayHourMarketData(@Param("time") String time, @Param("ascription") Integer ascription);
    
    @Query(value = "select company,DATE_FORMAT(created_time,'%H'),SUM(amto),count(pkey) from mkt_order "
        + "where amtn is not null and DATE_FORMAT(created_time, '%Y-%m-%d') = :time " + " and status in (1,2,3,4)  and ascription = :ascription "
        + "group by company,DATE_FORMAT(created_time,'%H')", nativeQuery = true)
    List<List<Object>> yesterdayHourCompanyData(@Param("time") String time, @Param("ascription") Integer ascription);
    
    @Query(value = "select COUNT(o.pkey) from mkt_order o, mkt_order_line l "
        + "where l.goods = :goodsPkey and o.pkey = l.order_pkey and o.order_type = 3 "
        + "and o.status in (1,2,3,4) ", nativeQuery = true)
    List<List<Object>> getOrderCutNum(@Param("goodsPkey") Integer goodsPkey);
    
    @Query(value = "select o.pkey from mkt_order o, mkt_order_line l "
        + "where o.pkey = l.order_pkey and l.goods = :goodsPkey and o.member_key = :member  "
        + "and o.status = 0 ", nativeQuery = true)
    List<List<Object>> judgOrderCut(@Param("goodsPkey") Integer goodsPkey, @Param("member") Integer member);
    
    @Query(value = "SELECT DATE_format(created_time, '%Y-%m-%d %k'),SUM(amtall)  from mkt_order "
        + "where created_time BETWEEN ?1 and ?2 and status in (1,2,3,4,5,6)  and ascription = ?3 "
        + "GROUP BY DATE_format(created_time, '%Y-%m-%d %k') ", nativeQuery = true)
    List<List<Object>> hourAmtn(@Temporal(TemporalType.TIMESTAMP) Date startTime,
        @Temporal(TemporalType.TIMESTAMP) Date endTime,  Integer ascription);
    
    @Query(value = "SELECT DATE_format(created_time, '%Y-%m-%d'),SUM(amtall)  from mkt_order "
        + "where created_time BETWEEN ?1 and ?2 and status in (1,2,3,4,5,6) and ascription = ?3 "
        + "GROUP BY DATE_format(created_time, '%Y-%m-%d') ", nativeQuery = true)
    List<List<Object>> dayAmtn(@Temporal(TemporalType.TIMESTAMP) Date startTime,
        @Temporal(TemporalType.TIMESTAMP) Date endTime, Integer ascription);
    
    @Query(value = "select * from mkt_order where kc_code = :code for update", nativeQuery = true)
    MktOrder getCodeLock(@Param("code") String code);

    @Query(value = "SELECT o.* FROM mkt_order o WHERE NOT EXISTS "
        + " (SELECT 1 FROM mkt_order_line l WHERE l.order_pkey = o.pkey ) "
        + " and `status` != 99", nativeQuery = true)
    List<MktOrder> byNotExists();
}
