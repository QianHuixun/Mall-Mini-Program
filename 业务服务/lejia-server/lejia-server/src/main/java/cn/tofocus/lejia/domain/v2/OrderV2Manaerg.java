//package cn.tofocus.lejia.domain.v2;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import cn.tofocus.common.util.BeanUtil;
//import cn.tofocus.common.util.date.DateUtil;
//import cn.tofocus.core.page.PageParameter;
//import cn.tofocus.core.page.PageResult;
//import cn.tofocus.lejia.bean.dto.market.MktOrderOnList;
//import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
//import cn.tofocus.lejia.bean.entity.market.MktOrder;
//import cn.tofocus.lejia.bean.entity.market.MktOrderDesc;
//import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
//import cn.tofocus.lejia.bean.enums.OrderOir;
//import cn.tofocus.lejia.bean.enums.OrderStatus;
//import cn.tofocus.lejia.bean.enums.OrderType;
//import cn.tofocus.lejia.bean.enums.PurchaseStatus;
//import cn.tofocus.lejia.core.CurrentSession;
//import cn.tofocus.lejia.dao.market.MktOrderDao;
//import cn.tofocus.lejia.dao.market.MktOrderLineDao;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//public class OrderV2Manaerg
//{
//    @Autowired
//    public MktOrderDao orderDao;
//    
//    @Autowired
//    public MktOrderLineDao orderLineDao;
//    
//    
//    public PageResult<MktOrderOnList> queryOrder(int page, int pagesize, OrderOir orderOir, String startDate,
//        String endDate, OrderStatus status, String code, String mobile, OrderType orderType,
//        PurchaseStatus purchaseStatus, Integer groupPkey, String vrifyCode, String farmer, Boolean falg)
//    {
//        PageResult<MktOrderOnList> result = new PageResult<MktOrderOnList>();
//        PageParameter pageable = new PageParameter(page, pagesize);
//        result.setPageable(pageable);
//        List<Integer> orderIds = new ArrayList<>();
//        if (StringUtils.isNotBlank(mobile))
//        {
//            List<MktOrderDesc> exec = descDao.select().like("mobile", mobile).exec();
//            if (exec.size() <= 0) return result;
//            for (MktOrderDesc od : exec)
//                orderIds.add(od.getPkey());
//        }
//        
//        PageResult<MktOrder> pageResult = orderDao.queryOrder(page,
//            pagesize,
//            orderOir,
//            startDate,
//            endDate,
//            status,
//            code,
//            orderIds,
//            orderType,
//            purchaseStatus,
//            groupPkey,
//            vrifyCode,
//            farmer,
//            falg);
//        result = BeanUtil.beanPageFrom(MktOrderOnList.class, pageResult);
//        Boolean flag = false;
//        if ("1".equals(CurrentSession.marketPkey()))
//        {
//            flag = true;
//        }
//        for (MktOrderOnList line : result.getContent())
//        {
//            if (line.getStatus() != null && line.getStatus().getIndex() == 0)
//            {
//                line.setAmtn(BigDecimal.ZERO);
//                line.setPstime("");
//                line.setPayType(null);
//            }
//            line.setStatusName(line.getStatus().getName());
//            line.setOrderTypeName(line.getOrderType().getName());
//            line.setPayTypeName(line.getPayType() == null ? "" : line.getPayType().getName());
//            if (line.getCgCheck() != null && line.getCgCheck() == 1)
//                line.setCgCheckName("已采购");
//            else
//                line.setCgCheckName("未采购");
//            MktOrderDesc orderDesc = descDao.get(line.getPkey());
//            if (flag) line.setPstime("");
//            if (orderDesc != null)
//            {
//                line.setAddr(orderDesc.getAddr());
//                line.setName(orderDesc.getName());
//                line.setMobile(orderDesc.getMobile());
//                line.setLogistics(orderDesc.getLogistics());
//                if (flag) line.setPstime(DateUtil.formatDate(orderDesc.getFhTime()));
//                line.setRemark(orderDesc.getRemark());
//            }
//            List<MktOrderLine> exec = orderLineDao.select().eq("orderPkey", line.getPkey()).exec();
//            if (exec != null && exec.size() > 0)
//            {
//                for (MktOrderLine ol : exec)
//                {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("goodsName", ol.getGoodsName());
//                    map.put("goodsPricen", ol.getPricen());
//                    map.put("goodsNum", ol.getNum());
//                    String spaceName = "";
//                    MktGoodsSpace space = goodsSpaceDao.get(ol.getSpace());
//                    if (space != null) spaceName = space.getSpace();
//                    map.put("spaceName", spaceName);
//                    line.getGoodsList().add(map);
//                }
//            }
//            // TODO 二维码地址 需要地址确定后调整
//            line.setQrCode("http://www.baidu.con");
//        }
//        return result;
//    }
//}
