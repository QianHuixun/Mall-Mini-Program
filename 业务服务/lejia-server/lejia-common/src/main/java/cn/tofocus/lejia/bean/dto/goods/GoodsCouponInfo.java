package cn.tofocus.lejia.bean.dto.goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GoodsCouponInfo
{
    @Schema
    private Integer pkey;
    
    @Schema(description = "商品库")
    private Integer gtype;
     
    @Schema(description = "商品库")
    private Integer goodsMain;
    
    @Schema(description = "三级分类")
    private Integer threeGtype;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<>();
    
    @Schema(description = "照片2")
    private String photo2;
    
    @Schema(description = "照片3")
    private String photo3;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "正文")
    private List<String> content = new ArrayList<>();
    
    private String content2;
    
    @Schema(description = "起售日期", example = "2020-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期", example = "2020-06-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "限购数量")
    private Integer purchaseNum;
    
    private Integer sort;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "规格")
    private MktGoodsSpaceOnList space;
    
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date cardStartDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
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
    
}
