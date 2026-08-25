package cn.tofocus.lejia.bean.entity.member;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.lejia.bean.enums.member.TagType;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2024/8/12]
 */
@Data
@Entity
@Schema(description = "标签表")
@Table(name = "mkt_tag")
@FieldNameConstants(innerTypeName = "F")
public class MktTag implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_tag")
    private Integer pkey;

    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "类型")
    private TagType type;
    
    /**
     * 名称
     */
    @Column(length = 50)
    @Schema(description = "名称")
    private String name;
    
    /**
     * 名称
     */
    @Column(length = 200)
    @Schema(description = "描述")
    private String description;

    @Column(columnDefinition = "tinyint")
    @Schema(description = "是否已删除")
    private Boolean idDel;
    
    /**
     * 建档时间
     */
    @Column
    @Schema(description = "建档时间")
    @CreatedDate
    private Date createdTime;
    
    @Column
    @Schema(description = "归属主键")
    private Integer ascription;
}
