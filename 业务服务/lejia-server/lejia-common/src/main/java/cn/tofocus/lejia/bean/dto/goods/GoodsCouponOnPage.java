package cn.tofocus.lejia.bean.dto.goods;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsCouponOnPage
{
    @Schema
    private Integer pkey;
    
    @Schema(description = "商品库")
    private Integer goodsMain;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "金额")
    private BigDecimal price;
    
    @Schema(description = "积分")
    private Integer point;
    
    @Schema(description = "库存数量")
    private Integer kcNum;
    
    @Schema(description = "起售日期", example = "2020-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期", example = "2020-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "限购数量")
    private Integer purchaseNum;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @JsonIgnore
    private String extendCon;
    @Schema(description = "排序字段")
    @JsonIgnore
    private Integer sort;
    @JsonIgnore
    private String space;
    // **************以下是卡券的内容**************//
    
    @Schema(description = "价值")
    private BigDecimal cost;
    
    @Schema(description = "最低消费")
    private BigDecimal limitCost;
    
    @Schema(description = "到期选择, true 为 多少天后到期   false 为 指定到期日期")
    private Boolean expireChoose;
    
    @Schema(description = "有效期(天)")
    private Integer effective;
    
    @Schema(description = "开始日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date cardStartDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date cardEndDate;
    
    @Schema(description = "使用市场pkey")
    private String userFarmer;
    
    @Schema(description = "使用市场名称")
    private String userFarmerName = "";
    
    @Schema(description = "使用分类")
    private Integer userType;
    
    @Schema(description = "使用分类名称")
    private String userTypeName = "";
    
    @Schema(description = "使用商品")
    private Integer userGoods;
    
    @Schema(description = "使用商品名称")
    private String userGoodsName = "";
    
    @Schema(description = "是否失效,false:未失效")
    private Boolean invalid;
}
