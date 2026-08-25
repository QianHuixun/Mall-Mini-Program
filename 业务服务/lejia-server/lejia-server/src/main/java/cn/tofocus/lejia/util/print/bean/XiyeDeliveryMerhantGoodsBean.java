package cn.tofocus.lejia.util.print.bean;

import java.util.List;

import lombok.Data;

@Data
public class XiyeDeliveryMerhantGoodsBean
{
    private String merchantName;
    
    private String area;
    
    private String booth;
    
    private Integer goodsCount; //合计件数
    
    private List<PrintOriInfo> ori;
}
