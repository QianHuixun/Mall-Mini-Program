package cn.tofocus.lejia.repository.market;

import cn.tofocus.lejia.bean.entity.market.MktGwc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
*  购物车
* @author zdw 2020-07-16
*/

@Repository
public interface MktGwcRepository extends JpaRepository<MktGwc,Integer>,  JpaSpecificationExecutor<MktGwc> 
{
    Integer countByMemberAndFarmer(Integer memberPkey, String farmerPkey);
}
