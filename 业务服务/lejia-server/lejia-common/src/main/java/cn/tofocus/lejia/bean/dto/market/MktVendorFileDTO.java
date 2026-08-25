package cn.tofocus.lejia.bean.dto.market;

import cn.tofocus.lejia.bean.enums.VendorFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 商户文件表DTO
 * @author geshaojian
 * @date   2021-10-12
 */
@Data
public class MktVendorFileDTO
{

    /**
     * 商户文件表主键
     */
    @Schema(description = "商户文件表主键，新增时不需要传递")
    private Integer pkey;

    /**
     * 文件地址
     */
    @Schema(description = "文件地址")
    @NotBlank(message = "文件地址不能为空")
    private String url;

    /**
     * 类型
     */
    @Schema(description = "类型")
    @Type(type = "cn.tofocus.db.jpa.usertype.DbEnumType")
    @NotNull(message = "类型不能为空")
    private VendorFileType type;

}