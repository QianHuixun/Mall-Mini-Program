package cn.tofocus.lejia.bean.entity.market;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.SearchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
*  mkt_search
* @author lai 2020-06-15
*/

@Entity
@Data
@Table(name="mkt_search")
public class MktSearch implements HasPkey<Integer> {
   

    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_search")
    /**
    * pkey
    */
	@Schema(description = "pkey")
    private Integer pkey;

    /**
     * 搜索类型 商品/菜谱/积分商城
     */
    @Schema(description = "搜索类型 商品/菜谱/积分商城")
    @Column(nullable = false, columnDefinition = "tinyint(4)")
    private SearchType stype;

    /**
    * 搜索内容
    */
	@Schema(description = "搜索内容")
    private String descp;

    /**
     * 用户
     */
    @Schema(description = "用户")
	@Column(name="member_key")
    private Integer member;

    /**
    * 搜索时间
    */
	@Schema(description = "搜索时间")
    @CreatedDate
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
