package cn.tofocus.lejia.api.pub;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.IndexLinHandleList;
import cn.tofocus.lejia.bean.dto.PubGiftFullDto;
import cn.tofocus.lejia.bean.dto.pub.MemberUsingCouponDto;
import cn.tofocus.lejia.bean.dto.pub.MemberUsingCouponInfo;
import cn.tofocus.lejia.bean.dto.pub.MemberUsingGiftOnPage;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.cache.IndexDataLinDaoMap;
import cn.tofocus.lejia.cache.IndexDataLinHandleListMap;
import cn.tofocus.lejia.cache.IndexDataLinHandleMap;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.app.AppZxEqManager;
import cn.tofocus.lejia.domain.app.AppZxEqManager2;
import cn.tofocus.lejia.domain.pub.PubMemberManager;
import cn.tofocus.lejia.utils.DateUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RequestMapping("/v1/pub/member")
@RestController
public class PubMemberApiImpl
{
    @Autowired
    private PubMemberManager manager;
    
    @Autowired
    private IndexDataLinDaoMap indexDataLinDaoMap;
    
    @Autowired
    private IndexDataLinHandleMap indexDataLinHandleMap;
    
    @Autowired
    private IndexDataLinHandleListMap indexDataLinHandleListMap;
    
    @Autowired
    private WxManager wxManager;
    
    @Operation(summary = "获取礼品券和优惠券可使用数量", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/get/num")
    public Result<Map<String, Integer>> getGiftAndCouponNum(@RequestParam(value = "mobile")String mobile)
    {
        Map<String, Integer> res = manager.getGiftAndCouponNum(mobile);
        return new Result<>(res);
    }
    
    @Operation(summary = "获取优惠券列表", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/query")
    public Result<PageResult<MemberUsingCouponDto>> queryMemberConsumCoupon(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "status")CardStatus status)
    {
        PageResult<MemberUsingCouponDto> res = manager.queryMemberConsumCoupon(page, pagesize, mobile, status);
        return new Result<>(res);
    }
    
    @Operation(summary = "去除优惠券列表返回", tags = ApiTags.PUB_MEMBER)
    @GetMapping(value = "/coupon/add")
    public Result<Boolean> addMemberConsumCoupon(@RequestParam(value = "card", required = false)Integer card)
    {
        return new Result<>(manager.addMemberConsumCoupon(card));
    }
    
    @Operation(summary = "获取优惠券列表", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/merchant/use/query")
    public Result<PageResult<MemberUsingCouponInfo>> queryMerchantUse(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始日期") StartDate startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束日期") EndDate endDate,
        @RequestParam(value = "keyword", required = false) @Parameter(description = "卡券名称") String keyword,
        @RequestParam(value = "merchant") Integer merchant)
    {
        PageResult<MemberUsingCouponInfo> res = manager.queryMerchantUse(page, pagesize, startDate, endDate, keyword, merchant);   
        return new Result<>(res);
    }
    
    @Operation(summary = "获取礼品券列表", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/gift/list")
    public Result<List<MemberUsingGiftOnPage>> listMemberGift(        
        @RequestParam(value = "mobile")String mobile)
    {
        List<MemberUsingGiftOnPage> res = manager.listMemberGift(mobile);
        return new Result<>(res);
    }
    
    @Operation(summary = "使用优惠券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/use")
    public Result<Boolean> useCoupon(        
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "coupon")Integer coupon,
        @RequestParam(value = "merchant",required = false)Integer merchant,
        @RequestParam(value = "merchantName",required = false)String merchantName)
    {
        Boolean res = manager.useCoupon(mobile, coupon, merchant, merchantName);
        return new Result<>(res);
    }
    
    @Operation(summary = "赠送优惠券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/add")
    public Result<Boolean> addCoupon(        
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "card")Integer card,
        @RequestParam(value = "num")Integer num)
    {
        Boolean res = manager.addCoupon(mobile, card, num);
        return new Result<>(res);
    }
    
