package cn.tofocus.account.command.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import lombok.Data;

@Data
public class MenuEntityTree
{
    @Size(max = 40)
    private String pkey;
    
    //名称
    @Size(max = 40)
    private String name;
    
    //描述
    @Size(max = 100)
    private String description;
    
    private String domainid;
    
    private String modelId;
    
    private String appid;
    
    private String parentid;
    
    private MenuType type;
    
    private Integer sort;
    
    private boolean enable = true;
    
    @Valid
    private List<MenuEntityTree> sub;
    
    @Valid
    private List<MenuEntityTree> buttons;
    
    public String getCode()
    {
        return pkey;
    }
    
    public MenuEntityTree()
    {
        
    }
    
    public MenuEntityTree(String pkey, String name, String description, MenuType type, List<MenuEntityTree> sub)
    {
        super();
        this.pkey = pkey;
        this.name = name;
        this.description = description;
        this.type = type;
        this.sub = sub;
    }
    
    public MenuEntityTree(String pkey, String name, String description, MenuType type, List<MenuEntityTree> sub,
        List<MenuEntityTree> buttons)
    {
        super();
        this.pkey = pkey;
        this.name = name;
        this.description = description;
        this.type = type;
        this.sub = sub;
        this.buttons = buttons;
    }
    
    public boolean isLeaf()
    {
        return sub == null || sub.isEmpty();
    }
    
    public void check(Set<String> menuKeySet)
    {
        if (StringUtil.isEmpty(pkey))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "AppMenu pkey");
        if (type == null)
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "AppMenu type");
        if (StringUtil.isEmpty(name))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "AppMenu name");
        if (StringUtil.isEmpty(appid))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "AppMenu appid");
        if (StringUtil.isEmpty(domainid))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_NULL, "AppMenu domainid");
        if (menuKeySet.contains(pkey))
            throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, "AppMenu pkey 重复 " + pkey);
        else
            menuKeySet.add(pkey);
        if (sub != null)
        {
            for (MenuEntityTree m : sub)
            {
                m.check(menuKeySet);
            }
        }
        if (buttons != null)
        {
            for (MenuEntityTree m : buttons)
            {
                m.check(menuKeySet);
            }
        }
    }
    
    public static AppMenuBuilder builder()
    {
        AppMenuBuilder build = new AppMenuBuilder();
        return build;
    }
    
    public static class AppMenuBuilder
    {
        private MenuEntityTree r;
        
        private AppMenuBuilder()
        {
            r = new MenuEntityTree();
        }
        
        public MenuEntityTree build()
        {
            return r;
        }
        
        public AppMenuBuilder pkey(String pkey)
        {
            r.pkey = pkey;
            return this;
        }
        
        public AppMenuBuilder name(String name)
        {
            r.name = name;
            return this;
        }
        
        public AppMenuBuilder add(MenuEntityTree var)
        {
            return this;
        }
        
        public AppMenuBuilder type(MenuType type)
        {
            r.type = type;
            return this;
        }
        
        public AppMenuBuilder sub(List<MenuEntityTree> sub)
        {
            r.sub = sub;
            return this;
        }
        
        public AppMenuBuilder addSub(MenuEntityTree appMenu)
        {
            if (r.sub == null)
                r.sub = new ArrayList<>();
            r.sub.add(appMenu);
            return this;
        }
        
        public AppMenuBuilder buttons(List<MenuEntityTree> buttons)
        {
            r.buttons = buttons;
            return this;
        }
        
        public AppMenuBuilder addButtons(MenuEntityTree button)
        {
            if (r.buttons == null)
                r.buttons = new ArrayList<>();
            r.buttons.add(button);
            return this;
        }
    }

    public void toEntity(List<MenuEntity> result)
    {
        MenuEntity entity = new MenuEntity();
        entity.setPkey(pkey);
        entity.setName(name);
        entity.setDomainid(domainid);
        entity.setAppid(appid);
        entity.setDescription(description);
        entity.setModelId(modelId);
        entity.setSort(sort);
        entity.setType(type);
        entity.setEnable(enable);
        entity.setParentid(parentid);
        result.add(entity);
        if (sub != null)
        {
            for (MenuEntityTree m : sub)
            {
                m.toEntity(result);
            }
        }
        if (buttons != null)
        {
            for (MenuEntityTree m : buttons)
            {
                m.toEntity(result);
            }
        }
    }
}
