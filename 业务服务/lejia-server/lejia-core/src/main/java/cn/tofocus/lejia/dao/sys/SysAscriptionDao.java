package cn.tofocus.lejia.dao.sys;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.WeixinConfig;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;

@Component
@DataSourceWithFileUrl
public class SysAscriptionDao extends JpaSpecificationDelegate<Integer, SysAscription>
{
    public List<String> getAccount()
    {
        List<SysAscription> list = this.findAll();
        return list.stream().map(SysAscription::getAccount).collect(Collectors.toList());
    }
    
    public SysAscription byAccount(String account)
    {
        return this.selectOne().eq("account", account).exec();
    }
    
    public WeixinConfig getWxConfig(Integer pkey)
    {
        SysAscription ascription = this.get(pkey);
        WeixinConfig wxc = new WeixinConfig();
        wxc.setPkey(pkey);
        wxc.setAPP_ID(ascription.getConfigAppid());
        wxc.setAB_NAME(ascription.getConfigAbname());
        wxc.setMCH_ID(ascription.getConfigMchid());
        wxc.setRE_URL(ascription.getConfigReurl());
        wxc.setFULL_NAME(ascription.getConfigFullname());
        wxc.setConfigKey(ascription.getConfigKey());
        wxc.setConfigLocalpath(ascription.getConfigLocalpath());
        wxc.setConfigPassword(ascription.getConfigPassword());
        return wxc;
    }
    
    public SysAscription byAppid(String appid)
    {
        return this.selectOne().eq("configAppid", appid).exec();
    }
}
