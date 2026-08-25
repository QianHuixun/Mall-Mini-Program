package cn.tofocus.lejia.domain.express;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.express.notify.SfOrderRouteNotify;
import cn.tofocus.lejia.bean.dto.express.notify.SfOrderStatusNotify;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderExpress;
import cn.tofocus.lejia.bean.entity.market.MktOrderExpressRoute;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.express.ExpressCompany;
import cn.tofocus.lejia.bean.enums.express.OrderExpressStatus;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderExpressDao;
import cn.tofocus.lejia.dao.market.MktOrderExpressRouteDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExpressCallbackManager
{
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderExpressDao orderExpressDao;
    
    @Autowired
    private MktOrderExpressRouteDao orderExpressRouteDao;
    
    @Transactional(rollbackFor = Exception.class)
    public void sfOrderStatusCallback(SfOrderStatusNotify notify)
    {
        // 查物流单
        MktOrderExpress orderExpress = orderExpressDao.getByExpressNo(ExpressCompany.SF, notify.getOrderNo());
        if (orderExpress == null)
        {
            log.error("[顺丰快递-订单状态回调] 找不到物流单，物流单号: {}，顺丰运单号：{}", notify.getOrderNo(), notify.getWaybillNo());
            throw TofocusException.of(LejiaErrCode.ORDER_EXPRESS_NOT_FOUND);
        }
        if (notify.isError())
        {
            log.info("[顺丰快递-订单状态回调] 物流状态异常，物流单号: {}，顺丰运单号：{}，订单状态：{}，描述：{}",
                notify.getOrderNo(),
                notify.getWaybillNo(),
                notify.getOrderStateCode(),
                notify.getDescription());
            orderExpress.setStatus(OrderExpressStatus.ERROR);
            if (StringUtil.isNotBlank(notify.getDescription())) orderExpress.setErrorMsg(notify.getDescription());
            orderExpressDao.update(orderExpress);
        }
        if ("04-40001".equals(notify.getOrderStateCode()))
        {
            orderExpress.setPickupCourierMobile(notify.getEmpPhone());
            orderExpress.setLatestPickupTime(notify.getLastTime());
            orderExpressDao.update(orderExpress);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void sfOrderRouteCallback(SfOrderRouteNotify notify)
    {
        // 查物流单
        MktOrderExpress orderExpress = orderExpressDao.getByExpressNo(ExpressCompany.SF, notify.getOrderid());
        if (orderExpress == null)
        {
            log.error("[顺丰快递-订单状态回调] 找不到物流单，物流单号: {}，顺丰运单号：{}", notify.getOrderid(), notify.getMailno());
            throw TofocusException.of(LejiaErrCode.ORDER_EXPRESS_NOT_FOUND);
        }
        // 查订单
        MktOrder order = orderDao.get(orderExpress.getOrderPkey());
        if (order == null)
        {
            log.error("[顺丰快递-订单路由回调] 找不到订单，订单号：{}，物流单号: {}，顺丰运单号：{}",
                orderExpress.getKcCode(),
                notify.getOrderid(),
                notify.getMailno());
            throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        }
        // 根据物流单和路由节点id查重
        MktOrderExpressRoute route =
            orderExpressRouteDao.getByOrderExpressAndThirdId(orderExpress.getPkey(), notify.getId());
        if (route != null)
        {
            log.info("[顺丰快递-订单路由回调] 路由节点已存在，已忽略，物流单号: {}，顺丰运单号：{}，路由节点id：{}",
                notify.getOrderid(),
                notify.getMailno(),
                notify.getId());
            return;
        }
        // 保存路由节点
        route = new MktOrderExpressRoute();
        route.setOrderExpress(orderExpress.getPkey());
        route.setExpressNo(orderExpress.getExpressNo());
        route.setOrderPkey(orderExpress.getOrderPkey());
        route.setKcCode(orderExpress.getKcCode());
        route.setMailNo(notify.getMailno());
        route.setThirdId(notify.getId());
        route.setTime(notify.getAcceptTime());
        route.setAddress(notify.getAcceptAddress());
        route.setOpCode(notify.getOpCode());
        route.setDescription(notify.getDescription());
        route.setAscription(orderExpress.getAscription());
        orderExpressRouteDao.add(route);
        // 处理状态
        if (notify.getOpCode() != null)
        {
            boolean changed = false;
            switch (notify.getOpCode())
            {
                // 运输中
                case "30":
                case "31":
                case "3036":
                case "50":
                    orderExpress.setStatus(OrderExpressStatus.IN_TRANSIT);
                    changed = true;
                    break;
                // 订单异常
                case "33":
                case "70":
                    orderExpress.setStatus(OrderExpressStatus.ERROR);
                    if (StringUtil.isNotBlank(notify.getDescription()))
                        orderExpress.setErrorMsg(notify.getDescription());
                    changed = true;
                    break;
                // 正在派件中
                case "44":
                case "123":
                    orderExpress.setStatus(OrderExpressStatus.OUT_FOR_DELIVERY);
                    changed = true;
                    break;
                // 已签收
                case "80":
                case "8000":
                case "130":
                case "607":
                    orderExpress.setStatus(OrderExpressStatus.RECEIVED);
                    changed = true;
                    break;
                default:
            }
            if (changed) orderExpressDao.update(orderExpress);
            if (orderExpress.getStatus() == OrderExpressStatus.RECEIVED
                && order.getStatus() == OrderStatus.SHIPPED_ORDER)
            {
                order.setStatus(OrderStatus.ARRIVED_ORDER);
                orderDao.update(order);
            }
        }
    }
}
