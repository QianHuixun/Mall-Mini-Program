package cn.tofocus.lejia.domain.market;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.esotericsoftware.minlog.Log;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnList;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsGift;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.GiftType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsGiftDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberGiftDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktOrderLineDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GiftManager
{
    @Autowired
    private MktMemberGiftDao memberGiftDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktOrderLineDao orderLineDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsGiftDao goodsGiftDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private WxManager wxManager;
    
    //卡券创建
    @Transactional(rollbackFor = Exception.class)
    public void insMemberGift(MktOrder order)
    {
        MktOrderLine line = orderLineDao.selectOne().eq("orderPkey", order.getPkey()).exec();
        MktGoodsGift gift = goodsGiftDao.getByGoods(line.getGoods().intValue());
//        MktGoodsGift gift = goodsGiftDao.get(line.getGoods());
        if (gift == null) return;
        List<MktMemberGift> addGift = new ArrayList<>();
        for (int i = 0; i < line.getNum(); i++)
        {
            MktMemberGift entity = new MktMemberGift();
            entity.setStatus(CardStatus.UNUSED);
            entity.setMember(order.getMember());
            entity.setOrderPkey(order.getPkey());
            entity.setGift(gift.getPkey());
            entity.setGoods(line.getGoods().intValue());
            entity.setSpace(line.getSpace().intValue());
            entity.setCardNumber(numberUtils.createGiftNumber());
            entity.setExpireChoose(gift.getExpireChoose());
            entity.setUserFarmer(gift.getUserFarmer());
            entity.setUserVendor(gift.getUserVendor());
            entity.setStartDate(gift.getStartDate());
            entity.setEndDate(gift.getEndDate());
            entity.setInvalid(false);
            entity.setFarmer(order.getFarmer());
            entity.setCompany(order.getCompany());
            addGift.add(entity);
        }
        memberGiftDao.addAll(addGift);
        goodsGiftDao.updIssuedNum(gift.getPkey(), gift.getIssuedNum() + line.getNum());
    }
    
    //卡券核销
    public void hxMemberGift(String cardNum)
    {
        int vendor = MobileSession.vendorPkey();
        MktMemberGift ent = memberGiftDao.selectOne().eq("cardNumber", cardNum).exec();
        if (ent == null) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券不存在");
        MktGoodsGift gift = goodsGiftDao.get(ent.getGift());
//        MktGoodsGift gift = goodsGiftDao.getByGoods(ent.getGift());
        if (gift == null || gift.getInvalid() || ent.getInvalid())
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已失效");
        if (ent.getStatus().getIndex() == 1) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已被使用过");
        if (ent.getStatus().getIndex() == 2) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已过期");
        if (gift.getGiftType() != GiftType.INTEGRAL_BUY)
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该渠道仅支持核销积分商城购买礼券");
        if (vendor != gift.getUserVendor().intValue()) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "你无权核销该礼券");
        
        if (gift.getStartDate() != null && new Date().getTime() < gift.getStartDate().getTime())
            throw TofocusException.of(WsaleErrCode.GIFT_DATE_ERROR);
        if(gift.getEndDate() != null)
        {
            Date date = gift.getEndDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);
            if (new Date().getTime() > cal.getTime().getTime())
                throw TofocusException.of(WsaleErrCode.GIFT_DATE_ERROR);
        }
        
        ent.setUserTime(new Date());
        ent.setStatus(CardStatus.USED);
        memberGiftDao.update(ent);
        goodsGiftDao.updUsedNum(gift.getPkey(), gift.getIssuedNum() + 1);
        
        try
        {
            if(ent.getOrderPkey() != null)
            {
                // 礼券核销后,微信发货
                StringBuilder sb = new StringBuilder();
                sb.append("核销已提货：");
                sb.append(gift.getTitle());
                String itemDesc = sb.toString();
                if(itemDesc.length() > 120)
                    itemDesc = itemDesc.substring(0, 120);
                String openid = null;
                String mchid = null;
                SysAscription sysAscription = ascriptionDao.get(gift.getAscription());
                if(sysAscription != null)
                {
                    mchid = sysAscription.getConfigMchid();
                }
                MktMember mktMember = memberDao.get(ent.getMember());
                if(mktMember != null)
                    openid = mktMember.getOpenid1();
                if(openid != null && mchid != null)
                {
                    MktOrder order = orderDao.get(ent.getOrderPkey());
                    if(order != null)
                    {
                        wxManager.uploadShippingInfo(
                            null,
                            order.getCode(),
                            mchid,
                            itemDesc,
                            4,
                            null,
                            null,
                            null,
                            null,
                            openid,
                            gift.getAscription());
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
            log.error("微信确认收货报错");
        }
    }
    
    //取卡券名称
    public Map<String, String> loadMemberGift(String cardNum)
    {
        int vendor = MobileSession.vendorPkey();
        MktMemberGift ent = memberGiftDao.selectOne().eq("cardNumber", cardNum).exec();
        if (ent == null) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券不存在");
//        MktGoodsGift gift = goodsGiftDao.selectOne().eq("goods", ent.getGift()).exec();
        MktGoodsGift gift = goodsGiftDao.get(ent.getGift());
        if (gift == null || gift.getInvalid() || ent.getInvalid())
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已失效");
        if (ent.getStatus().getIndex() == 1) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已被使用过");
        if (ent.getStatus().getIndex() == 2) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该卡券已过期");
        if (gift.getGiftType() != GiftType.INTEGRAL_BUY)
            throw TofocusException.of(LejiaErrCode.GIFT_ERR, "该渠道仅支持核销积分商城购买礼券");
        if (vendor != ent.getUserVendor().intValue()) throw TofocusException.of(LejiaErrCode.GIFT_ERR, "你无权核销该礼券");
        MktGoodsSpace space = goodsSpaceDao.get(ent.getSpace());
        MktGoods gds = goodsDao.get(ent.getGoods());
        Map<String, String> map = new HashMap<>();
        map.put("goodsName", gds.getTitle());
        map.put("spaceName", space.getSpace());
        return map;
    }
    
    public PageResult<MktGiftOnList> listByMember(Integer page, Integer pagesize, Integer status)
    {
        Integer memberPkey = MobileSession.memberPkey();
        SelectPageBuilder<Integer, MktMemberGift> build =
            memberGiftDao.selectPage().page(page).pagesize(pagesize).isNotNull("orderPkey").eq("member", memberPkey);
        if (status != null) build.eq("status", status);
        PageResult<MktMemberGift> pageList = build.exec();
        PageResult<MktGiftOnList> result = BeanUtil.beanPageFrom(MktGiftOnList.class, pageList);
        for (MktGiftOnList bean : result)
        {
            bean.setVendorObj(vendorDao.get(bean.getUserVendor()));
            bean.setGoodsObj(goodsDao.get(bean.getGoods()));
        }
        return result;
    }
    
    public PageResult<MktGiftOnPage> giftList(Integer page, Integer pagesize, String startDate, String endDate,
        CardStatus status)
    {
        PageResult<MktGiftOnPage> result = memberGiftDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .isNotNull("orderPkey")
            .eq("status", status)
            .eq("userVendor", MobileSession.vendorPkey())
            .between("userTime", DateUtil.atStartOfDay(startDate), DateUtil.atEndOfDay(endDate))
            .execDto(MktGiftOnPage.class);
        
        for (MktGiftOnPage bean : result)
        {
            MktOrder order = orderDao.get(bean.getOrderPkey());
            if (order != null)
            {
                bean.setCode(order.getCode());
                bean.setAmtn(order.getAmtn());
            }
            MktOrderLine line = orderLineDao.selectOne().eq("orderPkey", bean.getOrderPkey()).exec();
            if (line != null)
            {
                bean.setNum(line.getNum());
                bean.setGoodsName(line.getGoodsName());
            }
            MktGoodsSpace space = goodsSpaceDao.get(bean.getSpace());
            if (space != null)
            {
                bean.setSpaceName(space.getSpace());
            }
        }
        return result;
    }
    
    public List<MktGiftOnList> listByOrder(int orderPkey)
    {
        List<MktMemberGift> list = memberGiftDao.select().eq("orderPkey", orderPkey).exec();
        List<MktGiftOnList> result = BeanUtil.beanListFrom(MktGiftOnList.class, list);
        for (MktGiftOnList bean : result)
        {
            bean.setVendorObj(vendorDao.get(bean.getUserVendor()));
            bean.setGoodsObj(goodsDao.get(bean.getGoods()));
        }
        return result;
    }
}
