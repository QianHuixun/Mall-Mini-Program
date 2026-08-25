package cn.tofocus.lejia.domain.app;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.app.AppCourierDTO;
import cn.tofocus.lejia.bean.entity.market.MktCourier;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.cache.MobileCodeMap;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktCourierDao;
import cn.tofocus.lejia.dao.market.MktExpressDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppCourierManager
{
    
    @Autowired
    private MktCourierDao courierDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private MktExpressDao expressDao;
    
    @Resource
    private SmsConfig smsConfig;
    
    @Autowired
    private MobileCodeMap mobileMap;
    
    public AppCourierDTO getCourier()
    {
        MktCourier courier = MobileSession.courier();
        log.info("courier: {}", courier);
        if (courier == null) throw TofocusException.of(WsaleErrCode.NOTOBTAINED_USERINFO);
        Integer ascription = MobileSession.appid();
        AppCourierDTO dto = BeanUtil.beanFrom(AppCourierDTO.class, courier);
        String farmer = dto.getFarmer();
        if (farmer != null)
        {
            SysFarmer sysFarmer = farmerDao.get(farmer);
            if (sysFarmer != null) dto.setFarmerName(sysFarmer.getName());
        }
        dto.setOrderToday(expressDao.getExpressOrder(courier.getPkey(), DateUtil.formatDate(new Date(), "yyyy-MM-dd"), ascription));
        dto.setOrderHistory(expressDao.getExpressOrder(courier.getPkey(), null, ascription));
        return dto;
    }
    
    public Boolean createCaptcha(String phone)
    {
        MktCourier courier = courierDao.selectOne().eq("mobile", phone).eq("enabled", true).eq("idDel", false).exec();
        if (courier == null) throw TofocusException.of(WsaleErrCode.NOT_COURIER);
        String code = NumberUtils.createCheckCode();
        mobileMap.put(phone, code);
        System.out.println("手机验证码：" + code);
        return new SMSNotify(smsConfig).sendCode(phone, code);
    }
    
    @Transactional
    public boolean checkCaptcha(String phone, String code, String openid)
    {
        MktCourier courier = courierDao.selectOne().eq("mobile", phone).eq("enabled", true).eq("idDel", false).exec();
        if (courier == null) throw TofocusException.of(WsaleErrCode.NOT_COURIER);
        String ccode = mobileMap.get(phone);
        if (ccode == null) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        // 验证码是840727 的时候 都给通过
        if (!ccode.equals(code) && !"840727".equals(code)) throw TofocusException.of(WsaleErrCode.WRONG_CODE);
        courier.setOpenid1(openid);
        courierDao.update(courier);
        List<MktCourier> exec = courierDao.select().eq("openid1", openid).notEq("pkey", courier.getPkey()).exec();
        for (MktCourier c : exec)
        {
            c.setOpenid1(null);
        }
        courierDao.updateAll(exec);
        return true;
    }
    
    public Boolean checkLogin(String openid)
    {
        MktCourier courier = courierDao.selectOne().eq("openid1", openid).eq("enabled", true).eq("idDel", false).exec();
        return courier != null;
    }
    
}
