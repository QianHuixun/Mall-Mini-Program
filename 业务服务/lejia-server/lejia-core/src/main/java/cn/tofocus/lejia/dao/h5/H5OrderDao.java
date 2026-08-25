package cn.tofocus.lejia.dao.h5;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.h5.H5OrderInfo;
import cn.tofocus.lejia.bean.entity.h5.H5Order;
import cn.tofocus.lejia.bean.entity.h5.H5Order.F;

@Component
public class H5OrderDao extends JpaSpecificationDelegate<Integer, H5Order>
{
    public PageResult<H5OrderInfo> query(int page, int pagesize, Integer userKey, String farmer)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.userKey, userKey)
            .eq(F.farmer, farmer)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(H5OrderInfo.class);
    }
    
}
