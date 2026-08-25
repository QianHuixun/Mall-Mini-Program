package cn.tofocus.lejia.domain.v3;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.v3.PromoteOnPage;
import cn.tofocus.lejia.bean.dto.v3.PromoteUpdDto;
import cn.tofocus.lejia.bean.entity.sys.MktPromote;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.MktPromoteDao;
import cn.tofocus.lejia.exception.LejiaErrCode;

@Component
public class PromoteV3Manager
{
    @Autowired
    private MktPromoteDao promoteDao;
    
    public Integer ins(PromoteUpdDto dto)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktPromote add = BeanUtil.beanFrom(MktPromote.class, dto);
        add.setEnabled(false);
        add.setAscription(ascription);
        add = promoteDao.add(add);
        return add.getPkey();
    }
    
    public Boolean upd(PromoteUpdDto dto)
    {
        if (dto.getPkey() == null) throw TofocusException.of(LejiaErrCode.PKEY_NOT_EMPTY);
        MktPromote promote = promoteDao.get(dto.getPkey());
        if (promote == null) throw TofocusException.of(LejiaErrCode.PROMOTE_ERROR);
        if (Boolean.TRUE.equals(promote.getEnabled())) throw TofocusException.of(LejiaErrCode.ENABLED_ERROR);
        BeanUtils.copyProperties(dto, promote, "createdTime", "enabled");
        promoteDao.update(promote);
        return true;
    }
    
    public Boolean del(Integer pkey)
    {
        MktPromote promote = promoteDao.get(pkey);
        if (promote == null) throw TofocusException.of(LejiaErrCode.PROMOTE_ERROR);
        if (Boolean.TRUE.equals(promote.getEnabled())) throw TofocusException.of(LejiaErrCode.ENABLED_ERROR);
        promoteDao.removeById(pkey);
        return true;
    }
    
    @Transactional
    public Boolean enabled(Integer pkey, Boolean flag)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktPromote promote = promoteDao.get(pkey);
        if (promote == null) throw TofocusException.of(LejiaErrCode.PROMOTE_ERROR);
        if (Boolean.TRUE.equals(flag))
        {
            String farmer = promote.getFarmer();
            List<MktPromote> list = promoteDao.select()
                .iF(StringUtils.isBlank(farmer))
                    .isNull("farmer")
                .eLse()
                    .eq("farmer", farmer)
                .endIf()
                .eq("ascription", ascription).notEq("pkey", pkey).exec();
            for(MktPromote p : list)
                p.setEnabled(false);
            promote.setEnabled(true);
            promoteDao.updateAll(list);
        }
        else
        {
            promote.setEnabled(false);
        }
        promoteDao.update(promote);
        return true;
    }
    
    public PageResult<PromoteOnPage> query(int page, int pagesize, String title, String content)
    {
        return promoteDao.query(page, pagesize, title, content, CurrentSession.ascriptionPkey());
    }
}
