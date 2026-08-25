package cn.tofocus.lejia.bean.entity.zx;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.common.util.Util;
import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "zx_user_info")
@FieldNameConstants(innerTypeName = "F")
public class ZxUserInfo implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "zx_user_info")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "账户类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private ZxUserType type;
    
    @Schema(description = "账户对应的主键")
    private String value;
    
    // 仅工会使用，其他类型系统带出
    @Schema(description = "账户名称")
    private String name;
    
    @Schema(description = "账户余额")
    private BigDecimal comms;
    
    @Schema(description = "工会用户可以划分给消费者钱包的金额")
    private BigDecimal tradeUnionComms;
    
    @Schema(description = "市场自动提现,true:自动提现")
    private Boolean marketAuto;
    
    @Schema(description = "商户自动提现true:自动提现")
    private Boolean vendorAuto;
    
    //@Schema(description = "中信银行审核结果")
    //@Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    //@Column(nullable = false, columnDefinition = "tinyint(4)")
    //private ZxStatus zxStatus;
    
    @Schema(description = "中信银行绑卡结果")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private ZxCardStatus cardStatus;
    
    @Schema(description = "中信银行主键")
    private String zxUserId;
    
    @Schema(description = "中信注册时间")
    private Date zxRegisterTime;
    
    @Schema(description = "中信-备注(注册和绑卡的异常存储)")
    private String zxRemark;
    
    @Schema(description = "用户类型, 1-个人 2-企业 3-个体工商户")
    private String userType;
    
    @Schema(description = "用户姓名")
    private String userNm;
    
    @Schema(description = "证件类型 01-个人身份证  02-组织机构代码  03-统一社会信用代码 07-营业执照号码")
    private String userIdType;
    
    @Schema(description = "证件号码")
    private String userIdNo;
    
    @Schema(description = "用户手机号")
    private String userPhone;
    
    @Schema(description = "企业法人姓名")
    private String corpNm;
    
    @Schema(description = "企业法人证件号码")
    private String corpIdNo;
    
    @Schema(description = "企业法人证件类型")
    private String corpIdType;
    
    @Schema(description = "开户银行联行号")
    private String panNum;
    
    @Schema(description = "银行账户名称")
    private String acctNm;

    @Schema(description = "银行证件类型")
    private String bankCardType;

    @Schema(description = "银行证件号码")
    private String bankCardNo;
    
    @Schema(description = "银行账号")
    private String pan;
    
    /*
     * 1-中信个人账户 
     * 2-中信企业账户 
     * 3-他行个人账户 
     * 4-他行企业账户
     * 5-中信个人存折（必填）
     * 6-他行个人存折（必填）
     */
    @Schema(description = "银行账户类型")
    private String acctType;
    
    @Schema(description = "银行预留手机号")
    private String bankPhone;
    
    /*
     * 与个人用户签约的电子协议版本号，通过该版本号能够确定协议的具体内容
     * 该字段在绑定个人账户时必填。
     */
    @Schema(description = "用户授权协议版本号")
    private String authProtocolVersion;
    
    /*
     * 与个人用户签约的授权交易流水号，通过该流水号应能确定电子协议版本号、签约人、签约时间
     * 该字段在绑定个人账户时必填。
     */
    @Schema(description = "用户授权协议流水号")
    private String authProtocolNo;
    
    @Schema(description = "删除标志，true：已删除")
    private Boolean delFlag;
    
    @Schema(description = "原中信银行主体主键,后续可删除")
    private String oldZxUserId;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
    public void generateAuthProtocol()
    {
        this.authProtocolVersion = "ZY00001";
        this.authProtocolNo = this.authProtocolVersion + DateUtil.formatDate(new Date(), "yyyyMMddHHmmss")
            + StringUtil.left(Util.getUUID(), 8);
    }
    
    public boolean hasChangedZxUser(ZxUserInfo that)
    {
        if (that == null)
            return true;
        if (!Objects.equals(this.userType, that.getUserType()))
            return true;
        if (!Objects.equals(this.userNm, that.getUserNm()))
            return true;
        if (!Objects.equals(this.userPhone, that.getUserPhone()))
            return true;
        if (!Objects.equals(this.userIdType, that.getUserIdType()))
            return true;
        if (!Objects.equals(this.userIdNo, that.getUserIdNo()))
            return true;
        if (!Objects.equals(this.corpNm, that.getCorpNm()))
            return true;
        if (!Objects.equals(this.corpIdNo, that.getCorpIdNo()))
            return true;
        if (!Objects.equals(this.corpIdType, that.getCorpIdType()))
            return true;
        return false;
    }
    
    public boolean hasChangedZxBank(ZxUserInfo that)
    {
        if (that == null)
            return true;
        if (!Objects.equals(this.panNum, that.getPanNum()))
            return true;
        if (!Objects.equals(this.pan, that.getPan()))
            return true;
        if (!Objects.equals(this.bankPhone, that.getBankPhone()))
            return true;
        if (!Objects.equals(this.acctType, that.getAcctType()))
            return true;
        if (!Objects.equals(this.authProtocolVersion, that.getAuthProtocolVersion()))
            return true;
        if (!Objects.equals(this.authProtocolNo, that.getAuthProtocolNo()))
            return true;
        return false;
    }
}
