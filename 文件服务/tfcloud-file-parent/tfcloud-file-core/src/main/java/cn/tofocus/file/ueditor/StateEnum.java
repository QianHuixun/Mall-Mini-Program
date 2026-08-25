package cn.tofocus.file.ueditor;

import cn.tofocus.core.enums.IBaseDbEnum;
import lombok.Getter;

@Getter
public enum StateEnum implements IBaseDbEnum
{
    
    // @formatter:off
    SUCCESS(0, "SUCCESS"),
    MAX_SIZE(1, "文件大小超出限制"),
    PERMISSION_DENIED(2, "权限不足， 多指写权限"),
    FAILED_CREATE_FILE(3, "创建文件失败"),
    IO_ERROR(4, "IO错误"),
    NOT_MULTIPART_CONTENT(5, "上传表单不是multipart/form-data类型"),
    PARSE_REQUEST_ERROR(6, "解析上传表单错误"),
    NOTFOUND_UPLOAD_DATA(7, "未找到上传数据"),
    NOT_ALLOW_FILE_TYPE(8, "不允许的文件类型"),
    INVALID_ACTION(101, "无效的Action"),
    CONFIG_ERROR(102, "配置文件初始化失败"),
    PREVENT_HOST(301, "被阻止的远程主机"),
    CONNECTION_ERROR(202, "远程连接出错"),
    REMOTE_FAIL(203, "抓取远程图片失败"),
    NOT_DIRECTORY(301, "指定路径不是目录"),
    NOT_EXIST(302, "指定路径并不存在"),
    ILLEGAL(401, "callback参数名不合法");
    // @formatter:on
    
    private final int index;
    
    private final String name;
    
    private StateEnum(int index, String name)
    {
        this.name = name;
        this.index = index;
    }
}
