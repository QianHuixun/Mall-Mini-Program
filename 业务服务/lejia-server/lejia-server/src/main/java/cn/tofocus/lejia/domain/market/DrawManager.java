package cn.tofocus.lejia.domain.market;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktDrawConfOnList;
import cn.tofocus.lejia.bean.dto.market.MktDrawPrizeOnList;
import cn.tofocus.lejia.bean.dto.market.MktDrawWinOnList;
import cn.tofocus.lejia.bean.entity.market.MktAppConfig;
import cn.tofocus.lejia.bean.entity.market.MktDrawConf;
import cn.tofocus.lejia.bean.entity.market.MktDrawPrize;
import cn.tofocus.lejia.bean.entity.market.MktDrawWin;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.enums.PrizeStatus;
import cn.tofocus.lejia.bean.enums.PrizeType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktAppConfigDao;
import cn.tofocus.lejia.dao.market.MktDrawConfDao;
import cn.tofocus.lejia.dao.market.MktDrawPrizeDao;
import cn.tofocus.lejia.dao.market.MktDrawWinDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class DrawManager
{
    @Autowired
    private MktDrawConfDao drawConfDao;
    
    @Autowired
    private MktDrawPrizeDao drawPrizeDao;
    
    @Autowired
    private MktDrawWinDao drawWinDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktAppConfigDao appConfigDao;
    
    // 获取规则设置
    public MktDrawConfOnList getDrawConf()
    {
        MktAppConfig config = appConfigDao.selectOne().eq("ascription", CurrentSession.ascriptionPkey()).exec();
        MktDrawConfOnList r = new MktDrawConfOnList();
        r.setPoint(config.getPointsCjUser());
        return r;
    }
    
    // 修改规则设置
    public MktDrawConfOnList updDrawConf(Integer pkey, Integer point)
    {
        MktDrawConf drawConf = drawConfDao.selectOne().eq("pkey", pkey).eq("enabled", true).exec();
        if (drawConf == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        drawConf.setPoint(point);
        MktDrawConf update = drawConfDao.update(drawConf);
        MktAppConfig mktAppConfig = appConfigDao.selectOne().eq("ascription", CurrentSession.ascriptionPkey()).exec();
        if (mktAppConfig != null)
        {
            mktAppConfig.setPointsCjUser(point);
            appConfigDao.update(mktAppConfig);
        }
        return BeanUtil.beanFrom(MktDrawConfOnList.class, update);
    }
    
    // 获取礼品配置
    public List<MktDrawPrizeOnList> queryDrawPrize(Integer ascription)
    {
        List<MktDrawPrize> exec = drawPrizeDao.select().eq("ascription", ascription).exec();
        return BeanUtil.beanListFrom(MktDrawPrizeOnList.class, exec);
    }
    
    // 修改礼品配置
    public MktDrawPrizeOnList updDrawPrize(MktDrawPrizeOnList entity)
    {
        MktDrawPrize drawPrize = drawPrizeDao.get(entity.getPkey());
        BeanUtils.copyProperties(entity, drawPrize);
        MktDrawPrize update = drawPrizeDao.update(drawPrize);
        return BeanUtil.beanFrom(MktDrawPrizeOnList.class, update);
    }
    
    // 获取中奖记录
    public PageResult<MktDrawWinOnList> queryDrawWin(int page, int pagesize, PrizeStatus status)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        PageResult<MktDrawWinOnList> result =
            BeanUtil.beanPageFrom(MktDrawWinOnList.class, drawWinDao.queryDrawWin(page, pagesize, status, ascription));
        List<Integer> memberList = new ArrayList<>();
        List<MktDrawPrize> dpExec = drawPrizeDao.select().eq("ascription", ascription).exec();
        for (MktDrawWinOnList dw : result.getContent())
        {
            for (MktDrawPrize dp : dpExec)
            {
                if (dw.getPrize().intValue() == dp.getPkey().intValue()) dw.setName(dp.getName());
            }
            memberList.add(dw.getMember());
        }
        if (!memberList.isEmpty())
        {
            List<MktMember> exec = memberDao.select().eq("ascription", ascription).eq("enabled", true).exec();
            for (MktDrawWinOnList dw : result.getContent())
            {
                for (MktMember m : exec)
                {
                    if (dw.getMember().equals(m.getPkey())) dw.setMemberName(m.getName());
                }
            }
        }
        return result;
    }
    
    // 获取中奖记录次数
    public List<Map<String, Object>> queryNumDrawWin()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        // 中奖人次
        List<MktDrawWin> winMemberNumList =
            drawWinDao.select().eq("ascription", ascription).notEq("pType", PrizeType.THANK_PRIZE).exec();
        // 抽奖次数
        List<MktDrawWin> lotteryNumList = drawWinDao.select().eq("ascription", ascription).exec();
        // 抽奖人数
        int num = (int)drawWinDao.aggregation().eq("ascription", ascription).execCountDistinct("member");
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> winMap = new HashMap<>();
        winMap.put("title", "中奖人次");
        winMap.put("num", winMemberNumList.size());
        result.add(winMap);
        Map<String, Object> lotteryMap = new HashMap<>();
        lotteryMap.put("title", "抽奖次数");
        lotteryMap.put("num", lotteryNumList.size());
        result.add(lotteryMap);
        Map<String, Object> lotteryMemberMap = new HashMap<>();
        lotteryMemberMap.put("title", "抽奖人数");
        lotteryMemberMap.put("num", num);
        result.add(lotteryMemberMap);
        return result;
    }
    
    // 设置奖品已发货
    public MktDrawWinOnList updDrawWin(Integer pkey, String logistics, String express)
    {
        MktDrawWin drawWin = drawWinDao.get(pkey);
        drawWin.setLogistics(logistics);
        drawWin.setExpress(express);
        drawWin.setStatus(PrizeStatus.ISSUED);
        drawWin.setSendTime(new Date());
        MktDrawWin update = drawWinDao.update(drawWin);
        return BeanUtil.beanFrom(MktDrawWinOnList.class, update);
    }
}
