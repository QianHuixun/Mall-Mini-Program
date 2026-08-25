//package cn.tofocus.lejia.bean.enums;
//
//import cn.tofocus.core.enums.IBaseDbEnum;
//
//public enum VendorOrderStatus implements IBaseDbEnum 
//{
//	
//    AWAIT_CONFIRM(0, "待确认"),
//    AWAIT_SETTLEMENT(1, "未结算"),
//    ALREADY_SETTLEMENT(2, "已结算"),
//;
//
//	private final int index;
//
//	private final String name;
//
//	private VendorOrderStatus(int index, String name) {
//		this.name = name;
//		this.index = index;
//	}
//
//	@Override
//	public String getName() 
//	{
//		return name;
//	}
//
//	@Override
//	public int getIndex() 
//	{
//		return index;
//	}
//
//	public static VendorOrderStatus fromIndex(Integer index) {
//		return IBaseDbEnum.fromIndex(VendorOrderStatus.class, index);
//	}
//}
