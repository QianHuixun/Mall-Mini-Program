package cn.tofocus.lejia.dao.wx;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.wx.MktGzh;
import cn.tofocus.lejia.bean.entity.wx.MktGzh.F;

@Component
public class MktGzhDao extends JpaSpecificationDelegate<Integer, MktGzh>
{
    public List<MktGzh> listGzh(List<Integer> keys)
    {
        return this.select().in(F.pkey, keys).exec();
    }
}
