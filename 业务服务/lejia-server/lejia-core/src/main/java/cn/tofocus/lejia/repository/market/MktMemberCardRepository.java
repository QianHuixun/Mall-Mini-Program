package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberCard;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface MktMemberCardRepository extends JpaRepository<MktMemberCard,Integer>,  JpaSpecificationExecutor<MktMemberCard> 
{
	@Query(value = "select card,count(card) num from mkt_member_card where card = :id and status = :status GROUP BY card", nativeQuery = true)
	Map<String,Object> getCardStatusCount(@Param("id") Integer id,@Param("status") Integer status);
	
	@Query(value = "select card,count(card) num from mkt_member_card where card = :id  GROUP BY card", nativeQuery = true)
	Map<String,Object> getCardCount(@Param("id") Integer id);
	
	@Query(value = "select count(pkey) num from mkt_member_card where member_key = :memberPkey and status = 0", nativeQuery = true)
	Integer getMemberCardCount(@Param("memberPkey") Integer memberPkey);
	
    @Modifying
    @Query("update MktMemberCard set status = 2 where status = 0 and endDate < :today")
    void disableExpiredCards(@Param("today") Date today);
    
    @Query(value = "select o.kc_code,c.title,f.name, mc.user_time from mkt_member_card mc, mkt_order o ,mkt_card c, sys_farmer f "  
    		+ "where mc.order_id = o.pkey and mc.card = c.pkey and mc.status = 1 "
    		+ "and mc.member_key = :memberPkey and mc.farmer = f.pkey ORDER BY o.created_time desc limit :page, :pagesize ", nativeQuery = true)
    List<List<Object>> queryMemberCardRecord(@Param("memberPkey") Integer memberPkey, @Param("page") int page,  @Param("pagesize") int pagesize);
    
    @Query(value = "select count(mc.pkey) from mkt_member_card mc, mkt_order o ,mkt_card c, sys_farmer f " + 
    		"where mc.order_id = o.pkey and mc.card = c.pkey and mc.status = 1 and mc.member_key = :memberPkey and mc.farmer = f.pkey", nativeQuery = true)
    Long queryMemberCardRecordCount(@Param("memberPkey") Integer memberPkey);
    
    @Query(value = "select f.name,COUNT(c.pkey) s,SUM(c.cost) from mkt_member_card c, sys_farmer f "  
    		+ "where c.status = 1 and c.user_farmer = f.pkey " 
			+ "and if(:marketPkey != '', c.farmer = :marketPkey, 1=1) "
			+ "and if(:companyPkey != '', c.company = :companyPkey, 1=1) "
			+ "and if(:startTime != '', c.user_time between :startTime and :endTime, 1=1) "
    		+ "group by f.name ORDER BY s desc LIMIT :page, :pagesize ", nativeQuery = true)
    List<List<Object>> queryFarmerCardCount(
    		@Param("marketPkey") String marketPkey, 
			@Param("companyPkey") String companyPkey,
			@Param("startTime") String startTime, @Param("endTime") String endTime,
    		@Param("page") int page,  @Param("pagesize") int pagesize);
    
}
