package cn.tofocus.lejia.dao.market;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.MktIndexAdvertOnList;
import cn.tofocus.lejia.bean.entity.market.MktIndexAdvert;
import cn.tofocus.lejia.bean.enums.IndexAdvertSubject;
import cn.tofocus.lejia.Constant;

@Component
@DataSourceWithFileUrl
public class MktIndexAdvertDao extends JpaSpecificationDelegate<Integer, MktIndexAdvert>
{
    
    public PageResult<MktIndexAdvertOnList> queryIndexAdvert(int page, int pagesize, Integer ascription)
    {
        return selectPage()
            .page(page)
            .pagesize(pagesize).eq("ascription", ascription)
            .sort("createdTime", true)
            .execDto(MktIndexAdvertOnList.class);
    }
    
    public List<MktIndexAdvert> listIndexAdvert(List<IndexAdvertSubject> subjectList, String farmer, Integer ascription)
    {
        if (subjectList.isEmpty()) return new ArrayList<>();
        return select().in("subject", subjectList.toArray())
            .eq("ascription", ascription)
            .iF(StringUtils.isBlank(farmer))
                .like("farmer", Constant.Operation)
            .eLse()
                .eq("farmer", farmer)
            .endIf()
            .ge("endDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .le("startDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .sort("createdTime", true)
            .exec();
    }
    
    public List<MktIndexAdvert> listIndexAdvertMember(List<Integer> keys, String farmer, Integer ascription)
    {
        return select()
            .notIn("pkey", keys)
            .eq("ascription", ascription)
            .ge("endDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .le("startDate", DateUtil.formatDate(new Date(), "yyyy-MM-dd"))
            .or()
            .eq("farmer", Constant.Operation + ascription)
            .eq("farmer", farmer)
            .close()
            .done()
            .sort("createdTime", true)
            .exec();
    }
}