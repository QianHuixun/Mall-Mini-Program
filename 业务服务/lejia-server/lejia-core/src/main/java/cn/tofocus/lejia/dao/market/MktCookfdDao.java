package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
@DataSourceWithFileUrl
public class MktCookfdDao extends JpaSpecificationDelegate<Integer, MktCookfd> {
    public PageResult<MktCookfd> queryAppCookfd(int page, int pagesize, String name, Integer ctype,Boolean recom, Boolean hot) {
        String farmerPkey = MobileSession.farmerPkey();
        if (!StringUtils.isNotBlank(farmerPkey)) {
            throw TofocusException.of(WsaleErrCode.UNKOWN_MARKET);
        }
        SelectPageBuilder<Integer, MktCookfd> builder = selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("idDel", false)
                .eq("farmer", farmerPkey)
                .eq("enabled", true);
        if (StringUtils.isNotBlank(name))
            builder.like("name", name);
        if(ctype != null)
        	builder.eq("ctype", ctype);
        if (recom != null)
            builder.eq("recom", recom);
        if (hot != null && hot) {
            builder.sort("viewCount", true);
            builder.sort("collCount", true);
        } else {
            builder.sort("sort", false);
        }
        PageResult<MktCookfd> pageResult = builder.exec();
        return pageResult;
    }
    
    
    public MktCookfd getCookfd(Integer pkey) 
    {
    	return selectOne().eq("pkey", pkey).eq("idDel", false).exec();
    }
    
    public PageResult<MktCookfd> queryCookfd(int page, int pagesize, String name, Boolean recom, Boolean enabled, String marketPkey, Integer ctype, Integer ascription) 
    {
    	SelectPageBuilder<Integer, MktCookfd> builder = selectPage()
                .page(page)
                .pagesize(pagesize)
                .eq("idDel", false)
                .sort("sort", false);
    	if(!(Constant.Operation + ascription).equals(marketPkey))
    		builder.eq("farmer", marketPkey);
        if (StringUtils.isNotBlank(name))
            builder.like("name", name);
        if (recom != null)
            builder.eq("recom", recom);
        if (enabled != null)
            builder.eq("enabled", enabled);
        if(ctype != null)
        	builder.eq("ctype", ctype);
        return builder.exec();
    }
    
    public List<MktCookfd> getRecom(Boolean flag, String marketPkey)
    {
    	return select().eq("farmer", marketPkey).eq("idDel", false).eq("recom", flag).exec();
    }
}
