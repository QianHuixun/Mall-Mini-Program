package cn.tofocus.lejia.bean.dto.app;

import cn.tofocus.lejia.bean.dto.market.MktVendorFileDTO;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBigData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

/**
 * 商户风采分页数据
 * @author geshaojian
 * @since Created in 2021/10/19 9:17
 */
@Data
public class AppDemeanourPageDTO
{
    @Schema(description = "主键")
    private Integer pkey;

    @Schema(description = "商户名称")
    private String name;

    @Schema(description = "商户展示名称")
    @JsonIgnore
    private String displayName;
    
    @Schema(description = "经营范围主键列表")
    @JsonIgnore
    private String businessScope;

    @Schema(description = "经营范围中文")
    private List<String> businessScopesName;

    @Schema(description = "商户简介")
    private String shortContent;

    @Schema(description = "头像、视频、个性宣传")
    private List<MktVendorFileDTO> files;

    @Schema(description = "风采展示详情内容")
    private MktVendorBigData mktVendorBigData;
}
