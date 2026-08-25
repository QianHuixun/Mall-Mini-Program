package cn.tofocus.lejia.api.v3;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.XaszAssociationOnInfo;
import cn.tofocus.lejia.domain.v3.ThirdPaymentManager;

@RequestMapping("/v3/thrid/payment")
@RestController
public class ThirdPaymentApiImpl implements ThirdPaymentApi
{
    @Autowired
    private ThirdPaymentManager manager;
    
    @Override
    public Result<Boolean> ins(XaszAssociationOnInfo dto)
    {
        return new Result<>(manager.ins(dto));
    }

    @Override
    public Result<Boolean> upd(XaszAssociationOnInfo dto)
    {
        return new Result<>(manager.upd(dto));
    }

    @Override
    public Result<Boolean> del(Integer pkey)
    {
        return new Result<>(manager.del(pkey));
    }

    @Override
    public Result<PageResult<XaszAssociationOnInfo>> query(int page, int pagesize)
    {
        return new Result<>(manager.query(page, pagesize));
    }

    @Override
    public Result<Map<Integer, String>> listMarket()
    {
        return new Result<>(manager.listMarketName());
    }
    
}
