package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktGwc;
import cn.tofocus.lejia.bean.entity.market.MktGwc.F;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.repository.market.MktGwcRepository;

@Component
public class MktGwcDao extends JpaSpecificationDelegate<Integer, MktGwc>
{
    @Autowired
    MktGwcRepository gwcRepository;

    public Integer countAll(Integer memberPkey, String farmerPkey, Integer ascription)
    {
        Integer total = 0 ;
        Integer marketTotal = gwcRepository.countByMemberAndFarmer(memberPkey, farmerPkey);
        Integer pvMarketTotal = gwcRepository.countByMemberAndFarmer(memberPkey, (Constant.Operation + ascription));
        total = marketTotal + pvMarketTotal;
        return  total;
    }
    
    public int sumNum(Integer goodsPkey, Integer memberPkey, String farmerPkey, Integer ascription)
    {
        return this.aggregation()
            .eq(F.goods, goodsPkey)
            .eq(F.member, memberPkey)
            .eq(F.farmer, farmerPkey)
            .eq(F.ascription, ascription)
            .execSum(F.num)
            .intValue();
    }

    public List<MktGwc> listGwc(List<Integer> gwcKeys)
    {
        List<MktGwc> res = this.select().in("pkey", gwcKeys.toArray()).exec();
        return res;
    }
    
    public MktGwc getJdGwcMember(long skuId, Integer memberKey)
    {
        return this.selectOne().eq(F.member, memberKey).eq(F.skuId, skuId).eq(F.isJd, true).exec();
    }
} 
