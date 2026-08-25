package cn.tofocus.lejia.exception;

import cn.tofocus.core.exception.ErrCode;

public enum WsaleErrCode implements ErrCode
{
	
	UNKOWN_USER("lejia-0001", "用户不存在"),
	USER_NOT_BELONG_COMPANY("lejia-0002", "用户不属于此公司"),
	USER_NOT_BELONG_MARKET("lejia-0003", "用户不属于此市场"),
	UNKOWN_COMPANY("lejia-0005", "公司不存在"),
    UNKOWN_MARKET("lejia-0006", "市场不存在"),
    UNKOWN_ROLE("lejia-0007", "角色不存在"),
    NOT_MODIFY_MOBILE("lejia-0008", "不能修改号码"),
    NOT_DELETED("lejia-0009","启用状态,不能删除"),
    NOT_INQUIRE("lejia-0010", "查询不到数据"),
    NOT_GOODS("lejia-0011", "查询不到商品"),
    FAILED_DELETE_SPECIFICATION("lejia-0012", "删除商品规格失败!"),
    UNKOWN_COOKFD("lejia-0013", "菜谱不存在"),
    UNKOWN_MARKET_NOT_LOGIN("lejia-0014", "市场已关闭/不存在,无法登陆"),
    UNKOWN_COMPANY_NOT_LOGIN("lejia-0015", "公司已关闭/不存在,无法登陆"),
    COMMODITY_LIBRARY_DOES_NOT_EXIST("lejia-0016", "检测商品,商品库不存在"),
    COMMODITY_LIBRARY_ALREADY_EXISTS("lejia-0017", "该商品,商品库已存在"),
    EXCEL_PROBLEM("lejia-0018","excel表格有问题,无法导入"),
    NUMBER_EXCEEDED("lejia-0019", "超出限制条数"),
    PICK_ONE_OF_TWO("lejia-0020", "卡券有效期和到期日期只能选择一个"),
    FILL_IN_MOBILE("lejia-0021", "发放指定用户,需要填写用户的手机号码"),
    CAN_NOT_BE_EMPTY("lejia-0022", "不能为空"),
	NO_P0INTS("lejia-0023", "积分不足"),
	NO_COMMS("lejia-0024", "余额不足"),
	GOODS_TYPE_NAME_REPEAT("lejia-0025","商品分类名字重复"),
	GOODS_MAIN_NAME_REPEAT("lejia-0026","商品二级分类名字重复"),
	REQUIRED_PARAMETERS_NOT_EMPTY("lejia-0027","必要参数不能为空"),
	EXIST_MARKET("lejia-0028","该公司名下还有市场,不能删除"),
	CHECKED_IN("lejia-0029","今天已经签到!"),
	ALREADY_COLLECTION("lejia-0030","已经收藏"),
	GWC_FULL("lejia-0031","您的购物车已满"),
	TRY_AGAIN("lejia-0032", "再来一次"),
	GOODS_CANNOT_EDIT("lejia-0033", "上架的商品,不能编辑"),
	GOODS_NOT_AVAILABLE("lejia-0034","未到起售日期,不能上架"),
	MOBILE_REPEAT("lejia-0035","手机号码重复"),
	NOTOBTAINED_USERINFO("lejia-0036","未获取到用户信息"),
	NOT_COURIER("lejia-0037","该手机号码不是骑手"),
	NO_CHECKCODE("lejia-0038","验证码过期或未获取验证码,请获取验证码"),
	WRONG_CODE("lejia-0039","验证码不正确"),
    NOT_VENDOR("lejia-0040","该手机号码不是商户"),
    REFUND_STATUS_APPLYING("lejia-0041","该订单的状态不能修改"),
    REFUND_STATUS_AGREE("lejia-0042","该订单的状态不是同意退款状态"),
    GOODS_TYPE_NAME_NOTEXIST("lejia-0043","商品分类不存在,请输入正确的商品分类名称"),
    MEMBER_ALREADY_CUT("lejia-0044","该订单,您已经砍过了!"),
    GOODS_LOWEST_CUT("lejia-0045","该订单已经砍到最低了!"),
    GOOODS_CUT_TIME_OUT("lejia-0046","砍价时间已经结束!"),
    ORDER_NOT_DEL("lejia-0047","该订单无法删除!"),
	GOODS_PKEY_CORRECT("lejia-0048","请输入正确的商品!"),
	CARD_NOT_EXIST("lejia-0049","该卡券已经不可使用"),
	CARD_NOT_FARMER("lejia-0050","该卡券不可在该市场使用"),
    DATA_FORMAT_ERR("lejia-0051","时间格式有问题!"),
    DATE_ERR("lejia-0052","商品库的数据有误!"),
    USER_ROLE("lejia-0053","还有用户是该角色身份,无法删除!"),
    GOODS_NOT_ENDAVAILABLE("lejia-0054","有超出上架日期的选项,不能上架"),
    COOKFD_TYPE_USE("lejia-0055","还有菜谱在使用该分类,不能关闭"),
    COOKFD_SPACE_USE("lejia-0056","还有菜谱在使用该规格,不能删除该规格"),
    COOKFD_GOODS_USE("lejia-0057","还有菜谱在使用该商品,不能删除该商品"),
    MEMBER_NOT_LOGIN("lejia-0058","请先登录"),
    SORT_NOT_EMPTY("lejia-0059","排序不能为空!"),
    UNKOWN_FARMER("lejia-0061", "市场没确定"),
    NOT_VENDOR_ENABLED("lejia-0062","该商户已被禁用"),
    COURIER_DISPATCH_ENABLED("lejia-0063","当前骑手已在自动派单系统中，暂且无法停用和删除。"),
    GTYPE_CORRECT("LEJIA-0064","请输入正确的一级分类!"),
    GOODSMAIN_CORRECT("LEJIA-0064","请输入正确的二级分类!"),
    NOT_STAFF_ENABLED("lejia-0065","该店员已被禁用"),
    GTYPE_NOT_DEL("lejia-0066","该分类不能删除"),
    CARD_INVALID("lejia-0067","该卡券商品已经失效，不能操作"),
    EXIST_ORDER_DEL("lejia-0068","该卡券商品有销售，不能删除"),
    NAME_REPEAT("lejia-0069","名称重复"),
    OPENID_ERROR("lejia-0070","openid获取失败"),
    MEMBER_LOGGED_OUT("lejia-0071","会员已注销"),
    FARMER_TIME_ERROR("lejia-0072","营业时间设置不能重叠"),
    FARMER_TIME_ONE_ERROR("lejia-0073","营业时间最少设置一个"),
    TWO_GOODS_MAIN_EXISTS("lejia-0074", "该三级分类已经存在"),
    TWO_GOODS_MAIN_ERROR("lejia-0075","该二级分类不存在"),
    GOODS_DATE_ERROR("lejia-0076","该商品缺少上下架时间"),
    VENDOR_BUSINESSSCOPE_ERROR("lejia-0077","经营范围为空,不能上架"),
    GIFT_DATE_ERROR("lejia-0078","不在兑换时间范围内,不予兑换"),
    MARKET_NUM_ERROR("lejia-0079","请联系商务"),
    GOODS_NO_SUPPLIER("lejia-0080","商品没有关联供应商，无法上架"),
    
    ;
	
	private final String code;
    
    private final String description;
    
    private WsaleErrCode(String code, String description)
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
