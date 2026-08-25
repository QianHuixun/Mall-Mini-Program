package cn.tofocus.lejia.repository.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberPay;

/**
*  会员年费
* @author zdw 2020-07-29
*/

@Repository
public interface MktMemberPayRepository extends JpaRepository<MktMemberPay,Integer>,  JpaSpecificationExecutor<MktMemberPay> 
{
	@Query(value = "select p.* from mkt_member_pay p, mkt_member m " 
	    + "where p.member_key = m.pkey  " 
	    + " and p_type = 1 and status = 1 " 
	    + " and p.ascription = :ascription "
	    + "	and if(:mobile != '', m.mobile like concat('%', :mobile, '%'), 1=1) " 
	    + "	and if(:startTime != '', p.pay_time > :startTime, 1=1) "  
	    + "	and if(:endTime != '', p.pay_time < :endTime, 1=1) " 
	    + "   ORDER BY pkey desc", nativeQuery = true)
	List<MktMemberPay> queryMemberPay(@Param("mobile") String mobile, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("ascription") Integer ascription);
	
	@Query(value = "select DATE_FORMAT(created_time, '%Y-%m-%d'),COUNT(pkey) from mkt_member_pay " 
			+ "where status = 1 and p_type = 0 "
			+ "and if(:startTime != '', created_time between :startTime and :endTime, 1=1) "  
			+ "and ascription = :ascription "
			+ "GROUP BY DATE_FORMAT(created_time, '%Y-%m-%d')", nativeQuery = true)
	List<List<Object>> getMemberPay(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("ascription") Integer ascription);
	
	@Query(value = "SELECT SUM(amt),COUNT(pkey) from mkt_member_pay where status = 1 and ascription = :ascription ", nativeQuery = true)
	List<List<Object>> consumption(@Param("ascription") Integer ascription);
}
