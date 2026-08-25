package cn.tofocus.lejia.dao.sys;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.v3.PromoteOnPage;
import cn.tofocus.lejia.bean.dto.v3.PromoteUpdDto;
import cn.tofocus.lejia.bean.entity.sys.MktPromote;

@Component
@DataSourceWithFileUrl
public class MktPromoteDao extends JpaSpecificationDelegate<Integer, MktPromote>
{
    public PageResult<PromoteOnPage> query(int page, int pagesize, String title, String content, Integer ascription)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("ascription", ascription)
            .like("title", title)
            .like("content", content)
            .sort("createdTime")
            .sort("pkey")
            .execDto(PromoteOnPage.class);
    }
    
    public PromoteUpdDto getDto(Integer ascription, String farmer)
    {
        return this.selectOne()
            .eq("ascription", ascription)
            .eq("farmer", farmer)
            .eq("enabled", true).execDto(PromoteUpdDto.class);
    }
}