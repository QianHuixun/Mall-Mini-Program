package cn.tofocus.lejia.dao.refund;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefund.F;
import cn.tofocus.lejia.bean.enums.RefundStatus;

@Component
@DataSourceWithFileUrl
public class MktOrderRefundDao extends JpaSpecificationDelegate<Integer, MktOrderRefund>
{
    public List<MktOrderRefund> listOrderPkey(Integer orderPkey)
    {
        return this.select().eq("orderPkey", orderPkey)
//            .notEq("status", RefundStatus.REFUND_REFUSE)
            .sort("createdTime").sort("pkey").exec();
    }
    
    public MktOrderRefund getOrderPkey(Integer orderPkey)
    {
        return this.selectOne().eq("orderPkey", orderPkey).exec();
    }
    
    public Boolean checkApplying(Integer orderPkey)
    {
        List<MktOrderRefund> list = this.select().eq("orderPkey", orderPkey).eq("status", RefundStatus.REFUND_APPLYING).exec();
        return !list.isEmpty();
    }

    public BigDecimal aggRefundGoodsAmt(Integer orderPkey, List<RefundStatus> status)
    {
        return aggRefundGoodsAmt(orderPkey, status, null);
    }

    public BigDecimal aggRefundGoodsAmt(Integer orderPkey, List<RefundStatus> status, Integer notEqPkey)
    {
        Number res = this.aggregation()
            .eq(F.orderPkey, orderPkey)
            .in(F.status, status)
            .notEq(F.pkey, notEqPkey)
            .execSum(F.refundGoodsAmt);
        return new BigDecimal(res.toString());
    }
    
    public PageResult<MktOrderRefund> queryAppOrderRefund(int page, int pagesize, Integer member, Integer ascription,
        Integer orderPkey)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("member", member)
            .eq("orderPkey", orderPkey)
            .eq("ascription", ascription)
            .sort("createdTime")
            .sort("pkey")
            .exec();
    }
    
    public List<Integer> listStatusKey(Integer orderPkey)
    {
         List<MktOrderRefund> list = this.select().eq("orderPkey", orderPkey).eq("status", RefundStatus.REFUND_FINAL).exec();
         return CollectionUtil.keyList(list);
    }

    public boolean existApplying(Integer orderPkey)
    { 
        List<RefundStatus> rs = new ArrayList<>();
        rs.add(RefundStatus.REFUND_APPLYING);
        rs.add(RefundStatus.REFUND_JD_HANDLE);
        rs.add(RefundStatus.JD_PENDING_APPROVAL);
        rs.add(RefundStatus.JD_APPROVAL_ACCEPTED);
        rs.add(RefundStatus.JD_RECEIPTED);
        rs.add(RefundStatus.JD_PENDING_CONFIRM);
        MktOrderRefund exist =
            this.selectOne().eq(F.orderPkey, orderPkey)
            .in(F.status, rs)
//            .eq(F.status, RefundStatus.REFUND_APPLYING)
            .exec();
        return exist != null;
    }
    

    public BigDecimal aggRefundPostageAmt(List<Integer> orderPkeys)
    {
        Number res = this.aggregation()
            .in(F.orderPkey, orderPkeys)
            .in(F.status, RefundStatus.refundedStatus())
            .execSum(F.refundPostage);
        return new BigDecimal(res.toString());
    }
    
    public BigDecimal aggRefundPostageAmt(Integer orderPkey)
    {
        Number res = this.aggregation()
            .eq(F.orderPkey, orderPkey)
            .in(F.status, RefundStatus.refundedStatus())
            .execSum(F.refundPostage);
        return new BigDecimal(res.toString());
    }
    
    public BigDecimal aggRefundAmt(List<Integer> orderPkeys)
    {
        Number res = this.aggregation()
            .in(F.orderPkey, orderPkeys)
            .in(F.status, RefundStatus.refundedStatus())
            .execSum(F.refundGoodsAmt);
        return new BigDecimal(res.toString());
    }
    
    public BigDecimal aggRefundAmtre(Integer orderPkey)
    {
        Number res = this.aggregation()
            .eq(F.orderPkey, orderPkey)
            .in(F.status, RefundStatus.refundedStatus())
            .execSum(F.amtre);
        return new BigDecimal(res.toString());
    }

    /**
     * 批量聚合多个订单的退款金额（REFUND_FINAL 状态的 amtre 之和），返回 orderPkey -> 退款金额。
     * 替代逐单调用 aggRefundAmtre，避免 N+1 查询；无退款单不在 map 中，调用方用 getOrDefault(pkey, ZERO)。
     */
    public Map<Integer, BigDecimal> mapAggRefundAmtre(List<Integer> orderPkeys)
    {
        Map<Integer, BigDecimal> map = new HashMap<>();
        if (orderPkeys == null || orderPkeys.isEmpty())
            return map;
        // 框架 .in() 单次参数上限 10000，按 9000 分批查询后合并
        for (List<Integer> batch : Lists.partition(orderPkeys, 9000))
        {
            Map<String, Number> raw = this.aggregation()
                .in(F.orderPkey, batch)
                .in(F.status, RefundStatus.refundedStatus())
                .execGroupBySum(F.orderPkey, F.amtre);
            for (Map.Entry<String, Number> e : raw.entrySet())
            {
                map.put(Integer.valueOf(e.getKey()), new BigDecimal(e.getValue().toString()));
            }
        }
        return map;
    }
    
    /**
     * 批量查询多个订单的退款单，返回 orderPkey -> List&lt;MktOrderRefund&gt;。
     * 内部自动分批（每批 9000），避免 .in() 超过框架参数上限 10000。
     */
    public Map<Integer, List<MktOrderRefund>> mapOrderPkeyRefunds(List<Integer> orderPkeys)
    {
        Map<Integer, List<MktOrderRefund>> map = new HashMap<>();
        if (orderPkeys == null || orderPkeys.isEmpty())
            return map;
        for (List<Integer> batch : Lists.partition(orderPkeys, 9000))
        {
            List<MktOrderRefund> list = this.select().in(F.orderPkey, batch).exec();
            for (MktOrderRefund refund : list)
                map.computeIfAbsent(refund.getOrderPkey(), k -> new ArrayList<>()).add(refund);
        }
        return map;
    }

    public MktOrderRefund byJdOrderCodeHandle(String code)
    {
        return byJdOrderCodeHandle(code, null);
    }
    
    public MktOrderRefund byJdOrderCodeHandle(String code, String outRefundNo)
    {
        return this.selectOne().eq(F.code, code).eq(F.outRefundNo, outRefundNo).exec();
    }

    public MktOrderRefund byJdOrderCode(String code)
    {
        return this.selectOne()
            .eq("code", code).exec();
    }

    public MktOrderRefund orderPkeyJdHandle(Integer orderPkey)
    {
        return this.selectOne()
            .eq("orderPkey", orderPkey)
            .eq("status", RefundStatus.REFUND_JD_HANDLE)
            .exec();
    }
    
    public MktOrderRefund byOutRefundNo(String outRefundNo)
    {
        return this.selectOne().eq("outRefundNo", outRefundNo).exec();
    }
    
    public Long countApplying(Integer memberKey)
    {
        return this.aggregation()
        .eq(F.member, memberKey)
        .in(F.status, RefundStatus.REFUND_APPLYING,
            RefundStatus.REFUND_JD_HANDLE,
            RefundStatus.JD_PENDING_APPROVAL,
            RefundStatus.JD_APPROVAL_ACCEPTED,
            RefundStatus.JD_RECEIPTED
            )
        .execCount();
    }
}
