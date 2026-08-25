package cn.tofocus.lejia.dao.applet;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.applet.XaszAssociationEntity;

@Component
public class XaszAssociationDao extends JpaSpecificationDelegate<Integer, XaszAssociationEntity>
{
    public Boolean checkFarmerExist(String farmer)
    {
        long count = this.aggregation().eq("farmer", farmer).execCount();
        return count > 0;
    }
    
    public XaszAssociationEntity getFarmer(String farmer)
    {
        return this.selectOne().eq("farmer", farmer).exec();
    }
    
    public XaszAssociationEntity getMarket(Integer market)
    {
        return this.selectOne().eq("market", market).exec();
    }
}
