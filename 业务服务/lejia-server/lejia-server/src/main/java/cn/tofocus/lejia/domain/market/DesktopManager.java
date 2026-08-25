package cn.tofocus.lejia.domain.market;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.DesktopOnInfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsBox;
import cn.tofocus.lejia.bean.entity.market.MktDesktop;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsBoxDao;
import cn.tofocus.lejia.dao.market.MktDesktopDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DesktopManager
{
    @Autowired
    private MktDesktopDao desktopDao;
    
    @Autowired
    private MktGoodsBoxDao goodsBoxDao;
    
    public PageResult<DesktopOnInfo> query(Integer page, Integer pagesize, String name)
    {
        String marketPkey = CurrentSession.marketPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        return desktopDao.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq("farmer", marketPkey)
            .eq("ascription", ascription)
            .like("name", name)
            .sort("pkey")
            .execDto(DesktopOnInfo.class);
    }
    
    public Boolean put(@RequestBody DesktopOnInfo info)
    {
        String marketPkey = CurrentSession.marketPkey();
        String companyPkey = CurrentSession.companyPkey();
        Integer ascription = CurrentSession.ascriptionPkey();
        MktDesktop d = new MktDesktop();
        long count = desktopDao.aggregation()
        .eq("farmer", marketPkey)
        .eq("ascription", ascription)
        .eq("name", info.getName())
        .notEq("pkey", info.getPkey())
        .execCount();
        if(count > 0)
            throw TofocusException.of(LejiaErrCode.DESKTOP_ERROR);
        d.setPkey(info.getPkey());
        d.setName(info.getName());
    
        d.setAscription(ascription);
        d.setFarmer(marketPkey);
        d.setCompany(companyPkey);
        MktDesktop put = desktopDao.put(d);
        if(StringUtils.isBlank(put.getQrCode()))
        {
            String qrCode = "https://small.xinanshizu.com/desktop_"+ ascription + "?pkey=" + put.getPkey();
            if(ascription == 22)
                qrCode = "https://small.xinanshizu.com/desktop_test_"+ ascription + "?pkey=" + put.getPkey();
            put.setQrCode(qrCode);
            desktopDao.update(put);
        }
        List<MktGoodsBox> list = goodsBoxDao.select().eq("desktop", put.getPkey()).exec();
        if(!list.isEmpty())
        {
            for(MktGoodsBox gb : list)
            {
                gb.setDesktopName(put.getName());
            }
            goodsBoxDao.updateAll(list);
        }
        return true;
    }
    
    public Boolean del(Integer pkey)
    {
        return desktopDao.removeById(pkey);
    }
    
}
