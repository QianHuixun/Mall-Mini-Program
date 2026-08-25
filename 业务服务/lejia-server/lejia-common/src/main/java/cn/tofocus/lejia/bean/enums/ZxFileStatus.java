package cn.tofocus.lejia.bean.enums;

import cn.tofocus.core.enums.IBaseDbEnum;

public enum ZxFileStatus implements IBaseDbEnum 
{
	UPLOAD_SYCCESS(0, "文件上传成功"), 
	SEPARATE(4, "已另行核对"),
	PASS(7, "文件合法性检查通过"),
	FAIL(8, "文件合法性检查不通过"),
	FINISH(9, "清分处理完成"),
	WITHDRAW_FINISH(10, "已提现成功"),
	ABNORMAL(11, "异常,需联系管理员解决"),
	OTHER(99, "其他状态-文件处理中"),
	
	;

    /*
     * 0-文件上传成功
4-已另行核对
7-文件合法性检查通过
8-文件合法性检查不通过
9-清分处理完成
其他状态-文件处理中

     */
    
	private final int index;

	private final String name;

	private ZxFileStatus(int index, String name) {
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

	public static ZxFileStatus fromIndex(Integer index) {
		return IBaseDbEnum.fromIndex(ZxFileStatus.class, index);
	}
}
