package cn.tofocus.lejia.dao.vendor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.ConditionBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktVendorQueryParamDTO;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor.F;
import cn.tofocus.lejia.Constant;

@Component
public class MktVendorDao extends JpaSpecificationDelegate<Integer, MktVendor>
{
    /**
     * 获取有效的商户列表
     * @param farmer  商户主键
     * @param company 公司主键
     * @return		  结果
     */
    public List<MktVendor> getValidVendor(String farmer, String company)
    {
        return this.select().eq("farmer", farmer).eq("company", company).eq("idDel", false).exec();
    }
    
    public MktVendor getVendor(Integer pkey)
    {
        return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
    }
    
    public PageResult<MktVendor> queryVendor(MktVendorQueryParamDTO paramDTO, Integer ascription)
    {
        int page = Objects.nonNull(paramDTO.getPage()) ? paramDTO.getPage() : 0;
        int pagesize = Objects.nonNull(paramDTO.getPagesize()) ? paramDTO.getPagesize() : 10;
        SelectPageBuilder<Integer, MktVendor> builder = selectPage().page(page).pagesize(pagesize).eq("ascription", ascription).eq("idDel", false);
        if (CollectionUtils.isNotEmpty(paramDTO.getPkeys()))
        {
            builder.in("pkey", paramDTO.getPkeys());
        }
        // 市场
        if (CollectionUtils.isNotEmpty(paramDTO.getMarketPkeys()))
        {
            builder.in("farmer", paramDTO.getMarketPkeys());
        }
        // 运营端-市场商城，不返回默认市场
        if ("market".equals(paramDTO.getFlag()))
        {
            builder.notEq("farmer", (Constant.Operation + ascription));
        }
        if (StringUtils.isNotBlank(paramDTO.getName()))
        {
            builder.like("name", paramDTO.getName());
        }
        if (StringUtils.isNotBlank(paramDTO.getDisplayName()))
        {
            builder.like("displayName", paramDTO.getDisplayName());
        }
        if (StringUtils.isNotBlank(paramDTO.getMobile()))
        {
            builder.like("mobile", paramDTO.getMobile());
        }
        if (paramDTO.getZxStatus() != null)
        {
            builder.eq("zxStatus", paramDTO.getZxStatus());
        }
        // 经营范围
        if (CollectionUtils.isNotEmpty(paramDTO.getScopes()))
        {
            ConditionBuilder<SelectPageBuilder<Integer, MktVendor>> builder2 = builder.or();
            List<Integer> scopes = paramDTO.getScopes();
            scopes.forEach(scope -> builder2.like("businessScope", scope));
            return builder2.close().done().sort("pkey", true).exec();
        }
        return builder.sort("pkey", true).exec();
    }
    
    public List<DropIntegerDown> listDropName(String marketPkey, Boolean enabled, Integer ascription)
    {
        List<MktVendor> exec = this.select()
            .eq("idDel", false)
            .eq("farmer", marketPkey)
            .eq("enabled", enabled)
            .eq("ascription", ascription).exec();
        List<DropIntegerDown> res = new ArrayList<>();
        for(MktVendor v : exec)
        {
            DropIntegerDown dto = new DropIntegerDown();
            dto.setPkey(v.getPkey());
            dto.setName(v.getDisplayName());
            res.add(dto);
        }
        return res;
    }
    
    // 获取已经在中信注册的商户并且有银行卡号和身份证号码
    public Map<Integer, MktVendor> getZxVenodrMap(List<Integer> keys)
    {
        List<MktVendor> list = this.select()
            .eq("idDel", false)
            .in("pkey", keys)
            .exec();
        Map<Integer, MktVendor> res = new HashMap<>();
        list.forEach(e -> {
            res.put(e.getPkey(), e);
        });
        return res;
    }
    
    public <T> List<T> findByMarket(String marketPkey, List<Integer> vendor, Class<T> clazz)
    {
        return this.select().eq("farmer", marketPkey).in("pkey", vendor).eq("idDel", false).execDto(clazz);
    }
    
    public Boolean checkVendorZxRegisterTime(List<Integer> vendorKey)
    {
        long count = this.aggregation()
            .in("pkey", vendorKey.toArray())
            .or()
            .isNull("zxRegisterTime")
            .eq("zxRegisterTime", new Date())
            .close()
            .done()
            .execCount();
        return count > 0;
    }
    
    public Map<String, Integer> findPkeyMap()
    {
        Map<String, Integer> res = new HashMap<>();
        List<MktVendor> list = this.select().eq("idDel", false).exec();
        for (MktVendor v : list)
        {
            res.put(v.getFarmer() + ":" + v.getDisplayName(), v.getPkey());
        }
        return res;
    }
    
    public Boolean checkRepeatName(Integer pkey, String name, String farmer)
    {
        long count =
            this.aggregation().notEq("pkey", pkey).eq("name", name).eq("farmer", farmer).eq("idDel", false).execCount();
        return count > 0;
    }
    public Boolean checkDisplayRepeatName(Integer pkey, String name, String farmer)
    {
        long count =
            this.aggregation().notEq("pkey", pkey).eq("displayName", name).eq("farmer", farmer).eq("idDel", false).execCount();
        return count > 0;
    }
    
    public Map<Integer, MktVendor> getMapVendor(List<Integer> keys)
    {
        List<MktVendor> list = this.select().in("pkey", keys).eq("idDel", false).exec();
        Map<Integer, MktVendor> map = new HashMap<>();
        list.forEach(e -> map.put(e.getPkey(), e));
        return map;
    }
    
    public List<MktVendor> listVendor(String marketPkey)
    {
        return this.select()
            .eq("farmer", marketPkey)
            .eq("idDel", false)
            .eq("enabled", true).exec();
    }
    
    public Map<Integer, MktVendor> getMapVendorGoods(String marketPkey)
    {
        List<MktVendor> list = this.select().eq("farmer", marketPkey).eq("idDel", false).exec();
        Map<Integer, MktVendor> map = new HashMap<>();
        list.forEach(e -> map.put(e.getPkey(), e));
        return map;
    }
    
    public List<Integer> byNameAndBooth(String name, String booth, String marketPkey, Integer ascription)
    {
        List<MktVendor> list = this.select()
            .like("displayName", name)
            .like("booth", booth)
            .eq("farmer", marketPkey)
            .eq("ascription", ascription)
            .exec();
        return CollectionUtil.keyList(list);
    }
    
    public boolean existMobile(String mobile, Integer notEqPkey, Integer ascription)
    {
        return this.selectOne()
            .eq(F.mobile, mobile)
            .eq(F.ascription, ascription)
            .notEq(F.pkey, notEqPkey)
            .exec() != null;
    }
    
    public Map<Integer,MktVendor> mapZxAsc(Integer ascription)
    {
        List<MktVendor> list = this.select()  
            .eq("idDel", false)
            .eq("ascription", ascription)
            .exec();
        Map<Integer,MktVendor> vendorMap = new HashMap<>();
        list.forEach(e -> vendorMap.put(e.getPkey(), e));
        return vendorMap;
    }
    
    public Boolean checkCommissionRate(BigDecimal commissionRate, String farmer, Integer ascription)
    {
        long count = this.aggregation()
        .eq("idDel", false)
        .eq("ascription", ascription)
        .eq("farmer", farmer)
        .lt("commissionRate", commissionRate)
        .execCount();
        // true: 有小于指定佣金费率的商户
        return count > 0;
    }
    
}