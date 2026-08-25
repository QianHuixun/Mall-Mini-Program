package cn.tofocus.file.exception;

import cn.tofocus.core.exception.ErrCode;

public enum FileErrCode implements ErrCode
{
    //@formatter:off
    FILE_RECORD_NOT_EXIST("00300001","文件不存在，秒传失败，请使用普通上传"),
    
    FILE_CHECK_FAIL("00300002","文件MD5校验失败"),

    DOWNLOAD_FAIL("00300003","下载文件失败"),

    THUMB_RESIZE_FAIL("00300004","缩略图转换失败"),

    UPLOAD_FAIL("00300005","文件上传失败"),

    UNAUTHENTICATION_APP("00300006","没有权限"),

    FILE_DOWNLOAD_CHECK_FAIL("00300008","验证失败，文件禁止访问"),
    BACKUP_RUNNING("00300009","备份或还原任务已经在执行"),
    BACKUP_FAIL("00300010","备份失败"),
    RESTORE_FAIL("00300011","还原失败"),
    CLEAR_RUNNING("00300012","清理任务还在执行"),
    CLEAR_FAIL("00300013","清理失败"),
    ;
    //@formatter:on
    
    private final String code;
    
    private final String description;
    
    private FileErrCode(String code, String description)
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
