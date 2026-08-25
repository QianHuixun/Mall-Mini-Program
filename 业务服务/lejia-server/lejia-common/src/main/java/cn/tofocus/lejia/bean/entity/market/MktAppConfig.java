package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  mkt_app_config
* @author zdw 2020-07-20
*/

@Entity
@Data
@Table(name = "mkt_app_config")
public class MktAppConfig implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_app_config")
    @Schema(description = "pkey")
    private Integer pkey;
    
    
    @Schema(description = "积分比")
    private Integer pointsRate;
    
    @Schema(description = "价格比")
    private Integer moneyRate;
    
    @Schema(description = "积分清理日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd", timezone = "GMT+8")
    private Date pointsDate;
    
    @Schema(description = "签到积分")
    private Integer pointsQd;
    
    @Schema(description = "签到递增积分")
    private Integer pointsQdDz;
    
    @Schema(description = "签到天数上限")
    private Integer pointsQdSx;
    
    @Schema(description = "抽奖消费积分")
    private Integer pointsCjUser;
    
    @Schema(description = "抽奖限制")
    private Integer pointsCjXz;
    
    @Schema(description = "会员原价")
    private BigDecimal memberPrice;
    
    @Schema(description = "会员优惠价")
    @Column(name = "member_price_n")
    private BigDecimal memberPriceN;
    
    @Schema(description = "会员赠送积分")
    private Integer memberPoints;
    
    @Schema(description = "会员积分比例")
    private Integer memberGetPoints;
    
    @Schema(description = "会员赠送卡券")
    @Convert(converter = ListConverter.class)
    private List<Map<String, Integer>> memberCard;
    
    @Schema(description = "新人赠送卡券")
    @Convert(converter = ListConverter.class)
    private List<Map<String, Integer>> newcomerCard;
    
    @Schema(description = "联系电话")
    private String tel;
    
    @Schema(description = "退货地址")
    private String addr;
    
    @Schema(description = "微信号")
    private String wechatNum;
    
    @Schema(description = "微信二维码")
    @FileUrl
    private String wechatCode;
    
    @Schema(description = "会员办理图片1")
    @FileUrl
    private String memberPhoto1;
    
    @Schema(description = "会员办理图片2")
    @FileUrl
    private String memberPhoto2;
    
    @Schema(description = "邀请有礼图片")
    @FileUrl
    private String invitationPhoto;
    
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "版本")
    @Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}