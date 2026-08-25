package cn.tofocus.lejia.dao.goods;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift.F;
import cn.tofocus.lejia.repository.goods.MktGoodsGiftRepository;
import cn.tofocus.lejia.utils.DateUtil;

@Component
@DataSourceWithFileUrl
public class MktGoodsGiftDao extends JpaSpecificationDelegate<Integer, MktGoodsGift>
{
    
    @Autowired
    private MktGoodsGiftRepository repository;
    
    public Map<Integer, MktGoodsGift> getMap(List<Integer> pkeys)
    {
        Map<Integer, MktGoodsGift> res = new HashMap<>();
        List<MktGoodsGift> list = this.select().in("pkey", pkeys).exec();
        list.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        return res;
    }
    
    public Map<Integer, MktGoodsGift> getGoodsMap(List<Integer> pkeys)
    {
        Map<Integer, MktGoodsGift> res = new HashMap<>();
        List<MktGoodsGift> list = this.select().in("goods", pkeys).exec();
        list.forEach(e -> {
            res.put(e.getGoods(), e);
        });
        return res;
    }
    
    public BigDecimal sumAmtn(Integer vendor, String startDate, String endDate)
    {
        return repository.sumAmtn(vendor, DateUtil.atStartOfDay(startDate), DateUtil.atEndOfDay(endDate));
    }
    
    public List<MktGoodsGift> listGoodsGiftV3True(List<Integer> keys, int page, int pagesize)
    {
        return repository.listGiftV3True(keys, page * pagesize, pagesize);
    }
    
    public List<MktGoodsGift> listGoodsGiftV3False(List<Integer> keys, int page, int pagesize)
    {
        return repository.listGiftV3False(keys, page * pagesize, pagesize);
    }
    
    public void updIssuedNum(Integer pkey, Integer num)
    {
        this.select().strict(true).eq(F.pkey, pkey).update(F.issuedNum, num);
    }
    
    public void updUsedNum(Integer pkey, Integer num)
    {
        this.select().strict(true).eq(F.pkey, pkey).update(F.usedNum, num);
    }
    
    public <T> PageResult<T> query(int page, int pagesize, String title, Boolean enabled, Boolean invalid,
        String farmer, Integer ascription, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .like(F.title, title)
            .eq(F.enabled, enabled)
            .eq(F.invalid, invalid)
            .eq(F.farmer, farmer)
            .eq(F.ascription, ascription)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public <T> T get(Integer pkey, Class<T> clazz)
    {
        return this.selectOne().eq(F.pkey, pkey).execDto(clazz);
    }
    
    public MktGoodsGift getByGoods(Integer goods)
    {
        return this.selectOne().eq(F.goods, goods).exec();
    }
    
    public List<Integer> listPkeys(Integer ascription, String farmer, String title)
    {
        return this.select()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .like(F.title, title)
            .execDto(F.pkey, Integer.class);
    }
    
}