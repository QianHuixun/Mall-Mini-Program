package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberPointLine;

/**
*  mkt_member_point_line
* @author lai 2020-06-15
*/

@Repository
public interface MktMemberPointLineRepository extends JpaRepository<MktMemberPointLine,Integer>,  JpaSpecificationExecutor<MktMemberPointLine> 
{
    @Query(value = "select sum(points) from mkt_member_point_line where direct = 1 and member_key = ?", nativeQuery = true)
    Integer sumPoints(Integer memberPkey);
}
