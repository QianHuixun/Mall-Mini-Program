package cn.tofocus.lejia.bean.entity.vendor;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.SettlementMethodType;
import cn.tofocus.lejia.bean.enums.v2.VendorZxStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "mkt_vendor")
@FieldNameConstants(innerTypeName = "F")
public class MktVendor implements HasPkey<Integer>
{

    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_vendor")
    @Schema(description = "pkey")
    private Integer pkey;

    @Schema(description = "商户名称")
    private String name;
    
    @Schema(description = "商户展示名称")
    private String displayName;
    
    @Schema(description = "商户展示名称开关")
    private Boolean displayFlag;
    
    @Schema(description = "摊位号")
    private String booth;

    @Schema(description = "负责人")
    private String manager;

    @Schema(description = "地址")
    private String addr;

    @Schema(description = "手机号码")
    private String mobile;
    
    @Schema(description = "是否可以清分")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private Boolean isClear;
    
    @Schema(description = "中信银行审核结果")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private VendorZxStatus zxStatus;
    
    @Schema(description = "中信银行主键")
    private String zxUserId;
    
    @Schema(description = "中信注册时间")
    private Date zxRegisterTime;
    
    @Schema(description = "中信-身份证号码")
    private String zxIdentity;
    
    @Schema(description = "中信-备注(注册和绑卡的异常存储)")
    private String zxRemark;
    
    
    @Schema(description = "unionid")
    private String unionid;

    @Schema(description = "openid1")
    private String openid1;

    /**
     * openid2
     */
    @Schema(description = "openid2")
    private String openid2;

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

    @Schema(description = "银行卡号")
    private String bankcard;

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
     * 访问数量
     */
    @Schema(description = "访问数量")
    private Integer visitCount;

    /**
     * 经营范围
     */
    @Schema(description = "经营范围")
    private String businessScope;

    /**
     * 佣金费率配置
     */
    @Schema(description = "佣金费率配置")
    private BigDecimal commissionRate;

    /**
     * 佣金费率更新时间
     */
    @Schema(description = "佣金费率更新时间")
    private Date rateUpdateTime;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 商户简介
     */
    @Schema(description = "商户简介")
    private String shortContent;

    /**
     * 启用标志
     */
    @Schema(description = "启用标志")
    private Boolean enabled;

    /**
     * 是否已删除
     */
    @Schema(description = "是否已删除")
    private Boolean idDel;

    
    @Schema(description = "cust商户主键")
    private Integer merchant;
    /**
     * 市场
     */
    @Schema(description = "市场")
    private String farmer;

    /**
     * 公司
     */
    @Schema(description = "公司")
    private String company;

    @Schema(description = "修改时间")
    @LastModifiedDate
    private Date updateTime;

    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;

    @Schema(description = "建档员")
    @CreatedBy
    private Integer updateBy;

    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "商户结算方式")
    private SettlementMethodType settlementMethod;

    @Schema(description = "归属主键")
    private Integer ascription;
}