package cn.tofocus.lejia.domain.jdvop.bean.msg;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 订单完成消息
 * {"jdOrderState":19,"pin":客户PIN,"completeTime":"2023-07-25 08:21:23","orderId":订单号}
 */
@Data
public class JdVOPOrderFinishMsg
{
    private Integer jdOrderState;
    
    private String pin;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;
    
    private Long orderId;
}
