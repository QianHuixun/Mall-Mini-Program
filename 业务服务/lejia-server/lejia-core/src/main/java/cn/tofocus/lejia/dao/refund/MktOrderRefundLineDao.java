package cn.tofocus.lejia.dao.refund;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine;
import cn.tofocus.lejia.bean.entity.refund.MktOrderRefundLine.F;

@Component
public class MktOrderRefundLineDao extends JpaSpecificationDelegate<Integer, MktOrderRefundLine>
{
    public Map<Integer, MktOrderRefundLine> mapRefundPkey(Integer refundPkey)
    {
        List<MktOrderRefundLine> list = this.select().eq("refundPkey", refundPkey).exec();
        Map<Integer, MktOrderRefundLine> res = new HashMap<>();
        list.forEach(e -> res.put(e.getPkey(), e));
        return res;
    }
    
    public Map<Integer, MktOrderRefundLine> mapOrderLinePkey(Integer refundPkey)
    {
        List<MktOrderRefundLine> list = this.select().eq("refundPkey", refundPkey).exec();
        Map<Integer, MktOrderRefundLine> res = new HashMap<>();
        list.forEach(e -> res.put(e.getOrderLinePkey(), e));
        return res;
    }
    
    public List<MktOrderRefundLine> listRefundPkey(Integer refundPkey)
    {
        return this.select().eq("refundPkey", refundPkey).exec();
    }
    
    public MktOrderRefundLine getOrderLinePkey(Integer orderLinePkey)
    {
        return this.selectOne().eq("orderLinePkey", orderLinePkey).exec();
    }
    
    public List<MktOrderRefundLine> listOrderLinePkey(Integer orderLinePkey)
    {
        return this.select().eq("orderLinePkey", orderLinePkey).exec();
    }
    
    public int sumNumByOrderLinePkey(Integer orderLinePkey)
    {
        return this.aggregation().eq(F.orderLinePkey, orderLinePkey).execSum(F.refundNum).intValue();
    }
    
    public BigDecimal sumAmtByOrderLinePkey(Integer orderLinePkey)
    {
        return new BigDecimal(this.aggregation().eq(F.orderLinePkey, orderLinePkey).execSum(F.refundAmt).toString());
    }
    
    public void removeRefundPkey(Integer refundPkey)
    {
        List<MktOrderRefundLine> list = this.select().eq("refundPkey", refundPkey).exec();
        this.removeAll(list);
    }
    
    public List<MktOrderRefundLine> listRefundPkeys(List<Integer> refundPkeyList)
    {
        return this.select().in("refundPkey", refundPkeyList).exec();
    }
    
    public Map<Integer, BigDecimal> aggLinePkeyAmt(List<Integer> refundPkeyList)
    {
        List<MktOrderRefundLine> exec = this.select().in("refundPkey", refundPkeyList).exec();
        Map<Integer, BigDecimal> res = new HashMap<>();
        exec.forEach(e -> {
            if (!res.containsKey(e.getOrderLinePkey()))
                res.put(e.getOrderLinePkey(), BigDecimal.ZERO);
            res.put(e.getOrderLinePkey(), res.get(e.getOrderLinePkey()).add(e.getRefundAmt()));
        });
        return res;
    }
    
    public Map<Integer, Integer> aggLinePkeyPoint(List<Integer> refundPkeyList)
    {
        List<MktOrderRefundLine> exec = this.select().in("refundPkey", refundPkeyList).exec();
        Map<Integer, Integer> res = new HashMap<>();
        exec.forEach(e -> {
            if (!res.containsKey(e.getOrderLinePkey()))
                res.put(e.getOrderLinePkey(), 0);
            if (e.getRefundPoint() != null)
                res.put(e.getOrderLinePkey(), res.get(e.getOrderLinePkey()) + e.getRefundPoint());
        });
        return res;
    }
    
    public Map<Long, BigDecimal> aggLineGoodsAmt(List<Integer> refundPkeyList)
    {
        List<MktOrderRefundLine> exec = this.select().in("refundPkey", refundPkeyList).exec();
        Map<Long, BigDecimal> res = new HashMap<>();
        exec.forEach(e -> {
            if (!res.containsKey(e.getGoods()))
                res.put(e.getGoods(), BigDecimal.ZERO);
            res.put(e.getGoods(), res.get(e.getGoods()).add(e.getRefundAmt()));
        });
        return res;
    }
    
    public BigDecimal aggSumRefundJd(Integer refundPkey)
    {
        List<MktOrderRefundLine> list = this.select().eq("refundPkey", refundPkey).exec();
        BigDecimal res = BigDecimal.ZERO;
        for(MktOrderRefundLine orl : list)
        {
            res = res.add(orl.getRefundJd());
        }
        return res;
    }
}
