package cn.tofocus.lejia.dao.market;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktKryOrder;

@Component
public class MktKryOrderDao extends JpaSpecificationDelegate<Integer, MktKryOrder>
{
    public PageResult<MktKryOrder> queryKryOrder(int page, int pagesize, String name, String startDate, String endDate,
        Integer ascription)
    {
        SelectPageBuilder<Integer, MktKryOrder> builder =
            selectPage().page(page).pagesize(pagesize).eq("ascription", ascription).sort("pkey", true);
        if (StringUtils.isNotBlank(startDate)) builder.ge("orderTime", startDate);
        if (StringUtils.isNotBlank(endDate))
        {
            try
            {
                Date str = DateUtil.formatDateStr(endDate);
                Calendar calendar = new GregorianCalendar();
                calendar.setTime(str);
                calendar.add(Calendar.DATE, 1);
                str = calendar.getTime();
                builder.le("orderTime", str);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        
        return builder.exec();
    }
}