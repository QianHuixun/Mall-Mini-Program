package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberPoint;

/**
*  mkt_member_point
* @author lai 2020-06-15
*/

@Repository
public interface MktMemberPointRepository extends JpaRepository<MktMemberPoint,Integer>,  JpaSpecificationExecutor<MktMemberPoint> 
{
}
