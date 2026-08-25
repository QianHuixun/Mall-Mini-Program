package cn.tofocus.lejia.bean.dto.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorBigData;
import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 合作商户新增/修改查询入参
 * @author geshaojian
 */
@Data
public class MktVendorDTO
{
    /**
     * 主键
     */
    @Schema(description = "主键，新增时不需要传递")
    private Integer pkey;
    
    /**
     * 市场主键
     */
    @Schema(description = "市场主键")
    @NotNull(message = "市场主键不能为空")
    private String farmer;
    
    /**
     * 商户名称
     */
    @Schema(description = "商户名称", required = true)
    @NotBlank(message = "商户名称不能为空")
    private String name;
    
    @Schema(description = "商户展示名称")
    private String displayName;
    
    @Schema(description = "摊位号")
    private String booth;
    
    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    @NotBlank(message = "手机号码不能为空")
    private String mobile;
    
    @Schema(description = "中信银行审核结果")
    private VendorZxStatus zxStatus;
    
    public String getZxStatusName()
    {
        if(zxStatus != null)
            return zxStatus.getName();
        return "";
    }
    
    @Schema(description = "中信-身份证号码")
    private String zxIdentity;
    
    /**
     * 经营范围
     */
    @Schema(description = "经营范围")
    @NotEmpty(message = "经营范围不能为空")
    private List<Integer> businessScopes;
    
    @Schema(description = "经营范围中文，仅仅查询详情get接口显示用")
    private String businessScopesName;
    
    /**
     * 开户行（银行名称）
     */
    @Schema(description = "银行名称")
    private String bankname;
    
    /**
     * 开户人
     */
    @Schema(description = "开户人")
    private String bankuser;
    
    /**
     * 银行卡号
     */
    @Schema(description = "银行卡号")
    private String bankcard;
    
    /**
     * 开户支行名称
     */
    @Schema(description = "开户支行名称")
    private String bankBranchName;
    
    /**
     * 开户行大额行号
     */
    @Schema(description = "开户行大额行号")
    private String bankNo;
    
    @Schema(description = "银行卡绑定手机")
    private String bankuserMoblie;
    
    /**
     * 佣金费率配置
     */
    @Schema(description = "佣金费率配置")
    private BigDecimal commissionRate;
    
    /**
     * 地址
     */
    @Schema(description = "地址")
    private String addr;
    
    @Schema(description = "佣金更新时间")
    private Date rateUpdateTime;
    
    @Schema(description = "商户简介")
    @Size(max = 50, message = "商户简介最多50字")
    private String shortContent;
    
    @Schema(description = "头像、视频、个性宣传")
    private List<MktVendorFileDTO> files;
    
    @Schema(description = "风采展示详情内容")
    private MktVendorBigData mktVendorBigData;
}