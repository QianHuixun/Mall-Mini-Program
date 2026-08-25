package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberComm;

/**
*  mkt_member_comm
* @author lai 2020-06-15
*/

@Repository
public interface MktMemberCommRepository extends JpaRepository<MktMemberComm,Integer>,  JpaSpecificationExecutor<MktMemberComm> 
{
}
