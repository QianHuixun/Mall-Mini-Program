package cn.tofocus.lejia.domain.jdvop.bean.msg;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 商品池内商品添加、删除消息
 * {"skuId": 商品编号, "page_num":商品池编号, "state":"1添加，2删除"}
 */
@Data
public class JdVOPSkuAddDelMsg
{
    private Long skuId;
    
    // 商品池编号
    @JSONField(name = "page_num")
    private String pageNum;
    
    // 1添加，2删除
    private Integer state;
}
