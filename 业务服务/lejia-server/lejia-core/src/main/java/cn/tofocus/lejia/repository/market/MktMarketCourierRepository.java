package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.hasPkey.CourierPkey;
import cn.tofocus.lejia.bean.entity.market.MktMarketCourier;

/**
*  市场骑手派单顺序
* @author zdw 2021-09-22
*/

@Repository
public interface MktMarketCourierRepository
    extends JpaRepository<MktMarketCourier, CourierPkey>, JpaSpecificationExecutor<MktMarketCourier>
{
}
