package cn.tofocus.lejia.bean.entity.market;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.AutoRedisID;
import cn.tofocus.lejia.bean.enums.SearchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
*  mkt_search_hot
* @author pty 2020-07-02
*/

@Entity
@Data
@Table(name="mkt_search_hot")
public class MktSearchHot implements HasPkey<Integer> {
   

    @Id
    @AutoRedisID(domain = "zyysc", sequence="mkt_search_hot")
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
    * 搜索时间
    */
	@Schema(description = "搜索时间")
    private Date createdTime;

    @Schema(description = "归属主键")
    private Integer ascription;

}
