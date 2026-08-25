package cn.tofocus.lejia.repository.market;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.member.MktMemberGift;

/**
*  礼券明细
* @author zdw 2020-11-09
*/

@Repository
public interface MktMemberGiftRepository extends JpaRepository<MktMemberGift,Integer>,  JpaSpecificationExecutor<MktMemberGift> 
{
}
