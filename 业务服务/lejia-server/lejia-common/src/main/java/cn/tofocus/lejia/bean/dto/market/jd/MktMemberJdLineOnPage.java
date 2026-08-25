package cn.tofocus.lejia.bean.dto.market.jd;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.common.data.Amt;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(innerTypeName = "F")
public class MktMemberJdLineOnPage
{
    @Schema(description = "主键")
    private Long pkey;
    
    @Schema(description = "用户")
    private Integer member;
    
    @Schema(description = "会员名称")
    private String name;
    
    @Schema(description = "会员手机号")
    private String mobile;
    
    @Schema(description = "标签主键")
    private Integer tag;
    
    @Schema(description = "标签名称")
    @JoinProperty(dataQuery = "mktTagDao", from = "tag", propertyName = "name")
    private String tagName;
    
    /**
     * 加减标志 true:加 false:减
     */
    @Schema(description = "加减标志")
    private Boolean direct;
    
    @Schema(description = "操作金额")
    private BigDecimal amt;
    
    @Schema(description = "交易金额")
    public String getAmtStr()
    {
        if (direct == null || amt == null)
            return null;
        return direct ? "+" + amt.toPlainString() : "-" + amt.toPlainString();
    }
    
    @Amt
    @Schema(description = "余额")
    private BigDecimal balance;
    
    @Schema(description = "操作类型")
    private MsdOperationType operationType;
    
    @Schema(description = "操作类型")
    @JoinEnum(from = "operationType")
    private String operationTypeName;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
