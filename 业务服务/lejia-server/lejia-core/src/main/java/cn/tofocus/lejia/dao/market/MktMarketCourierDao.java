package cn.tofocus.lejia.dao.market;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.hasPkey.CourierPkey;
import cn.tofocus.lejia.bean.entity.market.MktMarketCourier;

@Component
public class MktMarketCourierDao extends JpaSpecificationDelegate<CourierPkey, MktMarketCourier>
{
}