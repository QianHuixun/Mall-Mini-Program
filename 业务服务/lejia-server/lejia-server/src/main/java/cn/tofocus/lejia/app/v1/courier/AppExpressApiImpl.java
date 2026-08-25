package cn.tofocus.lejia.app.v1.courier;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.AppExpressArrivedParam;
import cn.tofocus.lejia.bean.dto.app.AppExpressDTO;
import cn.tofocus.lejia.bean.dto.app.AppExpressDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.AppExpressFulfillDTO;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.domain.app.AppExpressManager;

@RequestMapping("/v1/app/courier/express")
@RestController
public class AppExpressApiImpl implements AppExpressApi
{
    @Autowired
    private AppExpressManager expressManager;
    
    @Override
    public Result<PageResult<AppExpressDTO>> queryExpress(int page, int pagesize, ExpressStatus status)
    {
        return new Result<>(expressManager.queryExpress(page, pagesize, status));
    }
    
    @Override
    public Result<PageResult<AppExpressFulfillDTO>> queryFulfillExpress(int page, int pagesize)
    {
        return new Result<>(expressManager.queryFulfillExpress(page, pagesize));
    }
    
    @Override
    public Result<AppExpressDetailsDTO> getExpress(Integer pkey)
    {
        return new Result<>(expressManager.getExpress(pkey));
    }
    
    @Override
    public Result<Boolean> goodsExpress(Integer pkey)
    {
        return new Result<>(expressManager.alterExpressStatus(pkey, 1));
    }
    
    @Override
    public Result<Boolean> arrivedExpress(@Valid AppExpressArrivedParam param)
    {
        boolean sign = expressManager.arrivedExpress(param);
        return new Result<>(sign);
    }
    
}
