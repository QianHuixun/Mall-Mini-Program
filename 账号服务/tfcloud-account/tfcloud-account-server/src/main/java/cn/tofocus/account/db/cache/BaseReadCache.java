package cn.tofocus.account.db.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import cn.tofocus.common.Constant;
import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.common.cachemap.read.BaseReadOnlyKvCacheMapNotified;
import cn.tofocus.core.data.KeyName;
import cn.tofocus.db.DataRead;

public abstract class BaseReadCache<K, T extends KeyName<K>, E extends HasPkey<K>>
    extends BaseReadOnlyKvCacheMapNotified<K, T>
{
    private DataRead<K, T> dao;
    
    @Override
    public void init()
    {
        dao = new DataRead<K, T>()
        {
            @Override
            public boolean isExistKey(K key)
            {
                return getDbAccess().isExistKey(key);
            }
            
            @Override
            public T get(K key)
            {
                E entity = getDbAccess().get(key);
                return convert(entity);
            }
            
            @Override
            public List<T> get(Collection<K> keys)
            {
                if (keys == null)
                    return null;
                else
                {
                    List<T> l = new ArrayList<>();
                    List<E> entitys = getDbAccess().get(keys);
                    for (E e : entitys)
                    {
                        l.add(convert(e));
                    }
                    return l;
                }
            }
            
            @Override
            public Class<K> getKeyClass()
            {
                return getKClass();
            }
            
            @Override
            public Class<T> getValueClass()
            {
                return getKVClass();
            }
        };
        super.init();
    }
    
    protected abstract Class<K> getKClass();
    
    protected abstract Class<T> getKVClass();
    
    protected abstract DataRead<K, E> getDbAccess();
    
    protected abstract T convert(E entity);
    
    @Override
    protected DataRead<K, T> dataQuery()
    {
        return dao;
    }
    
    @Override
    protected String domain()
    {
        return Constant.TfDomain;
    }
    
    @Override
    public long getCacheTimeout()
    {
        return 86400000L;
    }
}
