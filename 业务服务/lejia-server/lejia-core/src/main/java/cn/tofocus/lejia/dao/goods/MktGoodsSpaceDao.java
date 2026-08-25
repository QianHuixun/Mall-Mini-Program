package cn.tofocus.lejia.dao.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace.F;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.enums.v3.GoodsSpaceKcV3Dto;
import cn.tofocus.lejia.bean.enums.v3.SortType;
import cn.tofocus.lejia.repository.market.MktGoodsSpaceRepository;

@Component
@DataSourceWithFileUrl
public class MktGoodsSpaceDao extends JpaSpecificationDelegate<Integer, MktGoodsSpace>
{
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private MktGoodsSpaceRepository repository;
    
    public Map<Integer, BigDecimal> findSpacePrice(Set<Integer> pkeys)
    {
        Map<Integer, BigDecimal> res = new HashMap<>();
        List<MktGoodsSpace> list = this.select().in("pkey", pkeys.toArray()).exec();
        for (MktGoodsSpace gs : list)
        {
            BigDecimal price = gs.getPrice();
            if (gs.getPriceMember().compareTo(BigDecimal.ZERO) > 0) price = gs.getPriceMember();
            res.put(gs.getPkey(), price);
        }
        return res;
    }
    
    public Map<Integer, MktGoodsSpace> getSpaceMap(List<Integer> pkeys)
    {
        Map<Integer, MktGoodsSpace> res = new HashMap<>();
        List<MktGoodsSpace> exec;
        int size = pkeys.size();
        int num = size / 10000;
        if(num > 0)
        {
            SelectBuilder<Integer,MktGoodsSpace> builder = this.select();
            for(int i = 0; i < num; i++)
            {
                builder.in("pkey", pkeys.subList(i * 10000, i * 10000 + 10000));
            }
            builder.in("pkey", pkeys.subList(num * 10000, pkeys.size()));
            exec = builder.exec();
        } 
        else
            exec = this.select().in("pkey", pkeys).exec();
        if (exec.isEmpty()) return res;
        exec.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        return res;
    }
    
    public List<MktGoodsSpace> listGoodsSpace(List<Integer> pkeys)
    {
        return this.select().in("pkey", pkeys).exec();
    }
    
    public Map<Integer, MktGoodsSpace> getGoodsSpaceMap(List<Integer> pkeys)
    {
        Map<Integer, MktGoodsSpace> res = new HashMap<>();
        if(pkeys.isEmpty())
            return res;
        List<MktGoodsSpace> exec = this.select().in("goods", pkeys).exec();
        if (exec.isEmpty()) return res;
        List<Integer> keys = new ArrayList<>();
        exec.forEach(e -> keys.add(e.getPkey()));
        List<MktSpaceKc> kcList = spaceKcDao.select().in("pkey", keys).exec();
        Map<Integer, MktSpaceKc> map = new HashMap<>();
        kcList.forEach(e -> map.put(e.getPkey(), e));
        for (MktGoodsSpace e : exec)
        {
            if (map.containsKey(e.getPkey())) e.setKcNum(map.get(e.getPkey()).getKcNum());
            res.put(e.getGoods(), e);
        }
        return res;
    }
    
    public PageResult<MktGoodsSpaceOnList> listSpaceV3(int page, int pagesize, SortType sortType, Boolean sort,
        List<Integer> keys)
    {
        SelectPageBuilder<Integer, MktGoodsSpace> builder =
            this.selectPage().page(page).pagesize(pagesize).in("goods", keys);
        if (sortType != null)
        {
            switch (sortType)
            {
                case ORIGINAL_PRICE_SORT:
                    builder.sort("priceOld", sort);
                    break;
                case CURRENT_PRICE_SORT:
                    builder.sort("price", sort);
                    break;
                case STOCK_SORT:
                    builder.sort("kcNum", sort);
                    break;
                case MEMBER_SORT:
                    builder.sort("priceMember", sort);
                    break;
                case COMMISSION_SORT:
                    builder.sort("comm", sort);
                    break;
                case INTEGRAL_SORT:
                    builder.sort("point", sort);
                    break;
                default:
                    break;
            }
        }
        return builder.execDto(MktGoodsSpaceOnList.class);
    }
    
    public List<GoodsSpaceKcV3Dto> listKcNumSort(Integer page, Integer pagesize, List<Integer> keys)
    {
        return repository.listKcNumSort(page, page * pagesize, keys);
    }
    
    public void updateKcNum(Integer pkey, Integer kcNum)
    {
        this.select().strict(true).eq(MktGoodsSpace.F.pkey, pkey).update(MktGoodsSpace.F.kcNum, kcNum);
    }
    
    public Integer getKcNum(Integer goods)
    {
        List<MktGoodsSpace> list = this.select().eq("goods", goods).strict(true).exec();
        Integer res = 0;
        for(MktGoodsSpace s : list)
            res = res + s.getKcNum();
        return res;
    }
    
    public MktGoodsSpace byH5Space(String space, Integer goods)
    {
        return this.selectOne().eq("goods", goods).eq("space", space).exec();
    }
    
    public <T> List<T> listByGoodsSortByPrice(List<Integer> goods, boolean desc, Class<T> clazz)
    {
        return this.select().in(F.goods, goods).sort(F.price, desc).execDto(clazz);
    }
    
    
}