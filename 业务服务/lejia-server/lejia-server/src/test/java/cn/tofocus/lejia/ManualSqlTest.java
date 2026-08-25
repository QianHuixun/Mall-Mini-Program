package cn.tofocus.lejia;

import javax.persistence.Table;

import org.junit.jupiter.api.Test;

import cn.tofocus.common.util.EntityUtil;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsUpdNotice;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktCard;

public class ManualSqlTest
{
    /**
     * 从新加的实体产生建表Sql
     */
    @Test
    public void entity2Sql()
    {
        entity2Sql(JdGoodsUpdNotice.class);
    }
    
    private void entity2Sql(Class<?>... list)
    {
        for (Class<?> clazz : list)
        {
            String sql = EntityUtil.entity2Sql(clazz);
            System.out.println("DROP TABLE IF EXISTS `" + clazz.getAnnotation(Table.class).name() + "`;");
            System.out.println(sql);
            System.out.println();
        }
    }
    
    /**
     * 删表Sql
     */
    @Test
    public void entity2DropSql()
    {
        //String sql = EntityUtil.entity2DropSql(MktTag.class);
        //System.out.println(sql);
    }
    
    /**
     * 表结构变更Sql
     */
    @Test
    public void entity2AlterSql()
    {
        String sql = EntityUtil.entityAddColSql(MktCard.class, MktCard.F.visibleRange);
        System.out.println(sql);
        sql = EntityUtil.entityAddColSql(MktActivity.class, MktActivity.F.visibleRange);
        System.out.println(sql);
        sql = EntityUtil.entityAddColSql(MktGoods.class, MktGoods.F.visibleRange);
        System.out.println(sql);
        
        //sql = EntityUtil.entityModifyColSql(CaterMerchantEntity.class, "updatedTime");
        //System.out.println(sql);
    }
}
