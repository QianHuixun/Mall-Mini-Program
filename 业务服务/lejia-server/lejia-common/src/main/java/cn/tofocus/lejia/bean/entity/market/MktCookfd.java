package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.ListConverter;
import cn.tofocus.db.file.FileUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  菜谱
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_cookfd")
public class MktCookfd implements HasPkey<Integer> {
   

	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_cookfd")
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 名称
    */
	@Schema(description = "名称")
    private String name;

	/**
    * 今日推荐
    */
	@Schema(description = "今日推荐")
    private Boolean recom;
	
    /**
     * 分类
     */
    @Schema(description = "分类")
    private Integer ctype;

    /**
    * 照片1
    */
	@Schema(description = "照片1")
	@FileUrl
	@Convert(converter = ListConverter.class)
    private List<String> photo1;

    /**
    * 照片2
    */
	@Schema(description = "照片2")
	@FileUrl
	@Convert(converter = ListConverter.class)
    private List<String> photo2;

	@Schema(description = "照片3")
	@FileUrl
	@Convert(converter = ListConverter.class)
    private List<String> photo3;
	
    /**
    * 排序
    */
	@Schema(description = "排序")
    private Integer sort;

    /**
    * 描述
    */
	@Schema(description = "描述")
    private String descp;

    /**
    * 正文
    */
	@Schema(description = "正文")
//	@FileUrl
	@Column(name = "content",columnDefinition = "text")
	@Convert(converter = ListConverter.class)
    private List<String> content;

	/**
	 * 浏览数量
	 */
	@Schema(description = "浏览数量")
	private Integer viewCount;
	
	/**
	 * 收藏数量
	 */
	@Schema(description = "收藏数量")
	private Integer collCount;
	
    /**
    * 启用标志
    */
	@Schema(description = "启用标志")
    private Boolean enabled;

	/**
	 * 是否已删除
	 */
	@Schema(description = "是否已删除")
	private Boolean idDel;
	
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

}