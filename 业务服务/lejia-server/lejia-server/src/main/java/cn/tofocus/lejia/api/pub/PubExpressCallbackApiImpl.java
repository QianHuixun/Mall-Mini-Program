package cn.tofocus.lejia.api.pub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.express.notify.SfNotifyResult;
import cn.tofocus.lejia.bean.dto.express.notify.SfOrderRouteNotify;
import cn.tofocus.lejia.bean.dto.express.notify.SfOrderStatusNotify;
import cn.tofocus.lejia.domain.express.ExpressCallbackManager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/pub/express")
@RestController
public class PubExpressCallbackApiImpl
{
    @Autowired
    private ExpressCallbackManager expressCallbackManager;
    
    @Operation(summary = "顺丰快递-订单状态回调", tags = ApiTags.EXPRESS_CALLBACK)
    @PostMapping("/sf/orderStatus/callback")
    public SfNotifyResult sfOrderStatusCallback(@RequestBody SfOrderStatusNotify notify)
    {
        try
        {
            log.info("[顺丰快递-订单状态回调] notify: {}", JsonUtil.toString(notify));
            expressCallbackManager.sfOrderStatusCallback(notify);
            return SfNotifyResult.ok();
        }
        catch (TofocusException te)
        {
            return SfNotifyResult.error();
        }
        catch (Exception e)
        {
            log.error("[顺丰快递-订单状态回调] 处理异常", e);
            return SfNotifyResult.error();
        }
    }
    
    @Operation(summary = "顺丰快递-订单路由回调", tags = ApiTags.EXPRESS_CALLBACK)
    @PostMapping("/sf/orderRoute/callback")
    public SfNotifyResult sfOrderRouteCallback(@RequestBody SfOrderRouteNotify notify)
    {
        try
        {
            log.info("[顺丰快递-订单路由回调] notify: {}", JsonUtil.toString(notify));
            expressCallbackManager.sfOrderRouteCallback(notify);
            return SfNotifyResult.ok();
        }
        catch (TofocusException te)
        {
            return SfNotifyResult.error();
        }
        catch (Exception e)
        {
            log.error("[顺丰快递-订单路由回调] 处理异常", e);
            return SfNotifyResult.error();
        }
    }
}
