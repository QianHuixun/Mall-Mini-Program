package cn.tofocus.lejia.domain.jdvop.bean.msg;

import lombok.Data;

/**
 * 售后申请单环节变更消息
 * {"expectationChanged":期望发生变化,"thirdApplyId":三方申请单号,"pin":客户pin,"stepPassType":售后环节通过情况10:全部通过20:部分通过30:没有通过【注】applyStep为20或40的时候stepPassType有值,"isOffline":线上线下标识,"applyStep":申请环节标识10:申请20:审核30:收货40:处理50:待用户确认60:完成70:取消,"contractNumber":合同号,"orderId":京东订单号}
 */
@Data
public class JdVOPRefundStepChangeMsg
{
    private String expectationChanged;
    
    private String thirdApplyId;
    
    private String pin;
    
    private Integer stepPassType;
    
    private Boolean isOffline;
    
    private Integer applyStep;
    
    private String contractNumber;
    
    private Long orderId;
}
