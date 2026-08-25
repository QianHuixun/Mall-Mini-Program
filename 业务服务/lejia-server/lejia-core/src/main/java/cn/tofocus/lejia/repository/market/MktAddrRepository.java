package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktAddr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  mkt_addr
* @author lai 2020-06-15
*/

@Repository
public interface MktAddrRepository extends JpaRepository<MktAddr,Integer>,  JpaSpecificationExecutor<MktAddr> 
{
}
