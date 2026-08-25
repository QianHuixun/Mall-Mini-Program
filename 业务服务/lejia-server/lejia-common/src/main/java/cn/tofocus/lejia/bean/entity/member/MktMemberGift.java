package cn.tofocus.lejia.bean.entity.member;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.lejia.bean.enums.CouponExpireChoose;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  礼券明细
* @author zdw 2020-11-09
*/

@Entity
@Data
@Table(name = "mkt_member_gift")
@FieldNameConstants(innerTypeName = "F")
public class MktMemberGift implements HasPkey<Integer>
{
    
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "mkt_member_gift")
    @Schema(description = "pkey")
    private Integer pkey;

    // 已写定时任务，过期会改状态
    @Schema(description = "状态 初始/已使用/已过期")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CardStatus status;
    
    @Schema(description = "用户")
    @Column(name = "member_key")
    private Integer member;
    
    @Schema(description = "订单pkey")
    private Integer orderPkey;
    
    @Schema(description = "卡券编号")
    private String cardNumber;

    @Schema(description = "礼品券")
    private Integer gift;

    // 为空表示非活动发放
    @Schema(description = "卡券活动")
    private Integer activity;
    
    @Schema(description = "商品")
    private Integer goods;
    
    @Schema(description = "规格")
    private Integer space;
    
    @Schema(description = "有效期类型")
    @Column(columnDefinition = "tinyint(4)")
    private CouponExpireChoose expireChoose;
    
    @Schema(description = "开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "使用市场")
    private String userFarmer;
    
    @Schema(description = "使用商户")
    private Integer userVendor;
    
    @Schema(description = "使用日期")
    private Date userTime;

    @Schema(description = "是否失效,false:未失效")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean invalid;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}