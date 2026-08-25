package cn.tofocus.lejia.dao.h5;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.h5.H5GoodsOnPage;
import cn.tofocus.lejia.bean.entity.h5.H5Goods;
import cn.tofocus.lejia.bean.entity.h5.H5Goods.F;
import cn.tofocus.lejia.bean.enums.h5.H5Level;

@Component
public class H5GoodsDao extends JpaSpecificationDelegate<Integer, H5Goods>
{
    public PageResult<H5GoodsOnPage> query(int page, int pagesize, int level, String farmer, Integer ascription)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.farmer, farmer)
            .eq(F.ascription, ascription)
            .iF(level == 1)
            .notEq(F.levelA, H5Level.INVISIBLE)
            .endIf()
            .iF(level == 2)
            .notEq(F.levelB, H5Level.INVISIBLE)
            .endIf()
            .iF(level == 3)
            .notEq(F.levelC, H5Level.INVISIBLE)
            .endIf()
            .sort(F.sort, false)
            .sort(F.pkey, false)
            .execDto(H5GoodsOnPage.class);
    }
    
    public List<H5Goods> listAll()
    {
        return this.select().eq(F.enabled, true).eq(F.idDel, false).exec();
    }
    
    public H5Goods getGoods(Integer goods)
    {
        return this.selectOne().eq(F.pkey, goods).eq(F.enabled, true).eq(F.idDel, false).exec();
    }
    
    public H5Goods byH5Goods(String title, String farmer)
    {
        return this.selectOne()
        .eq("title", title)
        .eq("farmer", farmer)
        .eq("idDel", false)
        .exec();
        
    }
}
