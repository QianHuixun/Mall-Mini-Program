package cn.tofocus.lejia.dao.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktGtype;

@Component
@DataSourceWithFileUrl
public class MktGtypeDao extends JpaSpecificationDelegate<Integer, MktGtype>
{
    public List<MktGtype> quaryAppGtype(String farmer, Integer ascription)
    {
        return this.select()
            .eq(MktGtype.F.ascription, ascription)
            .eq(MktGtype.F.idDel, false)
            .eq(MktGtype.F.enabled, true)
            .eq(MktGtype.F.farmer, farmer)
            .sort(MktGtype.F.sort, false)
            .sort(MktGtype.F.pkey, true)
            .exec();
    }
    
    public List<MktGtype> quaryAppPointGtype(String farmer, Integer ascription)
    {
        return this.select()
            .eq(MktGtype.F.ascription, ascription)
            .eq(MktGtype.F.idDel, false)
            .eq(MktGtype.F.enabled, true)
            .eq(MktGtype.F.farmer, farmer)
            .sort(MktGtype.F.pointSort, false)
            .sort(MktGtype.F.pkey, true)
            .exec();
    }
    
    public MktGtype getGtype(Integer pkey)
    {
        return selectOne().eq("idDel", false).eq("pkey", pkey).exec();
    }
    
    public MktGtype getCouponGtype(Integer ascription)
    {
        return this.selectOne().eq("ascription", ascription).eq("idDel", false).eq("name", "优惠券").exec();
    }
    
    public MktGtype getGiftGtype(Integer ascription)
    {
        return this.selectOne().eq("ascription", ascription).eq("idDel", false).eq("name", "礼券").exec();
    }
    
    public PageResult<MktGtype> queryGtype(int page, int pagesize, String gtyprName, String farmer, Integer ascription)
    {
        SelectPageBuilder<Integer, MktGtype> builder =
            selectPage().page(page).pagesize(pagesize).eq("idDel", false).eq("farmer", farmer).sort("sort", true).sort("pkey");
//        if (showPoint != null)
//        {
//            builder.eq("showPoint", showPoint).notEq("name", "优惠券").notEq("name", "礼券");
//        }
//        if (showMarket != null)
//        {
//            builder.eq("showMarket", showMarket).notEq("name", "优惠券").notEq("name", "礼券");
//        }
        if (StringUtils.isNotBlank(gtyprName)) builder.like("name", gtyprName);
        return builder.eq("ascription", ascription).exec();
    }
    
    public List<MktGtype> listGtype(String farmer, Integer ascription)
    {
        return select().eq("farmer", farmer).eq("ascription", ascription).eq("idDel", false).sort("sort", false).exec();
    }
    
    public List<MktGtype> listEnabledGtype(Integer ascription)
    {
        return this.select()
            .eq("ascription", ascription)
            .eq("enabled", true)
            .eq("idDel", false)
            .notEq("name", "优惠券")
            .notEq("name", "礼券")
            .exec();
    }
    
    public Map<Integer, MktGtype> mapGtype(List<Integer> keys)
    {
        Map<Integer, MktGtype> res = new HashMap<>();
        if (keys == null || keys.isEmpty()) return res;
        List<MktGtype> exec = this.select().in("pkey", keys.toArray()).eq("idDel", false).exec();
        exec.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        return res;
    }
    
    public Map<String, MktGtype> getMarketNameGtype(Integer ascription)
    {
        Map<String, MktGtype> res = new HashMap<>();
        List<MktGtype> exec =
            this.select().eq("showMarket", true).eq("ascription", ascription).eq("idDel", false).exec();
        exec.forEach(e -> {
            res.put(e.getName(), e);
        });
        return res;
    }
    
    public List<MktGtype> listMarketGtype(String farmer, Integer ascription)
    {
        return select()
            .eq("ascription", ascription)
            .eq("farmer", farmer)
//            .eq("showMarket", true)
            .eq("enabled", true)
            .eq("idDel", false)
            .sort("marketSort", false)
            .exec();
    }
    
    public Boolean checkGtypeName(Integer pkey, String name, String farmer, Integer ascription)
    {
        MktGtype exec = this.selectOne()
        .eq("name", name)
        .eq("farmer", farmer)
        .eq("idDel", false)
        .notEq("pkey", pkey)
        .eq("ascription", ascription)
        .exec();
        return exec != null;
    }
    
    public List<MktGtype> listGeSort(Integer sort, Integer ysort, String farmer, Integer ascription)
    {
        return this.select()
//            .ge("sort", sort)
            .between("sort", sort, ysort)
            .eq("farmer", farmer)
            .eq("idDel", false)
            .eq("ascription", ascription)
            .exec();
    }
    
    public <T> List<T> listGtypeThreeV4OnList(Boolean enabled, String name, String farmer, Integer ascription, Class<T> clazz)
    {
        return this.select()
            .eq("enabled", enabled)
            .like("name", name)
            .eq("farmer", farmer)
            .eq("ascription", ascription)
            .eq("idDel", false)
            .sort("sort", false)
            .sort("pkey")
            .execDto(clazz);
    }
    
    public Integer getSort(Integer sort, String farmer, Integer ascription)
    {
        MktGtype gtype = this.selectOne()            
        .eq("farmer", farmer)
        .eq("sort", sort)
        .eq("ascription", ascription)
        .eq("idDel", false)
        .exec();
        if(gtype != null)
            return gtype.getSort();
        return null;
    }
    
    public Map<String, MktGtype> nameMap(String farmer, Integer ascription)
    {
        Map<String, MktGtype> map = new HashMap<>();
        List<MktGtype> list = this.select()
        .eq("farmer", farmer)
        .eq("ascription", ascription)
        .eq("idDel", false)
        .exec();
        list.forEach(e -> map.put(e.getName(), e));
        return map;
    }
    
    public Integer maxSort(String farmer, Integer ascription)
    {
        Number number = this.aggregation()
        .eq("farmer", farmer)
        .isNotNull("sort")
        .eq("ascription", ascription)
        .eq("idDel", false)
        .execMax("sort");
        return number.intValue();
    }
}
