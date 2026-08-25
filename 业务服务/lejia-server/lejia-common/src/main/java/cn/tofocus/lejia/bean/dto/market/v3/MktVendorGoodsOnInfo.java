package cn.tofocus.lejia.bean.dto.market.v3;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MktVendorGoodsOnInfo
{
    @Schema(hidden = true)
    private Integer pkey;
    
    /**
    * 分类
    */
    @Schema(description = "分类", required = true)
    private Integer gtype;
    
    /**
     * 分类名称
     */
    @Schema()
    private String gtypeName;
    
    /**
     * 商品名称 新增的时候 不需要传
     */
    @Schema(hidden = true)
    private String name;
    
    @Schema(description = "商品库", required = true)
    private Integer goodsMain;
    
    /**
    * 标题
    */
    @Schema(description = "标题", required = true)
    private String title;
    
    /**
    * 照片1
    */
    @Schema(description = "照片1")
    private List<String> photo1 = new ArrayList<>();
    
    /**
    * 照片2
    */
    @Schema(description = "照片2")
    private String photo2;
    
    /**
    * 照片2
    */
    @Schema(description = "照片3")
    private String photo3;
    
    /**
    * 标准编号
    */
    @Schema(description = "标准编号")
    private String serialNumber;
    
    /**
    * 描述
    */
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "起售日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期", example = "2020-06-01", required = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "备注")
    private String remark;
    
    private Integer sort;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    // 规格
    @Schema(description = "规格主键")
    private Integer spacePkey;
    
    @Schema(description = "价格")
    private BigDecimal price;
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    private Integer kcNum;
}
