package cn.tofocus.file.bean;

public enum UploadType
{
    file, image, video, doc;
    
    public String viewUrl()
    {
        switch (this)
        {
            case doc:
                return Constant.docUrl;
            case file:
                break;
            case image:
                return Constant.imgUrl;
            case video:
                return Constant.videoUrl;
            default:
                break;
        }
        return Constant.fileDownLoadUrl;
    }
    
    public String downUrl()
    {
        switch (this)
        {
            case doc:
                return Constant.docDownLoadUrl;
            case file:
                break;
            case image:
                return Constant.imgDownLoadUrl;
            case video:
                return Constant.videoDownLoadUrl;
            default:
                break;
        }
        return Constant.fileDownLoadUrl;
    }
}
