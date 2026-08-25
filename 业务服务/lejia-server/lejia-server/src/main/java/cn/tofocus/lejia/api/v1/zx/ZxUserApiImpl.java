package cn.tofocus.lejia.api.v1.zx;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.zx.*;
import cn.tofocus.lejia.domain.zx.ZxUserManager;

@RequestMapping("/v1/zxUser")
@RestController
public class ZxUserApiImpl implements ZxUserApi
{
    @Autowired
    private ZxUserManager zxUserManager;
    
    @Override
    public Result<PageResult<ZxUserInfoOnPage>> query(int page, int pagesize, String name)
    {
        PageResult<ZxUserInfoOnPage> result = zxUserManager.query(page, pagesize, name);
        return new Result<>(result);
    }
    
    @Override
    public Result<ZxUserInfoForUpdUser> getUserInfo(Integer pkey)
    {
        ZxUserInfoForUpdUser result = zxUserManager.getUserInfo(pkey);
        return new Result<>(result);
    }
    
    @Override
    public Result<Boolean> updUserInfo(ZxUserInfoForUpdUser forUpd)
    {
        boolean sign = zxUserManager.updUserInfo(forUpd);
        return new Result<>(sign);
    }
    
    @Override
    public Result<ZxUserInfoForUpdBank> getUserBank(Integer pkey)
    {
        ZxUserInfoForUpdBank result = zxUserManager.getUserBank(pkey);
        return new Result<>(result);
    }
    
    @Override
    public Result<Boolean> updUserBank(ZxUserInfoForUpdBank forUpd)
    {
        boolean sign = zxUserManager.updUserBank(forUpd);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> enableMarketAuto(Integer pkey, Boolean enabled)
    {
        boolean sign = zxUserManager.enableMarketAuto(pkey, enabled);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> enableVendorAuto(Integer pkey, Boolean enabled)
    {
        boolean sign = zxUserManager.enableVendorAuto(pkey, enabled);
        return new Result<>(sign);
    }
    
    @Override
    public Result<ZxUserInfoForUpdVendorUser> getVendorUserInfo(Integer vendor)
    {
        ZxUserInfoForUpdVendorUser res = zxUserManager.getVendorUserInfo(vendor);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> updVendorUserInfo(ZxUserInfoForUpdVendorUser forUpd)
    {
        boolean sign = zxUserManager.updVendorUserInfo(forUpd);
        return new Result<>(sign);
    }

    @Override
    public Result<ZxUserInfoForUpdVendorBank> getVendorUserBank(Integer vendor)
    {
        ZxUserInfoForUpdVendorBank res = zxUserManager.getVendorUserBank(vendor);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> updVendorUserBank(ZxUserInfoForUpdVendorBank forUpd)
    {
        boolean sign = zxUserManager.updVendorUserBank(forUpd);
        return new Result<>(sign);
    }

    @Override
    public Result<List<ZxUserInfoDrop>> allocatioDrop()
    {
        return new Result<>(zxUserManager.allocatioDrop());
    }

    @Override
    public Result<Boolean> allocation(Integer pkey, BigDecimal amt, String remark)
    {
        return new Result<>(zxUserManager.allocation(pkey, amt, remark));
    }
}
