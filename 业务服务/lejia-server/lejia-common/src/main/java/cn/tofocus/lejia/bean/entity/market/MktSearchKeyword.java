package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

/**
 * @author czy
 * @version [版本号, 2025/7/1]
 */
@Data
@Entity
@Schema(description = "搜索关键词")
@Table(name = "mkt_search_keyword")
@FieldNameConstants(innerTypeName = "F")
public class MktSearchKeyword implements HasPkey<Integer>
{
    @Id
    @Column
    @Schema(description = "主键")
    @AutoRedisID(domain = "zyysc", sequence = "mkt_search_keyword")
    private Integer pkey;

    @Column(columnDefinition = "tinyint")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @Schema(description = "模块")
    private SearchKeywordModule module;

    @Column(length = 50)
    @Schema(description = "关键词")
    private String keyword;

    @Column
    @Schema(description = "排序")
    private Integer sort;
    
    @Schema(description = "市场")
    @Column(length = 40)
    private String farmer;
    
    @Schema(description = "公司")
    @Column(length = 40)
    private String company;
    
    @Schema(description = "修改时间")
    @LastModifiedDate
    @Column
    private Date updatedTime;
    
    @Schema(description = "建档时间")
    @CreatedDate
    @Column
    private Date createdTime;
    
    @Schema(description = "建档员")
    @CreatedBy
    @Column
    private Integer createdBy;
    
    @Schema(description = "归属主键")
    private Integer ascription;
}
