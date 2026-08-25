package cn.tofocus.lejia.dao.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;

@Component
public class MktGoodsMainThreeDao extends JpaSpecificationDelegate<Integer, MktGoodsMainThree>
{
    
    private long count;
    
    public PageResult<MktGoodsMainThree> queryGoodsMain(Integer page, Integer pagesize, Integer twoGtype, String name,
        Boolean enabled, Integer ascription)
    {
        SelectPageBuilder<Integer, MktGoodsMainThree> builder = selectPage().page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .sort("sort", true)
            .sort("pkey", true);
        if (twoGtype != null) builder.eq("twoGtype", twoGtype);
        if (StringUtils.isNotBlank(name)) builder.like("name", name);
        if (enabled != null) builder.eq("enabled", enabled);
        return builder.exec();
    }
    
    public MktGoodsMainThree getGoodsMain(Integer pkey)
    {
        return selectOne().eq("idDel", false).eq("pkey", pkey).exec();
    }
    
    @SuppressWarnings("hiding")
    public <T> List<T> listDto(Integer twoGtype, Boolean enabled, Integer ascription, Class<T> clazz)
    {
        return this.select()
            .eq("twoGtype", twoGtype)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .eq("enabled", enabled)
            .sort("sort", false)
            .sort("pkey")
            .execDto(clazz);
    }
    
    public Boolean checkGtype(Integer twoGtype, Integer pkey)
    {
        count = this.aggregation().eq("twoGtype", twoGtype).eq("pkey", pkey).execCount();
        return count <= 0;
    }
    
    public Map<Integer, MktGoodsMainThree> getAllMap(Integer ascription)
    {
        List<MktGoodsMainThree> exec = this.select().eq("ascription", ascription).eq("idDel", false).exec();
        Map<Integer, MktGoodsMainThree> res = new HashMap<>();
        exec.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        return res;
    }
    
    public List<MktGoodsMainThree> listSortFalse(Integer ascription)
    {
        return this.select().eq("ascription", ascription).eq("idDel", false).sort("sort", false).exec();
    }
    
    public Boolean checkGtypeName(Integer pkey, String name, Integer gtype, Integer twoGtype, String farmer, Integer ascription)
    {
        MktGoodsMainThree exec = this.selectOne()
            .eq("name", name)
            .eq("gtype", gtype)
            .notEq("pkey", pkey)
            .eq("twoGtype", twoGtype)
            .eq("farmer", farmer)
            .eq("idDel", false)
            .eq("ascription", ascription)
            .exec();
        return exec != null;
    }
    
    public List<MktGoodsMainThree> listGeSort(Integer sort, Integer gtype, Integer twoGtype, String farmer,
        Integer ascription)
    {
        return this.select()
            .ge("sort", sort)
            .eq("gtype", gtype)
            .eq("twoGtype", twoGtype)
            .eq("farmer", farmer)
            .eq("idDel", false)
            .eq("ascription", ascription)
            .exec();
    }
    
    public <T> List<T> listGtypeThreeV4OnList(Boolean enabled, String name, Integer gtype, Integer twoGtype,
        String farmer, Integer ascription, Class<T> clazz)
    {
        return this.select()
            .eq("enabled", enabled)
            .like("name", name)
            .eq("gtype", gtype)
            .eq("twoGtype", twoGtype)
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .sort("sort", false)
            .sort("pkey")
            .execDto(clazz);
    }
    
    public Integer getSort(Integer sort, Integer gtype, Integer twoGtype, String farmer, Integer ascription)
    {
        MktGoodsMainThree goodsMainThree = this.selectOne()
            .eq("farmer", farmer)
            .eq("gtype", gtype)
            .eq("twoGtype", twoGtype)
            .eq("sort", sort)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .exec();
        if (goodsMainThree != null) return goodsMainThree.getSort();
        return null;
    }
    
    public Map<Integer, Integer> aggMaxSort(String farmer, Integer ascription)
    {
        Map<String, Number> map = this.aggregation()
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .isNotNull("sort")
            .execGroupByMax("twoGtype", "sort");
        Map<Integer, Integer> res = new HashMap<>();
        for (String key : map.keySet())
        {
            res.put(Integer.valueOf(key), map.get(key).intValue());
        }
        return res;
    }
    
    public Integer maxSort(Integer twoGtype, String farmer, Integer ascription)
    {
        Number number = this.aggregation()
        .eq("farmer", farmer)
        .isNotNull("sort")
        .eq("twoGtype", twoGtype)
        .eq("ascription", ascription)
        .eq("idDel", false)
        .execMax("sort");
        return number.intValue();
    }
}
