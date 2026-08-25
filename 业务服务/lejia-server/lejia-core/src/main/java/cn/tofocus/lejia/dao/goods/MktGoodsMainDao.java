package cn.tofocus.lejia.dao.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain.F;

@Component
public class MktGoodsMainDao extends JpaSpecificationDelegate<Integer, MktGoodsMain>
{
    
    private long count;
    
    public PageResult<MktGoodsMain> queryGoodsMain(Integer page, Integer pagesize, Integer gtype, String name,
        Boolean enabled, Integer ascription)
    {
        SelectPageBuilder<Integer, MktGoodsMain> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .sort("sort", true)
            .sort("pkey", true);
        if (gtype != null) builder.eq("gtype", gtype);
        if (StringUtils.isNotBlank(name)) builder.like("name", name);
        if (enabled != null) builder.eq("enabled", enabled);
        return builder.exec();
    }
    
    public MktGoodsMain getGoodsMain(Integer pkey)
    {
        return selectOne().eq("idDel", false).eq("pkey", pkey).exec();
    }
    
    @SuppressWarnings("hiding")
    public <T> List<T> listDto(Integer gtype, Integer ascription, Class<T> clazz)
    {
        return this.select().eq("gtype", gtype).eq("ascription", ascription).sort("sort").execDto(clazz);
    }
    
    public Boolean checkGtype(Integer gtype, Integer pkey)
    {
        count = this.aggregation().eq("gtype", gtype).eq("pkey", pkey).execCount();
        return count <= 0;
    }
    
    public Map<Integer, MktGoodsMain> getAllMap(Integer ascription)
    {
        List<MktGoodsMain> exec = this.select().eq("ascription", ascription).eq("idDel", false).exec();
        Map<Integer, MktGoodsMain> res = new HashMap<>();
        exec.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        
        return res;
    }
    
    public List<MktGoodsMain> listSortFalse(Integer gtype, Boolean enabled, String farmer, Integer ascription)
    {
        return this.select()
            .eq("gtype", gtype)
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .eq("enabled", enabled)
            .sort("sort", false)
            .exec();
    }
    
    public Boolean checkGtypeName(Integer pkey, String name, Integer gtype, String farmer, Integer ascription)
    {
        MktGoodsMain exec = this.selectOne()
            .eq("name", name)
            .eq("gtype", gtype)
            .notEq("pkey", pkey)
            .eq("farmer", farmer)
            .eq("idDel", false)
            .eq("ascription", ascription)
            .exec();
        return exec != null;
    }
    
    public List<MktGoodsMain> listGeSort(Integer sort, Integer gtype, String farmer, Integer ascription)
    {
        return this.select()
            .ge("sort", sort)
            .eq("gtype", gtype)
            .eq("farmer", farmer)
            .eq("idDel", false)
            .eq("ascription", ascription)
            .exec();
    }
    
    public <T> List<T> listGtypeThreeV4OnList(Boolean enabled, String name, Integer gtype, String farmer,
        Integer ascription, Class<T> clazz)
    {
        return this.select()
            .eq("enabled", enabled)
            .like("name", name)
            .eq("gtype", gtype)
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .sort("sort", false)
            .sort("pkey")
            .execDto(clazz);
    }
    
    public Integer getSort(Integer sort, Integer gtype, String farmer, Integer ascription)
    {
        MktGoodsMain goodsMain = this.selectOne()
            .eq("farmer", farmer)
            .eq("gtype", gtype)
            .eq("sort", sort)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .exec();
        if (goodsMain != null) return goodsMain.getSort();
        return null;
    }
    
    public Map<Integer, Integer> aggMaxSort(String farmer, Integer ascription)
    {
        Map<String, Number> map = this.aggregation()
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .isNotNull("sort")
            .execGroupByMax("gtype", "sort");
        Map<Integer, Integer> res = new HashMap<>();
        for (String key : map.keySet())
        {
            res.put(Integer.valueOf(key), map.get(key).intValue());
        }
        return res;
    }
    
    public Integer maxSort(Integer gtype, String farmer, Integer ascription)
    {
        Number number = this.aggregation()
        .eq("farmer", farmer)
        .isNotNull("sort")
        .eq("gtype", gtype)
        .eq("ascription", ascription)
        .eq("idDel", false)
        .execMax("sort");
        return number.intValue();
    }
    
    public List<MktGoodsMain> listEnabledByFarmer(Integer ascription, String farmer)
    {
        return this.select()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.enabled, true)
            .eq(F.idDel, false)
            .sort(F.sort, false)
            .exec();
    }
    
    public MktGoodsMain byNameSys(Integer ascription, String name)
    {
        return this.selectOne()
        .eq(F.ascription, ascription)
        .eq(F.farmer, Constant.Operation + ascription)
        .eq(F.name, name)
        .eq(F.idDel, false)
        .exec();
    }
}
