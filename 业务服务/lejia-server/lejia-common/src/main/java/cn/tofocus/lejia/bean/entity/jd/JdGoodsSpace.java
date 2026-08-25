package cn.tofocus.lejia.bean.entity.jd;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "jd_goods_space")
@FieldNameConstants(innerTypeName = "F")
public class JdGoodsSpace implements HasPkey<Long>
{
    @Id
    @Schema(description = "pkey")
    private Long pkey;
    
    @Schema(description = "规格")
    private String spaceValue1;
    
    @Schema(description = "规格")
    private String spaceValue2;
    
    @Schema(description = "规格")
    private String spaceValue3;
    
    @Schema(description = "规格")
    private String spaceValue4;
    
    @Schema(description = "规格")
    private String spaceValue5;
    
    @Schema(description = "规格")
    private String spaceValue6;
    
    @Schema(description = "规格")
    private String spaceValue7;
    
    @Schema(description = "规格")
    private String spaceValue8;
    
    @Schema(description = "规格")
    private String spaceValue9;
    
    @Schema(description = "规格")
    private String spaceValue10;
    
    @Schema(description = "商品池")
    private String bizPoolId;
    
    /**
     * 返回规格拼接名称
     */
    public String getSpaceName()
    {
        String split = ",";
        StringBuilder sb = new StringBuilder();
        if (this.spaceValue1 != null)
            sb.append(this.spaceValue1).append(split);
        if (this.spaceValue2 != null)
            sb.append(this.spaceValue2).append(split);
        if (this.spaceValue3 != null)
            sb.append(this.spaceValue3).append(split);
        if (this.spaceValue4 != null)
            sb.append(this.spaceValue4).append(split);
        if (this.spaceValue5 != null)
            sb.append(this.spaceValue5).append(split);
        if (this.spaceValue6 != null)
            sb.append(this.spaceValue6).append(split);
        if (this.spaceValue7 != null)
            sb.append(this.spaceValue7).append(split);
        if (this.spaceValue8 != null)
            sb.append(this.spaceValue8).append(split);
        if (this.spaceValue9 != null)
            sb.append(this.spaceValue9).append(split);
        if (this.spaceValue10 != null)
            sb.append(this.spaceValue10).append(split);
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
