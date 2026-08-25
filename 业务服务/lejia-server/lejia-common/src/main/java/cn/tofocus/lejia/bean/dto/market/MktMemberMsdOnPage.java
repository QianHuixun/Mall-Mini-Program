package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;

import cn.tofocus.common.data.Amt;
import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants(innerTypeName = "F")
public class MktMemberMsdOnPage
{
    @Schema(description = "用户主键")
    private Integer pkey;
    
    @Schema(description = "用户手机号")
    private String mobile;
    
    @Schema(description = "标签主键")
    private Integer tag;
    
    @Schema(description = "标签名称")
    @JoinProperty(dataQuery = "mktTagDao", from = "tag", propertyName = "name")
    private String tagName;

    @Amt
    @Schema(description = "余额")
    private BigDecimal balance;
}
