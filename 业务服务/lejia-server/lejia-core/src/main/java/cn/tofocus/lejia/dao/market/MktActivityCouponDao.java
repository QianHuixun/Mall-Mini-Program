package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktActivityCoupon;
import cn.tofocus.lejia.bean.entity.market.MktActivityCoupon.F;
import cn.tofocus.lejia.bean.entity.market.MktActivityCouponPkey;
import cn.tofocus.lejia.bean.enums.CouponType;

@Component
public class MktActivityCouponDao extends JpaSpecificationDelegate<MktActivityCouponPkey, MktActivityCoupon>
{
    public void removeAllByActivity(Integer activity)
    {
        this.select().strict(true).eq(F.activity, activity).del();
    }
    
    public boolean existByActivity(Integer activity)
    {
        return this.selectOne().eq(F.activity, activity).exec() != null;
    }
    
    public boolean existByActivityNotEqCoupon(Integer activity, CouponType couponType, Integer coupon)
    {
        return this.selectOne()
            .eq(F.activity, activity)
            .notEq(F.couponType, couponType)
            .notEq(F.coupon, coupon)
            .exec() != null;
    }
    
    public boolean existByCoupon(CouponType couponType, Integer coupon)
    {
        return this.selectOne().eq(F.couponType, couponType).eq(F.coupon, coupon).exec() != null;
    }
    
    public List<MktActivityCoupon> listByCoupon(CouponType couponType, Integer coupon)
    {
        return this.select().eq(F.couponType, couponType).eq(F.coupon, coupon).exec();
    }

    public List<MktActivityCoupon> listByActivity(Integer activity)
    {
        return this.select().eq(F.activity, activity).exec();
    }
    
    public Integer getSumNum(Integer activity)
    {
        Number sum = this.aggregation().eq("activity", activity).execSum("num");
        if(sum == null)
            return 0;
        return sum.intValue();
    }
    
    public MktActivityCoupon byActivityCoupon(CouponType couponType, Integer coupon)
    {
        return this.selectOne().eq(F.couponType, couponType).eq(F.coupon, coupon).exec();
    }
    
}
