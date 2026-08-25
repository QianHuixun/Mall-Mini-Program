package cn.tofocus.lejia.bean.entity.market;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.db.file.FileUrl;
import cn.tofocus.lejia.bean.enums.IndexAdvertSubject;
import cn.tofocus.lejia.bean.enums.LinkType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
*  app弹窗广告
* @author zdw 2020-09-22
*/

@Entity
@Data
@Table(name="mkt_index_advert")
public class MktIndexAdvert implements HasPkey<Integer> 
{
   
	/**
	 * pkey
	 */
    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_index_advert")
	@Schema(description = "pkey")
    private Integer pkey;

	/**
    * 名称
    */
	@Schema(description = "名称")
    private String name;

	/**
    * 图片
    */
	@Schema(description = "图片")
	@FileUrl
    private String photo;

	/**
    * 活动对象 全部/年费会员/活跃会员/..
    */
	@Schema(description = "subject")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private IndexAdvertSubject subject;

	/**
    * 链接类型 无/链接/积分商城/会员办理
    */
	@Schema(description = "链接类型 无/链接/积分商城/会员办理")
	@Column(nullable = false, columnDefinition = "tinyint(4)")
    private LinkType urlType;

	/**
    * 对象
    */
	@Schema(description = "对象", required = false)
    private String objKey;

	/**
    * sort
    */
	@Schema(description = "sort")
    private Integer sort;

	/**
    * 启用日期
    */
	@Schema(description = "启用日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date startDate;

	/**
    * 结束日期
    */
	@Schema(description = "结束日期")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd" , timezone = "GMT+8")
    private Date endDate;

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
    private Integer updateBy;

	/**
    * 版本
    */
	@Schema(description = "版本")
	@Column(nullable = false, columnDefinition = "smallint(6)")
    private Integer rowVension;

    @Schema(description = "归属主键")
    private Integer ascription;

}