package cn.tofocus.lejia.bean.dto.app;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.db.dto.JoinProperty;
import cn.tofocus.lejia.bean.enums.CouponExpireChoose;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppGiftV2ForWriteOff
{
    @Schema(description = "主键")
    private Integer pkey;
    
    @Schema(description = "卡券编号")
    private String cardNumber;
    
    @Schema(description = "礼品券")
    private Integer gift;
    
    @Schema(description = "礼品券名称")
    @JoinProperty(dataQuery = "mktGoodsGiftDao", from = "gift", propertyName = "title")
    private String giftTitle;
    
    @Schema(description = "礼品券图片")
    @JoinProperty(dataQuery = "mktGoodsGiftDao", from = "gift", propertyName = "picture")
    private String giftPicture;
    
    @Schema(description = "礼品券介绍")
    @JoinProperty(dataQuery = "mktGoodsGiftDao", from = "gift", propertyName = "content")
    private String giftContent;
    
    @Schema(description = "有效期类型")
    private CouponExpireChoose expireChoose;
    
    @Schema(description = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    
    @Schema(description = "使用市场")
    private String userFarmer;
}