    @Operation(summary = "兑换优惠券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/exchange")
    public Result<Boolean> exchangeCoupon(        
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "xaszCoupon")Integer xaszCoupon)
    {
        Boolean res = manager.exchangeCoupon(mobile, xaszCoupon);
        return new Result<>(res);
    }

    @Operation(summary = "兑换礼品券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/gift/exchange")
    public Result<Boolean> exchangeGift(        
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "xaszGift")Integer xaszGift)
    {
        Boolean res = manager.exchangeGift(mobile, xaszGift);
        return new Result<>(res);
    }
    
    @Operation(summary = "添加心安食足和云商城优惠券关联", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/put/map")
    public Result<Boolean> putMapCoupon(        
        @RequestParam(value = "coupon")Integer coupon,
        @RequestParam(value = "xaszCoupon")Integer xaszCoupon)
    {
        manager.putMapCoupon(coupon, xaszCoupon);
        return new Result<>(true);
    }
    
    @Operation(summary = "添加心安食足和云商城礼品券关联", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/gift/put/map")
    public Result<Boolean> putMapGift(        
        @RequestParam(value = "gift")Integer gift,
        @RequestParam(value = "xaszGift")Integer xaszGift)
    {
        manager.putMapGift(gift, xaszGift);
        return new Result<>(true);
    }
    
    @Operation(summary = "判断用户是否存在优惠券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/exist")
    public Result<Boolean> existCoupon(        
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "card")Integer card)
    {
        Boolean res = manager.existCoupon(mobile, card);
        return new Result<>(res);
    }
    
    @Operation(summary = "获取优惠券库存", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/getStock")
    public Result<Integer> getCouponStock(@RequestParam(value = "card") Integer card)
    {
        Integer res = manager.getCouponStock(card);
        return new Result<>(res);
    }
    
    @Operation(summary = "获取优惠券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/coupon/get")
    public Result<Map<String,Object>> getCoupon(   
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "coupon")Integer coupon)
    {
        Map<String, Object> res = manager.getCoupon(mobile, coupon);
        return new Result<>(res);
    }
    
    @Operation(summary = "核销礼品券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/gift/verify")
    public Result<Boolean> verifyGift(
        @RequestParam(value = "mobile")String mobile,
        @RequestParam(value = "gift")Integer gift)
    {
        Boolean res = manager.verifyGift(mobile, gift);
        return new Result<>(res);
    }
    
    @Operation(summary = "获取会员当日消费金额", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/day/consumptionAmt")
    public Result<BigDecimal> getDayConsumptionAmt(@RequestParam(value = "mobile")String mobile)
    {
        BigDecimal res = manager.getDayConsumptionAmt(mobile);
        return new Result<>(res);
    }
    
    @Operation(summary = "判断是否可以发礼品券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/gift/judgeGiftReceive")
    public Result<Boolean> judgeGiftReceive(@RequestParam(value = "mobile")String mobile)
    {
        Boolean res = manager.judgeGiftReceive(mobile);
        return new Result<>(res);
    }
    
    @Operation(summary = "满一百送礼品券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/gift/fullGift")
    public Result<Boolean> fullGift(@RequestParam(value = "mobile")String mobile)
    {
        Boolean res = manager.fullGift(mobile);
        return new Result<>(res);
    }
    
    @Operation(summary = "判断是否可以发礼品券", tags = ApiTags.PUB_MEMBER)
    @PostMapping(value = "/gift/fullGift/add")
    public Result<Boolean> addFullGift(@RequestBody PubGiftFullDto dto)
    {
        Boolean res = manager.addFullGift(dto);
        return new Result<>(res);
    }
    
    @GetMapping(value = "/add/IndexDataLinDaoMap")
    public Result<Boolean> addIndexDataLinDaoMap(Integer value)
    {
        indexDataLinDaoMap.put("abcd", value);
        return new Result<>(true);
    }
    
    @PostMapping(value = "/add/IndexDataLinHandleMap")
    public Result<Boolean> addIndexDataLinHandleMap(@RequestBody Map<String, Object> map)
    {
        indexDataLinHandleMap.put(map.get("market").toString(), map);
        return new Result<>(true);
    }
    
    @PostMapping(value = "/add/IndexDataLinHandleList")
    public Result<Boolean> addIndexDataLinHandleList(@RequestBody IndexLinHandleList info)
    {
        if(info.getList() != null && !info.getList().isEmpty())
        {
            for(Map<String, Object> map : info.getList())
            {
                String ts = map.get("timeStamp").toString();
                Date date = DateUtil.formatDateStr(ts, "yyyy-MM-dd HH:mm:ss");
                map.put("timeStamp", date);
            }
        }
        indexDataLinHandleListMap.put(info.getMarket(), info.getList());
        return new Result<>(true);
    }
    
    @PostMapping(value = "/del/IndexDataLinHandleList")
    public Result<Boolean> delIndexDataLinHandleList(@RequestParam(value = "market")String market)
    {
        Boolean res = indexDataLinHandleListMap.remove(market);
        return new Result<>(res);
    }
    
    @Autowired
    private AppZxEqManager2 appZxEqManager;
    @PostMapping(value = "/wx/test")
    public Result<Boolean> test()
    {
//        wxManager.getDeliveryList(22);
        MktVendor vendor = new MktVendor();
        vendor.setMobile("15257716818");
        vendor.setPkey(0);
        vendor.setName("哈哈哈哈");
        appZxEqManager.zxRegister2(vendor);
        return new Result<>(true);
    }
}
