package cn.tofocus.lejia.repository.market;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberSign;

/**
*  mkt_member_sign
* @author zdw 2020-07-16
*/

@Repository
public interface MktMemberSignRepository extends JpaRepository<MktMemberSign,Integer>,  JpaSpecificationExecutor<MktMemberSign> 
{
	@Query(value = "select * from mkt_member_sign where member_key = :member and year(sign_date) = :year and month(sign_date) = :month order by pkey desc;", nativeQuery = true)
	public List<MktMemberSign> getSigns(
			@Param("member") Integer member, 
			@Param("year") Integer year,
			@Param("month") Integer month);
}
