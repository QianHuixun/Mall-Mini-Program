package cn.tofocus.lejia.api.v1.market;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.core.msgpipe.queue.MsgSenderTemplate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.Constant.SysConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.CardStatisticsInfo;
import cn.tofocus.lejia.bean.dto.market.CardUpDTO;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktCardInsDTO;
import cn.tofocus.lejia.bean.dto.market.MktCardOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberCardOnList;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberCard;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.CardStatus;
import cn.tofocus.lejia.bean.enums.CardType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.sys.AccountDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.domain.WxManager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.FileUtil;
import cn.tofocus.lejia.util.WxDataBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/v1/market/card")
@RestController
@Validated
@Slf4j
public class CardApiImpl implements CardApi
{
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private TagManager tagManager;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MsgSenderTemplate msgSenderTemplate;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Value("${zx.qingfen.ascription:13}")
    private Integer qfAscription;
    
    @Autowired
    private AccountDao accountDao;
    
    @Override
    @LogApi(operation = "新增卡券", format = "新增卡券[{entity.title}]", resultFormat = "卡券介绍: {result.content}")
    public Result<MktCardOnList> insCard(MktCardInsDTO entity)
    {
        MktCardOnList card = cardManager.insCard(entity);
        if (entity.isSendWechatMsg())
        {
            try
            {
                msgSenderTemplate.put("", "", card, new WxMsgSender(CurrentSession.ascriptionPkey()));
            }
            catch (Exception e)
            {
                log.warn("发送优惠券通知异常", e);
            }
        }
        return new Result<>(card);
    }
    
    @Override
    public Result<MktCardOnList> getCard(Integer pkey)
    {
        return new Result<>(cardManager.getCard(pkey));
    }
    
    @Override
    public Result<PageResult<MktCardOnList>> queryCard(int page, int pagesize, String title, CardType cardType,
        Boolean enabled, Boolean invalid)
    {
        return new Result<>(cardManager.queryCard(page, pagesize, title, cardType, enabled, invalid));
    }
    
    @Override
    @LogApi(operation = "卡券失效", format = "修改卡券  主键为:{pkey}")
    public Result<Boolean> invalidCard(@RequestParam(value = "pkey") Integer pkey)
    {
        return new Result<>(cardManager.invalidCard(pkey));
    }
    
    @Override
    @Operation(summary = "修改卡券", tags = ApiTags.custCard)
    @PostMapping(value = "/upd")
    @LogApi(operation = "修改卡券", format = "修改卡券  名称为:{entity.title}")
    public Result<MktCardOnList> updCard(@RequestBody CardUpDTO entity)
    {
        MktCardOnList card = cardManager.updCard(entity);
        if (entity.isSendWechatMsg())
        {
            try
            {
                msgSenderTemplate.put("", "", card, new WxMsgSender(CurrentSession.ascriptionPkey()));
            }
            catch (Exception e)
            {
                log.warn("发送优惠券通知异常", e);
            }
        }
        return new Result<>(card);
    }
    
    class WxMsgSender implements MsgListener<MktCardOnList, Boolean>
    {
        private Integer ascription;
        
        public WxMsgSender(Integer ascription)
        {
            super();
            this.ascription = ascription;
        }
        
        @Override
        public Boolean handleMessage(String pipeId, String correlationId, MktCardOnList card)
            throws Exception
        {
            String templateid = sysConfigDao.getTemplate(SysConfig.TEMPLATE_NEW_CARD, ascription);
            if (templateid == null)
                return false;
            List<Integer> tags = tagManager.getCardTags(card.getPkey().longValue());
            List<String> openids = tagManager.listMemberOpenid(ascription, tags);
            
//            JSONObject miniprogram =
//                wxManager.getMiniprogram(AccountType.USER, ascription, "pages/home/memberBenefits/index");
            
            JSONObject data = new WxDataBuilder().param("thing1")
                .value(card.getTitle())
                .param("thing2")
                .value("领券中心已上架优惠券，赶快领取吧～")
                .build();
            
            for (String openid : openids)
            {
//                wxManager.wechatSendMsgYs(templateid, openid, miniprogram, data, ascription);
                AccountEntity account = accountDao.get(ascription, AccountType.USER);
                wxManager.sendWeappSubscribeMessage(account, openid, templateid, "pages/my/coupon/coupon?pkey=" + card.getFarmer(), data);
            }
            return true;
        }
        
