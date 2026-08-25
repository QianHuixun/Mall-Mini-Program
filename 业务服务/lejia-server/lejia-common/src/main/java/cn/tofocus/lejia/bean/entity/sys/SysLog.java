package cn.tofocus.lejia.bean.entity.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * sys_log
 * 
 * @author lai 2020-06-15
 */

@Entity
@Data
@Table(name = "sys_log")
public class SysLog implements HasPkey<Long> {

	@Id
	@AutoRedisID(domain = "wsale", sequence = "sys_log")
	@Schema(description = "编号")
	private Long pkey;

	@Schema(description = "用户主键")
	private Integer userPkey;

	@Schema(description = "用户ID")
	@Column(length = 40)
	private String userId;

	@Schema(description = "用户名称")
	@Column(length = 100)
	private String userName;

	@Schema(description = "手机号码")
	@Column(length = 40)
	private String mobile;

	@Schema(description = "应用ID")
	@Column(length = 40)
	private String appId;

	@Schema(description = "远端地址")
	@Column(length = 20)
	private String remoteAddress;

	@Schema(description = "操作")
	@Column(length = 20)
	private String operation;

	@Schema(description = "开始时间")
	private Date beginTime;

	@Schema(description = "处理用时")
	private Integer procMillisecond;

	@Schema(description = "是否成功")
	private Boolean success;

	@Schema(description = "操作内容")
	@Column(length = 200)
	private String content;

	@Schema(description = "结果")
	@Column(length = 200)
	private String result;

	@Schema(description = "市场")
	private String market;

	@Schema(description = "公司")
	private String company;
	
    @Schema(description = "归属主键")
    private Integer ascription;
}