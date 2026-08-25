package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

import java.util.ArrayList;
import java.util.List;

public enum RefundStatus implements IBaseDbEnum 
{
	
	REFUND_APPLYING(0, "未处理"), 
	REFUND_AGREE(1, "同意"),
	REFUND_FINAL(2, "退款成功"),
	REFUND_REFUSE(3, "退款失败"),
	REFUND_JD_HANDLE(4, "等待审核"),

	JD_PENDING_APPROVAL(11, "申请中待审核"),
	JD_APPROVAL_ACCEPTED(12, "审核完成待收货"),
	JD_APPROVAL_REJECTED(13, "审核不通过"),
	JD_RECEIPTED(14, "收货完成待处理"),
	JD_PROCESSED_SUCCESS(15, "处理完成"),
	JD_PROCESSED_FAILED(16, "处理失败"),
	JD_PENDING_CONFIRM(17, "待用户确认"),
	JD_CONFIRMED(18, "用户确认完成"),
	JD_CANCELED(19, "售后取消"),
	;

	private final int index;

	private final String name;

	private RefundStatus(int index, String name) {
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

	public static RefundStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(RefundStatus.class, index);
	}

	// 已退钱的状态（不管用户是否确认）
	public static List<RefundStatus> refundedStatus()
	{
		List<RefundStatus> list = new ArrayList<>();
		list.add(REFUND_FINAL);
		list.add(JD_PENDING_CONFIRM);
		list.add(JD_CONFIRMED);
		return list;
	}
}
