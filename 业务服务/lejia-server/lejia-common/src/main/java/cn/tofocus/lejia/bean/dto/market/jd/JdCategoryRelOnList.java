package cn.tofocus.lejia.bean.dto.market.jd;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.tofocus.db.dto.JoinProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 京东分类关联列表 DTO。
 * <p>
 * 列出 jd_category 第二级有效分类，父级分类名称通过 {@link JoinProperty} 由 execDto 自动带出，
 * 京东名称按 parentId 向上一级，商城名称按 mallCategory 向上一级（mkt_goods_main → mkt_gtype）。
 * {@link #getName()} 组装 "1级/2级"，{@link #getMallName()} 组装商城 "1级/2级"。
 */
@Data
public class JdCategoryRelOnList
{
    @Schema(description = "pkey")
    private Long pkey;

    @Schema(description = "当前分类名称（京东2级）")
    private String categoryName;

    @JsonIgnore
    private Long parentId;

    @Schema(description = "京东一级分类名称")
    @JoinProperty(dataQuery = "jdCategoryDao", from = "parentId", propertyName = "categoryName", order = 1)
    private String firstName;

    @Schema(description = "关联商城二级分类pkey")
    private Integer mallCategory;

    @JsonIgnore
    @JoinProperty(dataQuery = "mktGoodsMainDao", from = "mallCategory", propertyName = "gtype", order = 1)
    private Integer mallGtypePkey;

    @Schema(description = "商城二级分类名称")
    @JoinProperty(dataQuery = "mktGoodsMainDao", from = "mallCategory", propertyName = "name", order = 1)
    private String mallSecondName;

    @Schema(description = "商城一级分类名称")
    @JoinProperty(dataQuery = "mktGtypeDao", from = "mallGtypePkey", propertyName = "name", order = 2)
    private String mallFirstName;

    /**
     * 组装京东分类名称："1级分类名称/2级分类名称"
     */
    @Schema(description = "京东分类名称")
    public String getName()
    {
        return joinName(firstName, categoryName);
    }

    /**
     * 组装商城分类名称："1级分类名称/2级分类名称"
     */
    @Schema(description = "商城分类名称")
    public String getMallName()
    {
        return joinName(mallFirstName, mallSecondName);
    }

    private static String joinName(String... names)
    {
        StringBuilder sb = new StringBuilder();
        for (String name : names)
        {
            if (name == null || name.isEmpty())
            {
                continue;
            }
            if (sb.length() > 0)
            {
                sb.append("/");
            }
            sb.append(name);
        }
        return sb.toString();
    }
}
