package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.MktSearchKeywordInfo;
import cn.tofocus.lejia.bean.entity.market.MktSearchKeyword;
import cn.tofocus.lejia.bean.entity.market.MktSearchKeyword.F;
import cn.tofocus.lejia.bean.enums.SearchKeywordModule;

@Component
public class MktSearchKeywordDao extends JpaSpecificationDelegate<Integer, MktSearchKeyword>
{
    public PageResult<MktSearchKeywordInfo> query(int page, int pagesize, Integer ascription, String farmer,
        SearchKeywordModule module, String keyword)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.module, module)
            .like(F.keyword, keyword)
            .sort(F.module, false)
            .sort(F.sort, false)
            .sort(F.pkey, false)
            .execDto(MktSearchKeywordInfo.class);
    }
    
    public <T> T get(Integer pkey, String farmer, Integer ascription, Class<T> clazz)
    {
        return this.selectOne().eq(F.pkey, pkey).eq(F.farmer, farmer).eq(F.ascription, ascription).execDto(clazz);
    }
    
    public int maxSort(Integer ascription, String farmer, SearchKeywordModule module, Integer notPkey)
    {
        Number num = this.aggregation()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.module, module)
            .notEq(F.pkey, notPkey)
            .execMax(F.sort);
        return num.intValue();
    }
    
    public List<String> listKeywords(Integer ascription, String farmer, SearchKeywordModule module)
    {
        return this.select()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.module, module)
            .sort(F.sort, false)
            .sort(F.pkey, false)
            .execDto(F.keyword, String.class);
    }
    
    public boolean isKeywordsRepeat(String keyword, SearchKeywordModule module, Integer notPkey, String farmer,
        Integer ascription)
    {
        MktSearchKeyword bean = this.selectOne()
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmer)
            .eq(F.module, module)
            .notEq(F.pkey, notPkey)
            .eq(F.keyword, keyword)
            .exec();
        return bean != null;
    }
}
