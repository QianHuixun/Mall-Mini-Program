package cn.tofocus.lejia.dao.h5;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.h5.H5GoodsSpace;
import cn.tofocus.lejia.bean.entity.h5.H5GoodsSpace.F;

@Component
public class H5GoodsSpaceDao extends JpaSpecificationDelegate<Integer, H5GoodsSpace>
{
    public List<H5GoodsSpace> byGoods(Integer goods)
    {
        return this.select().eq(F.goods, goods).sort(F.boxSd, false).exec();
    }
    
    public H5GoodsSpace byH5Space(String space, Integer goods)
    {
        return this.selectOne().eq(F.goods, goods).eq(F.space, space).exec();
    }
}
