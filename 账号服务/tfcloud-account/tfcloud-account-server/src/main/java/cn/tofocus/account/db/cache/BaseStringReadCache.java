package cn.tofocus.account.db.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.core.data.NamedBean;
import cn.tofocus.core.data.StrKeyName;
import cn.tofocus.domain.cache.NamedCache;

public abstract class BaseStringReadCache<T extends StrKeyName, E extends HasPkey<String>> extends BaseReadCache<String, T, E>
    implements NamedCache<String>
{
    
    @Override
    protected Class<String> getKClass()
    {
        return String.class;
    }
    
    @Override
    public String getValueName(String key)
    {
        T kv = this.get(key);
        if (kv != null)
            return kv.getName();
        else
            return null;
    }
    
    @Override
    public List<String> getValueNames(Collection<String> keys)
    {
        List<String> list = new ArrayList<>();
        List<T> l = this.get(keys);
        for (T kv : l)
        {
            list.add(kv.getName());
        }
        return list;
    }

    public NamedBean getNamedBean(String key)
    {
        T kv = this.get(key);
        if (kv != null)
        {
            NamedBean b = new NamedBean(key, kv.getName());
            return b;
        }
        else
            return null;
    }
}
