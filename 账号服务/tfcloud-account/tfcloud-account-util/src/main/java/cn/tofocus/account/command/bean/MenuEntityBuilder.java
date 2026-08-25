package cn.tofocus.account.command.bean;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.core.enums.MenuType;

public class MenuEntityBuilder
{
    private MenuEntityTree root;
    
    private Deque<MenuEntityTree> menuDeque;
    
    private MenuEntityTree current;
    
    private String domainid;
    
    private String appid;
    
    private int sort = 0;
    
    public MenuEntityBuilder(String domainid, String appid)
    {
        this.domainid = domainid;
        this.appid = appid;
        root = new MenuEntityTree();
        menuDeque = new ArrayDeque<>();
        menuDeque.push(root);
        current = root;
    }
    
    public MenuEntityBuilder model(String pkey, String name)
    {
        return addMenu(pkey, name, MenuType.model, null);
    }
    
    public MenuEntityBuilder menu(String pkey, String name)
    {
        return addMenu(pkey, name, MenuType.menu, null);
    }
    
    public MenuEntityBuilder button(String pkey, String name)
    {
        return addMenu(pkey, name, MenuType.button, null);
    }

    public MenuEntityBuilder model(String pkey, String name, String modelId)
    {
        return addMenu(pkey, name, MenuType.model, modelId);
    }
    
    public MenuEntityBuilder menu(String pkey, String name, String modelId)
    {
        return addMenu(pkey, name, MenuType.menu, modelId);
    }
    
    public MenuEntityBuilder button(String pkey, String name, String modelId)
    {
        return addMenu(pkey, name, MenuType.button, modelId);
    }
    
    private MenuEntityBuilder addMenu(String pkey, String name, MenuType type, String modelId)
    {
        MenuEntityTree menu = new MenuEntityTree();
        menu.setPkey(pkey);
        menu.setName(name);
        menu.setType(type);
        menu.setAppid(appid);
        menu.setModelId(modelId);
        menu.setDomainid(domainid);
        menu.setSort(sort);
        menu.setEnable(true);
        
        MenuEntityTree parent = menuDeque.peek();
        menu.setParentid(parent.getPkey());
        List<MenuEntityTree> list = parent.getSub();
        if (list == null)
        {
            list = new ArrayList<>();
            parent.setSub(list);
            list.add(menu);
        }
        else
        {
            list.add(menu);
        }
        current = menu;
        sort++;
        return this;
    }
    
    public MenuEntityBuilder description(String description)
    {
        current.setDescription(description);
        return this;
    }
    
    public MenuEntityBuilder sub()
    {
        menuDeque.push(current);
        current = null;
        return this;
    }
    
    public MenuEntityBuilder done()
    {
        current = menuDeque.pop();
        current.check(new HashSet<>());
        return this;
    }
    
    public List<MenuEntity> build()
    {
        List<MenuEntity> result = new ArrayList<>();
        List<MenuEntityTree> sub = root.getSub();
        for(MenuEntityTree t : sub)
        {
            t.toEntity(result);
        }
        return result;
    }
    
}
