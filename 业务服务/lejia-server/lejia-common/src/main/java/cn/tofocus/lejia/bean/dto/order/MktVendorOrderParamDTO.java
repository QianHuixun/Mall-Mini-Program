package cn.tofocus.lejia.bean.dto.order;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.lejia.bean.enums.SettlementType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 商户结算查询DTO
 */
@Data
public class MktVendorOrderParamDTO
{
    @Schema(description = "页号（不传默认为0）", example = "0")
    private Integer page;
    
    @Schema(description = "每页大小（不传默认为10）", example = "10")
    private Integer pagesize;
    
    @Parameter(description = "商户pkey列表")
    private List<Integer> vendor;
    
    @Parameter(description = "订单时间-开始")
    private String startDate;
    
    @Parameter(description = "订单时间-结束")
    private String endDate;
    
    @Parameter(description = "结算状态")
    private List<SettlementType> status;
    
    @Parameter(description = "采购日期是否降序")
    @JsonIgnore
    private Boolean createTimeSort;
}
