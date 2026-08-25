package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.entity.member.MktMemberMsd;
import cn.tofocus.lejia.bean.entity.member.MktMemberTag;
import cn.tofocus.lejia.bean.entity.member.MktRechargeCard;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.bean.enums.RechargeStatus;
import cn.tofocus.lejia.bean.enums.member.RechargeCardType;
import cn.tofocus.lejia.bean.enums.member.TagType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktRechargeCardDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.domain.market.MktMemberMsdManager;
import cn.tofocus.lejia.exception.LejiaErrCode;

@Component
public class AppMemberMsdManager
{
    @Autowired
    private MktRechargeCardDao rechargeCardDao;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private MktMemberMsdManager memberMsdManager;
    
    public BigDecimal getBalance()
    {
        Integer memberPkey = MobileSession.memberPkey();
        // 查看是否是民生豆用户
        MktMemberMsd memberMsd = memberMsdDao.get(memberPkey);
        if (memberMsd != null)
        {
            return memberMsd.getBalance();
        }
        return BigDecimal.ZERO;
    }
    
    public Boolean rechargeCard(String cardNumber, String cardPassword, int memberPkey, String mobile,
        Integer ascription)
    {
        MktRechargeCard card = rechargeCardDao.byCardNumber(cardNumber);
        if (card == null || card.getType() != RechargeCardType.MSD)
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_NUMBER_ERROR);
        Calendar cal = Calendar.getInstance();
        if (card.getDeadline().compareTo(cal.getTime()) < 0)
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_DEADLINE_ERROR);
        if (card.getAscription() != ascription)
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_ASCRIPTION_ERROR);
        if (!card.getCardPassword().equals(cardPassword))
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_CARDPASSWORD_ERROR);
        if (!RechargeStatus.UNUSED.equals(card.getStatus()))
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_UNUSED_ERROR);
        card.setUseTime(cal.getTime());
        card.setStatus(RechargeStatus.USED);
        card.setMobile(mobile);
        rechargeCardDao.update(card);
        // 充值热力豆账户
        rechargeMsd(memberPkey, card.getCost(), card.getCardNumber(), card.getTag(), ascription);
        return true;
    }
    
    private void rechargeMsd(int memberPkey, BigDecimal amt, String cardNumber, Integer tag, Integer ascription)
    {
        // 如果tag找不到，则按没有tag处理
        Integer realTag = null;
        MktTag tagBean = tagDao.get(tag);
        if (tagBean != null)
            realTag = tag;
        // 查询原民生豆原标签
        MktMemberMsd oldBean = memberMsdDao.get(memberPkey, ascription);
        // 加余额（如果没有民生豆账户，新建一个余额为0的账户），生成充值明细
        memberMsdManager
            .updMsdBalance(memberPkey, realTag, true, amt, MsdOperationType.RECHARGE, "卡密充值", cardNumber, ascription, true);
        if (realTag != null && (oldBean == null || !Objects.equals(oldBean.getTag(), realTag)))
        {
            if (oldBean != null)
            {
                // 民生豆账户改标签
                memberMsdDao.updateTag(memberPkey, ascription, realTag);
                // 用户去掉原标签
                if (oldBean.getTag() != null)
                {
                    MktMemberTag memberOldTag = memberTagDao.get(MktMemberTag.makePkey(memberPkey, oldBean.getTag()));
                    if (memberOldTag != null)
                        memberTagDao.remove(memberOldTag);
                }
            }
            // 如果该member还没有该标签，加上
            MktMemberTag memberTag = memberTagDao.get(MktMemberTag.makePkey(memberPkey, realTag));
            if (memberTag == null)
            {
                memberTag = new MktMemberTag();
                memberTag.setPkey(memberPkey, realTag);
                memberTag.setAscription(ascription);
                memberTagDao.put(memberTag);
            }
            // 修改标签类型为热力豆标签
            if (tagBean.getType() != TagType.MSD)
            {
                tagDao.updateType(realTag, TagType.MSD);
            }
        }
    }
}
