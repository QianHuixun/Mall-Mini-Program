package cn.tofocus.lejia.bean.dto.market;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 优惠券
 */
@Data
public class MktGiftOnList
{
    
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "状态 初始/已使用/已过期")
    private CardStatus status;
    
    @Schema(description = "订单pkey")
    private Integer orderPkey;
    
    @Schema(description = "卡券编号")
    private String cardNumber;
    
    /**
    * 卡券
    */
    @Schema(description = "卡券")
    private Integer goods;
    
    private MktGoods goodsObj;
    
    /**
    * 规格
    */
    @Schema(description = "规格")
    private Integer space;
    
    /**
    * 到期日期
    */
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    /**
    * 使用商户
    */
    @Schema(description = "使用商户")
    private Integer userVendor;
    
    private MktVendor vendorObj;
    
    /**
    * 使用日期
    */
    @Schema(description = "使用日期")
    private Date userTime;
    
    /**
    * 市场
    */
    @Schema(description = "市场")
    private String farmer;
    
    /**
    * 公司
    */
    @Schema(description = "公司")
    private String company;
    
    /**
    * 建档时间
    */
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
}
