package cn.tofocus.lejia.bean.dto.app.market;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车
 *
 * @author zdw 2020-07-16
 */

@Data
public class MktGwcOnList {

    @Schema(description = "当前菜场购物车列表")
    private List<MktGwcDetailsDTO> currentFarmer = new ArrayList<>();

    @Schema(description = "积分商城购物车列表")
    private List<MktGwcDetailsDTO> pointsMall = new ArrayList<>();

    @Schema(description = "当前购物车总数量")
    private Integer total;
    public Integer getTotal() {
        return currentFarmer.size() + pointsMall.size();
    }

    @Schema(description = "包邮金额")
	private BigDecimal freeDelivery;

	private Boolean isFree;

	@Schema(description = "起步价")
    private BigDecimal startingPrice;

}
