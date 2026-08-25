package cn.tofocus.lejia.bean.dto.app.market;

import java.util.List;

import cn.tofocus.lejia.bean.enums.PrizeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppDrawPrizeDTO {

    /**
     * pkey
     */
    @Schema(description = "pkey")
    private Integer pkey;

    /**
     * 礼品类型 积分/优惠券/礼品券/礼品/
     */
    @Schema(description = "礼品类型 积分/优惠券/礼品券/礼品/")
    private PrizeType pType;

    @Schema(description = "商品名称")
    private String name;

    /**
     * 图片
     */
    @Schema(description = "图片")
    private List<String> photo;


    /**
     * 图片1
     */
    @Schema(description = "图片1")
    private String photo1;


    /**
     * 奖品值
     */
    @Schema(description = "奖品值")
    private Integer pvalue;

    /**
     * 中奖描述
     */
    @Schema(description = "中奖描述")
    private String descp;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 位置
     */
    @Schema(description = "位置")
    private Integer position;

    public Integer getPosition() {
        return getPkey() - 1;
    }

}
