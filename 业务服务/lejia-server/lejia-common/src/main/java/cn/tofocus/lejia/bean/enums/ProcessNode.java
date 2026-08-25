package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ProcessNode implements IBaseDbEnum 
{
    REPORT(1, "生成结算报表"), 
    APPLY(2, "结算申请"),
	FAIL(3, "结算失败"),
	SUCCESS(4, "结算成功"),
    
    ;

	private final int index;

	private final String name;

	private ProcessNode(int index, String name) {
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

	public static ProcessNode fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(ProcessNode.class, index);
	}
}
