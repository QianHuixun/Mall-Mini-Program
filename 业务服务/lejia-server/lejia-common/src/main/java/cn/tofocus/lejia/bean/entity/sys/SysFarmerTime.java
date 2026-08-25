package cn.tofocus.lejia.bean.entity.sys;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Entity
@Data
@Table(name = "sys_farmer_time")
public class SysFarmerTime implements HasPkey<Integer>
{
    @Id
    @AutoRedisID(domain = "zyysc", sequence = "sys_farmer_time")
    @Schema(description = "pkey")
    private Integer pkey;
    
    @Schema(description = "市场")
    private String farmer;
    
    @Schema(description = "开始小时")
    private Integer startHour;
    
    @Schema(description = "开始分钟")
    private Integer startMinute;
    
    @Schema(description = "结束小时")
    private Integer endHour;
    
    @Schema(description = "结束分钟")
    private Integer endMinute;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
