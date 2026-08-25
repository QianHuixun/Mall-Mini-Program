package cn.tofocus.lejia.repository.market;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktCommDraw;

/**
*  提现表
* @author zdw 2020-09-22
*/

@Repository
public interface MktCommDrawRepository extends JpaRepository<MktCommDraw,Integer>,  JpaSpecificationExecutor<MktCommDraw> 
{
}
