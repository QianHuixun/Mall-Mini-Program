package cn.tofocus.account.exception;

import cn.tofocus.core.exception.ErrCode;

public enum AccErrCode implements ErrCode
{

    //@formatter:off
    /**
     * 权限不存在
     */
    FUNC_NOT_EXIST("acc_func_01", "权限不存在"),
    /**
     * 机构不存在
     */
    ORG_NOT_EXIST("acc_org_01", "机构不存在"),
    /**
     * 部门不存在
     */
    DEPT_NOT_EXIST("acc_dept_01", "部门不存在"),
    /**
     * 模块不存在
     */
    MODEL_NOT_EXIST("acc_model_01", "模块不存在"),
    /**
     * 模块不存在
     */
    MENU_NOT_EXIST("acc_memu_01", "菜单不存在"),
    ;
    //@formatter:on
    
    private final String code;
    
    private final String description;
    
    private AccErrCode(String code, String description)
    {
        this.code = code;
        this.description = description;
    }
    
    @Override
    public String getCode()
    {
        return code;
    }
    
    @Override
    public String getDescription()
    {
        return description;
    }
    
    @Override
    public boolean equalsCode(String code)
    {
        return this.code.equals(code);
    }
}
