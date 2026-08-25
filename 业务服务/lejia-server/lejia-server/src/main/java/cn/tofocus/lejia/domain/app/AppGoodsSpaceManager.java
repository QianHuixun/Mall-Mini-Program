package cn.tofocus.lejia.domain.app;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.lejia.bean.dto.app.goods.AppSpaceDTO;
import cn.tofocus.lejia.bean.dto.app.goods.AppSpaceDetailsDTO;
import cn.tofocus.lejia.bean.dto.goods.GoodsProcessOnInfo;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsProcessDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.market.MktGwcDao;
import cn.tofocus.lejia.repository.market.MktGoodsSpaceRepository;

@Component
public class AppGoodsSpaceManager
{
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGwcDao gwcDao;
    
    @Autowired
    private MktGoodsSpaceRepository goodsSpaceRepository;
    
    @Autowired
    private MktGoodsProcessDao goodsProcessDao;
    
    public AppSpaceDTO getSpaceList(Integer goodsPkey)
    {
        AppSpaceDTO appSpaceDTO = new AppSpaceDTO();
        MktGoods goods = goodsDao.get(goodsPkey);
        appSpaceDTO.setGoods(goods.getPkey());
        appSpaceDTO.setGoodsTitle(goods.getTitle());
        MType mType = goods.getMType();
        if (mType != null
            && (mType.getIndex() == MType.INTEGRAL_GOODS.getIndex() || mType.getIndex() == MType.GIFT_GOODS.getIndex()))
        {
            appSpaceDTO.setIsGoodsIntegral(true);
        }
        
        List<AppSpaceDetailsDTO> spaceList =
            goodsSpaceDao.select().eq("goods", goodsPkey).execDto(AppSpaceDetailsDTO.class);
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey != null)
        {
            Map<String, Number> map =
                gwcDao.aggregation().eq("goods", goodsPkey).eq("member", memberPkey).execGroupBySum("space", "num");
            for (AppSpaceDetailsDTO dto : spaceList)
            {
                String pkey = dto.getPkey().toString();
                if (map.containsKey(pkey))
                {
                    Number number = map.get(pkey);
                    if (number != null) dto.setGwcNum(number.intValue());
                }
            }
        }
        appSpaceDTO.setSpaceList(spaceList);
        List<GoodsProcessOnInfo> processLines = new ArrayList<>();
        if(Boolean.TRUE.equals(goods.getIsProcess()))
        {
            List<Integer> listProcess = goodsProcessDao.listProcess(goodsPkey);
            if(!listProcess.isEmpty())
            {
                List<MktGoodsSpace> gsList = goodsSpaceDao.select()
                    .in("pkey", listProcess)
                    .exec();
                
                gsList.forEach(e -> {
                    MktGoods mktGoods = goodsDao.selectOne().eq("pkey", e.getGoods())
                        .eq("mType", MType.PROCESS_GOODS)
                        .eq("idDel", false)
                        .eq("enabled", true)
                        .exec();
                    if(mktGoods != null)
                    {
                        GoodsProcessOnInfo p = new GoodsProcessOnInfo();
                        p.setProcess(e.getPkey());
                        p.setProcessName(mktGoods.getTitle());
                        if(StringUtils.isNotBlank(e.getPhoto1()))
                            e.setPhoto1(e.getPhoto1());
                        else if(mktGoods.getPhoto1() != null && !mktGoods.getPhoto1().isEmpty())
                            p.setPhoto(mktGoods.getPhoto1().get(0));
                        p.setPrice(e.getPrice());
                        processLines.add(p);
                    }
                });
            }
        }
        appSpaceDTO.setProcessLines(processLines);
        return appSpaceDTO;
    }
    
    public AppSpaceDTO getSpaceMemberList(Integer goodsPkey)
    {
        AppSpaceDTO appSpaceDTO = new AppSpaceDTO();
        MktGoods goods = goodsDao.get(goodsPkey);
        appSpaceDTO.setGoods(goods.getPkey());
        appSpaceDTO.setGoodsTitle(goods.getTitle());
        MType mType = goods.getMType();
        if (mType != null
            && (mType.getIndex() == MType.INTEGRAL_GOODS.getIndex() || mType.getIndex() == MType.GIFT_GOODS.getIndex()))
        {
            appSpaceDTO.setIsGoodsIntegral(true);
        }
        
        List<AppSpaceDetailsDTO> spaceList = goodsSpaceDao.select()
            .eq("goods", goodsPkey)
            .notEq("priceMember", BigDecimal.ZERO)
            .execDto(AppSpaceDetailsDTO.class);
        Integer memberPkey = MobileSession.memberPkey();
        if (memberPkey != null)
        {
            Map<String, Number> map =
                gwcDao.aggregation().eq("goods", goodsPkey).eq("member", memberPkey).execGroupBySum("space", "num");
            for (AppSpaceDetailsDTO dto : spaceList)
            {
                String pkey = dto.getPkey().toString();
                if (map.containsKey(pkey))
                {
                    Number number = map.get(pkey);
                    if (number != null) dto.setGwcNum(number.intValue());
                }
            }
        }
        appSpaceDTO.setSpaceList(spaceList);
        return appSpaceDTO;
    }
    
    public Integer totalAmount(Integer goodsPkey)
    {
        return goodsSpaceRepository.countAllByGoods(goodsPkey);
    }
}
