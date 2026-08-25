package cn.tofocus.lejia.dao.market;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.entity.market.MktAddr.F;
import cn.tofocus.lejia.bean.enums.AddrType;

@Component
public class MktAddrDao extends JpaSpecificationDelegate<Integer, MktAddr>
{
    public boolean existByMember(Integer member, AddrType type)
    {
        return this.selectOne().eq(F.member, member).eq(F.type, type).exec() != null;
    }
    
    public <T> PageResult<T> query(int page, int pagesize, Integer ascription, Integer member, AddrType type,
        Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .eq(F.member, member)
            .eq(F.type, type)
            .sort(F.defaultAddr)
            .sort(F.pkey, true)
            .execDto(clazz);
    }
    
    public MktAddr getDefaultAddrPickup(Integer member)
    {
        MktAddr addr = this.selectOne().eq(F.member, member).eq(F.type, AddrType.PICKUP).eq("defaultAddr", true).exec();
        if(addr != null)
            return addr;
        return this.selectOne().eq(F.member, member).eq(F.type, AddrType.PICKUP).exec();
    }

    public MktAddr getDefaultAddrDelivery(Integer member)
    {
        MktAddr addr = this.selectOne().eq(F.member, member).eq(F.type, AddrType.DELIVERY).eq("defaultAddr", true).exec();
        if(addr != null)
            return addr;
        return this.selectOne().eq(F.member, member).eq(F.type, AddrType.DELIVERY).exec();
    }

    public MktAddr getAddrDelivery(Integer member, String addr)
    {
        MktAddr bean = this.selectOne().eq(F.member, member).eq(F.addr, addr).eq(F.type, AddrType.DELIVERY).eq("defaultAddr", true).exec();
        if(bean != null)
            return bean;
        MktAddr exec = this.selectOne().eq(F.member, member).eq(F.addr, addr).eq(F.type, AddrType.DELIVERY).exec();
        if(exec != null)
            return exec;
        return this.selectOne().eq(F.member, member).eq(F.type, AddrType.DELIVERY).exec();
    }
}