package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class MktCookfdAppOnList {
    /**
     * pkey
     */
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;

    /**
     * 名称
     */
    @Schema(description = "名称", required = true)
    private String name;

    /**
     * 今日推荐
     */
    @Schema(description = "今日推荐", required = true)
    private Boolean recom;
    
    /**
     * 分类
     */
    @Schema(description = "分类")
    private Integer ctype;

    /**
     * 照片1
     */
    @Schema(description = "照片1", required = true)
    private List<String> photo1;

    /**
     * 列表小图
     */
    @Schema(description = "列表小图")
    private String wrapperPhoto;

    public String getWrapperPhoto() {
        return getPhoto1().size() > 0 ? getPhoto1().get(0) : "";
    }

    /**
     * 排序
     */
    @Schema(description = "排序", required = true)
    private Integer sort;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String descp;

    /**
     * 浏览数量
     */
    @Schema(description = "浏览数量", required = true)
    private Integer viewCount;

    /**
     * 收藏数量
     */
    @Schema(description = "收藏数量", required = true)
    private Integer collCount;

    /**
     * 是否收藏
     */
    @Schema(description = "是否收藏", required = true)
    private boolean isCollection = false;

    /**
     * 启用标志
     */
    @Schema(description = "启用标志", required = true)
    private Boolean enabled;

    /**
     * 建档时间
     */
    @Schema(description = "建档时间", hidden = true)
    private Date createdTime;

}
