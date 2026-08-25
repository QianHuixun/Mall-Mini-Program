package cn.tofocus.lejia.bean.entity.member;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.LoginType;
import cn.tofocus.lejia.bean.enums.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name="mkt_member")
@FieldNameConstants(innerTypeName = "F")
public class MktMember implements HasPkey<Integer> 
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_member")
	@Schema(description = "pkey")
    private Integer pkey;

	@Schema(description = "名称")
    private String name;
	
    @Schema(description = "状态 正常/注销中/已注销")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private MemberStatus status;

    @Schema(description = "提交注销时间")
    private Date logOutTime;
    
	@Schema(description = "手机")
    private String mobile;
	
    @Schema(description = "推荐人")
    private Integer tjr;
    
    @Schema(description = "推荐商户")
    private Integer tjv;
    
    @Schema(description = "推荐时间")
    private Date tjvTime;
    
    @Schema(description = "用户来源")
    private String source;
    
    @Schema(description = "是否是活动(比如 工会用户), true:是")
    private Boolean isActivity;

	@Schema(description = "unionid")
    private String unionid;

	@Schema(description = "openid1")
    private String openid1;

	@Schema(description = "openid2")
    private String openid2;

	@Schema(description = "密码")
    private String password;

	@Schema(description = "等级")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private LevelType level;

	@Schema(description = "头像")
	@FileUrl
    private String photo;

	@Schema(description = "身份证")
    private String idcard;

	@Schema(description = "性别")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private Integer sex;

	@Schema(description = "出生日期")
    private String birth;

	@Schema(description = "上次登陆时间")
    private Date loginTime;

	@Schema(description = "登陆类型 小程序/公众号/app")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private LoginType loginType;

	@Schema(description = "登陆市场")
    private String lastFarmer;
	
	@Schema(description = "到期日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
	private Date endDate;

    @Schema(description = "最近消费时间")
    @Column
    private Date lastConsumeTime;

    @Schema(description = "最近消费市场")
    @Column
    private String lastConsumeFarmer;

	@Schema(description = "地区")
	private String area;
	
	@Schema(description = "提现银行卡")
	private String custCard;
	
	@Schema(description = "提现银行卡用户名")
	private String custName;
	
	@Schema(description = "提现银行卡 开户行")
	private String accountBank;
	
	@Schema(description = "备注")
	private String remark;
    
	@Schema(description = "启用标志")
    private Boolean enabled;

	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}