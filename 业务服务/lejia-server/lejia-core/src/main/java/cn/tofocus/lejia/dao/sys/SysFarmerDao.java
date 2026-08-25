package cn.tofocus.lejia.dao.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer.F;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.Constant;


@Component
@DataSourceWithFileUrl
public class SysFarmerDao extends JpaSpecificationDelegate<String, SysFarmer>
{
    private static final String ID_PREFIX = "zy_mkt_";

    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;

    /**
     * String 类型的 Redis 自增主键无法由通用 DAO 直接转换为数值，启动时需要从现有主键恢复序列。
     */
    @Override
    protected long maxId()
    {
        long max = 0;
        for (SysFarmer farmer : select().exec())
        {
            String id = farmer.getPkey();
            if (id == null || !id.startsWith(ID_PREFIX))
                continue;
            try
            {
                max = Math.max(max, Long.parseLong(id.substring(ID_PREFIX.length())));
            }
            catch (NumberFormatException ignored)
            {
                // 兼容数据库中运营端等非数字历史主键。
            }
        }
        return max;
    }
    
    private SelectPageBuilder<String, SysFarmer> pageBuilder(Integer page, Integer pagesize, String name, Integer ascription)
    {
        SelectPageBuilder<String, SysFarmer> builder =
            selectPage().page(page).pagesize(pagesize).notEq("pkey", (Constant.Operation + ascription)).sort("pkey", true);
        
        if (StringUtils.isNotBlank(name))
        {
            builder.like("name", name);
        }
        return builder;
    }
    
    public PageResult<SysFarmer> queryAppMarketByArea(Integer page, Integer pagesize, String area, String name,
        Integer ascription, List<String> pkeys, List<String> notPkeys)
    {
        // 市场配置数据
        List<SysFarmerConfig> configList = sysFarmerConfigDao.select()
            .eq("ascription", ascription)
            .notEq("pkey", (Constant.Operation + ascription))
            .in("pkey", pkeys)
            .notIn("pkey", notPkeys)
            .like("addr", area)
            .sort("pkey", true)
            .exec();
        List<String> pkeyList = configList.stream().map(SysFarmerConfig::getPkey).collect(Collectors.toList());

        SelectPageBuilder<String, SysFarmer> builder = pageBuilder(page, pagesize, name, ascription);
        return builder.in("pkey", pkeyList).eq("enabled", true).eq("ascription", ascription).eq("idDel", false).exec();
    }
    

    private SelectBuilder<String, SysFarmer> builder(String name, Integer ascription)
    {
        return select().notEq("pkey", (Constant.Operation + ascription)).like("name", name);
    }

    public List<SysFarmer> queryAppMarketAll(String area, String name, Integer ascription, List<String> pkeys, List<String> notPkeys)
    {
        // 市场配置数据
        List<SysFarmerConfig> list = sysFarmerConfigDao.select()
            .eq("ascription", ascription)
            .notEq("pkey", (Constant.Operation + ascription))
            .in("pkey", pkeys)
            .notIn("pkey", notPkeys)
            .like("addr", area)
            .sort("pkey", true)
            .exec();
        List<String> pkeyList = list.stream().map(SysFarmerConfig::getPkey).collect(Collectors.toList());

        SelectBuilder<String, SysFarmer> builder = builder(name, ascription);
        return builder.in("pkey", pkeyList).eq("ascription", ascription).eq("enabled", true).eq("idDel", false).exec();
    }

    public PageResult<SysFarmer> queryMarket(int page, int pagesize, String marketName, String marketPkey,
        String companyPkey, Integer ascription)
    {
        SelectPageBuilder<String, SysFarmer> builder =
            selectPage().page(page).pagesize(pagesize).eq("ascription", ascription).eq("idDel", false).sort("createdTime", true);
        if (StringUtils.isNotBlank(marketPkey)) builder.eq("pkey", marketPkey);
        if (StringUtils.isNotBlank(companyPkey)) builder.eq("org", companyPkey);
        if (StringUtils.isNotBlank(marketName)) builder.like("name", marketName);
        return builder.exec();
    }
    
    public Map<String,String> findNameMap(Integer ascription)
    {
        Map<String,String> res = new HashMap<>();
        List<SysFarmer> list = this.select().eq("ascription", ascription).eq("idDel", false).exec();
        for(SysFarmer f : list)
        {
            res.put(f.getPkey(), f.getName());
        }
        return res;
    }
    
    public Map<String,String> findPkeyMap(Integer ascription)
    {
        Map<String,String> res = new HashMap<>();
        List<SysFarmer> list = this.select().eq("ascription", ascription).eq("idDel", false).exec();
        for(SysFarmer f : list)
        {
            res.put(f.getName(), f.getPkey());
        }
        return res;
    }

    /**
     * 获取有效的非运营端的市场pkey
     * @return                 结果
     */
    public List<SysFarmer> queryValidMarketList(Integer ascription)
    {
        return this.select()
            .notEq("pkey", (Constant.Operation + ascription))
            .eq("ascription", ascription)
            .eq("enabled",true)
            .eq("idDel", false)
            .exec();
    }
    
    public <T> List<T> listValidFarmer(Integer ascription, String org, String pkey, Class<T> clazz)
    {
        return this.select()
            .notEq(F.pkey, (Constant.Operation + ascription))
            .eq(F.ascription, ascription)
            .eq(F.org, org)
            .eq(F.pkey, pkey)
            .eq(F.enabled, true)
            .eq(F.idDel, false)
            .sort(F.pkey)
            .execDto(clazz);
    }

    
    public Boolean checkRepeatName(String pkey, String name, Integer ascription)
    {
        long count = this.aggregation()
            .notEq("pkey", pkey)
            .eq("ascription", ascription)
            .eq("name", name)
            .eq("idDel", false)
            .execCount();
        return count > 0;
    }
    
    public List<String> listPkeysLikeName(String name, Integer ascription)
    {
        return this.select().eq(F.ascription, ascription).like(F.name, name).eq("idDel", false).execDto(F.pkey, String.class);
    }
    
    public int countMarketNum(Integer ascription)
    {
        return (int)this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.idDel, false)
            .execCount();
    }
    
}
