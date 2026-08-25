package cn.tofocus.lejia.bean.entity.market;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * 订单收货地址
 */

@Entity
@Data
@Table(name = "mkt_order_desc")
@FieldNameConstants(innerTypeName = "F")
public class MktOrderDesc implements HasPkey<Integer>
{
    
    @Id
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "快递公司")
    private String logistics;
    
    @Schema(description = "快递单号")
    private String kdCode;
    
    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "省")
    private String pro;
    
    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "市")
    private String city;
    
    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "区")
    private String area;
    
    @Column(length = 40)
    @Size(max = 40)
    @Schema(description = "街道")
    private String town;
    
    @Schema(description = "地址")
    private String addr;
    
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "距离")
    private BigDecimal distance;
    
    @Schema(description = "收货人")
    private String name;
    
    @Schema(description = "收货人手机")
    private String mobile;
    
    @Schema(description = "留言")
    private String remark;
    
    @Schema(description = "付款时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date fkTime;
    
    @Schema(description = "发货时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date fhTime;
    
    @Schema(description = "确认时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date drTime;
    
    @Schema(description = "退款申请时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tkTime;
    
    @Schema(description = "退款说明")
    private String tkDesc;
    
    @Schema(description = "完成时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    
    @Schema(description = "预约时间起始")
    @Column(length = 40)
    @Size(max = 40)
    private String yytb;
    
    @Schema(description = "预约时间结束")
    @Column(length = 40)
    @Size(max = 40)
    private String yyte;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}