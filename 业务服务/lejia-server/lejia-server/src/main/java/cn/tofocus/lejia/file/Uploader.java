package cn.tofocus.lejia.file;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.tofocus.file.api.v3.FileApiV3;
import cn.tofocus.lejia.domain.market.VendorManager;
import cn.tofocus.lejia.file.define.State;

public class Uploader
{
    private HttpServletRequest request = null;
    
    private Map<String, Object> conf = null;
    
    private FileApiV3 fileApiV3 = null;
    
    public Uploader(HttpServletRequest request, Map<String, Object> conf, FileApiV3 fileApiV3)
    {
        this.request = request;
        this.conf = conf;
        this.fileApiV3 = fileApiV3;
    }
    
    public final State doExec()
    {
        String filedName = (String)this.conf.get("fieldName");
        State state = null;
        
        if ("true".equals(this.conf.get("isBase64")))
        {
            state = Base64Uploader.save(this.request.getParameter(filedName), this.conf);
        }
        else
        {
            VendorManager v = new VendorManager();
            state = v.uploadImage(this.request, this.conf, this.fileApiV3);
            //			state = BinaryUploader.save(this.request, this.conf);
        }
        
        return state;
    }
}
