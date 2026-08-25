package cn.tofocus.lejia.bean.entity.market;



import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
@Schema(description = "功能菜单配置")
@Table(name = "mkt_function_menu_config")
@FieldNameConstants(innerTypeName = "F")
public class MktFunMenuConfig implements HasPkey<Integer>
{
    /**
     * pkey
     */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_function_menu_config")
    @Schema(description = "pkey")
    private Integer pkey;


    /**
    * 名称
    */
    @Schema(description = "名称")
    @Column(length=64)
    private String name;
    

    
    @Schema(description = "图片Url")
    @Column(length=255)
    @FileUrl
    private String photos;
    
    @Schema(description = "点击效果")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private LinkType urlType;
    
    
    @Schema(description = "内容")
    @Column(length=255)
    private String  objKey;
    


    @Schema(description = "排序")
    @Column
    private Integer sort;
    
    
    @Schema(description = "标签")
    @Column
    @Convert(converter = ListConverter.class)
    private List<Integer> targerKeys;
    
    @Schema(description = "状态")
    @Column
    private Boolean  enabled;
    
    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    /**
    * 市场
    */
    @Schema(description = "市场")
    private String farmer;

  

    
    @Schema(description = "修改时间")
    @Column
    @LastModifiedDate
    private Date  updatedTime;
    
    @Schema(description = "建立时间")
    @Column
    @CreatedDate
    private Date createdTime;
}
