package cn.tofocus.lejia.api.wanli;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.wanli.OrderBillingOnInfo;
import cn.tofocus.lejia.bean.dto.wanli.WanliCourierOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.enums.CancelType;
import cn.tofocus.lejia.bean.enums.ThirdPartyStatus;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.domain.wanli.WanliManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.print.XiyeCloudPrint;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequestMapping("/v1/wanli")
@RestController
public class WanliApiImpl implements WanliApi
{
    @Autowired
    private WanliManager wanliManager;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    public MktOrderDescDao orderDescDao;
    
    @Override
    public Result<List<OrderBillingOnInfo>> orderBilling(Integer pkey)
    {
        if (!wanliManager.checkAsc()) throw TofocusException.of(LejiaErrCode.SEC_WANLI_ERROR);
        MktOrder mktOrder = orderDao.get(pkey);
        MktOrderDesc desc = orderDescDao.get(pkey);
        SysFarmerConfig config = sysFarmerConfigDao.get(mktOrder.getFarmer());
        
        List<OrderBillingOnInfo> res = wanliManager.orderBilling(config.getShopId(),
            mktOrder.getCode(),
            desc.getAddr(),
            desc.getAddr(),
            desc.getLongitude().toString(),
            desc.getLatitude().toString(),
            desc.getName(),
            desc.getMobile(),
            mktOrder.getWeight().intValue());
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> orderCreate(Integer pkey, List<Integer> multipleSupplierCodes)
    {
        if (!wanliManager.checkAsc()) throw TofocusException.of(LejiaErrCode.SEC_WANLI_ERROR);
        MktOrder mktOrder = orderDao.get(pkey);
        MktOrderDesc desc = orderDescDao.get(pkey);
        SysFarmerConfig config = sysFarmerConfigDao.get(mktOrder.getFarmer());
        if (mktOrder.getThirdPartyStatus() != null
            && mktOrder.getThirdPartyStatus().equals(ThirdPartyStatus.THIRD_PARTY_PENDING))
        {
            log.info("cancle {}", pkey);
            wanliManager.cancleOrder(CancelType.OTHER, pkey);
        }
        
        Boolean res = wanliManager.orderCreate(config.getShopId(),
            mktOrder.getCode(),
            multipleSupplierCodes,
            desc.getAddr(),
            desc.getAddr(),
            desc.getLongitude().toString(),
            desc.getLatitude().toString(),
            desc.getName(),
            desc.getMobile(),
            mktOrder.getWeight().intValue(),
            mktOrder.getSmallTicket());
        if (res) wanliManager.paidan(pkey);
        return new Result<>(res);
    }
    
    // 创建门店
    @PostMapping(value = "/storeCreate")
    public Result<Boolean> storeCreate(String farmer, String contactName, String shopName, String shopAddress, String contactPhone,
        String shopLng, String shopLat)
    {
        wanliManager.storeCreate(farmer, contactName, shopName, shopAddress, contactPhone, shopLng, shopLat);
        return new Result<>(true);
    }
    
    // 创建店铺
    @PostMapping(value = "/shopCreate")
    public Result<Boolean> shopCreate(String farmer, String cityName, String contactName, String contactPhone)
    {
        SysFarmer sysFarmer = sysFarmerDao.get(farmer);
        SysFarmerConfig config = sysFarmer.getConfig();
        if (StringUtils.isBlank(contactName)) contactName = sysFarmer.getManager();
        if (StringUtils.isBlank(contactPhone)) contactPhone = sysFarmer.getMobile();
        String shopId = wanliManager.shopCreate(farmer, config.getLongitude().toString(),
            config.getLatitude().toString(),
            sysFarmer.getName(),
            contactName,
            config.getAddr(),
            cityName,
            contactPhone,
            config.getAddr());
        config.setShopId(shopId);
        sysFarmerConfigDao.update(config);
        return new Result<>(true);
    }
    
    @PostMapping(value = "/testH")
    public Result<Boolean> testH(String farmer, String orderNo, Integer status)
    {
        wanliManager.testH(farmer, orderNo, status);
        return new Result<>(true);
    }
    
    // 充值接口 1支付宝二维码 2：微信二维码 
    @GetMapping(value = "/wallet/accountRecharge")
    public String walletAccountRecharge(String farmer, BigDecimal rechargePrice, String rechargeType)
    {
        return wanliManager.walletAccountRecharge(farmer, rechargePrice, rechargeType);
    }
    
    // 查询账户余额 
    @PostMapping(value = "/wallet/balance")
    public String walletBalance(String farmer)
    {
        return wanliManager.walletBalance(farmer);
    }
    
    // 充值订单查询  
    @PostMapping(value = "/wallet/queryRechargeStatus")
    public Result<Boolean> walletQueryRechargeStatus(String farmer, String rechargeOrdNo)
    {
        wanliManager.walletQueryRechargeStatus(farmer, rechargeOrdNo);
        return new Result<>(true);
    }
    
    @Override
    public String callback(String farmer, JSONObject json)
    {
        return wanliManager.callback(json, farmer);
    }
    
    @Override
    public Result<Boolean> cancleOrder(CancelType cancelType, Integer pkey)
    {
        wanliManager.cancleOrder(cancelType, pkey);
        return new Result<>(true);
    }
    
    @Override
    public Result<List<NamedBean>> cancelTypeList()
    {
        
        List<NamedBean> list = new ArrayList<>();
        CancelType[] values = CancelType.values();
        for (CancelType name : values)
        {
            NamedBean kv = new NamedBean();
            kv.setPkey(name);
            kv.setName(name.getName());
            list.add(kv);
        }
        
        return new Result<>(list);
    }
    
    @Override
    public Result<Boolean> ordertest(String orderNo, Integer status)
    {
        return new Result<>(wanliManager.ordertest(orderNo, status));
        
    }
    
    @Override
    public Result<Boolean> orderReach(Integer pkey)
    {
        return new Result<>(wanliManager.orderReach(pkey));
    }
    
    @Override
    public Result<WanliCourierOnInfo> getCourier(Integer pkey)
    {
        return new Result<>(wanliManager.getWanliCourier(pkey));
    }
    
    @Autowired
    private XiyeCloudPrint xiyeCloudPrint;
    
    //    @Operation(summary = "打印机测试客户联", tags = ApiTags.WANLI)
    //    @PostMapping(value = "/print/cust/test")
    //    Result<Boolean> printCustTest(@RequestBody CustomerPrintBean info)
    //    {
    //        return new Result<>( xiyeCloudPrint.xiyeCustomerPrint(info, ""));
    //    }
    //    
    //    @Operation(summary = "打印机测试配送联", tags = ApiTags.WANLI)
    //    @PostMapping(value = "/print/delivery/test")
    //    Result<Boolean> printdeliveryTest(@RequestBody XiyePrintDeliveryBean info)
    //    {
    //        return new Result<>( xiyeCloudPrint.xiyeDeliveryPrint(info, ""));
    //    }
    //    
    //    
    //    @Operation(summary = "打印机测试客户联和配送联", tags = ApiTags.WANLI)
    //    @PostMapping(value = "/print/data/test")
    //    Result<Boolean> printDataTest(@RequestBody XiyePrintBean info)
    //    {
    //        return new Result<>( xiyeCloudPrint.xiyePrint(info.getBean(), info.getInfo()));
    //    }
    
}
