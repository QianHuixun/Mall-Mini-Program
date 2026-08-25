package cn.tofocus.lejia.domain.app;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.AppExpressArrivedParam;
import cn.tofocus.lejia.bean.dto.app.AppExpressDTO;
import cn.tofocus.lejia.bean.dto.app.AppExpressDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.AppExpressFulfillDTO;
import cn.tofocus.lejia.bean.dto.app.AppWxErrMsgDTO;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.market.MktExpress;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.ExpressStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktCourierDao;
import cn.tofocus.lejia.dao.market.MktExpressDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderDescDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppExpressManager
{
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderDescDao orderDescDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktCourierDao courierDao;
    
    public PageResult<AppExpressDTO> queryExpress(int page, int pagesize, ExpressStatus status)
    {
        log.info("status: {}", status.getName());
        Integer courierPkey = MobileSession.courierPkey();
        if (courierPkey == null) throw TofocusException.of(WsaleErrCode.NOTOBTAINED_USERINFO);
        log.info("courierPkey: {}", courierPkey);
        PageResult<MktExpress> pageResult = expressDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("courier", courierPkey)
            .eq("status", status)
            .sort("createdTime", false)
            .exec();
        PageResult<AppExpressDTO> result = BeanUtil.beanPageFrom(AppExpressDTO.class, pageResult);
        for (AppExpressDTO dto : result.getContent())
        {
            Integer orderId = dto.getOrderId();
            if (orderId != null)
            {
                MktOrderDesc orderDesc = orderDescDao.get(orderId);
                if (orderDesc != null)
                {
                    dto.setOrderTime(orderDesc.getFkTime());
                    dto.setMobile(orderDesc.getMobile());
                    dto.setName(orderDesc.getName());
                    dto.setAddr(orderDesc.getAddr());
                    dto.setLongitude(orderDesc.getLongitude());
                    dto.setLatitude(orderDesc.getLatitude());
                }
                MktOrder order = orderDao.get(orderId);
                if (order != null)
                {
                    dto.setSmallTicket(order.getSmallTicket());
                    dto.setWeight(order.getWeight());
                    dto.setPstime(order.getPstime());
                }
            }
        }
        return result;
    }
    
    public AppExpressDetailsDTO getExpress(Integer pkey)
    {
        MktExpress express = expressDao.get(pkey);
        AppExpressDetailsDTO dto = BeanUtil.beanFrom(AppExpressDetailsDTO.class, express);
        Integer orderId = dto.getOrderId();
        if (orderId != null)
        {
            MktOrderDesc orderDesc = orderDescDao.get(orderId);
            if (orderDesc != null)
            {
                dto.setOrderTime(orderDesc.getFkTime());
                dto.setMobile(orderDesc.getMobile());
                dto.setName(orderDesc.getName());
                dto.setAddr(orderDesc.getAddr());
                dto.setRemark(orderDesc.getRemark());
            }
            MktOrder order = orderDao.get(orderId);
            if (order != null)
            {
                dto.setSmallTicket(order.getSmallTicket());
                dto.setWeight(order.getWeight());
                dto.setPstime(order.getPstime());
            }
            
            List<MktOrderLine> exec = orderLineDao.select().eq("orderPkey", orderId).exec();
            for (MktOrderLine line : exec)
            {
                Map<String, Object> map = new HashMap<>();
                map.put("goodsName", line.getGoodsName());
                map.put("num", line.getNum());
                dto.getOrderLines().add(map);
            }
        }
        return dto;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Boolean alterExpressStatus(Integer pkey, int flag)
    {
        MktExpress express = expressDao.get(pkey);
        Integer orderId = express.getOrderId();
        MktOrder order = orderDao.get(orderId);
        if (order == null) throw TofocusException.of(LejiaErrCode.NOT_FIND_ORDER);
        // 确认揽货
        if (flag == 1)
        {
            express.setStatus(ExpressStatus.EXPRESS_GOODS);
            express.setJdTime(new Date());
            expressDao.update(express);
            return true;
        }
        // 确认收货
        if (flag == 2)
        {
            if (order.getStatus().equals(OrderStatus.SHIPPED_ORDER) 
                || order.getStatus().equals(OrderStatus.WAIT_ARRIVAL_ORDER))
            {
                order.setStatus(OrderStatus.ARRIVED_ORDER);
                order.setQrTime(new Date());
                orderDao.update(order);
            }
            express.setStatus(ExpressStatus.EXPRESS_ARRIVED);
            express.setQrTime(new Date());
            expressDao.update(express);
            // 小程序消息推送
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    expressArrivedSend(order, express);
                }
            }).start();
            
            return true;
        }
        // 拒收
        if (flag == 3)
        {
            express.setStatus(ExpressStatus.EXPRESS_REJECT);
            expressDao.update(express);
            return true;
        }
        return false;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean arrivedExpress(AppExpressArrivedParam param)
    {
        alterExpressStatus(param.getPkey(), 2);
        MktExpress express = expressDao.get(param.getPkey());
        express.setPhoto(param.getPhoto());
        expressDao.update(express);
        return true;
    }
    
    public void expressArrivedSend(MktOrder order, MktExpress express)
    {
        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("value", order.getCode());
        data.put("character_string1", jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        if(StringUtils.isBlank(express.getCourierMobile()))
        {
            MktCourier courier = courierDao.get(express.getCourier());
            jsonObject2.put("value", " ");
            if(courier != null)
                jsonObject2.put("value", courier.getName());
        }
        else
            jsonObject2.put("value", express.getCourierMobile());
        data.put("phone_number2", jsonObject2);
        AccountEntity account = wxManager.getAccountEntity(AccountType.USER, order.getAscription());
        MktMember member = memberDao.get(order.getMember());
        if(member != null && StringUtils.isNotBlank(member.getOpenid1()) && StringUtils.isNoneBlank(account.getTemplateId()))
        {
            AppWxErrMsgDTO dto =
                wxManager.sendWeappSubscribeMessage(account, member.getOpenid1(), account.getTemplateId(), "/pages/my/orderDetail/index?pkey=" + order.getPkey(), data);
            log.info("确认送达,小程序消息推送结果: {}", JsonUtil.toString(dto, true));
        }
        else
            log.info("确认送达,条件不满足,未推送小程序消息");
    }
    
    public PageResult<AppExpressFulfillDTO> queryFulfillExpress(int page, int pagesize)
    {
        Integer courierPkey = MobileSession.courierPkey();
        if (courierPkey == null) throw TofocusException.of(WsaleErrCode.NOTOBTAINED_USERINFO);
        log.info("courierPkey: {}", courierPkey);
        PageResult<MktExpress> pageResult = expressDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("courier", courierPkey)
            .eq("status", ExpressStatus.EXPRESS_ARRIVED)
            .sort("pkey", true)
            .exec();
        PageResult<AppExpressFulfillDTO> result = BeanUtil.beanPageFrom(AppExpressFulfillDTO.class, pageResult);
        for (AppExpressFulfillDTO dto : result.getContent())
        {
            Integer orderId = dto.getOrderId();
            if (orderId != null)
            {
                MktOrderDesc orderDesc = orderDescDao.get(orderId);
                if (orderDesc != null)
                {
                    dto.setOrderTime(orderDesc.getFkTime());
                    dto.setAddr(orderDesc.getAddr());
                }
                MktOrder order = orderDao.get(orderId);
                if (order != null)
                {
                    dto.setSmallTicket(order.getSmallTicket());
                    dto.setWeight(order.getWeight());
                }
            }
        }
        return result;
    }
}
