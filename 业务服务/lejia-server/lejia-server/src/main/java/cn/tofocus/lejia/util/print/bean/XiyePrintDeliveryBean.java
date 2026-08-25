package cn.tofocus.lejia.util.print.bean;

import java.util.List;

import lombok.Data;

@Data
public class XiyePrintDeliveryBean
{
    
    private String orderTrace;//订单追溯码
    
    private String orderNumber;
    
    private String orderTime;
    
    private String pickUp;//取货方式
    
    private String deliveryMode;//配送方式
    
    private String receivedTime;//期望送达时间
    
    private String remarket;//备注
    
    private String address;//地址
    
    private String name;//用户名字
    
    private String mobile;//用户手机号
    
    private Integer totalCount;//总 件数
    
    private List<XiyeDeliveryMerhantGoodsBean> merchantGoods;
}
