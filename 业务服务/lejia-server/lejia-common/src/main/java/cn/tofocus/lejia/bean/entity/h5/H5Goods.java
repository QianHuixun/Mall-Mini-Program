package cn.tofocus.lejia.bean.entity.h5;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.query.param.valid.ListStrLength;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.h5.H5Level;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "h5_goods")
@FieldNameConstants(innerTypeName = "F")
public class H5Goods implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "h5_goods")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "标题")
    private String title;
    
    @Schema(description = "照片1")
    @FileUrl
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
    private List<String> photo1;
    
    @Schema(description = "照片2")
    @FileUrl
    private String photo2;
    
    @Schema(description = "照片3")
    @FileUrl
    private String photo3;
    
    @Schema(description = "描述")
    private String description;
    
    @Schema(description = "正文")
    @FileUrl
    @Convert(converter = ListConverter.class)
    @ListStrLength(length = 1000)
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
    private BigDecimal noonPrice;
    
    @Schema(description = "晚上场价格")
    private BigDecimal nightPrice;
    
    @Schema(description = "包厢门锁ID")
    private String lockId;
    
    @Schema(description = "排序字段")
    private Integer sort;
    
    @Schema(description = "备注")
    private String remark;
    
    @Schema(description = "用户可见类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private H5Level levelA;
    
    @Schema(description = "用户可见类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private H5Level levelB;
    
    @Schema(description = "用户可见类型")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private H5Level levelC;
    
    @Schema(description = "小程序商品关联")
    private Integer correlation;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "启用标志")
    private Boolean enabled;
    
    @Schema(description = "是否已删除")
    private Boolean idDel;
    
    @Schema(description = "最后更新时间")
    @LastModifiedDate
    private Date updateTime;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "建档员")
    @CreatedBy
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
