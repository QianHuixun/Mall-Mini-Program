package cn.tofocus.lejia.bean.entity.h5;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.Name;
import cn.tofocus.lejia.bean.enums.h5.H5OrderStatus;
import cn.tofocus.lejia.bean.enums.h5.H5PayType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "h5_order")
@FieldNameConstants(innerTypeName = "F")
public class H5Order implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "h5_order")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "订单号")
    @Column(name = "kc_code")
    @Name
    private String code;
    
    @Schema(description = "用户")
    private Integer userKey;
    
    @Schema(description = "状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private H5OrderStatus status;
    
    @Schema(description = "支付类型  支付宝 微信 电子账号")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
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
    
    @Schema(description = "包厢门锁ID")
    private String lockId;
    
    @Schema(description = "门锁密码时间-开始")
    private Date boxSd;
    
    @Schema(description = "门锁密码时间-结束")
    private Date boxEd;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "商品主键")
    private Integer goods;
    
    @Schema(description = "规格主键")
    private Integer space;
    
    @Schema(description = "商品图片")
    private String photo1;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
}
