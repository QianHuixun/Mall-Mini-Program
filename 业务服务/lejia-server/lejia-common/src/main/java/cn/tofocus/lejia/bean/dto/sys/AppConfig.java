package cn.tofocus.lejia.bean.dto.sys;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.entity.sys.SysFarmerTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppConfig
{
    @Schema(description = "pkey", required = true)
    private Integer pkey;
    
    /**
    * 积分比
    */
    @Schema(description = "积分比", required = true)
    private Integer pointsRate;
    
    @Schema(description = "价格比")
    private Integer moneyRate;
    
    /**
    * 积分清理日期
    */
    @Schema(description = "积分清理日期", required = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd", timezone = "GMT+8")
    private Date pointsDate;
    
    /**
    * 签到积分
    */
    @Schema(description = "签到积分", required = true)
    private Integer pointsQd;
    
    /**
    * 签到递增积分
    */
    @Schema(description = "签到递增积分", required = true)
    private Integer pointsQdDz;
    
    /**
    * 签到天数上限
    */
    @Schema(description = "签到天数上限", required = true)
    private Integer pointsQdSx;
    
    /**
    * 抽奖消费积分
    */
    @Schema(description = "抽奖消费积分", required = true)
    private Integer pointsCjUser;
    
    /**
    * 抽奖限制
    */
    @Schema(description = "抽奖限制", required = true)
    private Integer pointsCjXz;
    
    /**
    * 会员原价
    */
    @Schema(description = "会员原价", required = true)
    private BigDecimal memberPrice;
    
    /**
    * 会员优惠价
    */
    @Schema(description = "会员优惠价", required = true)
    private BigDecimal memberPriceN;
    
    /**
    * 会员赠送积分
    */
    @Schema(description = "会员赠送积分", required = true)
    private Integer memberPoints;
    
    /**
    * 会员积分比例
    */
    @Schema(description = "会员积分比例", required = true)
    private Integer memberGetPoints;
    
    /**
    * 会员赠送卡券
    */
    @Schema(description = "会员赠送卡券", required = false)
    private List<Map<String, Integer>> memberCard;
    
    @Schema(description = "新人赠送卡券", required = false)
    private List<Map<String, Integer>> newcomerCard;
    
    /**
    * 联系电话
    */
    @Schema(description = "联系电话", required = true)
    private String tel;

    @Schema(description = "微信客服企业ID") //来自SysFarmerConfig
    private String customerServiceId;
    
    @Schema(description = "客服链接") //来自SysFarmerConfig
    private String customerServiceLink;
    
    @Schema(description = "营业时间") //来自SysFarmerTime
    private List<SysFarmerTime> times;
    
    /**
    * 退货地址
    */
    @Schema(description = "退货地址", required = true)
    private String addr;
    
    @Schema(description = "微信号", required = false)
    private String wechatNum;
    
    @Schema(description = "微信二维码", required = false)
    private String wechatCode;
    
    @Schema(description = "会员办理图片1", required = false)
    private String memberPhoto1;
    
    @Schema(description = "会员办理图片2", required = false)
    private String memberPhoto2;
    
    @Schema(description = "邀请有礼图片", required = false)
    private String invitationPhoto;
    
    /**
    * 最后更新时间
    */
    @Schema(description = "最后更新时间", required = true)
    private Date updateTime;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间", required = true)
    private Date createdTime;
    
    /**
    * 建档员
    */
    @Schema(description = "建档员", required = true)
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
