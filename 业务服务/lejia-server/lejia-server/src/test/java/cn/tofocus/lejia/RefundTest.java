package cn.tofocus.lejia;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import com.alibaba.excel.util.DateUtils;

import cn.tofocus.common.notify.SMSNotify;
import cn.tofocus.common.notify.config.SmsConfig;
import cn.tofocus.core.security.AuthenticationContext;
import cn.tofocus.core.security.SecurityContextUtil;
import cn.tofocus.lejia.bean.dto.refund.RefundOnLine;
import cn.tofocus.lejia.bean.dto.refund.RefundOrderOnInfo;
import cn.tofocus.lejia.bean.dto.refund.RefundUpdOnInfo;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderLine;
import cn.tofocus.lejia.bean.entity.member.MktMemberCouponLinshi;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.bean.enums.RefundStatus;
import cn.tofocus.lejia.bean.enums.RefundType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktMemberCouponLinshiDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.domain.OrderRefundManager;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.OrderManager;
import cn.tofocus.lejia.domain.pay.ChinaUmsRefundManager;
import cn.tofocus.lejia.util.NumberUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class RefundTest
{
    @Autowired
    private OrderRefundManager manager;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private NumberUtils numberUtils;
    
    @Autowired
    private MktMemberCouponLinshiDao memberCouponLinshiDao;
    
    @Autowired
    private CardManager cardManager;
    
    @Autowired
    private ChinaUmsRefundManager chinaUmsRefundManager;
    
    @Autowired
    private OrderManager orderManager;
    
//    @Autowired
//    private SmsConfig smsConfig;
    
    @Test
    public void test1()
    {
        System.out.println("AAA");
        RefundOrderOnInfo info = new RefundOrderOnInfo();
        info.setPkey(5173);
        info.setReason("理由");
        List<String> photo = new ArrayList<>();
        photo.add("sadasdas");
        info.setPhoto(photo);
        List<RefundOnLine> lines = new ArrayList<>();
        RefundOnLine ol1 = new RefundOnLine();
        RefundOnLine ol2 = new RefundOnLine();
        ol1.setNum(1);
        ol1.setRefundAmt(new BigDecimal("11"));
        ol1.setPkey(12889);
        ol2.setNum(1);
        ol2.setRefundAmt(new BigDecimal("5"));
        ol2.setPkey(12891);
        lines.add(ol1);
        lines.add(ol2);
        info.setLines(lines);
        Integer applyForOrderRefund = manager.applyForOrderRefund(info, RefundStatus.REFUND_APPLYING, RefundType.REFUND_MEMBER);
        log.info("结束: {}", applyForOrderRefund);
    }
    
    @Test
    public void test3()
    {
        RefundUpdOnInfo info = new RefundUpdOnInfo();
        info.setRefundPkey(1);
        List<RefundOnLine> lines = new ArrayList<>();
        RefundOnLine ol1 = new RefundOnLine();
        RefundOnLine ol2 = new RefundOnLine();
        ol1.setNum(null);
        ol1.setRefundAmt(new BigDecimal("19"));
        ol1.setPkey(1);
        ol2.setNum(null); 
        ol2.setRefundAmt(new BigDecimal("6"));
        ol2.setPkey(2);
        lines.add(ol1);
        lines.add(ol2);
        info.setLines(lines);
        Boolean updRefundLine = manager.updRefundLine(info);
        log.info("结束: {}", updRefundLine);
    }
    
    @Test
    public void test2()
    {
        List<MktOrder> list = orderDao.select()
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER)
            .isNotNull("cardAmt")
            .gt("cardAmt", 0)
            .gt("createdTime", "2024-01-01")
            .exec();
        for (MktOrder o : list)
        {
            manager.assembleOrderLine(o.getPkey(), o.getCardAmt());
        }
    }
    
    @Test
    public void test4()
    {
        String payNumber = numberUtils.createOrderNumber();
        payNumber = "95" + payNumber;
        MktMemberCouponLinshi mcl = new MktMemberCouponLinshi();
        mcl.setCode(payNumber);
        mcl.setOpenid1("ovehA5SkSQayrnXpqhe1d3uatQcI");
        mcl.setAscription(MobileSession.appid());
        mcl.setStatus(OrderStatus.UNPAID_ORDER);
        memberCouponLinshiDao.add(mcl);
    }
    
    @Autowired
    private SecurityContextUtil securityContextUtil;
    @Test
    public void test5()
    {
//        String payNumber = "9591220324522475";
//        cardManager.insMemberCardLinshi(payNumber);
//        cardManager.testAddMemberCard(612, 2044, null);
//        cardManager.testAddMemberCard(611, 2044, null);
//        AuthenticationContext loginAsClient = securityContextUtil.loginAsClient("zyysc-server", "CHANGE_ME");
//        OAuth2AccessToken accessToken = loginAsClient.getAccessToken();
//        
//        System.out.println("accessToken.getValue(): " + accessToken.getValue());
//        System.out.println("accessToken.getExpiration(): " + DateUtils.format(accessToken.getExpiration()));
        
        String boxPassword = "123465";
        // 短信内容 已经 短信模板ID  临时门锁ID  16304453
        List<String> params = new ArrayList<>();
        params.add("家和菜-东屿农贸市场");
        params.add("8月16日晚上场");
        params.add("V999");
        params.add(boxPassword);
        params.add("18867777246");
        params.add("鹿城区东屿路66号 东屿农贸市场3楼   \nhttps://j.map.baidu.com/5c/-Qzi");
//        params.add("鹿城区东屿路66号 东屿农贸市场3楼                                                https://j.map.baidu.com/5c/-Qzi");
        params.add("店门口与地下均有停车场");
//        new SMSNotify(smsConfig).sendNotify("15825605939", params, "TDVGPrkepo2d");
    }
    
    @Test
    public void test6()
    {
        String outRefundNo = numberUtils.createRefundOrderNumber();
        BigDecimal refund = new BigDecimal("1200");
        chinaUmsRefundManager.chinaUmsRefund("3EY591170726164849", outRefundNo, refund);
    }
}
