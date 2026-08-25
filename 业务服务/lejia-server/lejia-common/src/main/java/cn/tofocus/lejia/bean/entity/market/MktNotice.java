package cn.tofocus.lejia.bean.entity.market;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import lombok.Data;

/**
*  公告
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_notice")
public class MktNotice implements HasPkey<Integer> {
   

    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_notice")
    /**
    * pkey
    */
	@Schema(description = "pkey")
    private Integer pkey;

    /**
    * 标题
    */
	@Schema(description = "标题")
    private String title;

    /**
    * 作者
    */
	@Schema(description = "作者")
    private String author;

    /**
    * 正文
    */
	@Schema(description = "正文")
	@Column(name = "content",columnDefinition = "text")
    private String content;

    /**
    * 启用标志
    */
	@Schema(description = "启用标志")
    private Boolean enabled;

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
    private Date updateTime;

    /**
    * 建档时间
    */
	@Schema(description = "建档时间")
    private Date createdTime;

    /**
    * 建档员
    */
	@Schema(description = "建档员")
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