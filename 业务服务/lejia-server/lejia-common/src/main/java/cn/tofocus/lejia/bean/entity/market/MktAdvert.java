package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.AdvertType;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.LocationType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  广告位
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_advert")
@FieldNameConstants(innerTypeName = "F")
public class MktAdvert implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_advert")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 名称
    */
	@Schema(description = "名称")
    private String name;

    /**
    * 位置 1号/2号/3号/4号/5号
    */
	@Schema(description = "位置 1号/2号/3号/4号/5号")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private AdvertPosition position;

    @Schema(description = "位置关联对象主键")
    @Column(length = 200)
    private String positionObj;

    /**
    * 图片
    */
	@Schema(description = "图片")
	@FileUrl
    private String photo;

    /**
    * 链接类型 无/公告/商品/菜谱
    */
	@Schema(description = "链接类型 无/链接/积分商城/会员办理")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private LinkType urlType;

    /**
    * 对象
    */
	@Schema(description = "对象")
    private String objKey;

	/**
	 * 排序
	 */
	@Schema(description = "排序")
	private Integer sort;
	
    /**
    * 启用标志
    */
	@Schema(description = "启用标志")
    private Boolean enabled;

	@Schema(description = "广告类型:专区、自有")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
	private AdvertType type;
	
    /**
    * 市场
    */
	@Schema(description = "市场")
    private String farmer;

    /**
    * 公司
    */
	@Schema(description = "公司")
    private String company;

    /**
    * 最后更新时间
    */
	@Schema(description = "最后更新时间")
	@LastModifiedDate
    private Date updateTime;

    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
	@CreatedDate
    private Date createdTime;

    /**
    * 建档员
    */
	@Schema(description = "建档员")
	@CreatedBy
    private Integer createdBy;
	
	

    /**
    * 版本
    */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "归属主键")
    private Integer ascription;

    @Schema(description = "展示位置")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    private LocationType locationType;
    
    
    @Schema(description = "标签")
    @Column
    @Convert(converter = ListConverter.class)
    private List<Integer> targerKeys;
    

    @Schema(description = "用户可见范围")
    private MemberVisibleRange visibleRange;
    
    
    
}