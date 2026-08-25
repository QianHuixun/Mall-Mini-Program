package cn.tofocus.lejia.dao.zx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.zx.ZxUserInfoDrop;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo;
import cn.tofocus.lejia.bean.entity.zx.ZxUserInfo.F;
import cn.tofocus.lejia.bean.enums.ZxUserType;
import cn.tofocus.lejia.bean.enums.v2.ZxCardStatus;

@Component
public class ZxUserInfoDao extends JpaSpecificationDelegate<Integer, ZxUserInfo>
{
    public ZxUserInfo whateverInfo(Integer ascription)
    {
        return this.selectOne()
            .isNotNull(F.zxUserId)
            //.eq(F.zxStatus, ZxStatus.AUDIT_SUCCESS)
            .eq(F.cardStatus, ZxCardStatus.BINDING_SUCCESS)
            .notEq(F.type, ZxUserType.SYSTEM)
            .exec();
    }
    
    //    public Map<String,String> mapZxUserId(Integer ascription)
    //    {
    //        List<ZxUserInfo> list = this.select()
    //        .isNotNull(F.zxUserId)
    //        .eq(F.zxStatus, ZxStatus.AUDIT_SUCCESS)
    //        .eq(F.cardStatus, ZxCardStatus.BINDING_SUCCESS)
    //        .exec();
    //        Map<String,String> map = new HashMap<>();
    //        list.forEach(e -> map.put(e.getType() + "_" + e.getValue(), e.getZxUserId()));
    //        return map;
    //    }
    
    public Map<String, ZxUserInfo> mapZxUserInfo(Integer ascription)
    {
        List<ZxUserInfo> list =
            this.select().isNotNull(F.zxUserId).eq(F.cardStatus, ZxCardStatus.BINDING_SUCCESS).exec();
        Map<String, ZxUserInfo> map = new HashMap<>();
        list.forEach(e -> map.put(e.getType() + "_" + e.getValue(), e));
        return map;
    }
    
    public Map<String, ZxUserInfo> mapZxUserId(Integer ascription)
    {
        List<ZxUserInfo> list = this.select()
            .isNotNull(F.zxUserId)
            //.eq(F.zxStatus, ZxStatus.AUDIT_SUCCESS)
            .eq(F.cardStatus, ZxCardStatus.BINDING_SUCCESS)
            .exec();
        Map<String, ZxUserInfo> map = new HashMap<>();
        list.forEach(e -> map.put(e.getZxUserId(), e));
        return map;
    }
    
    public ZxUserInfo byZxUserId(String zxUserId)
    {
        return this.selectOne().eq(F.zxUserId, zxUserId).exec();
    }
    
    public <T> T get(Integer pkey, Integer ascription, Class<T> clazz)
    {
        return this.selectOne().eq(F.pkey, pkey).eq(F.ascription, ascription).execDto(clazz);
    }
    
    public ZxUserInfo get(ZxUserType type, String value)
    {
        return this.selectOne().eq(F.type, type).eq(F.value, value).exec();
    }
    
    public <T> T get(ZxUserType type, String value, Integer ascription, Class<T> clazz)
    {
        return this.selectOne().eq(F.type, type).eq(F.value, value).eq(F.ascription, ascription).execDto(clazz);
    }
    
    public ZxUserInfo getByFarmer(String farmerPkey)
    {
        return getByFarmer(farmerPkey, ZxUserInfo.class);
    }
    
    public <T> T getByFarmer(String farmerPkey, Class<T> clazz)
    {
        // @formatter:off
        return this.selectOne()
            .and()
            .or()
                .eq(F.type, ZxUserType.MARKET)
                .eq(F.type, ZxUserType.SELF_MARKET)
            .close()
            .eq(F.value, farmerPkey)
            .done()
            .execDto(clazz);
        // @formatter:on
    }
    
    public <T> PageResult<T> query(int page, int pagesize, Integer ascription, List<ZxUserType> types, String name,
        Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .in(F.type, types)
            .like(F.name, name)
            .notEq(F.delFlag, true)
            .sort(F.pkey, false)
            .execDto(clazz);
    }
    
    public List<ZxUserInfoDrop> listDrop(Integer ascription)
    {
        return this.select()
            .in(F.type, ZxUserType.SYSTEM, ZxUserType.MARKET)
            .isNotNull(F.zxUserId)
            .eq(F.cardStatus, ZxCardStatus.BINDING_SUCCESS)
            .eq(F.ascription, ascription)
            .sort(F.pkey)
            .execDto(ZxUserInfoDrop.class);
    }
}
