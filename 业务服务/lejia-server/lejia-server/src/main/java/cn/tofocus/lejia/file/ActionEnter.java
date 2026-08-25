package cn.tofocus.lejia.file;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONException;

import cn.tofocus.file.api.v3.FileApiV3;
import cn.tofocus.lejia.file.define.ActionMap;
import cn.tofocus.lejia.file.define.AppInfo;
import cn.tofocus.lejia.file.define.BaseState;
import cn.tofocus.lejia.file.define.State;

public class ActionEnter
{
    
    private HttpServletRequest request = null;
    
    private String rootPath = null;
    
    private String contextPath = null;
    
    private String actionType = null;
    
    private FileApiV3 fileApiV3 = null;
    
    private ConfigManager configManager = null;
    
    public ActionEnter(HttpServletRequest request, String rootPath, FileApiV3 fileApiV3)
    {
        
        this.request = request;
        this.rootPath = rootPath;
        this.actionType = request.getParameter("action");
        this.contextPath = request.getContextPath();
        this.configManager = ConfigManager.getInstance(this.rootPath, this.contextPath, request.getRequestURI());
        this.fileApiV3 = fileApiV3;
    }
    
    public String exec()
    {
        
        String callbackName = this.request.getParameter("callback");
        
        if (callbackName != null)
        {
            
            if (!validCallbackName(callbackName))
            {
                return new BaseState(false, AppInfo.ILLEGAL).toJSONString();
            }
            
            return callbackName + "(" + this.invoke() + ");";
            
        }
        else
        {
            return this.invoke();
        }
        
    }
    
    public String invoke()
        throws JSONException
    {
        
        if (actionType == null || !ActionMap.mapping.containsKey(actionType))
        {
            return new BaseState(false, AppInfo.INVALID_ACTION).toJSONString();
        }
        
        if (this.configManager == null || !this.configManager.valid())
        {
            return new BaseState(false, AppInfo.CONFIG_ERROR).toJSONString();
        }
        
        State state = null;
        
        int actionCode = ActionMap.getType(this.actionType);
        
        Map<String, Object> conf = null;
        
        switch (actionCode)
        {
            
            case ActionMap.CONFIG:
                return this.configManager.getAllConfig().toString();
            case ActionMap.UPLOAD_IMAGE:
            case ActionMap.UPLOAD_SCRAWL:
            case ActionMap.UPLOAD_VIDEO:
            case ActionMap.UPLOAD_FILE:
                conf = this.configManager.getConfig(actionCode);
                state = new Uploader(request, conf, fileApiV3).doExec();
                break;
            
            case ActionMap.CATCH_IMAGE:
                conf = configManager.getConfig(actionCode);
                String[] list = this.request.getParameterValues((String)conf.get("fieldName"));
                state = new ImageHunter(conf).capture(list);
                break;
            
            case ActionMap.LIST_IMAGE:
            case ActionMap.LIST_FILE:
                conf = configManager.getConfig(actionCode);
                int start = this.getStartIndex();
                state = new FileManager(conf).listFile(start);
                break;
            
        }
        
        return state.toJSONString();
        
    }
    
    public int getStartIndex()
    {
        
        String start = this.request.getParameter("start");
        
        try
        {
            return Integer.parseInt(start);
        }
        catch (Exception e)
        {
            return 0;
        }
        
    }
    
    /**
     * callback参数验证
     */
    public boolean validCallbackName(String name)
    {
        
        if (name.matches("^[a-zA-Z_]+[\\w0-9_]*$"))
        {
            return true;
        }
        
        return false;
        
    }
    
}