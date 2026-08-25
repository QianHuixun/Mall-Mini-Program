package cn.tofocus.lejia.dao.vendor;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorStaff;

@Component
public class MktVendorStaffDao extends JpaSpecificationDelegate<Integer, MktVendorStaff>
{
    public <T> PageResult<T> query(int page, int pagesize, Integer vendor, String content, 
        String farmer, Integer ascription, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("farmer", farmer)
            .eq("idDel", false)
            .eq("vendor", vendor)
            .eq("ascription", ascription)
            .or()
            .like("name", content)
            .like("vendorName", content)
            .like("mobile", content)
            .close()
            .done()
            .sort("farmer")
            .sort("vendor", false)
            .sort("pkey", false)
            .execDto(clazz);
    }
}