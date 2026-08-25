package cn.tofocus.lejia.dao.market;

import org.springframework.stereotype.Component;

import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktDrawPrize;

@Component
@DataSourceWithFileUrl
public class MktDrawPrizeDao extends JpaSpecificationDelegate<Integer,MktDrawPrize>
{
}