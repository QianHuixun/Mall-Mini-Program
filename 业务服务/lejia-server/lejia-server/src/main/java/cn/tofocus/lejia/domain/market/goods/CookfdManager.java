package cn.tofocus.lejia.domain.market.goods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktCookfdOnList;
import cn.tofocus.lejia.bean.dto.market.MktCookfdUpDTO;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import cn.tofocus.lejia.bean.entity.market.MktCookfdLine;
import cn.tofocus.lejia.bean.entity.market.MktCookfdType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktCookfdDao;
import cn.tofocus.lejia.dao.market.MktCookfdLineDao;
import cn.tofocus.lejia.dao.market.MktCookfdTypeDao;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CookfdManager {
    @Autowired
    private MktCookfdDao cookfdDao;
    @Autowired
    private MktCookfdLineDao cookfdLineDao;
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    @Autowired
    private MktGoodsDao goodsDao;
    @Autowired
    private MktCookfdTypeDao ctypeDao;
    
    @Transactional
    public Integer insCookfd(MktCookfdOnList entity) {
        MktCookfd cookfd = BeanUtil.beanFrom(MktCookfd.class, entity);
        cookfd.setRowVension(1);
        cookfd.setIdDel(false);
        if (entity.getEnabled() == null)
            cookfd.setEnabled(true);
        if (entity.getSort() == null)
            cookfd.setSort(0);
        cookfd.setCollCount(0);
        cookfd.setViewCount(0);
        cookfd.setCompany(CurrentSession.companyPkey());
        cookfd.setFarmer(CurrentSession.marketPkey());
        cookfd.setAscription(CurrentSession.ascriptionPkey());

        MktCookfd add = cookfdDao.add(cookfd);
        Integer cookfdPkey = add.getPkey();
        if (entity.getLines() != null && entity.getLines().size() > 0) {
            for (MktCookfdLine bean : entity.getLines()) {
                bean.setCookfd(cookfdPkey);
            }
            cookfdLineDao.addAll(entity.getLines());
        }
        return add.getPkey();
    }

    public MktCookfdOnList getCookfd(Integer pkey) {
        MktCookfd cookfd = cookfdDao.getCookfd(pkey);
        if (cookfd == null)
            throw TofocusException.of(WsaleErrCode.UNKOWN_COOKFD);
        MktCookfdOnList result = BeanUtil.beanFrom(MktCookfdOnList.class, cookfd);
        MktCookfdType ctype = ctypeDao.getCookfdType(result.getCtype());
        if(ctype != null)
        	result.setCtypeName(ctype.getName());
        assembleLines(Arrays.asList(result));
        return result;
    }

    public PageResult<MktCookfdOnList> queryCookfd(int page, int pagesize, String name, Boolean recom, Boolean enabled, Integer ctype) {
        String marketPkey = CurrentSession.marketPkey();
    	log.info("菜谱query-marketPkey: {}", marketPkey);
        PageResult<MktCookfd> pageResult = cookfdDao.queryCookfd(page, pagesize, name, recom, enabled, marketPkey, ctype, CurrentSession.ascriptionPkey());
        PageResult<MktCookfdOnList> result = BeanUtil.beanPageFrom(MktCookfdOnList.class, pageResult);
        for(MktCookfdOnList dto : result.getContent())
        {
        	MktCookfdType ctypeBean = ctypeDao.getCookfdType(dto.getCtype());
            if(ctypeBean != null)
            	 dto.setCtypeName(ctypeBean.getName());
        }
        assembleLines(result.getContent());
        return result;
    }

    // 组装lines
    private void assembleLines(List<MktCookfdOnList> list)
    {
    	for(MktCookfdOnList bean : list)
    	{
    		List<MktCookfdLine> lines = cookfdLineDao.select().eq("cookfd", bean.getPkey()).exec();
    		bean.setLines(lines);
    		for(MktCookfdLine line : bean.getLines())
    		{
    			MktGoods goods = goodsDao.selectOne().eq("pkey", line.getGoods()).eq("idDel", false).exec();
    			if(goods == null)
    				line.setGoodsName("该商品不存在或者已经被删除");
    			else
    				line.setGoodsName(goods.getTitle());
    			MktGoodsSpace space = goodsSpaceDao.get(line.getSpace());
    			if(space == null)
    				line.setSpaceName("该规格不存在或者已经被删除");
    			else
    				line.setSpaceName(space.getSpace());
    			
    		}
    	}
    }
    
    public Boolean delCookfd(Integer pkey) {
        MktCookfd cookfd = cookfdDao.getCookfd(pkey);
        if (cookfd.getEnabled())
            throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        cookfd.setIdDel(true);
        cookfdDao.update(cookfd);
        return true;
    }

    @Transactional
    public Boolean updCookfd(MktCookfdUpDTO entity) {
        MktCookfd cookfd = cookfdDao.getCookfd(entity.getPkey());
        if (cookfd == null)
            throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        entity.conversionCookfd(cookfd, entity);
        log.info("updCookfd-cookfd: {}", cookfd);
        cookfdDao.update(cookfd);
        List<MktCookfdLine> lines = entity.getLines();
        if (lines.size() <= 0)
            return true;
        List<MktCookfdLine> addLines = new ArrayList<>();
        List<MktCookfdLine> upLines = new ArrayList<>();
        List<Integer> delLines = new ArrayList<>();
        for (MktCookfdLine cl : lines) {
            if (cl.getStatus() == 1)
            {
            	cl.setCookfd(cookfd.getPkey());
            	addLines.add(cl);
            }
            if (cl.getStatus() == 2)
                upLines.add(cl);
            if (cl.getStatus() == 3)
                delLines.add(cl.getPkey());
        }
        cookfdLineDao.addAll(addLines);
        cookfdLineDao.updateAll(upLines);
        cookfdLineDao.removeAllById(delLines);
        return true;
    }

    public Boolean enabledCookfd(Integer pkey, Boolean flag) {
        MktCookfd cookfd = cookfdDao.get(pkey);
        cookfd.setEnabled(flag);
        MktCookfd update = cookfdDao.update(cookfd);
        if (update.getEnabled() == flag)
            return true;
        return false;
    }

    public Boolean recomCookfd(Integer pkey, Boolean flag) {
        MktCookfd cookfd = cookfdDao.getCookfd(pkey);
        if (cookfd == null)
            throw TofocusException.of(WsaleErrCode.UNKOWN_COOKFD);
        if (flag) {
            List<MktCookfd> exec = cookfdDao.getRecom(true, CurrentSession.marketPkey());
            if (exec.size() >= 6)
                throw TofocusException.of(WsaleErrCode.NUMBER_EXCEEDED);
        }
        cookfd.setRecom(flag);
        MktCookfd update = cookfdDao.update(cookfd);
        if (update.getEnabled() == flag)
            return true;
        return false;
    }
}
