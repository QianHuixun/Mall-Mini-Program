package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktDesktop;

@Component
public class MktDesktopDao extends JpaSpecificationDelegate<Integer, MktDesktop>
{
    public List<MktDesktop> list(String name, String farmer, Integer ascription)
    {
        return this.select()
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .like("name", name)
            .sort("pkey")
            .exec();
    }
    
}
