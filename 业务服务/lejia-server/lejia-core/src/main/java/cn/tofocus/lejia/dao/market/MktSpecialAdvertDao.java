package cn.tofocus.lejia.dao.market;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.hasPkey.AdvertPkey;
import cn.tofocus.lejia.bean.entity.market.MktSpecialAdvert;

@Component
public class MktSpecialAdvertDao extends JpaSpecificationDelegate<AdvertPkey, MktSpecialAdvert>
{
}