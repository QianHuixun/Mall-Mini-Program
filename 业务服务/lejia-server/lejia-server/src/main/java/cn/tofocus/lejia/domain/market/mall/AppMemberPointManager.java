package cn.tofocus.lejia.domain.market.mall;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberPointDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberPointLineOnList;
import cn.tofocus.lejia.bean.dto.app.market.AppMktVendorDTO;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorPointLine;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import cn.tofocus.lejia.config.SysProConfig;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktMemberPointLineDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.MemberPointManager;
import cn.tofocus.lejia.domain.market.VendorPointManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.repository.market.MktMemberPointLineRepository;
import cn.tofocus.lejia.util.CryptStr;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppMemberPointManager
{
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktVendorFileDao vendorFileDao;
    
    @Autowired
    private MemberPointManager mpManager;
    
    @Autowired
    private VendorPointManager vpManager;
    
    @Autowired
    private MktMemberPointLineDao memberPointLineDao;
    
    @Autowired
    private MktMemberPointLineRepository mktMemberPointLineRepository;
    
    @Autowired
    private WxManager wxManager;
    
    public AppMktVendorDTO loadIndex(String eCode)
        throws Exception
    {
        AppMktVendorDTO dto = new AppMktVendorDTO();
        MktVendor vendor = vendorDao.get(Integer.parseInt(CryptStr.decryptStr(eCode, SysProConfig.VENDOR_AES_KEY)));
        vendor.setVisitCount(vendor.getVisitCount() + 1);
        vendorDao.update(vendor);
        int point = mpManager.loadPoints(MobileSession.memberPkey());
        dto.setName(vendor.getName());
        dto.setPoints(point);
        MktVendorFile file =
            vendorFileDao.selectOne().eq("vendorPkey", vendor.getPkey()).eq("type", VendorFileType.HEAD_ICON).exec();
        if (file != null) dto.setUrl(file.getUrl());
        return dto;
    }
    
    @Transactional
    public void payPoints(String eCode, int points)
    {
        MktVendor vendor = null;
        try
        {
            vendor = vendorDao.get(Integer.parseInt(CryptStr.decryptStr(eCode, SysProConfig.VENDOR_AES_KEY)));
        }
        catch (Exception e)
        {
            // TODO: handle exception
            throw TofocusException.of(LejiaErrCode.VENDOR_WRONG);
        }
        MktVendorPointLine line = vpManager.updPoint(vendor
            .getPkey(), MobileSession.memberPkey(), points, SourceType.POINTS_BUY, MobileSession.memberPkey(), MobileSession.appid());
        mpManager.updPoint(MobileSession
            .memberPkey(), points, false, SourceType.POINTS_BUY, line.getPkey() + "", vendor.getPkey() + "", MobileSession.appid());
        
//        if (vendor.getOpenid1() != null) sendNewOrderMsg(100000 + line.getPkey() + "",
//            vendor.getOpenid1(),
//            points,
//            MobileSession.member().getName());
    }
    
//    private void sendNewOrderMsg(String orderNum, String touser, int points, String memberName)
//    {
//        String templateId = "TlM1Gvf4NCbg4cozvfTI3Z9Xj438rHVcWxrndESWH9g";
//        AccountEntity accountEntity = wxManager.getAccountEntity(AccountType.VENDOR);
//        JSONObject data = new JSONObject();
//        data.put("character_string1", new JSONObject().put("value", orderNum));
//        data.put("date2", new JSONObject().put("value", DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm")));
//        data.put("thing7", new JSONObject().put("value", "积分消费"));
//        data.put("amount8", new JSONObject().put("value", points));
//        data.put("thing4", new JSONObject().put("value", memberName));
//    }
    
    public AppMemberPointDTO getPoints()
    {
        Integer memberPkey = MobileSession.memberPkey();
        AppMemberPointDTO appMemberPointDTO = new AppMemberPointDTO();
        appMemberPointDTO.setPoints(mpManager.loadPoints(memberPkey));
        Integer sumPoints = mktMemberPointLineRepository.sumPoints(memberPkey);
        appMemberPointDTO.setAccumulatedPoints(sumPoints == null ? 0 : sumPoints);
        return appMemberPointDTO;
    }
    
    public PageResult<AppMemberPointLineOnList> queryLine(Integer page, Integer pagesize, Boolean direct)
    {
        return memberPointLineDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("direct", direct)
            .eq("member", MobileSession.memberPkey())
            .sort("createdTime")
            .execDto(AppMemberPointLineOnList.class);
    }
}
