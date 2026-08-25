package cn.tofocus.lejia.domain.app;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.HttpRequestBuilder2;
import cn.tofocus.common.util.HttpUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.file.api.v3.FileApiV3;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.util.FileUploader;
import cn.tofocus.lejia.bean.dto.app.AppCardCheckDTO;
import cn.tofocus.lejia.bean.dto.app.AppUsePointsRecordOnList;
import cn.tofocus.lejia.bean.dto.app.AppVendorDTO;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorOrderInfoV2;
import cn.tofocus.lejia.bean.dto.app.vendor.VendorOrderOnPage;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.member.MktMemberGift;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorOrder;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPointLine;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorStaff;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.PurchaseStatus;
import cn.tofocus.lejia.bean.enums.SettlementType;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.cache.MobileCodeMap;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberCardDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberGiftDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.dao.vendor.MktVendorOrderDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPointDao;
import cn.tofocus.lejia.dao.vendor.MktVendorPointLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorStaffDao;
import cn.tofocus.lejia.domain.market.VendorOrderManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppVendorManager
{
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorStaffDao vendorStaffDao;
    
    @Resource
    private MktVendorFileDao mktVendorFileDao;
    
    @Autowired
    private MktVendorPointDao vendorPointDao;
    
    @Autowired
    private MktVendorPointLineDao vendorPointLineDao;
    
    @Autowired
    private MktMemberCardDao memberCardDao;
    
    @Autowired
    private SmsConfig smsConfig;
    
    @Autowired
    private MobileCodeMap mobileMap;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktMemberGiftDao memberGiftDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private MktVendorOrderDao vendorOrderDao;
    
    @Autowired
    private VendorOrderManager vorderMng;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private FileApiV3 fileApiV3;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private SecurityContextUtil securityContextUtil;
    
    @Autowired
    private FileUploader fileUploader;
    
    public AppVendorDTO getVendor()
    {
        MktVendor vendor = MobileSession.vendor();
        log.info("vendor: {}", vendor);
        if (vendor == null) throw TofocusException.of(WsaleErrCode.NOTOBTAINED_USERINFO);
        AppVendorDTO dto = BeanUtil.beanFrom(AppVendorDTO.class, vendor);
        dto.setPoints(vendorPointDao.getPoints(dto.getPkey()));
        
        SysFarmer farmer = farmerDao.get(vendor.getFarmer());
        if(farmer != null && FarmerType.VENDOR_SHOPPING_MALL.equals(farmer.getType()))
        {
            dto.setGoodsManage(true);
        }
        
        List<MktMemberGift> mgList =
            memberGiftDao.select().eq("userVendor", MobileSession.vendorPkey()).eq("status", CardStatus.USED).exec();
        BigDecimal useCardNum = BigDecimal.ZERO;
        if (mgList.size() > 0)
        {
            List<Integer> spaceKeys = new ArrayList<>();
            mgList.forEach(e -> {
                spaceKeys.add(e.getSpace());
            });
            
            HashSet<Integer> h = new HashSet<Integer>(spaceKeys);
            spaceKeys.clear();
            spaceKeys.addAll(h);
            List<MktGoodsSpace> exec = goodsSpaceDao.select().in("pkey", spaceKeys.toArray()).exec();
            Map<Integer, BigDecimal> priceMap = new HashMap<>();
            exec.forEach(e -> {
                priceMap.put(e.getPkey(), e.getPrice());
            });
            
            for (MktMemberGift mg : mgList)
            {
                if (priceMap.containsKey(mg.getSpace()))
                {
                    useCardNum = useCardNum.add(priceMap.get(mg.getSpace()));
                }
            }
        }
//        BigDecimal countAmtDate = vendorOrderDao.countAmtDate(MobileSession.vendorPkey(), "2020-01-01 00:00:00");
//        dto.setUseCardNum(useCardNum.add(countAmtDate));
        dto.setUseCardNum(useCardNum);
        dto.setAmtToday(vorderMng.countAmtToday(vendor.getPkey()));
        dto.setAmtMonth(vorderMng.countAmtMon(vendor.getPkey()));
        
        MktVendorFile exec =
            mktVendorFileDao.selectOne().eq("vendorPkey", vendor.getPkey()).eq("type", VendorFileType.HEAD_ICON).exec();
        if (exec != null) dto.setHeadIcon(exec.getUrl());
        return dto;
    }
    
    public PageResult<AppUsePointsRecordOnList> queryUsePointsRecord(Integer page, Integer pagesize)
    {
        MktVendor vendor = MobileSession.vendor();
        PageResult<MktVendorPointLine> pageResult =
            vendorPointLineDao.queryPageResult(page, pagesize, vendor.getPkey());
        PageResult<AppUsePointsRecordOnList> result = BeanUtil.beanPageFrom(AppUsePointsRecordOnList.class, pageResult);
        for (AppUsePointsRecordOnList appUsePointsRecordOnList : result)
        {
            MktMember member = memberDao.get(appUsePointsRecordOnList.getMember());
            appUsePointsRecordOnList.setMemberName(member.getName());
        }
        return result;
    }
    
    public Boolean createCaptcha(String phone)
    {
        MktVendor vendor = vendorDao.selectOne().eq("mobile", phone).eq("idDel", false).exec();
        MktVendorStaff staff = null;
        if (vendor == null)
        {
            staff = vendorStaffDao.selectOne().eq("mobile", phone).eq("idDel", false).exec();
            if (staff == null) throw TofocusException.of(WsaleErrCode.NOT_VENDOR);
            if (!staff.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_STAFF_ENABLED);
        }
        else if (!vendor.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_VENDOR_ENABLED);
        String code = NumberUtils.createCheckCode();
        mobileMap.put(phone, code);
        System.out.println("手机验证码：" + code);
        return new SMSNotify(smsConfig).sendCode(phone, code);
    }
    
    @Transactional
    public boolean checkCaptcha(String phone, String code, String openid)
    {
        log.info("phone: {}", phone);
        MktVendor vendor = vendorDao.selectOne().eq("mobile", phone).eq("enabled", true).eq("idDel", false).exec();
        MktVendorStaff staff = null;
        if (vendor == null)
        {
            staff = vendorStaffDao.selectOne().eq("mobile", phone).eq("enabled", true).eq("idDel", false).exec();
            if (staff == null) throw TofocusException.of(WsaleErrCode.NOT_VENDOR);
        }
        String ccode = mobileMap.get(phone);
        if (ccode == null) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        // 验证码是840727 的时候 都给通过
        if (!ccode.equals(code) && !"840727".equals(code)) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        if (vendor == null)
        {
            staff.setOpenid1(openid);
            vendorStaffDao.update(staff);
            List<MktVendorStaff> exec =
                vendorStaffDao.select().eq("openid1", openid).notEq("pkey", staff.getPkey()).exec();
            for (MktVendorStaff v : exec)
            {
                v.setOpenid1(null);
            }
            vendorStaffDao.updateAll(exec);
        }
        else
        {
            if("[object Null]".equals(openid))
            {
                throw TofocusException.of(WsaleErrCode.OPENID_ERROR);
            }
            vendor.setOpenid1(openid);
            vendorDao.update(vendor);
            List<MktVendor> exec = vendorDao.select().eq("openid1", openid).notEq("pkey", vendor.getPkey()).exec();
            for (MktVendor v : exec)
            {
                v.setOpenid1(null);
            }
            vendorDao.updateAll(exec);
        }
        return true;
    }
    
    public Boolean checkLogin(String openid)
    {
        MktVendor vendor = vendorDao.selectOne().eq("openid1", openid).eq("enabled", true).eq("idDel", false).exec();
        return vendor != null;
    }
    
    // 查询该优惠券的名称
    public String getCardName(String cardNumber)
    {
        MktVendor vendor = MobileSession.vendor();
        String farmerPkey = vendor.getFarmer();
        MktMemberCard memberCard = checkCardNumber(cardNumber, farmerPkey);
        MktCard card = cardDao.get(memberCard.getCard());
        if (card == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE, "卡券已经不存在!");
        return card.getTitle();
    }
    
    // 商户核销用户的优惠券
    public Boolean insCard(String cardNumber)
    {
        MktVendor vendor = MobileSession.vendor();
        String farmerPkey = vendor.getFarmer();
        MktMemberCard memberCard = checkCardNumber(cardNumber, farmerPkey);
        memberCard.setUserFarmer(vendor.getPkey() + "");
        memberCard.setUserTime(new Date());
        memberCard.setStatus(CardStatus.USED);
        memberCardDao.update(memberCard);
        return true;
    }
    
    private MktMemberCard checkCardNumber(String cardNumber, String farmerPkey)
    {
        MktMemberCard memberCard = memberCardDao.selectOne().eq("cardNumber", cardNumber).exec();
        if (memberCard == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (memberCard.getStatus().getIndex() != 0) throw TofocusException.of(WsaleErrCode.CARD_NOT_EXIST);
        if (!farmerPkey.equals(memberCard.getFarmer())) throw TofocusException.of(WsaleErrCode.CARD_NOT_FARMER);
        return memberCard;
    }
    
    public PageResult<AppCardCheckDTO> queryCard(int page, int pagesize)
    {
        MktVendor vendor = MobileSession.vendor();
        PageResult<MktMemberCard> pageResult = memberCardDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .isNull("orderId")
            .isNotNull("userTime")
            .eq("status", CardStatus.USED)
            .eq("userFarmer", vendor.getPkey())
            .sort("userTime", true)
            .exec();
        PageResult<AppCardCheckDTO> result = BeanUtil.beanPageFrom(AppCardCheckDTO.class, pageResult);
        for (AppCardCheckDTO dto : result)
        {
            MktCard card = cardDao.get(dto.getCard());
            if (card != null) dto.setTitle(card.getTitle());
        }
        return result;
    }
    
    @Transactional
    public Boolean finishPurchase(Integer pkey)
    {
        MktVendorOrder vendorOrder = vendorOrderDao.get(pkey);
        if (vendorOrder == null) return false;
        PurchaseStatus purchaseStatus = vendorOrder.getPurchaseStatus();
        if (purchaseStatus == null || purchaseStatus.getIndex() != PurchaseStatus.PURCHASEING.getIndex())
            throw TofocusException.of(LejiaErrCode.PURCHASESTATUS_ERROR3);
        vendorOrder.setPurchaseStatus(PurchaseStatus.PURCHASE_FINISH);
        vendorOrder.setStatus(SettlementType.AWAIT_CONFIRM);
        vendorOrder.setVendorTime(new Date());
        vendorOrderDao.update(vendorOrder);
        MktOrder order = orderDao.get(vendorOrder.getOrderPkey());
        if (order != null)
        {
            List<MktVendorOrder> exec2 = vendorOrderDao.select()
                .eq("orderPkey", vendorOrder.getOrderPkey())
                .notEq("purchaseStatus", PurchaseStatus.PURCHASE_REVOKE)
                .sort("status", false)
                .exec();
            if (!exec2.isEmpty())
            {
                order.setPurchaseStatus(exec2.get(0).getPurchaseStatus());
                orderDao.update(order);
            }
        }
        return true;
    }
    
    public VendorOrderInfoV2 queryOrder(int page, int pagesize, SettlementType status, String startDate, String endDate,
        Boolean flag)
    {
        Integer vendorPkey = MobileSession.vendorPkey();
        VendorOrderInfoV2 res = new VendorOrderInfoV2();
        if (flag)
        {
            PageResult<VendorOrderOnPage> lines = vendorOrderDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("vendor", vendorPkey)
                .eq("status", status)
                .in("purchaseStatus", PurchaseStatus.PURCHASEING, PurchaseStatus.AWAIT_PURCHASE)
                .sort("startDate", false)
                .sort("createdTime")
                .execDto(VendorOrderOnPage.class);
            // 排序
            res.setLines(lines);
            res.setAwaitSettlement("");
            res.setAlreadySettlement("");
        }
        else
        {
            Date start = null;
            Date end = null;
            if (StringUtils.isNotBlank(startDate)) start = DateUtil.atStartOfDay(startDate);
            if (StringUtils.isNotBlank(endDate)) end = DateUtil.atStartOfNextDay(endDate);
            
            PageResult<VendorOrderOnPage> lines = vendorOrderDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("status", status)
                //                .eq("purchaseStatus", PurchaseStatus.PURCHASE_CONFIRM)
                .in("purchaseStatus", PurchaseStatus.PURCHASE_FINISH, PurchaseStatus.PURCHASE_CONFIRM)
                //                .isNotNull("status")
                .eq("vendor", vendorPkey)
                .ge("createdTime", start)
                .lt("createdTime", end)
                .sort("createdTime")
                .execDto(VendorOrderOnPage.class);
            res.setLines(lines);
            
            Map<String, Number> map = vendorOrderDao.aggregation()
                .ge("createdTime", start)
                .lt("createdTime", end)
                .eq("vendor", vendorPkey)
                .in("purchaseStatus", PurchaseStatus.PURCHASE_FINISH, PurchaseStatus.PURCHASE_CONFIRM)
                .isNotNull("status")
                .execGroupBySum("status", "amt");
            res.setAwaitSettlement("0");
            res.setAlreadySettlement("0");
            BigDecimal awaitAmt = BigDecimal.ZERO;
            for (String key : map.keySet())
            {
                if (SettlementType.SUCCESS.name().equals(key))
                {
                    res.setAlreadySettlement(map.get(key) + "");
                }
                else
                {
                    awaitAmt = awaitAmt.add(BigDecimal.valueOf(map.get(key).doubleValue()));
                }
                res.setAwaitSettlement(awaitAmt.toString());
            }
        }
        if (res.getLines() != null && res.getLines().getContent().size() > 0)
        {
            for (VendorOrderOnPage l : res.getLines())
            {
                MktOrder order = orderDao.get(l.getOrderPkey());
                if (order != null) l.setCode(order.getCode());
            }
        }
        return res;
    }
    
    public Result<FileInfoV3> uploadImage(MultipartFile file)
    {
//        securityContextUtil.runAsUser("admin", "Zhiyi@123456");
//        securityContextUtil.runAsUser("admin", "123456");
//        securityContextUtil.runAsAnonymous();
//        AuthenticationContext ac = SecurityContextUtil.loginAsClient();
//        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
//        param.set("file", file);
//        param.set("title", file.getName());
//        param.set("memo", "小程序上传");
        
        try
        {
            FileInfoV3 uploadFile = fileUploader.uploadFile(file.getBytes(), file.getName(), file.getName(), "小程序上传");
            return new Result<>(uploadFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.add("Content-Type", "application/x-www-form-urlencoded");
//        AuthenticationContext ac = securityContextUtil.loginAsClient("zyysc-web", "CHANGE_ME");
//        String token = ac.getAccessToken().getTokenType() + " " + ac.getAccessToken().getValue();
//        System.out.println("token: " + token);
//        FileInfoV3 exec = HttpUtil.forResult("https://cloudtest.xinanshizu.com/file/v3/image/upload", 
//            new ParameterizedTypeReference<Result<FileInfoV3>>() {})
//            .form(param)
//            .post()
////            .headers(headers)
////            .body(param)
//            .token(ac.getAccessToken().getValue())
//            .exec();
//        return fileApiV3.uploadImage(file, file.getName(), "小程序上传");
        return new Result<>();
    }
}