        @Override
        public void handleResult(String pipeId, String correlationId, Result<Boolean> result)
            throws Exception
        {
            if (!result.isSuccess())
                log.warn("发送优惠券开抢通知异常, {}", result.getMsg());
            else if (!result.getResult())
                log.warn("优惠券开抢通知的模板未配置");
        }
    }
    
    class WxMsgMemberCardSender implements MsgListener<MktMemberCard, Boolean>
    {
        @Override
        public Boolean handleMessage(String pipeId, String correlationId, MktMemberCard memberCard)
            throws Exception
        {
            Integer ascription = memberCard.getAscription();
            String templateid = sysConfigDao.getTemplate(SysConfig.TEMPLATE_NEW_MEMBERCARD, ascription);
            if (templateid == null)
                return false;
            MktMember member = memberDao.get(memberCard.getMember());
            String openid = member.getOpenid1();
//            JSONObject miniprogram = wxManager.getMiniprogram(AccountType.USER, ascription, "pages/my/coupon/coupon");
            MktCard card = cardDao.get(memberCard.getCard());
            
            JSONObject data = new WxDataBuilder().param("thing1")
                .value(card.getTitle())
                .param("time3")
                .value(DateUtil.formatDate(memberCard.getEndDate(), "yyyy年M月d日"))
                .param("thing4")
                .value("优惠券已到账，请注意查收～")
                .build();
            
//            wxManager.wechatSendMsgYs(templateid, openid, miniprogram, data, ascription);
            AccountEntity account = accountDao.get(ascription, AccountType.USER);
            wxManager.sendWeappSubscribeMessage(account, openid, templateid, "pages/my/card/index", data);
            return true;
        }
        
        @Override
        public void handleResult(String pipeId, String correlationId, Result<Boolean> result)
            throws Exception
        {
            if (!result.isSuccess())
                log.warn("发送优惠券到账通知异常, {}", result.getMsg());
            else if (!result.getResult())
                log.warn("优惠券到账通知的模板未配置");
        }
    }
    
    @Override
    @LogApi(operation = "删除卡券", format = "删除卡券")
    public Result<Boolean> delCard(Integer pkey)
    {
        return new Result<>(cardManager.delCard(pkey));
    }
    
    @Override
    @LogApi(operation = "启动卡券", format = "启动卡券")
    public Result<Boolean> startCard(Integer pkey)
    {
        return new Result<>(cardManager.enabledCard(pkey, true));
    }
    
    @Override
    @LogApi(operation = "停止卡券", format = "停止卡券")
    public Result<Boolean> stopCard(Integer pkey)
    {
        return new Result<>(cardManager.enabledCard(pkey, false));
    }
    
    @Override
    @LogApi(operation = "发放卡券", format = "发放卡券")
    public Result<Boolean> insAllCard(Integer status, Integer card, Integer member)
    {
        List<MktMemberCard> list = cardManager.insAllCard(status, card, member, CurrentSession.ascriptionPkey());
        for (MktMemberCard c : list)
        {
            try
            {
                msgSenderTemplate.put("", "", c, new WxMsgMemberCardSender());
            }
            catch (Exception e)
            {
                log.warn("发送优惠券通知异常", e);
            }
        }
        return new Result<>(list != null);
    }
    
    @PostMapping("/member/ins/all/test") //测试用,手工发优惠券
    public Result<Boolean> insAllCardTest(Integer card, String mobile, Integer num)
    {
        if (num == null)
            num = 1;
        return new Result<>(cardManager.insAllCardTest(card, mobile, num, CurrentSession.ascriptionPkey()));
    }
    
    @Operation(summary = "领券中心二维码下载", tags = ApiTags.custCard)
    @GetMapping("/down/center")
    public void downCardCenter(HttpServletRequest request, HttpServletResponse response)
    {
        FileUtil.buildExcelDocument("lqzx.jpg", "领券中心二维码.jpg", "/data/tofocus/server/zyysc", request, response);
        //		BufferedImage img = null;
        //		try {
        //			img = FileUtil.createImage("/pages/my/coupon/coupon" , 500, 500);
        //		} catch (Exception e) {
        //			e.printStackTrace();
        //		}
        //		FileUtil.buildExcelDocument("领券中心二维码", img, request, response);
    }
    
