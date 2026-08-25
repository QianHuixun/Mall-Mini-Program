package cn.tofocus.lejia.bean.enums;

import java.util.ArrayList;
import java.util.List;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum OrderStatus implements IBaseDbEnum 
{
	//状态 未付款/待发货/已发货/已到货/确认/退款申请/已退款/作废
	UNPAID_ORDER(0, "未付款"), 
	DELIVERED_ORDER(1, "待发货"),//市场商品对应普通商品
	SHIPPED_ORDER(2, "已发货"),
	ARRIVED_ORDER(3, "已到货"),
	CONFIRM_ORDER(4, "已完成"),
	REFUND_APPLICATION_ORDER(5, "退款申请"),
	REFUNDED_ORDER(6, "已退款"),
	PAYING_ORDER(7, "支付受理中"),

	WAIT_ARRIVAL_ORDER(8, "待到货"),
	WAIT_WRITEOFF_ORDER(9, "待核销"),
	
	
	VOID_ORDER(99, "作废");

	private final int index;

	private final String name;

	private OrderStatus(int index, String name) {
		this.name = name;
		this.index = index;
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public int getIndex() 
	{
		return index;
	}

	public static OrderStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(OrderStatus.class, index);
	}
    
    public static List<OrderStatus> summaryStatus()
    {
        List<OrderStatus> list = new ArrayList<>();
        list.add(DELIVERED_ORDER);
        list.add(SHIPPED_ORDER);
        list.add(WAIT_ARRIVAL_ORDER);
        list.add(WAIT_WRITEOFF_ORDER);
        list.add(ARRIVED_ORDER);
        list.add(CONFIRM_ORDER);
        list.add(REFUND_APPLICATION_ORDER);
        list.add(REFUNDED_ORDER);
        return list;
    }

	public static List<OrderStatus> summaryStatusWithoutRefunded()
	{
		List<OrderStatus> list = new ArrayList<>();
		list.add(DELIVERED_ORDER);
		list.add(SHIPPED_ORDER);
        list.add(WAIT_ARRIVAL_ORDER);
        list.add(WAIT_WRITEOFF_ORDER);
		list.add(ARRIVED_ORDER);
		list.add(CONFIRM_ORDER);
		list.add(REFUND_APPLICATION_ORDER);
		return list;
	}
}
