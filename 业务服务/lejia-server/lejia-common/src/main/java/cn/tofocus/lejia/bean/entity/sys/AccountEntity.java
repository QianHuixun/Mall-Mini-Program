package cn.tofocus.lejia.bean.entity.sys;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "wx_account")
@FieldNameConstants(innerTypeName = "F")
public class AccountEntity implements HasPkey<Integer>
{

	@Id
	@AutoRedisID(domain = "zyysc", sequence = "wx_account")
	private Integer pkey;

	/**
	 * 公众帐号名称
	 */
	@Schema(description = "公众帐号名称")
	@Column(nullable = false)
	private String accountName;

	/**
	 * 公众帐号TOKEN
	 */
	@Schema(description = "公众帐号TOKEN")
	@Column(nullable = false)
	private String accountToken;

	/**
	 * 公众微信号
	 */
	@Schema(description = "公众微信号")
	@Column(nullable = false)
	private String accountNumber;

	/**
	 * 原始ID
	 */
	@Schema(description = "原始ID")
	@Column(nullable = false)
	private String accountId;

	/**
	 * 公众帐号类型
	 */
	@Schema(description = "公众帐号类型")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
	private AccountType accountType;

	/**
	 * 电子邮箱
	 */
	@Schema(description = "电子邮箱")
	private String accountEmail;

	/**
	 * 公众帐号描述
	 */
	@Schema(description = "公众帐号描述")
	private String accountDesc;

	/**
	 * 公众帐号APPID
	 */
	@Schema(description = "公众帐号APPID")
	@Column(nullable = false)
	private String accountAppid;

	/**
	 * 公众帐号APPSECRET
	 */
	@Schema(description = "公众帐号APPSECRET")
	@Column(nullable = false)
	private String accountAppsecret;

	/**
	 * ACCESS_TOKEN
	 */
	@Schema(description = "ACCESS_TOKEN")
	private String accessToken;

	/**
	 * TOKEN获取时间
	 */
	@Schema(description = "TOKEN获取时间")
	@LastModifiedDate
	private Date accessTime;

	@Schema(description = "屏蔽版本号")
	@Column(length = 50)
	private String shieldVersion;

	// 英文逗号隔开
	// 如果屏蔽版本号一致，仅显示此处允许市场；否则显示除了此处允许市场以外的市场
	@Schema(description = "屏蔽后允许主键")
	@Column(length = 100)
	private String shieldAllowedPkey;
	
	@Schema(description = "小程序模板ID")
	private String templateId;

	/**
	 * 版本
	 */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
	private Integer rowVersion;
	
    @Schema(description = "归属主键")
    private Integer ascription;
}
