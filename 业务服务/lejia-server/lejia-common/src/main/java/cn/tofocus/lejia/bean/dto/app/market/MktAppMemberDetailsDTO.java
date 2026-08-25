package cn.tofocus.lejia.bean.dto.app.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktAppMemberDetailsDTO
{
    /**
     * pkey
     */
    @Id
    private Integer pkey;
    
    /**
    * 名称
    */
    @Schema(description = "名称")
    private String name;
    
    /**
    * 手机
    */
    @Schema(description = "手机", required = true)
    private String mobile;
    
    /**
     * 推荐人
     */
    @Schema(description = "推荐人")
    private Integer tjr;
    
    private Integer tjv;
    
    @Schema(description = "用户来源")
    private String source;
    /*
     * 推荐佣金
     */
    private BigDecimal tjComm;
    
    /**
    * unionid
    */
    @Schema(description = "unionid")
    private String unionid;
    
    /**
    * openid1
    */
    @Schema(description = "openid1")
    private String openid1;
    
    /**
    * openid2
    */
    @Schema(description = "openid2", hidden = true)
    private String openid2;
    
    /**
    * 密码
    */
    @Schema(description = "密码")
    private String password;
    
    /**
    * 等级
    */
    @Schema(description = "等级")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private LevelType level;
    
    /**
    * 头像
    */
    @Schema(description = "头像")
    private String photo;
    
    /**
    * 身份证
    */
    @Schema(description = "身份证")
    private String idcard;
    
    /**
    * 性别
    */
    @Schema(description = "性别")
    @Column(nullable = false)
    private Integer sex;
    
    /**
    * 出生日期
    */
    @Schema(description = "出生日期")
    private String birth;
    
    /**
    * 上次登陆时间
    */
    @Schema(description = "上次登陆时间")
    private Date loginTime;
    
    /**
    * 登陆类型 小程序/公众号/app
    */
    @Schema(description = "登陆类型 小程序/公众号/app")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private LoginType loginType;
    
    /**
    * 登陆市场
    */
    @Schema(description = "登陆市场")
    private String lastFarmer;
    
    /**
    * 启用标志
    */
    @Schema(description = "启用标志", hidden = true)
    private Boolean enabled;
    
}
