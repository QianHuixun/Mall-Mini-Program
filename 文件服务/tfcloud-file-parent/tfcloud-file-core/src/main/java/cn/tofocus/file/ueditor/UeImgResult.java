package cn.tofocus.file.ueditor;

import cn.tofocus.file.bean.FileInfoV3;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UeImgResult extends UeResult
{
    private String name;//: "1648023586578098257.png"
    
    private String original;//: "微信图片_20200415164756.png"
    
    private long size;//: "10544"
    
    private String title;//: "1648023586578098257.png"
    
    private String type;//: ".png"
    
    private String url;//: "https://zhccfilebak.szrcb.com/file/uploads/image/20220323/1648023586578098257.png"
    
    public UeImgResult(StateEnum s)
    {
        super(s);
    }
    
    public UeImgResult(FileInfoV3 info)
    {
        super(StateEnum.SUCCESS);
        original = info.getFileName();
        name = info.getFileName();
        size = info.getSize();
        title = info.getFileName();
        url = info.getUrl();
        type = "." + info.getExtName();
    }
    
}
