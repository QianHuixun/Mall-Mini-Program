package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/8/12]
 */
@Data
@Entity
@Schema(description = "标签可见关联表")
@Table(name = "mkt_tag_visible")
@FieldNameConstants(innerTypeName = "F")
public class MktTagVisible implements HasPkey<String>
{
    @Id
    @Column(length = 50)
    @Schema(description = "主键")
    private String pkey;
    
    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "类型")
    private TagVisibleTargetType type;
    
    @Column
    @Schema(description = "对象主键")
    private Long target;
    
    @Column
    @Schema(description = "标签主键")
    private Integer tag;
    
    @Column
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
    
    public String makePkey(TagVisibleTargetType type, Long target, Integer tag)
    {
        return type.getIndex() + "_" + target + "_" + tag;
    }
    
    public void setPkey(String pkey)
    {
        this.pkey = pkey;
    }
    
    public void setPkey(TagVisibleTargetType type, Long target, Integer tag)
    {
        setType(type);
        setTarget(target);
        setTag(tag);
        setPkey(makePkey(type, target, tag));
    }
}
