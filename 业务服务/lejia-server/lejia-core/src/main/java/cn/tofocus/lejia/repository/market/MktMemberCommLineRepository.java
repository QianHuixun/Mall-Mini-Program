package cn.tofocus.lejia.repository.market;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberCommLine;

/**
 * mkt_member_comm_line
 * 
 * @author lai 2020-06-15
 */

@Repository
public interface MktMemberCommLineRepository
		extends JpaRepository<MktMemberCommLine, Integer>, JpaSpecificationExecutor<MktMemberCommLine> {
	@Query(value = "select m.name,COUNT(ol.goods),sum(ol.num) from mkt_member_comm_line l, mkt_order o, mkt_order_line ol,mkt_member m "  
			+ "where l.source = 1 and l.form_id = o.kc_code and o.farmer = :marketPkey  "  
			+ "and o.pkey = ol.order_pkey and l.member_key = m.pkey "
			+ "and if(:memberName != '', m.name like concat('%', :memberName, '%'), 1=1) " 
			+ "group by m.name limit :page, :pagesize", nativeQuery = true)
	List<List<Object>> getCommsNum(@Param("marketPkey") String marketPkey, @Param("memberName") String memberName,
			@Param("page") int page,  @Param("pagesize") int pagesize);
	
	@Query(value = "select m.name,SUM(l.comms) s from mkt_member_comm_line l, mkt_member m, mkt_order o  "  
			+ "where l.member_key = m.pkey and l.source = 1  " 
			+ "and l.form_id = o.kc_code and o.farmer = :marketPkey "
			+ "and if(:memberName != '', m.name like concat('%', :memberName, '%'), 1=1) " 
			+ "GROUP BY m.name order by s limit :page, :pagesize", nativeQuery = true)
	List<List<Object>> getComms(@Param("marketPkey") String marketPkey, @Param("memberName") String memberName,
			@Param("page") int page,  @Param("pagesize") int pagesize);
	
//	@Query(value = "select h.kc_code,h.created_time,mm.name, h.amtn, l.comms, h.mName,l.created_time lt from mkt_member_comm_line l,mkt_order oo , "
//			+ "(select m.name mName,o.* from mkt_member m, mkt_order o  " + 
//			"where  o.tjr = m.pkey ) h , mkt_member mm  " + 
//			"where l.source = 1 and l.form_id = h.kc_code and h.farmer = :marketPkey  " + 
//			"and oo.member_key = mm.pkey and l.form_id = oo.kc_code   " + 
//			"and if(:startTime != '', l.created_time between :startTime and :endTime, 1=1) " + 
//			"order by l.created_time desc limit :page, :pagesize", nativeQuery = true)
//	List<List<Object>> getCommsDetail(@Param("marketPkey") String marketPkey,
//			@Param("startTime") String startTime, @Param("endTime") String endTime,
//			@Param("page") int page,  @Param("pagesize") int pagesize);
	
	@Query(value = "select sum(l.balance) from mkt_member_comm_line l, (select member_key,MAX(created_time) time from mkt_member_comm_line " + 
			"where DATE_FORMAT(created_time, '%Y-%m-%d') = :time " + 
			"group by member_key) h " + 
			"where  l.created_time = h.time and l.member_key = h.member_key", nativeQuery = true)
	BigDecimal yesterdayComms(@Param("time") String time);
}
