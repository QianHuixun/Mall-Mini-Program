package cn.tofocus.lejia.bean.dto.h5;

import java.math.BigDecimal;
import java.util.Date;

import cn.tofocus.lejia.bean.enums.h5.H5OrderStatus;
import cn.tofocus.lejia.bean.enums.h5.H5PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class H5OrderInfo
{
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单号")
    private String code;
    
    @Schema(description = "状态")
    private H5OrderStatus status;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    private H5PayType payType;
    
    @Schema(description = "订单价格")
    private BigDecimal amto;
    
    @Schema(description = "支付金额")
    private BigDecimal amtn;
    
    @Schema(description = "包厢名称")
    private String boxName;
    
    @Schema(description = "包厢时间")
    private String boxTime;
    
    @Schema(description = "包厢门锁密码")
    private String boxPassword;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "商品图片")
    private String photo1;
    
    @Schema(description = "市场名称")
    private String farmerName;
    
    @Schema(description = "建档时间")
    private Date createdTime;
}
