package cn.tofocus.lejia.bean.dto.jd;

import java.math.BigDecimal;

import lombok.Data;

/**
 * JD 商品精简投影（仅排序键 + 标识）：用于 MSD 商城分页「阶段一」全量取数做内存排序分页，
 * 避开 JdGoods 的 text 长文本列（content2/introduce/introducePc/introduceApp/introduceWechat），
 * 仅在分页窗口确定后（阶段二）才用 byPkey 批量回填完整字段。
 */
@Data
public class AppJdGoodsSortKey
{
    /** 代表 SKU 的 pkey（每个 spuId 取最小 pkey） */
    private Long pkey;

    /** 主商品 ID，用于去重 */
    private Long spuId;

    /** 价格（排序键） */
    private BigDecimal price;

    /** 销量（排序键） */
    private Integer xsNum;
}
