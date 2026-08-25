package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 商品上下架变更消息
 * {"state":0,"skuId":商品编号}
 */
@Data
public class JdVOPSkuStateChangeMsg
{
    // state:1代表在主站（jd.com）上架； state:0代表下架
    private int state;
    
    private Long skuId;
}
