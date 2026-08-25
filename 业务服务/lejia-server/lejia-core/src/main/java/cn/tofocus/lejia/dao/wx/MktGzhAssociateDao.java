package cn.tofocus.lejia.dao.wx;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.wx.MktGzhAssociate;
import cn.tofocus.lejia.bean.entity.wx.MktGzhAssociate.F;

@Component
public class MktGzhAssociateDao extends JpaSpecificationDelegate<Integer, MktGzhAssociate>
{
    
    public List<Integer> listTrueAssKeys(String farmer)
    {
        List<MktGzhAssociate> list = this.select().eq(F.farmer, farmer).eq(F.enabled, true).exec();
        List<Integer> res = new ArrayList<>();
        list.forEach(e -> res.add(e.getGzh()));
        return res;
    }
    
    public MktGzhAssociate getGA(String farmer, Integer gzh)
    {
        return this.selectOne().eq(F.farmer, farmer).eq(F.gzh, gzh).exec();
    }

    
}
