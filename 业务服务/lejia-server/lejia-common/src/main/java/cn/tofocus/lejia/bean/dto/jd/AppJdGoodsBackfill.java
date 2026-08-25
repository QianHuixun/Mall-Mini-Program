package cn.tofocus.lejia.bean.dto.jd;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * JD 商品精简回填 DTO：仅 MSD 列表所需字段，避开 JdGoods 的 text 长文本列
 * （content2/introduce/introducePc/introduceApp/introduceWechat）。
 * 用于 msd/search 与 mall/query 窗口回填（窗口 ≤ pagesize，批量 byPkey 投影）。
 */
@Data
public class AppJdGoodsBackfill
{
    /** SKU pkey */
    private Long pkey;

    /** 标题 */
    private String title;

    /** 标签 */
    private String tag;

    /** 价格 */
    private BigDecimal price;

    /** 照片1（@Convert 列，投影时可正确还原为 List） */
    private List<String> photo1;

    /** 销量 */
    private Integer xsNum;

    /** 最低起购量 */
    private Integer lowestBuy;
}
