package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MktAppCookfdLineDetailsDTO {
    /**
     * pkey
     */
    @Schema(description = "pkey", hidden = true)
    private Integer pkey;

    // 1:新增  2:修改  3:删除
    @Schema(description = "状态", hidden = true)
    private Integer status = 0;

    /**
     * 菜谱
     */
    @Schema(description = "菜谱", hidden = true)
    private Integer cookfd;

    /**
     * 商品
     */
    @Schema(description = "商品", required = true)
    private Integer goods;

    /**
     * 商品名称
     */
    @Schema(description = "商品名称")
    private String goodsName = "";

    /**
     * 价格
     */
    @Schema(description = "价格")
    private BigDecimal price;

    /**
     * 列表小图
     */
    @Schema(description = "列表小图")
    private String wrapperPhoto;

    /**
     * 规格
     */
    @Schema(description = "规格", required = true)
    private Integer space;

    /**
     * 规格
     */
    @Schema(description = "规格名称")
    private String spaceName = "";

    /**
     * 数量
     */
    @Schema(description = "数量", required = true)
    private Integer num;

    /**
     * 排序
     */
    @Schema(description = "排序", required = true)
    private Integer sort;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
