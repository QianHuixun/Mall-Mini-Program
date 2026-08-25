package cn.tofocus.lejia.app.v1.supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierInfo;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderInfo;
import cn.tofocus.lejia.domain.app.AppSupplierManager;

@RequestMapping("/v1/app/supplier")
@RestController
public class AppSupplierApiImpl implements AppSupplierApi
{
    @Autowired
    private AppSupplierManager supplierManager;
    
    @Override
    public Result<AppSupplierInfo> get()
    {
        AppSupplierInfo res = supplierManager.get();
        return new Result<>(res);
    }
    
    @Override
    public Result<AppSupplierOrderInfo> getOrderByScanVerifyCode(String kcCode, String verifyCode)
    {
        AppSupplierOrderInfo res = supplierManager.getOrderByScanVerifyCode(kcCode, verifyCode);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> writeOffOrder(String kcCode, String verifyCode)
    {
        boolean sign = supplierManager.writeOffOrder(kcCode, verifyCode);
        return new Result<>(sign);
    }
}
