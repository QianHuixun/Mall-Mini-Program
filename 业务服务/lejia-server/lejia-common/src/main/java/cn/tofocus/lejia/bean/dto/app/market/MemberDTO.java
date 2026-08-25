package cn.tofocus.lejia.bean.dto.app.market;

import cn.tofocus.lejia.bean.enums.LevelType;
import lombok.Data;

@Data
public class MemberDTO {
	private Integer pkey;
	private String nickName;
	private String mobile;
	private String openid;
	private LevelType level;
	private String avatarUrl;
	private Integer gender;
	private String endDate;
}
