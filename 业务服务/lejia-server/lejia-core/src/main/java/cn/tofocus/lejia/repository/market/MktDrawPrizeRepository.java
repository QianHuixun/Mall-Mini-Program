package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.market.MktDrawPrize;

/**
*  广告位
* @author lai 2020-06-15
*/

@Repository
public interface MktDrawPrizeRepository extends JpaRepository<MktDrawPrize,Integer>,  JpaSpecificationExecutor<MktDrawPrize> 
{
}
