package cn.tofocus.lejia.bean.entity.market;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "mkt_supply")
public class MktSupply implements HasPkey<Integer>, Cloneable
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_supply")
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "商品pkey")
    private Integer good;
    
    @Schema(description = "商品属性：积分/市场/会员/特价/分享/砍价/团购/预售")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private MType mType;
    
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "商户结算方式")
    private SettlementMethodType settlementMethod;
    
    @Schema(description = "规格")
    private String space;
    
    @Schema(description = "供应商户pkey")
    private Integer vendor;
    
    @Schema(description = "采购价")
    private BigDecimal purchasingPrice;
    
    @Schema(description = "佣金费率")
    private BigDecimal commissionRate1;
    
    @Schema(description = "佣金费率")
    private BigDecimal commissionRate2;
    
    @Schema(description = "派送顺序")
    private Integer sort;
    
    @Schema(description = "是否启用")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean enabled;
    
    @Schema(description = "是否轮到自动采购")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean flag;
    
    @Schema(description = "市场pkey")
    private String farmer;
    
    @Schema(description = "公司pkey")
    private String company;
    
    /**
     * 重写深拷贝
     * @return  深拷贝对象
     * @throws CloneNotSupportedException 类型不支持异常
     */
    @Override
    public MktSupply clone()
        throws CloneNotSupportedException
    {
        return (MktSupply)super.clone();
    }
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}