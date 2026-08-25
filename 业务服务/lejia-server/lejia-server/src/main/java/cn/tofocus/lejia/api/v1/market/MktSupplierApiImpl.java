package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktSupplierInfo;
import cn.tofocus.lejia.bean.dto.market.MktSupplierOnPage;
import cn.tofocus.lejia.bean.dto.market.MktSupplierOption;
import cn.tofocus.lejia.domain.market.MktSupplierManager;

@RequestMapping("/v1/market/supplier")
@RestController
public class MktSupplierApiImpl implements MktSupplierApi
{
    @Autowired
    private MktSupplierManager supplierManager;
    
    @Override
    public Result<PageResult<MktSupplierOnPage>> query(int page, int pagesize, String name, String mobile,
        Boolean enabled)
    {
        PageResult<MktSupplierOnPage> res = supplierManager.query(page, pagesize, name, mobile, enabled);
        return new Result<>(res);
    }
    
    @Override
    public Result<MktSupplierInfo> get(Integer pkey)
    {
        MktSupplierInfo info = supplierManager.get(pkey);
        return new Result<>(info);
    }
    
    @Override
    public Result<Boolean> ins(MktSupplierInfo info)
    {
        boolean sign = supplierManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> upd(MktSupplierInfo info)
    {
        boolean sign = supplierManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> del(Integer pkey)
    {
        boolean sign = supplierManager.del(pkey);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> enable(Integer pkey, Boolean enabled)
    {
        boolean sign = supplierManager.enable(pkey, enabled);
        return new Result<>(sign);
    }
    
    @Override
    public Result<List<MktSupplierOption>> options(String keyword)
    {
        List<MktSupplierOption> options = supplierManager.options(keyword);
        return new Result<>(options);
    }
}
