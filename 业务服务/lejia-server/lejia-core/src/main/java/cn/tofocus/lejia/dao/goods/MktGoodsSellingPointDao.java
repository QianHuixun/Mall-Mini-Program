package cn.tofocus.lejia.dao.goods;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSellingPoint;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSellingPoint.F;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MktGoodsSellingPointDao extends JpaSpecificationDelegate<Integer, MktGoodsSellingPoint>
{
    public <T> List<T> listByGoods(Integer goods, Integer ascription, Class<T> clazz)
    {
        return this.select().eq(F.goods, goods).eq(F.ascription, ascription).sort(F.pkey, false).execDto(clazz);
    }
    
    public List<String> listContentByGoods(Integer goods, Integer ascription)
    {
        return this.select()
            .eq(F.goods, goods)
            .eq(F.ascription, ascription)
            .sort(F.pkey, false)
            .execDto(F.content, String.class);
    }
    
    public void removeByGoods(Integer goods, Integer ascription)
    {
        this.select().strict(true).eq(F.goods, goods).eq(F.ascription, ascription).del();
    }
}
