package cn.tofocus.lejia.dao.market;

import java.util.Date;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.member.MktMemberCouponLinshi;
import cn.tofocus.lejia.bean.enums.OrderStatus;
import cn.tofocus.lejia.utils.DateUtil;

@Deprecated
@Component
public class MktMemberCouponLinshiDao extends JpaSpecificationDelegate<Integer, MktMemberCouponLinshi>
{
    public Boolean byOpenid(String openid, Integer card)
    {
        long l = this.aggregation().eq("openid1", openid)
        .notEq("status", OrderStatus.UNPAID_ORDER)
        .notEq("status", OrderStatus.REFUNDED_ORDER)
        .notEq("status", OrderStatus.VOID_ORDER)
        .eq("card", card)
        .execCount();
        return l > 1;
    }
//    public MktMemberCouponLinshi byOpenid(String openid, Integer card)
//    {
//        return this.selectOne().eq("openid1", openid)
//            .notEq("status", OrderStatus.UNPAID_ORDER)
//            .notEq("status", OrderStatus.REFUNDED_ORDER)
//            .notEq("status", OrderStatus.VOID_ORDER)
//            .eq("card", card)
//            .exec();
//    }
    
    public MktMemberCouponLinshi byCode(String code)
    {
        return this.selectOne().eq("code", code)
            .exec();
    }
    
    public Integer count(Integer card)
    {
        Date date = new Date();
        long count = this.aggregation()
            .between("createdTime", DateUtil.atStartOfDay(date), DateUtil.atEndOfDay(date))
            .notEq("status", OrderStatus.UNPAID_ORDER)
            .notEq("status", OrderStatus.REFUNDED_ORDER)
            .notEq("status", OrderStatus.VOID_ORDER).eq("card", card).execCount();
        return (int)count;
    }
}
