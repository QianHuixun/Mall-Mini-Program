package cn.tofocus.lejia.bean.entity.goods;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.CouponExpireChoose;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.GiftType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

/**
*  礼品券商品扩展数据
* @author zdw 2022-03-14
*/

@Entity
@Data
@Table(name = "mkt_goods_gift")
@FieldNameConstants(innerTypeName = "F")
public class MktGoodsGift implements HasPkey<Integer>
{
    
    @Id
    @Schema(description = "pkey", required = false)
    @AutoRedisID(domain = "zyysc", sequence = "mkt_goods_gift")
    private Integer pkey;

    @Schema(description = "goods")
    private Integer goods;
    
    @Schema(description = "标题")
    @Column(length = 100)
    private String title;
    
    @Schema(description = "介绍")
    @Column(length = 2000)
    private String content;

    @Schema(description = "图片")
    @Column(length = 300)
    @FileUrl
    private String picture;
    
    @Schema(description = "领取方式")
    @Column(columnDefinition = "tinyint(4)")
    private GiftType giftType;

    // 暂未使用
    @Schema(description = "领券码")
    @Column(length = 100)
    private String giftCode;
    
    @Schema(description = "启用标志")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean enabled;
    
    @Schema(description = "是否失效,false:未失效")
    @Column(columnDefinition = "tinyint(4)")
    private Boolean invalid;
    
    @Schema(description = "有效期类型", required = false)
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private CouponExpireChoose expireChoose;
    
    @Schema(description = "使用市场", required = false)
    private String userFarmer;
    
    @Schema(description = "使用商户", required = false)
    private Integer userVendor;
    
    // 积分商城商品礼券暂未生效
    @Schema(description = "有效期(天)")
    private Integer effective;
    
    @Schema(description = "开始日期", required = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期", required = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    // null表示不限量
    @Schema(description = "优惠券数量")
    private Integer count;
    
    @Schema(description = "已发放数量")
    private Integer issuedNum = 0;
    
    @Schema(description = "已使用数量")
    private Integer usedNum = 0;
    
    public MktGoodsGift()
    {
    }
    
    public MktGoodsGift(Integer goods, String title, String content, CouponExpireChoose expireChoose, String userFarmer,
                        Integer userVendor, Date startDate, Date endDate, GiftType giftType)
    {
        super();
//        this.pkey = pkey;
        this.goods = goods;
        this.expireChoose = expireChoose;
        this.userFarmer = userFarmer;
        this.userVendor = userVendor;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.content = content;
        this.giftType = giftType;
        // 如果是新增，初始化数据
        if (pkey == null)
        {
            this.enabled = true;
            this.invalid = false;
            this.issuedNum = 0;
            this.usedNum = 0;
        }
    }

    @Schema(description = "市场")
    private String farmer;

    @Schema(description = "公司")
    private String company;

    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updatedTime;

    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;

    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    private Integer ascription;
    
}