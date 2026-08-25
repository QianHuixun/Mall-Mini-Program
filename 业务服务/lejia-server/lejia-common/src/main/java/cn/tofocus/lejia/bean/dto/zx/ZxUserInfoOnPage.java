package cn.tofocus.lejia.bean.dto.zx;

import java.math.BigDecimal;

import javax.persistence.Column;

import cn.tofocus.common.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import cn.tofocus.core.json.MaskBankCard;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ZxUserInfoOnPage
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ZxUserType type;
    
    @Schema(description = "类型名称")
    @JoinEnum(from = "type")
    private String typeName;
    
    @Schema(description = "账户对应的主键")
    private String value;
    
    // 工会直接表里带出，其他类型关联查询带出
    @Schema(description = "账户名称")
    private String name;
    
    @Schema(description = "账户余额")
    private BigDecimal comms;
    
    @Schema(description = "市场自动提现,true:自动提现")
    private Boolean marketAuto;
    
    @Schema(description = "商户自动提现true:自动提现")
    private Boolean vendorAuto;

    @Schema(description = "中信银行主键")
    @JsonIgnore
    private String zxUserId;

    @Schema(description = "是否注册")
    public boolean getRegistered()
    {
        return StringUtil.isNotBlank(zxUserId);
    }
    
    @Schema(description = "银行账户类型, 1-个人 2-企业 3-个体工商户")
    private String userType;
    
    @Schema(description = "用户手机号")
    private String userPhone;
    
    @MaskBankCard
    @Schema(description = "银行账号")
    private String pan;
    
    @Schema(description = "开户银行联行号")
    private String panNum;

    @Schema(description = "删除标志，true：已删除")
    @JsonIgnore
    private Boolean delFlag;
}
