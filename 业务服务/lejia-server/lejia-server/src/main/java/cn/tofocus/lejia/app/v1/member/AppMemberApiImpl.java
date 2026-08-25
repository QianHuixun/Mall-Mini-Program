package cn.tofocus.lejia.app.v1.member;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.thoughtworks.xstream.core.util.Base64Encoder;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.app.AppCardDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCentreDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberCentreMsdLine;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberSignOnList;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderStatusNum;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberCardOnList;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDrawOnList;
import cn.tofocus.lejia.bean.dto.app.wxPay.WxPayData;
import cn.tofocus.lejia.bean.dto.market.MktGiftOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberOnList;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.enums.MemberPType;
import cn.tofocus.lejia.bean.enums.PayType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.domain.app.AppMemberManager;
import cn.tofocus.lejia.domain.app.AppVendorManager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.GiftManager;
import cn.tofocus.lejia.domain.market.mall.AppConfigManager;
import cn.tofocus.lejia.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/app/market/lm/member")
@RestController
public class AppMemberApiImpl implements AppMemberApi
{
    
    @Autowired
    private AppMemberManager appMemberManager;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private AppConfigManager appConfigManager;
    
    @Autowired
    private GiftManager giftManager;
    
    @Autowired
    private AppVendorManager vendorManager;
    
    @Override
    public Result<WxPayData> beforePay(BigDecimal amt, MemberPType memberPType, PayType payType)
    {
        return new Result<>(appMemberManager.beforeMemberPay(amt, memberPType, payType));
    }
    
    @Override
    public Result<MktMemberOnList> getMember()
    {
        Integer memberPkey = MobileSession.memberPkey();
        log.info("app-getMember-pkey: {}", memberPkey);
        return new Result<>(appMemberManager.getMktMemberOnList(memberPkey));
    }
    
    @Override
    public Result<AppMemberSignOnList> queryMemberPoints(String signMonth)
    {
        return new Result<>(appMemberManager.queryMemberPoints(signMonth));
    }
    
    @Override
    public Result<Boolean> insMemberPoints()
    {
        return new Result<>(appMemberManager.insMemberPoints());
    }
    
    @Override
    public Result<AppMemberCentreDTO> getMemberCentre()
    {
        return new Result<>(appMemberManager.getMemberCentre());
    }
    
    @Override
    public Result<AppOrderStatusNum> getOrderStatusNum()
    {
        return new Result<>(appMemberManager.getOrderStatusNum());
    }
    
    @Override
    public Result<List<MktAppMemberCardOnList>> getMemberCard()
    {
        return new Result<>(appMemberManager.getMemberCard());
    }
    

    @Override
    public Result<Boolean> upd(String photo, String name)
    {
        return new Result<>(appMemberManager.upd(photo, name));
    }
    
    @Override
    public Result<PageResult<MktAppMemberDrawOnList>> getMemberDraw(Integer page, Integer pagesize)
    {
        return new Result<>(appMemberManager.getMemberDraw(page, pagesize));
    }
    
    @Override
    public Result<Boolean> insCard(Integer card)
    {
        Integer memberPkey = MobileSession.memberPkey();
        log.info("app-insCard-memberPkey: {}", memberPkey);
        return new Result<>(cardManager.insMemberCard(memberPkey, card));
    }

    @Override
    public Result<PageResult<AppCardDTO>> getCenterCard(int page, int pagesize, Integer cardPkey)
    {
        return new Result<>(cardManager.getCenterCard(page, pagesize, cardPkey));
    }
    
    @Override
    public Result<Boolean> ins(String custCard, String custName, String accountBank)
    {
        return new Result<>(appMemberManager.ins(custCard, custName, accountBank));
    }
    
    @Override
    public Result<Map<String, Object>> getMemberPrice()
    {
        String farmerPkey = MobileSession.farmerPkey();
        MktAppConfig config = appConfigManager.getAppConfig();
        Map<String, Object> map = new HashMap<>();
        map.put("memberPrice", config.getMemberPrice());
        map.put("memberPriceN", config.getMemberPriceN());
        map.put("memberPhoto1", config.getMemberPhoto1());
        map.put("memberPhoto3", config.getMemberPhoto2());
        if ((Constant.Operation + MobileSession.appid()).equals(farmerPkey))
            map.put("memberPhoto2", null);
        else
            map.put("memberPhoto2", MobileSession.farmer().getConfig().getMemberPhoto());
        return new Result<>(map);
    }
    
    @Operation(summary = "获取优惠券二维码", tags = AppTags.mobileMember)
    @GetMapping("/down/code")
    public String downExcel(HttpServletRequest request, HttpServletResponse response,
        @RequestParam(value = "cardNumber") @Parameter(description = "核销码") String cardNumber)
    {
        BufferedImage img = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try
        {
            img = FileUtil.createImage(cardNumber, 500, 500);
            response.setContentType("image/png");
            ImageIO.write(img, "png", os);
            boolean flag = ImageIO.write(img, "GIF", os);
            System.out.println("flag: " + flag);
            byte[] b = os.toByteArray();
            Base64Encoder base = new Base64Encoder();
            String res = base.encode(b);
            return res;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    
    @Override
    public Result<List<MktAppMemberDetailsDTO>> getTjrList()
    {
        return new Result<>(appMemberManager.listTjr());
    }
    
    @Override
    public Result<PageResult<MktGiftOnList>> giftList(int page, int pagesize, Integer status)
    {
        return new Result<>(giftManager.listByMember(page, pagesize, status));
    }
    
    @Override
    public Result<String> getInvitationPhoto()
    {
        MktAppConfig config = appConfigManager.getAppConfig();
        return new Result<>(config.getInvitationPhoto());
    }

    @Override
    public Result<Boolean> logOut()
    {
        return new Result<>(appMemberManager.logOut());
    }

    @Override
    public Result<Boolean> cancelLogOut()
    {
        return new Result<>(appMemberManager.cancelLogOut());
    }
    
    @Operation(summary = "商城端上传图片", tags = AppTags.mobileVendor)
    @PostMapping("/uploadImage")
    public Result<FileInfoV3> uploadImage(@RequestPart("file") MultipartFile file)
    {
        return vendorManager.uploadImage(file);
    }

    @Override
    public Result<PageResult<AppMemberCentreMsdLine>> queryMsdLine(int page, int pagesize)
    {
        return new Result<>(appMemberManager.queryMsdLine(page, pagesize));
    }
    
}
