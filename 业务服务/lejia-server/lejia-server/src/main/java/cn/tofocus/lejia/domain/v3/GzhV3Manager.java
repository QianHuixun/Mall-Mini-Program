package cn.tofocus.lejia.domain.v3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.common.util.PageUtil;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.v3.GzhUserOnInfo;
import cn.tofocus.lejia.bean.entity.wx.MktGzhAssociate;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.wx.MktGzhAssociateDao;
import cn.tofocus.lejia.dao.wx.MktGzhDao;

@Component
public class GzhV3Manager
{
    @Autowired
    private MktGzhDao gzhDao;
    
    @Autowired
    private MktGzhAssociateDao gzhAssociateDao;
    
    public PageResult<GzhUserOnInfo> queryGzh(int page, int pagesize, String name, Boolean enabled)
    {
        PageResult<GzhUserOnInfo> res;
        if (enabled != null)
        {
            List<MktGzhAssociate> list = gzhAssociateDao.select() 
                .eq("farmer", CurrentSession.marketPkey())
                .eq("ascription", CurrentSession.ascriptionPkey())
                .eq("enabled", true)
                .exec();
            List<Integer> keyList = new ArrayList<>();
            list.forEach(e -> keyList.add(e.getGzh()));
            if(Boolean.TRUE.equals(enabled))
            {
                if(list.isEmpty())
                    return PageUtil.page(new ArrayList<>(), PageParameter.of(page, pagesize));
                res = gzhDao.selectPage()
                    .page(page)
                    .pagesize(pagesize)
                    .like("name", name)
                    .in("pkey", keyList)
                    .eq("ascription", CurrentSession.ascriptionPkey())
                    .sort("createdTime")
                    .execDto(GzhUserOnInfo.class);
            }
            else
            {
                res = gzhDao.selectPage()
                    .page(page)
                    .pagesize(pagesize)
                    .like("name", name)
                    .notIn("pkey", keyList)
                    .eq("ascription", CurrentSession.ascriptionPkey())
                    .sort("createdTime")
                    .execDto(GzhUserOnInfo.class);
            }
        }
        else
        {
            res = gzhDao.selectPage()
                .page(page)
                .pagesize(pagesize)
                .like("name", name)
                .eq("ascription", CurrentSession.ascriptionPkey())
                .sort("createdTime")
                .execDto(GzhUserOnInfo.class);
        }
        for(GzhUserOnInfo g : res.getContent())
        {
            MktGzhAssociate ga = gzhAssociateDao.getGA(CurrentSession.marketPkey(), g.getPkey());
            if(ga != null)
            {
                g.setEnabled(ga.getEnabled());
            }
            else
                g.setEnabled(false);
        }
        List<GzhUserOnInfo> content = new ArrayList<>();
        content.addAll(res.getContent());
        Collections.sort(content, new Comparator<GzhUserOnInfo>()
        {
            @Override
            public int compare(GzhUserOnInfo o1, GzhUserOnInfo o2)
            {
                Boolean e1 = o1.getEnabled();
                Boolean e2 = o2.getEnabled();
                if (e1 ^ e2)
                {
                    return e1 ? -1 : 1;
                }
                else
                    return 0;
            }
        });
        res.setContent(content);
        return res;
    }
    
    public Boolean enabled(Integer pkey, Boolean enabled)
    {
        MktGzhAssociate associate = gzhAssociateDao.getGA(CurrentSession.marketPkey(), pkey);
        if(associate == null)
        {
            associate = new MktGzhAssociate();
            associate.setFarmer(CurrentSession.marketPkey());
            associate.setGzh(pkey);
            associate.setAscription(CurrentSession.ascriptionPkey());
        }
        associate.setEnabled(enabled);
        gzhAssociateDao.put(associate);
        return true;
    }
    
}
