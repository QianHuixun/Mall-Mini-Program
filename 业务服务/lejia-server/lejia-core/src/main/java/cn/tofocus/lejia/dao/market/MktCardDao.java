package cn.tofocus.lejia.dao.market;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.ConditionBuilder;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.MktCardOnList;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktCard.F;
import cn.tofocus.lejia.bean.enums.CardType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.Constant;
import static cn.tofocus.core.query.exp.ExpUtil.f;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.repository.market.MktCardRepository;

@Component
public class MktCardDao extends JpaSpecificationDelegate<Integer, MktCard>
{
    @Autowired
    private MktCardRepository repository;
    
    public Integer getCardNum(Integer memberPkey)
    {
        return repository.getCardNum(memberPkey);
    }
    
    public PageResult<MktCardOnList> queryCard(int page, int pagesize, String title, CardType cardType, Boolean enabled,
        Boolean invalid)
    {
        SelectPageBuilder<Integer, MktCard> builder = selectPage().page(page)
            .pagesize(pagesize)
            .in("cardType", CardType.CARD_CENTER, CardType.MANUALLY_ISSUE)
            .eq("farmer", CurrentSession.marketPkey())
            .eq("ascription", CurrentSession.ascriptionPkey())
            .eq("idDel", false)
            .sort("pkey", true);
        if (cardType != null) builder.eq("cardType", cardType);
        if (enabled != null) builder.eq("enabled", enabled);
        if (invalid != null) builder.eq("invalid", invalid);
        if (StringUtils.isNotBlank(title)) builder.like("title", title);
        return builder.execDto(MktCardOnList.class);
    }
    
    /**
     * 列表查询
     * @param farmer    市场pkey
     * @param title		名称
     * @param cardType  优惠券类型
     * @param enabled   是否启用
     * @param pkeyList  列表
     * @return          结果
     */
    public List<MktCard> queryCard(String farmer, String title, CardType cardType, Boolean enabled,
        List<Integer> pkeyList, Integer ascription, List<Long> cardPkeys)
    {
        Object[] pkeys = null;
        if(pkeyList != null)
        {
            pkeys = pkeyList.toArray();
        }
        String date = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        // @formatter:off
        ConditionBuilder<SelectBuilder<Integer,MktCard>> builder = this.select()
        .and()
            .eq("cardType", cardType)
            .eq("enabled", enabled)
            .like("title", title)
            .eq("invalid", false)
            .gt("count", f("issuedNum"))
            .in("pkey", pkeys)
            .or()
                .and()
                    .ge("endDate", date)
                    .le("startDate", date)
                .close()
                .isNull("endDate")
            .close()
            .or()
                .and()
                    .eq("farmer", (Constant.Operation + ascription))
                    .eq("userFarmer", farmer)
                .close()
                .and()
                    .eq("farmer", (Constant.Operation + ascription))
                    .isNull("userFarmer")
                .close()
                .eq("farmer", farmer)
            .close()
        .close();
        // @formatter:on
       ConditionBuilder<SelectBuilder<Integer, MktCard>> or = builder.or()
            .eq("visibleRange", MemberVisibleRange.ALL)
            .isNull("visibleRange");
        if(!cardPkeys.isEmpty())
        {
            or = or
            .and()
                .eq("visibleRange", MemberVisibleRange.TAG)
                .in("pkey", cardPkeys)
            .close();
        }
        builder = or.close();
        
        return builder.done().exec();
    }
    
    public MktCard getCard(Integer pkey)
    {
        return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
    }
    
    public List<MktCard> listCards(CardType cardType, Boolean enabled)
    {
        Date date = DateUtil.atStartOfToday();
        SelectBuilder<Integer, MktCard> builder =
            select().eq("idDel", false).eq("enabled", enabled).eq("cardType", cardType).eq("invalid", false);
        builder.eq("farmer", CurrentSession.marketPkey()).eq("ascription", CurrentSession.ascriptionPkey());
        SelectBuilder<Integer, MktCard> done =
            builder.or().ge("endDate", DateUtil.formatDate(date, "yyyy-MM-dd")).isNotNull("effective").close().done();
        return done.exec();
    }
    
    public Map<Integer,MktCard> mapCard(List<Integer> keys)
    {
        Map<Integer,MktCard> res = new HashMap<>();
        if(keys == null || keys.isEmpty())
            return res;
        List<MktCard> exec = this.select().in("pkey", keys.toArray()).exec();
        exec.forEach(e -> res.put(e.getPkey(), e));
        return res;
    }
    
    public Boolean checkInvalid(Integer pkey)
    {
        MktCard card = this.get(pkey);
        if(card == null)
            return false;
        return card.getInvalid();
    }
    
    public PageResult<MktCard> queryGoods(List<String> keys, Integer page, Integer pagesize, 
        Boolean sort)
    {
        return this.selectPage()
        .page(page)
        .pagesize(pagesize)
        .in("pkey", keys)
        .sort("cost", sort)
        .exec();
    }
    
    public List<Integer> listPkeys(Integer ascription, String farmer, String title)
    {
        return this.select()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .like(F.title, title)
            .execDto(F.pkey, Integer.class);
    }

    public void updIssuedNum(Integer pkey, Integer issuedNum)
    {
        this.select().strict(true).eq(F.pkey, pkey).update(F.issuedNum, issuedNum);
    }
    
}
