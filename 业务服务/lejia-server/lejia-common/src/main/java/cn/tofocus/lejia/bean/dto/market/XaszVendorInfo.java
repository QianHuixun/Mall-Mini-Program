package cn.tofocus.lejia.bean.dto.market;

import javax.validation.constraints.NotBlank;

import cn.tofocus.lejia.bean.enums.v5.MerchantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 合作商户新增/修改查询入参
 * @author geshaojian
 */
@Data
public class XaszVendorInfo
{
    @Schema(description = "主键，新增时不需要传递")
    private Integer pkey;
    
    private Integer market;
    
    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "摊位号")
    private String booth;
    
    @Schema(description = "系统区域")
    private Integer areaType;
    
    @Schema(description = "系统区域")
    private String areaTypeName;
    
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String mobile;
    
    @Schema(description = "中信-身份证号码")
    private String zxIdentity;
    
    @Schema(description = "银行名称")
    private String bankname;
    
    @Schema(description = "开户人")
    private String bankuser;
    
    @Schema(description = "银行卡号")
    private String bankcard;
    
    @Schema(description = "开户支行名称")
    private String bankBranchName;
    
    @Schema(description = "开户行大额行号")
    private String bankNo;
    
    @Schema(description = "cust商户主键")
    private Integer merchant;
    
    @Schema(description = "cust商户状态")
    private MerchantStatus status;
}