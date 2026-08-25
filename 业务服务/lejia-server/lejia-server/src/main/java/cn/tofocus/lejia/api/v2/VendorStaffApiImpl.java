package cn.tofocus.lejia.api.v2;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.vendor.VendorStaffApi;
import cn.tofocus.lejia.bean.dto.vendor.VendorStaffOnPage;
import cn.tofocus.lejia.bean.dto.vendor.VendorStaffUpdInfo;
import cn.tofocus.lejia.domain.vendor.VendorStaffManager;

@RequestMapping("/v1/vendor/staff")
@RestController
public class VendorStaffApiImpl implements VendorStaffApi
{
    @Autowired
    private VendorStaffManager manager;
    
    @Override
    public Result<PageResult<VendorStaffOnPage>> queryVendorStaff(int page, int pagesize, Integer vendor, String content)
    {
        return new Result<>(manager.queryVendorStaff(page, pagesize, vendor, content));
    }
    
    @Override
    public Result<Integer> addVendorStaff(@Valid VendorStaffUpdInfo info)
    {
        return new Result<>(manager.addVendorStaff(info));
    }
    
    @Override
    public Result<Boolean> updVendorStaff(@Valid VendorStaffUpdInfo info)
    {
        return new Result<>(manager.updVendorStaff(info));
    }
    
    @Override
    public Result<Boolean> enabledVendorStaff(Integer pkey, Boolean enabled)
    {
        return new Result<>(manager.enabledVendorStaff(pkey, enabled));
    }
    
    @Override
    public Result<Boolean> deVendorStaff(Integer pkey)
    {
        return new Result<>(manager.deVendorStaff(pkey));
    }
    
}
