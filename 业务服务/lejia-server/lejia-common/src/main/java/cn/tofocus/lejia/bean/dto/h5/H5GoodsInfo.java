package cn.tofocus.lejia.bean.dto.h5;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.lejia.bean.enums.h5.H5Level;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class H5GoodsInfo
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @Schema(description = "照片1")
    private List<String> photo1;
    
    @Schema(description = "照片2")
    private String photo2;
    
    @Schema(description = "照片3")
    private String photo3;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "正文")
    private List<String> content;
    
    @Schema(description = "起售日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    
    @Schema(description = "到期日期")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    
    @Schema(description = "销售数量")
    private Integer xsNum;
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    @Schema(description = "中午场价格")
    @NotNull(message = "价格不能为空")
    private BigDecimal noonPrice;
    
    @Schema(description = "晚上场价格")
    @NotNull(message = "价格不能为空")
    private BigDecimal nightPrice;
    
    @Schema(description = "包厢门锁ID")
    private String lockId;
    
    @Schema(description = "排序字段")
    private Integer sort;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "用户可见类型")
    private H5Level levelA;
    
    @Schema(description = "用户可见类型")
    private H5Level levelB;
    
    @Schema(description = "用户可见类型")
    private H5Level levelC;
    
    @Schema(description = "小程序商品关联")
    private Integer correlation;
    
}
