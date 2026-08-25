package cn.tofocus.lejia.repository.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMember;

/**
*  mkt_member
* @author lai 2020-06-15
*/

@Repository
public interface MktMemberRepository extends JpaRepository<MktMember,Integer>,  JpaSpecificationExecutor<MktMember> 
{
	@Query(value = "select * from mkt_member where pkey not in (select member_key FROM mkt_order) and ascription = :ascription "
			+ "and if(:member != '', pkey = :member , 1=1) ", nativeQuery = true)
	List<MktMember> getNotOrder(@Param("member") Integer member, @Param("ascription") Integer ascription);
	
	@Query(value = "select DATE_FORMAT(created_time, '%Y-%m-%d'),COUNT(pkey) from mkt_member  " 
			+ "where if(:startTime != '', created_time between :startTime and :endTime, 1=1)  " 
			+ "and ascription = :ascription "
			+ "GROUP BY DATE_FORMAT(created_time, '%Y-%m-%d')", nativeQuery = true)
	List<List<Object>> getAddMemberCount(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("ascription") Integer ascription);
}
