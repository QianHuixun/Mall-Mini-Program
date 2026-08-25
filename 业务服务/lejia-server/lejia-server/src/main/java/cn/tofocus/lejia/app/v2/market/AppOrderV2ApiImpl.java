package cn.tofocus.lejia.app.v2.market;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.v2.AppOrderV2Api;
import cn.tofocus.lejia.bean.dto.app.supplier.AppSupplierOrderInfo;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeTimeDTO;
import cn.tofocus.lejia.bean.dto.v2.order.OrderDetailsV2Info;
import cn.tofocus.lejia.bean.dto.v2.order.OrderTotalV2Info;
import cn.tofocus.lejia.bean.dto.wanli.WanliCourierOnInfo;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import cn.tofocus.lejia.domain.v2.AppOrderV2Manager;
import cn.tofocus.lejia.domain.wanli.WanliManager;

@RequestMapping("/v2/app/market/lm/order")
@RestController
public class AppOrderV2ApiImpl implements AppOrderV2Api
{
    @Autowired
    private AppOrderV2Manager manager;
    
    @Autowired
    private AppOrderManager appOrderManager;
    
    @Autowired
    private WanliManager wanliManager;
    
    @Override
    public Result<OrderTotalV2Info> bugGoods(Integer space, Integer num, String tjr, Boolean pickupType, Integer addressPkey, 
        Boolean dineIn, Integer association, BigDecimal longitude, BigDecimal latitude)
    {
        return new Result<>(manager.bugGoods(space, num, tjr, pickupType, addressPkey, dineIn, association, longitude, latitude));
    }

    @Override
    public Result<OrderTotalV2Info> buyGwc(List<Integer> gwcs, Boolean pickupType, Integer addressPkey, Boolean dineIn, BigDecimal longitude, BigDecimal latitude)
    {
        return new Result<>(manager.bugGwc(gwcs, pickupType, addressPkey, dineIn, longitude, latitude));
    }

    @Override
    public Result<OrderTotalV2Info> commitOrder(OrderTotalV2Info info)
    {
        return new Result<>(manager.commitOrder(info));
    }

    @Override
    public Result<OrderTotalV2Info> getUnpaidOrder(Integer pkey, Integer addressPkey, BigDecimal longitude, BigDecimal latitude)
    {
        return new Result<>(manager.getUnpaidOrder(pkey, addressPkey, longitude, latitude));
    }

    @Override
    public Result<OrderDetailsV2Info> loadOrder(Integer pkey, RefundJdType jdType)
    {
        return new Result<>(manager.loadOrder(pkey, jdType));
    }

    @Override
    public Result<BigDecimal> checkNmMemberPay()
    {
        return new Result<>(manager.checkNmMemberPay());
    }

    // 前端用的v1
    @Override
    public Result<DistributionTypeTimeDTO> getDistributionTypePsTimeV2(String marketPkey, DistributionType type, Integer addressPkey)
    {
        return new Result<>(appOrderManager.getDistributionTypePsTimeV2(marketPkey, type, addressPkey));
    }

    @Override
    public Result<WanliCourierOnInfo> getCourier(Integer pkey)
    {
        return new Result<>(wanliManager.getWanliCourier(pkey));
    }

    //@Override
    //public Result<WxPayData> payCoupon(String card)
    //{
    //    return new Result<>(manager.payCoupon(card));
    //}
//
    //@Override
    //public Result<MktCard> receiveCoupon(Integer vendor)
    //{
    //    return new Result<>(cardManager.vendorQrCodeAddMemberCard(vendor));
    //}

    @Override
    public Result<Boolean> checkWriteOffActivity(String name)
    {
        boolean sign = manager.checkWriteOffActivity(name);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> writeOffActivity(String name)
    {
        boolean sign = manager.writeOffActivity(name);
        return new Result<>(sign);
    }

    @Override
    public Result<Boolean> writeOffPickupOrder(String kcCode, String verifyCode)
    {
        manager.writeOffPickupOrder(kcCode, verifyCode);
        return new Result<>(true);
    }

    @Override
    public Result<AppSupplierOrderInfo> getOrderByScanVerifyCode(String kcCode, String verifyCode)
    {
        return new Result<>(manager.getOrderByScanVerifyCode(kcCode, verifyCode));
    }
}
