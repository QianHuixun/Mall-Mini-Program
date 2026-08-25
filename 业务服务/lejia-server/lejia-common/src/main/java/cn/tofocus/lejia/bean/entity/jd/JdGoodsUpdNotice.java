package cn.tofocus.lejia.bean.entity.jd;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.jd.JdGoodsUpdType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 京东商品变更通知记录
 * @author czy
 * @version [版本号, 2026/4/7]
 */
@Data
@Entity
@Schema(description = "京东商品变更通知记录")
@Table(name = "jd_goods_upd_notice")
@FieldNameConstants(innerTypeName = "F")
public class JdGoodsUpdNotice implements HasPkey<Long>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "jd_goods_upd_notice")
    private Long pkey;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "变更类型")
    private JdGoodsUpdType type;
    
    @Column
    @Schema(description = "京东skuid")
    private Long jdGoods;
    
    @Size(max = 200)
    @Column(length = 200)
    @Schema(description = "说明")
    private String description;
    
    @Column
    @Schema(description = "市场")
    private String farmer;
    
    @Column(updatable = false)
    @CreatedDate
    @Schema(description = "创建时间")
    private Date createdTime;
    
    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
    
    public static JdGoodsUpdNotice priceOf(JdGoods jdGoods)
    {
        if (jdGoods == null)
            return null;
        JdGoodsUpdNotice notice = new JdGoodsUpdNotice();
        notice.setType(JdGoodsUpdType.PRICE);
        notice.setJdGoods(jdGoods.getPkey());
        notice.setFarmer(jdGoods.getFarmer());
        notice.setAscription(jdGoods.getAscription());
        return notice;
    }

    public static JdGoodsUpdNotice saleStateOf(JdGoods jdGoods)
    {
        if (jdGoods == null)
            return null;
        JdGoodsUpdNotice notice = new JdGoodsUpdNotice();
        notice.setType(JdGoodsUpdType.SALE_STATE);
        notice.setJdGoods(jdGoods.getPkey());
        notice.setFarmer(jdGoods.getFarmer());
        notice.setAscription(jdGoods.getAscription());
        return notice;
    }

    public static JdGoodsUpdNotice lowestBuyOf(String farmer, Integer ascription)
    {
        JdGoodsUpdNotice notice = new JdGoodsUpdNotice();
        notice.setType(JdGoodsUpdType.LOWEST_BUY);
        notice.setFarmer(farmer);
        notice.setAscription(ascription);
        return notice;
    }
}
