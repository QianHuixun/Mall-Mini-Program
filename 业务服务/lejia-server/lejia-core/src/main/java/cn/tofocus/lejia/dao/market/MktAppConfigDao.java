package cn.tofocus.lejia.dao.market;

import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import org.springframework.stereotype.Component;

@Component
@DataSourceWithFileUrl
public class MktAppConfigDao extends JpaSpecificationDelegate<Integer,MktAppConfig>
{
}