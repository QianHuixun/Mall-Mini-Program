package cn.tofocus.lejia.bean.dto.h5;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinDTO;
import cn.tofocus.db.dto.JoinEnum;
import cn.tofocus.lejia.bean.enums.h5.H5Level;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class H5GoodsOnPage
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "正文")
    private List<String> content;
    
    @Schema(description = "照片1")
    private List<String> photo1;
    
    @Schema(description = "照片2")
    private String photo2;
    
    @Schema(description = "照片3")
    private String photo3;
    
    @Schema(description = "销售数量")
    private Integer xsNum;
    
    @Schema(description = "原价")
    private BigDecimal priceOld;
    
    @Schema(description = "中午场价格")
    private BigDecimal noonPrice;
    
    @Schema(description = "晚上场价格")
    private BigDecimal nightPrice;
    
    @Schema(description = "备注")
    private String remark;
    
    
//    private H5Level level;
//    
//    @Schema(description = "用户是否可预定")
//    @JoinEnum(from = "level")
//    private String levelName;
    
    @Schema(description = "是否可下单")
    private Boolean isBuy;
    
    @Schema(description = "规格")
    @JoinDTO(dataQuery = "h5GoodsSpaceDao", referencedName = "goods", type = H5GoodsSpaceOnInfo.class)
    private List<H5GoodsSpaceOnInfo> spaces;
    
    @JsonIgnore
    @Schema(description = "用户可见类型")
    private H5Level levelA;
    @JsonIgnore
    @Schema(description = "用户可见类型")
    private H5Level levelB;
    @JsonIgnore
    @Schema(description = "用户可见类型")
    private H5Level levelC;
}
