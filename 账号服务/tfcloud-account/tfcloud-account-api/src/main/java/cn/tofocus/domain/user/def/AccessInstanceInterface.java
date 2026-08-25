package cn.tofocus.domain.user.def;

import cn.tofocus.common.cachemap.bean.HasPkey;

public interface AccessInstanceInterface<K> extends HasPkey<K>
{
    String getFuncKey();
    
    void setOwnerid(String ownerid);
}
