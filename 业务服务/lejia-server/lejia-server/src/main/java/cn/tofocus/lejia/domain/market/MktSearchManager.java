package cn.tofocus.lejia.domain.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktSearchKeywordInfo;
import cn.tofocus.lejia.bean.entity.market.MktSearchKeyword;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktSearchKeywordDao;
import cn.tofocus.lejia.exception.LejiaErrCode;

@Component
public class MktSearchManager
{
    @Autowired
    private MktSearchKeywordDao mktSearchKeywordDao;
    
    public PageResult<MktSearchKeywordInfo> query(int page, int pagesize, SearchKeywordModule module, String keyword)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        return mktSearchKeywordDao.query(page, pagesize, ascription, farmer, module, keyword);
    }
    
    public MktSearchKeywordInfo get(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        return mktSearchKeywordDao.get(pkey, farmer, ascription, MktSearchKeywordInfo.class);
    }
    
    public boolean save(MktSearchKeywordInfo info)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        MktSearchKeyword bean = null;
        // 新增
        if (info.getPkey() == null)
        {
            bean = new MktSearchKeyword();
            bean.setAscription(ascription);
            bean.setFarmer(farmer);
            bean.setModule(info.getModule());
        }
        // 编辑
        else
        {
            bean = mktSearchKeywordDao.get(info.getPkey(), farmer, ascription, MktSearchKeyword.class);
            if (bean == null)
                throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到搜索词");
        }
        bean.setKeyword(info.getKeyword());
        bean.setSort(info.getSort());
        // 关键词查重
        if (mktSearchKeywordDao.isKeywordsRepeat(bean
            .getKeyword(), bean.getModule(), bean.getPkey(), bean.getFarmer(), bean.getAscription()))
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "关键词已存在");
        if (bean.getSort() == null)
        {
            int maxSort =
                mktSearchKeywordDao.maxSort(bean.getAscription(), bean.getFarmer(), bean.getModule(), bean.getPkey());
            bean.setSort(maxSort + 1);
        }
        mktSearchKeywordDao.put(bean);
        return true;
    }
    
    public boolean del(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String farmer = CurrentSession.marketPkey();
        MktSearchKeyword bean = mktSearchKeywordDao.get(pkey, farmer, ascription, MktSearchKeyword.class);
        if (bean == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到搜索词");
        mktSearchKeywordDao.remove(bean);
        return true;
    }
}
