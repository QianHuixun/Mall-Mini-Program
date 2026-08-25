package cn.tofocus.lejia.dao.sys;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysCompany;
import cn.tofocus.lejia.Constant;

@Component
public class SysCompanyDao extends JpaSpecificationDelegate<String, SysCompany>
{
    private static final String ID_PREFIX = "zyysc_company_";

    /**
     * 从已有 String 主键恢复 Redis 自增序列，避免新主键与备份数据冲突。
     */
    @Override
    protected long maxId()
    {
        long max = 0;
        for (SysCompany company : select().exec())
        {
            String id = company.getPkey();
            if (id == null || !id.startsWith(ID_PREFIX))
                continue;
            try
            {
                max = Math.max(max, Long.parseLong(id.substring(ID_PREFIX.length())));
            }
            catch (NumberFormatException ignored)
            {
                // 兼容数据库中的非数字历史主键。
            }
        }
        return max;
    }
    
    public SysCompany getCompany(String pkey)
    {
        return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
    }
    
    public PageResult<SysCompany> queryCompany(int page, int pagesize, String companyName, Integer ascription)
    {
        SelectPageBuilder<String, SysCompany> builder =
            selectPage().page(page).pagesize(pagesize).eq("ascription", ascription).notEq("pkey", (Constant.Operation + ascription)).sort("createdTime", true).eq("idDel", false);
        if (StringUtils.isNotBlank(companyName))
        {
            builder.like("name", companyName);
        }
        return builder.exec();
    }
}
