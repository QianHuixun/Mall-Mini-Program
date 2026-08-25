package cn.tofocus.lejia.domain.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCookfdDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCookfdLineDetailsDTO;
import cn.tofocus.lejia.bean.dto.app.market.MktAppCookfdTypeOnList;
import cn.tofocus.lejia.bean.dto.app.market.MktCookfdAppOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.market.MktCookfd;
import cn.tofocus.lejia.bean.entity.market.MktCookfdLine;
import cn.tofocus.lejia.bean.entity.market.MktCookfdType;
import cn.tofocus.lejia.bean.enums.SearchType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktCookfdDao;
import cn.tofocus.lejia.dao.market.MktCookfdLineDao;
import cn.tofocus.lejia.dao.market.MktCookfdTypeDao;
import cn.tofocus.lejia.domain.market.SearchManager;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.repository.market.MktCookfdLineRepository;
import cn.tofocus.lejia.repository.market.MktCookfdRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppCookfdManager
{
    @Autowired
    private MktCookfdDao cookfdDao;
    
    @Autowired
    private MktCookfdLineDao cookfdLineDao;
    
    @Autowired
    private MktCookfdTypeDao cookfdTypeDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private SearchManager searchManager;
    
    @Autowired
    private MktCookfdRepository cookfdRepository;
    
    @Autowired
    private MktCookfdLineRepository cookfdLineRepository;
    
    @Autowired
    private AppCollectionManager collectionManager;
    
    @Transactional
    public MktAppCookfdDetailsDTO getAppCookfd(Integer pkey)
    {
        MktCookfd cookfd = cookfdDao.selectOne().eq("pkey", pkey).eq("idDel", false).exec();
        if (cookfd == null) throw TofocusException.of(WsaleErrCode.UNKOWN_COOKFD);
        MktAppCookfdDetailsDTO result = BeanUtil.beanFrom(MktAppCookfdDetailsDTO.class, cookfd);
        
        List<MktAppCookfdLineDetailsDTO> lines = new ArrayList<>();
        List<MktCookfdLine> lineList = cookfdLineRepository.findByCookfd(pkey);
        for (MktCookfdLine cookfdLine : lineList)
        {
            MktAppCookfdLineDetailsDTO detailsDTO = BeanUtil.beanFrom(MktAppCookfdLineDetailsDTO.class, cookfdLine);
            if (cookfdLine.getGoods() != null)
            {
                MktGoods goods = goodsDao.selectOne().eq("pkey", cookfdLine.getGoods()).eq("idDel", false).exec();
                if (goods != null)
                {
                    detailsDTO.setGoodsName(goods.getTitle());
                    if (goods.getPhoto1() != null)
                        detailsDTO.setWrapperPhoto(goods.getPhoto1().size() > 0 ? goods.getPhoto1().get(0) : "");
                }
            }
            
            if (cookfdLine.getSpace() != null)
            {
                MktGoodsSpace goodsSpace = goodsSpaceDao.selectOne().eq("pkey", cookfdLine.getSpace()).exec();
                if (goodsSpace != null)
                {
                    detailsDTO.setSpaceName(goodsSpace.getSpace());
                    detailsDTO.setPrice(goodsSpace.getPrice());
                }
            }
            lines.add(detailsDTO);
        }
        result.setLines(lines);
        if (result.getDescp() == null) result.setDescp("");
        
        Integer collectionPkey = collectionManager.chkCollection(0, result.getPkey());
        if (collectionPkey != 0)
        {
            result.setCollection(true);
            result.setCollectionPkey(collectionPkey);
        }
        
        cookfdRepository.autoViewCount(pkey);
        return result;
    }
    
    public PageResult<MktCookfdAppOnList> queryAppCookfd(int page, int pagesize, String name, Integer ctype,
        Boolean recom, Boolean hot)
    {
        if (StringUtils.isNotBlank(name))
        {
            searchManager.insSearch(SearchType.COOKFD, name);
        }
        PageResult<MktCookfd> pageResult = cookfdDao.queryAppCookfd(page, pagesize, name, ctype, recom, hot);
        PageResult<MktCookfdAppOnList> result = BeanUtil.beanPageFrom(MktCookfdAppOnList.class, pageResult);
        
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey != null)
        {
            for (MktCookfdAppOnList mktCookfdAppOnList : result.getContent())
            {
                Integer collectionPkey = collectionManager.chkCollection(0, mktCookfdAppOnList.getPkey());
                if (collectionPkey != 0)
                {
                    mktCookfdAppOnList.setCollection(true);
                }
            }
        }
        return result;
    }
    
    public List<Map<String, Object>> queryRelatedCookfd(Integer goodsPkey)
    {
        List<Map<String, Object>> result = new ArrayList<>();
        List<MktCookfdLine> exec = cookfdLineDao.select().eq("goods", goodsPkey).exec();
        List<Integer> cookfdPkeyList = new ArrayList<>();
        for (MktCookfdLine line : exec)
        {
            cookfdPkeyList.add(line.getCookfd());
        }
        if (!cookfdPkeyList.isEmpty())
        {
            List<MktCookfd> cookfdList =
                cookfdDao.select().eq("enabled", true).eq("idDel", false).in("pkey", cookfdPkeyList.toArray()).exec();
            if (cookfdList != null && cookfdList.size() > 0)
            {
                for (MktCookfd c : cookfdList)
                {
                    Map<String, Object> map = new HashMap<>();
                    map.put("pkey", c.getPkey());
                    map.put("name", c.getName());
                    map.put("photo", c.getPhoto1().get(0));
                    result.add(map);
                    if (result.size() >= 8) break;
                }
            }
        }
        return result;
    }
    
    public List<MktAppCookfdTypeOnList> queryCookfdType()
    {
        String farmerPkey = MobileSession.farmerPkey();
        log.info("farmerPkey: {}", farmerPkey);
        List<MktCookfdType> list = cookfdTypeDao.listCookfdType(farmerPkey);
        return BeanUtil.beanListFrom(MktAppCookfdTypeOnList.class, list);
    }
    
}
