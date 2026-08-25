package cn.tofocus.lejia.bean.entity.h5;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@Table(name = "h5_user")
@FieldNameConstants(innerTypeName = "F")
public class H5User implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "h5_user")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "userid")
    private String userid;
    
    @Schema(description = "名称")
    private String name;
    
    @Schema(description = "手机号码")
    private String mobile;
    
    @Schema(description = "钱包金额")
    private BigDecimal money;
    
    @Schema(description = "等级")
    private Integer level;
    
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "公司")
    private String company;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
