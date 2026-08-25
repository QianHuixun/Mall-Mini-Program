package cn.tofocus.lejia.bean.entity.goods;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  预售商品扩展信息表
* @author zdw 2022-07-19
*/

@Entity
@Data
@Table(name = "mkt_goods_presale")
public class MktGoodsPresale implements HasPkey<Integer>
{
    
    @Id
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    @Schema(description = "开始日期", required = true)
    private Date startDate;
    
    @Schema(description = "结算日期", required = true)
    private Date endDate;
    
    @Schema(description = "归属主键", required = true)
    private Integer ascription;
    
}