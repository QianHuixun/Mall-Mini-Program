package cn.tofocus.lejia.repository.market;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktWareLine;

/**
*  mkt_ware_line
* @author zdw 2020-09-25
*/

@Repository
public interface MktWareLineRepository extends JpaRepository<MktWareLine,Integer>,  JpaSpecificationExecutor<MktWareLine> 
{
}
