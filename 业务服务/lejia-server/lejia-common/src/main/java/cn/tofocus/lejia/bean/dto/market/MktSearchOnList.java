package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.lejia.bean.enums.SearchType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
public class MktSearchOnList
{

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
    private Integer member;

    /**
     * 搜索时间
     */
    @Schema(description = "搜索时间")
    private Date createdTime;
}
