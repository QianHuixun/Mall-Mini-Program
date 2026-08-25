package cn.tofocus.lejia.dao.market;

import cn.tofocus.db.SelectBuilder;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktSupply;
import cn.tofocus.lejia.bean.enums.MType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 商品供应库(MktSupply) 封装JPA的dao层
 * 
 * @author geshaojian
 * @since 2021-09-17 22:40:38
 */
@Component
@Getter
public class MktSupplyDao extends JpaSpecificationDelegate<Integer, MktSupply>
{
    
    public List<MktSupply> listSupply(Boolean enabled, String farmer, List<Integer> goodsPkeys, MType mType)
    {
        SelectBuilder<Integer,MktSupply> builder = this.select()
        .eq("enabled", enabled)
        .eq("farmer", farmer)
        .in("good", goodsPkeys)
        .notEq("mType", MType.INTEGRAL_GOODS);
        if(mType != null && 
            (mType.equals(MType.MARKET_GOODS) || mType.equals(MType.MEMBER_GOODS)))
        {
            builder.in("mType", MType.MARKET_GOODS, MType.MEMBER_GOODS);
        }
        else
            builder.eq("mType", mType);
        return builder.exec();
    }
    
    public MktSupply getSupply(Integer vendor, Integer space)
    {
        return this.selectOne().eq("vendor", vendor).eq("space", space).exec();
    }
    
}