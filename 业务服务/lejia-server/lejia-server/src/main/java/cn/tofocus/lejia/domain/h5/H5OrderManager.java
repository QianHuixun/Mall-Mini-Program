package cn.tofocus.lejia.domain.h5;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.h5.H5OrderInfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.h5.H5Goods;
import cn.tofocus.lejia.bean.entity.h5.H5GoodsSpace;
import cn.tofocus.lejia.bean.entity.h5.H5Order;
import cn.tofocus.lejia.bean.entity.h5.H5User;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.enums.h5.H5Level;
import cn.tofocus.lejia.bean.enums.h5.H5OrderStatus;
import cn.tofocus.lejia.bean.enums.h5.H5PayType;
import cn.tofocus.lejia.cache.H5OrderTokenMap;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.h5.H5GoodsDao;
import cn.tofocus.lejia.dao.h5.H5GoodsSpaceDao;
import cn.tofocus.lejia.dao.h5.H5OrderDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.util.TongTongSuoUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class H5OrderManager
{
    @Autowired
    private H5OrderDao h5OrderDao;
    
    @Autowired
    private H5GoodsDao goodsDao;
    
    @Autowired
    private MktGoodsDao mktGoodsDao;

    @Autowired
    private MktGoodsSpaceDao mktGoodsSpaceDao;
    
    @Autowired
    private H5GoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private H5OrderTokenMap h5OrderTokenMap;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Resource
    private SmsConfig smsConfig;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private H5UserManager h5UserManager;
    
    @Autowired
    private H5GoodsManager h5GoodsManager;
    
    public H5OrderInfo buy(Integer space)
    {
        H5OrderInfo res = new H5OrderInfo();
        H5GoodsSpace gs = goodsSpaceDao.get(space);
        if(gs == null || gs.getKcNum() < 1)
            throw TofocusException.of(LejiaErrCode.H5_SPACE_ERROR);
        H5Goods goods = goodsDao.getGoods(gs.getGoods());
        if(goods == null)
            throw TofocusException.of(LejiaErrCode.H5_SPACE_ERROR);
        H5User user = CurrentSession.getH5User();
        int level = 3;
        if(user.getLevel() != null)
            level = user.getLevel();
        switch (level)
        {
            case 1:
                if(!H5Level.PREDETERMINE.equals(goods.getLevelA()))
                    throw TofocusException.of(LejiaErrCode.H5_BOX_LEVEL_ERROR);
                break;
            case 2:
                if(!H5Level.PREDETERMINE.equals(goods.getLevelB()))
                    throw TofocusException.of(LejiaErrCode.H5_BOX_LEVEL_ERROR);
                break;
            case 3:
                if(!H5Level.PREDETERMINE.equals(goods.getLevelC()))
                    throw TofocusException.of(LejiaErrCode.H5_BOX_LEVEL_ERROR);
                break;
        }
        res.setAmto(gs.getPrice());
        res.setAmtn(gs.getPrice());
        res.setBoxName(goods.getTitle());
        res.setBoxTime(gs.getSpace());
        if(goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
            res.setPhoto1(goods.getPhoto1().get(0));
        SysFarmer farmer = farmerDao.get(CurrentSession.marketPkey());
        if(farmer != null)
            res.setFarmerName(farmer.getName());
        return res;
    }
    
    @Transactional(rollbackFor = Throwable.class)
    public Boolean commitOrder(Integer space, String remark, H5PayType payType)
    {
        log.info("----------提交订单----------");
        Long ll = h5OrderTokenMap.get("order:" + space);
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            h5OrderTokenMap.put("order:" + space, System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.WRONG_TIME);
        }
        h5OrderTokenMap.put("order:" + space, System.currentTimeMillis());
        
        // 检查库存
        H5GoodsSpace gs = goodsSpaceDao.get(space);
        if(gs == null || gs.getKcNum() < 1)
            throw TofocusException.of(LejiaErrCode.H5_SPACE_ERROR);
        // 检查商品是否存在
        H5Goods goods = goodsDao.getGoods(gs.getGoods());
        if(goods == null || Boolean.FALSE.equals(goods.getEnabled()))
            throw TofocusException.of(LejiaErrCode.H5_SPACE_ERROR);
        // 判断对应商品库存量
        MktGoods mktGoods = mktGoodsDao.byH5Goods(goods.getTitle(), goods.getFarmer());
        if(mktGoods != null)
        {
            MktGoodsSpace mgs = mktGoodsSpaceDao.byH5Space(gs.getSpace(), mktGoods.getPkey());
            if(mgs != null)
            {
                Long kcNum = spaceKcCache.getLong(String.valueOf(mgs.getPkey()));
                if (kcNum == null) throw TofocusException.of(LejiaErrCode.GOODS_NONUM, goods.getTitle() + "库存不足");
                if (kcNum.intValue() < 1) throw TofocusException.of(LejiaErrCode.GOODS_NONUM, goods.getTitle() + "库存不足");
            }
        }
        // 备注字数限制
        if (StringUtils.isNotBlank(remark) && remark.length() > 200)
            throw TofocusException.of(LejiaErrCode.EXCEED_THE_LIMIT);
        H5User user = CurrentSession.getH5User();
        int level = 3;
        if(user.getLevel() != null)
            level = user.getLevel();
        // 判断用户是否有资格购买
        switch (level)
        {
            case 1:
                if(!H5Level.PREDETERMINE.equals(goods.getLevelA()))
                    throw TofocusException.of(LejiaErrCode.H5_BOX_LEVEL_ERROR);
                break;
            case 2:
                if(!H5Level.PREDETERMINE.equals(goods.getLevelB()))
                    throw TofocusException.of(LejiaErrCode.H5_BOX_LEVEL_ERROR);
                break;
            case 3:
                if(!H5Level.PREDETERMINE.equals(goods.getLevelC()))
                    throw TofocusException.of(LejiaErrCode.H5_BOX_LEVEL_ERROR);
                break;
        }
        // 电子帐户余额不足
        if (payType.equals(H5PayType.ORDER_ELECTRONIC_ACCOUNT) && user.getMoney().compareTo(gs.getPrice()) < 0)
            throw TofocusException.of(LejiaErrCode.NO_COMMS);
        
        // 处理h5商品库存
        h5GoodsManager.updateKu(gs, 1);
        
        // 处理mkt商品库存
        if(mktGoods != null)
        {
            MktGoodsSpace mgs = mktGoodsSpaceDao.byH5Space(gs.getSpace(), mktGoods.getPkey());
            if(mgs != null)
            {
                h5GoodsManager.updateGooddsKu(mgs.getPkey(), 1);
            }
        }
        
        // 处理账户余额
        h5UserManager.updUserMoney(user, gs.getPrice().negate());
        
        H5Order order = new H5Order();
        order.setAmto(gs.getPrice());
        order.setAmtn(gs.getPrice());
        order.setUserKey(user.getPkey());
        order.setBoxName(goods.getTitle());
        order.setBoxTime(gs.getSpace());
        order.setStatus(H5OrderStatus.NOTUSED_ORDER);
        order.setSpace(gs.getPkey());
        order.setGoods(goods.getPkey());
        if(goods.getPhoto1() != null && !goods.getPhoto1().isEmpty())
            order.setPhoto1(goods.getPhoto1().get(0));
        String payNumber = numberUtils.createH5OrderNumber();
        order.setCode(payNumber);
        order.setRemark(remark);
        order.setBoxName(goods.getTitle());
        order.setBoxTime(gs.getSpace());
        order.setLockId(goods.getLockId());
        order.setBoxSd(gs.getBoxSd());
        order.setBoxEd(gs.getBoxEd());
        order.setPayType(payType);
        String boxPassword = TongTongSuoUtil.timeLimitPwd(Integer.valueOf(order.getLockId()), order.getCode(), order.getBoxSd(), order.getBoxEd());
        log.info("包厢密码: {}", boxPassword);
        order.setBoxPassword(boxPassword);
        order.setFarmer(goods.getFarmer());
        order.setCompany(goods.getCompany());
        h5OrderDao.add(order);
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // 短信内容 已经 短信模板ID  临时门锁ID  16304453
                List<String> params = new ArrayList<>();
                SysFarmer farmer = sysFarmerDao.get(order.getFarmer());
                params.add("家和菜-" + farmer.getName());
                params.add(order.getBoxTime());
                params.add(order.getBoxName());
                params.add(boxPassword);
                params.add(farmer.getTel());
                params.add("鹿城区东屿路66号 东屿农贸市场3楼   \nhttps://j.map.baidu.com/5c/-Qzi");
                params.add("店门口与地下均有停车场");
                new SMSNotify(smsConfig).sendNotify(user.getMobile(), params, "TDVGPrkepo2d");
                new SMSNotify(smsConfig).sendNotify("17857047200", params, "TDVGPrkepo2d");
            }
        }).start();
        return true;
    }
    
    // mktSpace 主键
    public void upBoxSpace(Integer space)
    {
        MktGoodsSpace mgs = mktGoodsSpaceDao.get(space);
        if(mgs == null)
            return;
        MktGoods mktGoods = mktGoodsDao.get(mgs.getGoods());
        if(mktGoods == null)
            return;
        H5Goods h5Goods = goodsDao.byH5Goods(mktGoods.getTitle(), mktGoods.getFarmer());
        if(h5Goods == null)
            return;
        H5GoodsSpace h5Space = goodsSpaceDao.byH5Space(mgs.getSpace(), h5Goods.getPkey());
        if(h5Space == null)
            return;
        // 处理h5商品库存
        h5GoodsManager.updateKu(h5Space, 1);
    }
    
    public PageResult<H5OrderInfo> query(int page, int pagesize)
    {
        H5User h5User = CurrentSession.getH5User();
        PageResult<H5OrderInfo> res = h5OrderDao.query(page, pagesize, h5User.getPkey(), h5User.getFarmer());
        SysFarmer farmer = sysFarmerDao.get(h5User.getFarmer());
        for(H5OrderInfo o : res.getContent())
        {
            o.setFarmerName(farmer.getName());
        }
        return res;
    }
    
}
