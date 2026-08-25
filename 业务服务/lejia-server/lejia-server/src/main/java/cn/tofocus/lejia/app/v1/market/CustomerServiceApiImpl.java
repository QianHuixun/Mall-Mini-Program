package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.CustomerServiceInfo;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerConfig;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import cn.tofocus.lejia.bean.enums.OrderOir;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktAppConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysFarmerTimeDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/app/market/customerService")
@RestController
public class CustomerServiceApiImpl
{
    @Autowired
    private MktAppConfigDao mktAppConfigDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private SysFarmerConfigDao sysFarmerConfigDao;
    
    @Autowired
    private SysFarmerTimeDao sysFarmerTimeDao;
    
    @Operation(summary = "获取客服信息", tags = AppTags.mobileCustomerService)
    @PostMapping("/getInfo")
    public Result<CustomerServiceInfo> getInfo(@RequestParam(value = "type") OrderOir type)
    {
        Integer appid = MobileSession.appid();
        String farmerPkey = MobileSession.farmerPkey();
        if (appid == null || farmerPkey == null)
            throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        String pkey = null;
        CustomerServiceInfo info = new CustomerServiceInfo();
        if (OrderOir.SELF_EMPLOYED.equals(type))
        {
            pkey = Constant.Operation + appid;
            MktAppConfig appConfig = mktAppConfigDao.get(appid);
            if (appConfig != null)
                info.setTel(appConfig.getTel());
        }
        else
        {
            pkey = farmerPkey;
            SysFarmer farmer = sysFarmerDao.get(pkey);
            info.setTel(farmer.getTel());
        }
        
        SysFarmerConfig config = sysFarmerConfigDao.get(pkey);
        if (config != null)
        {
            //客服链接
            info.setCustomerServiceId(config.getCustomerServiceId());
            info.setCustomerServiceLink(config.getCustomerServiceLink());
            //营业日期
            boolean[] days = new boolean[7];
            days[0] = config.getMonday() == null ? false : config.getMonday();
            days[1] = config.getTuesday() == null ? false : config.getTuesday();
            days[2] = config.getWednesday() == null ? false : config.getWednesday();
            days[3] = config.getThursday() == null ? false : config.getThursday();
            days[4] = config.getFriday() == null ? false : config.getFriday();
            days[5] = config.getSaturday() == null ? false : config.getSaturday();
            days[6] = config.getSunday() == null ? false : config.getSunday();
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (int i = 0; i < 7; i++)
            {
                String str = makeWeekDay(days, i);
                if (str != null)
                {
                    if (sb.length() > 0)
                        sb.append(",");
                    sb.append(str);
                    count++;
                }
            }
            if (count == 7)
                info.setDays("周一至周日");
            else
                info.setDays(sb.toString());
        }
        
        //营业时间
        List<SysFarmerTime> listTime = sysFarmerTimeDao.listTime(pkey, appid);
        info.setTimes(listTime);
        return new Result<>(info);
    }
    
    private String makeWeekDay(boolean[] days, int i)
    {
        boolean b = days[i];
        if (b)
        {
            if (i == 0)
                return "周一";
            else if (i == 1)
                return "周二";
            else if (i == 2)
                return "周三";
            else if (i == 3)
                return "周四";
            else if (i == 4)
                return "周五";
            else if (i == 5)
                return "周六";
            else
                return "周日";
        }
        else
            return null;
    }
}
