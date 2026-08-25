package cn.tofocus.lejia.dao.sys;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SysLogDao extends JpaSpecificationDelegate<Long, SysLog>
{
    
    public PageResult<SysLog> queryLog(int page, int pagesize, String startTime, String endTime, Integer ascription)
    {
        SelectPageBuilder<Long, SysLog> builder = selectPage().page(page).pagesize(pagesize).eq("ascription", ascription).sort("pkey", true);
        log.info("startTime: {}, endTime: {}", startTime, endTime);
        if (StringUtils.isNotBlank(startTime)) builder.ge("beginTime", startTime);
        if (StringUtils.isNotBlank(endTime)) builder.le("beginTime", endTime + " 23:59:59");
        return builder.exec();
    }
    
}