package cn.tofocus.lejia.app.v2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.refund.AppRefundOrderOnInfo;
import cn.tofocus.lejia.bean.dto.app.refund.AppRefundOrderOnPage;
import cn.tofocus.lejia.bean.dto.refund.PreRefundOrderCommit;
import cn.tofocus.lejia.bean.dto.refund.PreRefundOrderInfo;
import cn.tofocus.lejia.bean.dto.refund.RefundOrderOnInfo;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.OrderType;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.RefundType;
import cn.tofocus.lejia.bean.enums.jd.RefundJdType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.OrderRefundManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v2/app/market/lm/refund")
@RestController
public class AppRefundOrderApiImpl
{
    @Autowired
    private OrderRefundManager manager;
    
    @Operation(summary = "预退款", tags = AppTags.mobileRefundV2)
    @PostMapping("/preRefundOrder")
    public Result<PreRefundOrderInfo> preRefundOrder(@RequestBody PreRefundOrderCommit info)
    {
        PreRefundOrderInfo res = manager.preRefundOrder(info);
        return new Result<>(res);
    }
    
    @Operation(summary = "申请退款", tags = AppTags.mobileRefundV2)
    @PostMapping("/applyForOrderRefund")
    public Result<Boolean> applyForOrderRefund(@RequestBody RefundOrderOnInfo info)
    {
        manager.applyForOrderRefund(info, RefundStatus.REFUND_APPLYING, RefundType.REFUND_MEMBER);
        return new Result<>(true);
    }
    
    @Operation(summary = "获取退款订单列表", tags = AppTags.mobileRefundV2)
    @PostMapping("/query")
    public Result<PageResult<AppRefundOrderOnPage>> queryAppRefundOrder(
        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
        @RequestParam(value = "pagesize", defaultValue = "0", required = false) int pagesize,
        @RequestParam(value = "orderPkey", required = false) Integer orderPkey)
    {
        PageResult<AppRefundOrderOnPage> res =
            manager.queryAppRefundOrder(page, pagesize, MobileSession.memberPkey(), MobileSession.appid(), orderPkey);
        return new Result<>(res);
    }
    
    @Operation(summary = "获取退款订单详情", tags = AppTags.mobileRefundV2)
    @PostMapping("/get")
    public Result<AppRefundOrderOnInfo> getAppRefundOrder(
        @RequestParam(value = "refundPkey", required = false) Integer refundPkey,
        @RequestParam(value = "orderPkey", required = false) Integer orderPkey)
    {
        AppRefundOrderOnInfo res = manager.getAppRefundOrder(refundPkey, orderPkey);
        return new Result<>(res);
    }
    
    @Operation(summary = "退款原因下拉选", tags = AppTags.mobileRefundV2)
    @PostMapping("/list/reason/drop")
    public Result<List<String>> listReasonDrop(@RequestParam(value = "status")OrderStatus status, 
        @RequestParam(value = "flag", required = false, defaultValue = "false")Boolean flag,
        @RequestParam(value = "type", required = false)OrderType type,
        @RequestParam(value = "jdType", required = false)RefundJdType jdType)
    {
        List<String> res = new ArrayList<>();
        if (status != null)
        {
            if (OrderStatus.DELIVERED_ORDER.equals(status) || OrderStatus.PAYING_ORDER.equals(status))
            {
                res.add("忘记填备注");
                res.add("买多/买错/买少");
                res.add("信息填写错误（地址/时间/联系方式）");
                res.add("不想要了");
            }
            if (OrderStatus.SHIPPED_ORDER.equals(status) || OrderStatus.ARRIVED_ORDER.equals(status)
                || OrderStatus.WAIT_ARRIVAL_ORDER.equals(status) || OrderStatus.WAIT_WRITEOFF_ORDER.equals(status))
            {
                res.add("不想要了");
                res.add("商品有异物/腐烂/变质");
                res.add("商品出现破损、变形、污渍");
                res.add("图片/产地/规格等描述不符");
                res.add("商品缺斤少两");
                res.add("收到的商品错了");
                res.add("收到的商品少了");
                if(flag)
                {
                    res.add("生产日期/保质期与商品描述不符");
                    res.add("质量问题");
                    res.add("商家发错货");
                    res.add("假冒品牌");
                }
                else
                {
                    res.add("配送超时");
                    res.add("显示已送达实际未收到货");
                    res.add("骑手送错地址/未送到指定位置");
                }
            }
        }
        if(OrderType.INTEGRAL_JD_ORDER.equals(type) && !RefundJdType.EXCHANGE.equals(jdType))
        {
            res.add("地址/电话等填写错误");
            res.add("没用/少用/错用优惠");
            res.add("发货时间不符合需求");
            res.add("商品无货");
            res.add("商品错选/多选");
            res.add("不想要了");
            res.add("商品降价");
            res.add("商品价格高于其他平台");
        }
        if(RefundJdType.EXCHANGE.equals(jdType))
        {
            res.add("商品质量/故障");
            res.add("少件/发错货");
            res.add("与商品描述不符");
            res.add("未收到货");
            res.add("物流原因商品破损");
            res.add("物流丢件");
        }
        res.add("其他原因");
        return new Result<>(res);
    }
}
