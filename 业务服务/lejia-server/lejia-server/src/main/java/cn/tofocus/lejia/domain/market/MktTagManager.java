package cn.tofocus.lejia.domain.market;

import java.util.List;
import java.util.Objects;

import cn.tofocus.lejia.bean.enums.RechargeStatus;
import cn.tofocus.lejia.dao.market.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktTagInfo;
import cn.tofocus.lejia.bean.dto.market.MktTagOnPage;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.enums.member.TagType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MktTagManager
{
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktTagVisibleDao tagVisibleDao;

    @Autowired
    private MktMemberMsdDao memberMsdDao;

    @Autowired
    private MktRechargeCardDao rechargeCardDao;
    
    public PageResult<MktTagOnPage> query(int page, int pagesize, List<TagType> types, String name, String description)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        return tagDao.query(page, pagesize, ascription, types, name, description, MktTagOnPage.class);
    }
    
    public MktTagInfo get(Integer pkey)
    {
        return tagDao.get(pkey, MktTagInfo.class);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MktTagInfo info)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktTag bean = null;
        if (tagDao.exist(info.getName(), info.getPkey(), ascription))
            throw TofocusException.of(LejiaErrCode.TAG_NAME_EXIST);
        if (info.getPkey() == null)
        {
            bean = BeanUtil.beanFrom(MktTag.class, info);
            bean.setIdDel(false);
            bean.setAscription(ascription);
            bean.setCreatedTime(null);
        }
        else
        {
            bean = tagDao.get(info.getPkey());
            if (bean == null)
                throw TofocusException.of(LejiaErrCode.TAG_NOT_FOUND);
            bean.setType(info.getType());
            bean.setName(info.getName());
            bean.setDescription(info.getDescription());
        }
        tagDao.put(bean);
        return true;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean del(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktTag bean = tagDao.get(pkey);
        if (bean == null || !Objects.equals(bean.getAscription(), ascription))
            throw TofocusException.of(LejiaErrCode.TAG_NOT_FOUND);
        if (memberMsdDao.existByTag(pkey))
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "存在该标签的热力豆账户，不允许删除");
        if (rechargeCardDao.existByTag(pkey, RechargeStatus.UNUSED))
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "存在该标签的充值卡密未使用，不允许删除");
        bean.setIdDel(true);
        tagDao.update(bean);
        memberTagDao.removeByTag(pkey);
        tagVisibleDao.removeByTag(pkey);
        return true;
    }
    
    public List<DropIntegerDown> listDrop(List<TagType> types)
    {
        return tagDao.select()
            .eq(MktTag.F.ascription, CurrentSession.ascriptionPkey())
            .in(MktTag.F.type, types)
            .eq(MktTag.F.idDel, false)
            .sort(MktTag.F.createdTime)
            .sort(MktTag.F.pkey)
            .execDto(DropIntegerDown.class);
    }
}
