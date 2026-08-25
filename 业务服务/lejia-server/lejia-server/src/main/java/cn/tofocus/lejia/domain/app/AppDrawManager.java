package cn.tofocus.lejia.domain.app;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.app.market.AppDrawMsgDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppDrawPrizeDTO;
import cn.tofocus.lejia.bean.dto.market.MktDrawPrizeOnList;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.market.MktDrawWin;
import cn.tofocus.lejia.bean.entity.market.MktPointPay;
import cn.tofocus.lejia.bean.enums.PType;
import cn.tofocus.lejia.bean.enums.SourceType;
import cn.tofocus.lejia.cache.OrderTokenMap;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktAppConfigDao;
import cn.tofocus.lejia.dao.market.MktDrawWinDao;
import cn.tofocus.lejia.domain.market.CardManager;
import cn.tofocus.lejia.domain.market.DrawManager;
import cn.tofocus.lejia.domain.market.MemberPointManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.DrawUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppDrawManager
{
    @Autowired
    private DrawManager drawManager;
    
    @Autowired
    private MemberPointManager pointManager;
    
    @Autowired
    private AppPointPayManager pointPayManager;
    
    @Autowired
    private OrderTokenMap orderTokenMap;
    
    @Autowired
    private MktDrawWinDao drawWinDao;
    
    @Autowired
    private MktAppConfigDao appConfigDao;
    
    @Autowired
    private CardManager cardManager;
    
    public AppDrawMsgDTO getDrawMessage()
    {
        AppDrawMsgDTO drawMsgDTO = new AppDrawMsgDTO();
        Integer memberPkey = MobileSession.memberPkey();
        Integer ascription = MobileSession.appid();
        drawMsgDTO.setPoints(pointManager.loadPoints(memberPkey));
        drawMsgDTO.setSingleDraw(drawManager.getDrawConf().getPoint());
        
        List<MktDrawPrizeOnList> list = drawManager.queryDrawPrize(ascription);
        List<AppDrawPrizeDTO> result = BeanUtil.beanListFrom(AppDrawPrizeDTO.class, list);
        for (AppDrawPrizeDTO dto : result)
        {
            if (dto.getPhoto() != null && dto.getPhoto().size() > 0) dto.setPhoto1(dto.getPhoto().get(0));
        }
        drawMsgDTO.setPrizeList(result);
        
        MktAppConfig mktAppConfig = appConfigDao.selectOne().eq("ascription", ascription).exec();
        if (mktAppConfig != null)
        {
            Integer cjXz = mktAppConfig.getPointsCjXz();
            Integer todayQD = pointPayManager.getTodayQD(memberPkey);
            log.info("cjXz: {}, todayQD: {}", cjXz, todayQD);
            drawMsgDTO.setSurplusNum(cjXz - todayQD);
        }
        return drawMsgDTO;
    }
    
    public AppDrawPrizeDTO draw()
    {
        Integer memberPkey = MobileSession.memberPkey();
        Integer ascription = MobileSession.appid();
        Long ll = orderTokenMap.get("draw:" + memberPkey);
        if (ll != null && System.currentTimeMillis() - ll.longValue() < 2000)
        {
            orderTokenMap.put("draw:" + memberPkey, System.currentTimeMillis());
            throw TofocusException.of(LejiaErrCode.WRONG_TIME);
        }
        
        int points = pointManager.loadPoints(memberPkey);
        MktAppConfig mktAppConfig = appConfigDao.selectOne().eq("ascription", ascription).exec();
        int singleDraw = mktAppConfig.getPointsCjUser();
        if (points < singleDraw)
        {
            throw TofocusException.of(LejiaErrCode.NO_P0INTS);
        }
        // 抽奖限制 查询 数量 是否超过次数 
        Integer pointsCjXz = mktAppConfig.getPointsCjXz();
        String formatDate = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        long count = drawWinDao.aggregation()
            .eq("member", memberPkey)
            .between("createdTime", formatDate + " 00:00:00", formatDate + " 23:59:59")
            .execCount();
        if (pointsCjXz != null && count >= pointsCjXz)
        {
            throw TofocusException.of(LejiaErrCode.DRAW_NUMBER);
        }
        List<MktDrawPrizeOnList> list = drawManager.queryDrawPrize(ascription);
        int num = DrawUtil.draw(list);
        AppDrawPrizeDTO result = BeanUtil.beanFrom(AppDrawPrizeDTO.class, list.get(num));
        
        drawWinDao.insDrawWin(memberPkey, result.getPkey(), result.getPType(), result.getDescp(), ascription);
        // 如果是积分 直接加到会员账号上
        if (result.getPType().getIndex() == 0)
        {
            MktPointPay pointsOrder = pointPayManager.createdOrder(memberPkey, singleDraw, PType.DRAW, ascription);
            pointManager.updPoint(memberPkey,
                result.getPvalue(),
                true,
                SourceType.POINTS_ACTIVITY,
                pointsOrder.getOrderNumber(),
                "积分抽奖",
                ascription);
        }
        // 如果是优惠券 直接发给用户
        if (result.getPType().getIndex() == 1)
        {
            cardManager.insAllCard(10, result.getPvalue(), memberPkey, ascription);
        }
        
        MktPointPay pointsOrder = pointPayManager.createdOrder(memberPkey, singleDraw, PType.DRAW, ascription);
        pointManager
            .updPoint(memberPkey, singleDraw, false, SourceType.POINTS_ACTIVITY, pointsOrder.getOrderNumber(), "积分抽奖", ascription);
        orderTokenMap.put("draw:" + memberPkey, System.currentTimeMillis());
        return result;
    }
    
    public Boolean insDrawAddr(Integer pkey, String addr)
    {
        MktDrawWin drawWin = drawWinDao.get(pkey);
        if (drawWin == null) throw TofocusException.of(LejiaErrCode.NOT_DRAW_RECORD);
        drawWin.setAddr(addr);
        drawWinDao.update(drawWin);
        return true;
    }
    
}
