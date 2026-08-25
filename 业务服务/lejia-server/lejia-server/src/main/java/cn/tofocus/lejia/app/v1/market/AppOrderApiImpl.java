package cn.tofocus.lejia.app.v1.market;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppGoodsCollageDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppAddrDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCardDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderCutDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppOrderDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppRefundDTO;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeDTO;
import cn.tofocus.lejia.bean.dto.market.DistributionTypeTimeDTO;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnList;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktPayLine;
import cn.tofocus.lejia.bean.enums.DistributionType;
import cn.tofocus.lejia.bean.enums.OrderGroupStatus;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktPayLineDao;
import cn.tofocus.lejia.domain.market.GiftManager;
import cn.tofocus.lejia.domain.market.mall.AppOrderManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/app/market/lm/order")
@RestController
public class AppOrderApiImpl implements AppOrderApi
{
    @Autowired
    private AppOrderManager orderManager;
    
    @Autowired
    private GiftManager giftManager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktPayLineDao payLineDao;
    
//    @Override
//    public Result<MktAppOrderDTO> bugGoods(Integer goods, Integer num, String tjr)
//    {
//        MktAppOrderDTO dto = orderManager.loadInitOrder(goods, num, tjr);
//        return new Result<>(dto);
//    }
//    
//    @Override
//    public Result<MktAppOrderDTO> buyGwc(List<String> gwcs)
//    {
//        MktAppOrderDTO dto = orderManager.loadInitOrder(gwcs);
//        return new Result<>(dto);
//    }
    
    @Override
    public Result<List<MktAppAddrDTO>> listAddr(DistributionType distributionType)
    {
        List<MktAppAddrDTO> list = orderManager.listAddr(distributionType);
        return new Result<>(list);
    }
    
    @Override
    public Result<List<MktAppCardDTO>> listCard(MktAppOrderDTO order)
    {
        List<MktAppCardDTO> list = orderManager.listCard(order);
        return new Result<>(list);
    }
    
//    @Override
//    public Result<MktAppOrderDTO> commitOrder(@Valid MktAppOrderDTO order)
//    {
//        MktAppOrderDTO res = orderManager.insOrder(order);
//        return new Result<>(res);
//    }
    
    @Override
    public Result<MktAppOrderDTO> loadOrder(Integer pkey)
    {
        MktAppOrderDTO res = orderManager.loadInitOrder(pkey, true);
        return new Result<>(res);
    }
    
    @Override
    public Result<PageResult<MktAppOrderDTO>> listOrder(int page, int pagesize, OrderStatus status)
    {
        return new Result<>(orderManager.listOrder(page, pagesize, status));
    }
    
    @Override
    public Result<Boolean> drOrder(Integer pkey)
    {
        MktOrder order = orderDao.get(pkey);
        orderManager.drOrder(order);
        return new Result<>(true);
    }
    
//    @Override
//    public Result<Boolean> drOrderWx(String transactionId)
//    {
//        MktPayLine payLine = payLineDao.getCode(transactionId);
//        List<MktOrder> listCode = orderDao.listCode(payLine.getOrderNumber());
//        for(MktOrder o : listCode)
//        {
//            try
//            {
//                orderManager.drOrder(o);
//            }
//            catch (Exception e)
//            {
//               log.error(e.getMessage());
//            }
//        }
//        return new Result<>(true);
//    }
    
    @Override
    public Result<Boolean> refund(MktAppRefundDTO refundDto)
    {
        orderManager.reFund(refundDto);
        return new Result<>(true);
    }
    
    @Override
    public Result<AppGoodsCollageDTO> getOrderCollage(int orderPkey)
    {
        return new Result<>(orderManager.getOrderCollage(orderPkey));
    }
    
    @Override
    public Result<PageResult<AppGoodsCollageDTO>> listOrderCollage(int page, int pagesize, OrderGroupStatus status)
    {
        return new Result<>(orderManager.listOrderCollage(page, pagesize, status));
    }
    
    @Override
    public Result<PageResult<MktAppOrderDTO>> getOrderCutList(int page, int pagesize)
    {
        return new Result<>(orderManager.getOrderCutList(page, pagesize));
    }
    
    @Override
    public Result<BigDecimal> cutOrder(int orderPkey)
    {
        return new Result<>(orderManager.cutOrder(orderPkey));
    }
    
    @Override
    public Result<MktAppOrderCutDTO> loadCutOrder(Integer pkey)
    {
        return new Result<>(orderManager.loadCutOrder(pkey));
    }
    
    @Override
    public Result<MktAppOrderCutDTO> initiateCut(Integer goods, Integer num, Integer addressPkey)
    {
        return new Result<>(orderManager.initiateCut(goods, num, "", addressPkey));
    }
    
    @Override
    public Result<Boolean> isshow(int orderPkey)
    {
        return new Result<>(orderManager.isshow(orderPkey));
    }
    
    @Override
    public Result<List<MktGiftOnList>> queryByOrder(Integer orderPkey)
    {
        
        return new Result<>(giftManager.listByOrder(orderPkey));
    }
    
    @Override
    public Result<DistributionTypeDTO> getDistributionType(String marketPkey, DistributionType type, Integer addressPkey)
    {
        return new Result<>(orderManager.getDistributionType(marketPkey, type, addressPkey));
    }
    
    @Override
    public Result<DistributionTypeTimeDTO> getDistributionTypePsTime(String marketPkey, DistributionType type, Integer addressPkey)
    {
        return new Result<>(orderManager.getDistributionTypePsTime(marketPkey, type, addressPkey));
    }

    @Override
    public Result<DistributionTypeTimeDTO> getSupplierPsTime(Integer supplier)
    {
        return new Result<>(orderManager.getSupplierPsTime(supplier));
    }

    /** {@inheritDoc} */
     
}