    //	@LogApi(operation = "下载卡券二维码", format = "下载卡券二维码[{pkey}]")	
    @Operation(summary = "卡券二维码下载", tags = ApiTags.custCard)
    @GetMapping("/down/code")
    public Result<Boolean> downExcel(Integer pkey, HttpServletRequest request, HttpServletResponse response)
    {
        BufferedImage img = null;
        if (pkey == null)
            throw TofocusException.of(WsaleErrCode.CAN_NOT_BE_EMPTY, "pkey");
        MktCard mktCard = cardManager.getMktCard(pkey);
        try
        {
            img = FileUtil.createImage("/pages/my/coupon/coupon?cardPkey=" + pkey, 500, 500);
            mktCard.setCardCode(img.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        FileUtil.buildExcelDocument(mktCard.getTitle(), img, request, response);
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> insCard(Integer card)
    {
        AuthenticationContext ac = SecurityContextUtil.getAuthenticationContext();
        System.out.println("ac: " + ac.getUserkey());
        return new Result<>(cardManager.insMemberCard(ac.getUserkey().intValue(), card));
        //		return new Result<>(cardManager.insMemberCard(10237, card));
    }
    
    @Override
    public Result<PageResult<MktMemberCardOnList>> queryUseCard(int page, int pagesize, String userFarmer,
        String startTime, String endTime, String st, String et, String mobile, String title, CardStatus status,
        Boolean invalid)
    {
        return new Result<>(cardManager
            .queryUseCard(page, pagesize, userFarmer, startTime, endTime, st, et, mobile, title, status, invalid));
    }
    
    @Operation(summary = "导出已使用优惠券列表", tags = ApiTags.custCard)
    @PostMapping(value = "/export/use")
    public void exportUseCard(
        @RequestParam(value = "userFarmer", required = false) @Parameter(description = "核销市场") String userFarmer,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") String startTime,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") String endTime,
        @RequestParam(value = "st", required = false) @Parameter(description = "开始时间-领取") String st,
        @RequestParam(value = "et", required = false) @Parameter(description = "结束时间-领取") String et,
        @RequestParam(value = "mobile", required = false) @Parameter(description = "手机号") String mobile,
        @RequestParam(value = "title", required = false) @Parameter(description = "卡券名称") String title,
        @RequestParam(value = "status", required = false) @Parameter(description = "卡券状态") CardStatus status,
        @RequestParam(value = "invalid", required = false) @Parameter(description = "卡券状态,false:未失效") Boolean invalid,
        HttpServletResponse response)
    {
        cardManager.exportUseCard(userFarmer, startTime, endTime, st, et, mobile, title, status, invalid, response);
    }
    
    @Override
    public Result<Boolean> setCenterCard(Integer pkey)
    {
        return new Result<>(cardManager.setCenterCard(pkey));
    }
    
    @Override
    public Result<List<DropDTO>> queryCard()
    {
        return new Result<>(cardManager.queryCard());
    }
    
    @Override
    public Result<CardStatisticsInfo> queryUseSumCard(String userFarmer, String startTime, String endTime, String st,
        String et, String mobile, String title, CardStatus status, Boolean invalid)
    {
        return new Result<>(
            cardManager.queryUseSumCard(userFarmer, startTime, endTime, st, et, mobile, title, status, invalid));
    }

    @Override
    public Result<List<DropIntegerDown>> dropMtypeName(String farmer)
    {
        List<DropIntegerDown> res = new ArrayList<>();
        Integer ascriptionPkey = CurrentSession.ascriptionPkey();
        String marketPkey = CurrentSession.marketPkey();
        
        
        DropIntegerDown d = new DropIntegerDown();
        d.setPkey(0);
        d.setName("积分商城");
        if(qfAscription.equals(ascriptionPkey))
            d.setName("滨海民生自营区");
        res.add(d);
        DropIntegerDown f1 = new DropIntegerDown();
        f1.setPkey(14);
        f1.setName("滨农优品");
        res.add(f1);
        DropIntegerDown f2 = new DropIntegerDown();
        f2.setPkey(13);
        f2.setName("预售专区");
        res.add(f2);
        DropIntegerDown f3 = new DropIntegerDown();
        f3.setPkey(1);
        f3.setName("市场商品");
//        res.add(f3);
        DropIntegerDown f4 = new DropIntegerDown();
        f4.setPkey(3);
        f4.setName("特价商品");
//        res.add(f4);
        
        
        List<DropIntegerDown> res2 = new ArrayList<>();
        res2.add(f3);
        res2.add(f4);
        
        if(marketPkey.startsWith(Constant.Operation))
        {
            System.out.println("res: " + 11);
            if(StringUtils.isNotBlank(farmer) && !farmer.startsWith(Constant.Operation))
            {
                System.out.println("res: " + 11222);
               res = res2;
            }
        }
        else
        {
            res = res2;
        }
        return new Result<>(res);
    }
    
}
