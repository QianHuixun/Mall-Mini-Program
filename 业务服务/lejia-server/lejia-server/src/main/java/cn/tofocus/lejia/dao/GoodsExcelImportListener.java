//package cn.tofocus.lejia.dao;
//
//import java.math.BigDecimal;
//import java.text.ParseException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.alibaba.excel.util.DateUtils;
//
//import cn.tofocus.core.exception.TofocusException;
//import cn.tofocus.db.excel.ExcelImportListener;
//import cn.tofocus.lejia.bean.dto.MktGoodsExcel2;
//import cn.tofocus.lejia.bean.entity.market.MktGoods;
//import cn.tofocus.lejia.bean.entity.market.MktGoodsMain;
//import cn.tofocus.lejia.bean.entity.market.MktGoodsSpace;
//import cn.tofocus.lejia.bean.entity.market.MktGtype;
//import cn.tofocus.lejia.bean.entity.market.MktWareLine;
//import cn.tofocus.lejia.bean.enums.MType;
//import cn.tofocus.lejia.bean.enums.WareType;
//import cn.tofocus.lejia.core.CurrentSession;
//import cn.tofocus.lejia.dao.market.MktGoodsDao;
//import cn.tofocus.lejia.dao.market.MktGoodsMainDao;
//import cn.tofocus.lejia.dao.market.MktGoodsSpaceDao;
//import cn.tofocus.lejia.dao.market.MktGtypeDao;
//import cn.tofocus.lejia.exception.LejiaErrCode;
//import cn.tofocus.lejia.exception.WsaleErrCode;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//public class GoodsExcelImportListener implements ExcelImportListener<MktGoodsExcel2>
//{
//    
//    @Autowired
//    private MktGoodsDao goodsDao;
//    
//    @Autowired
//    private MktGoodsSpaceDao goodsSpaceDao;
//    
//    @Autowired
//    private MktGoodsMainDao goodsMainDao;
//    
//    @Autowired
//    private MktGtypeDao gtypeDao;
//    
//    private Map<String, MktGoods> map = new HashMap<>();
//    
//    private Map<String, MktGoodsMain> goodsMainMap = new HashMap<>();
//    
//    private Map<String, MktGoodsSpace> spaceMap = new HashMap<>();
//    private Map<String, Integer> sapcesRepeat = new HashMap<>();
//    
//    @PostConstruct
//    public void init()
//    {
//        List<MktGoods> exec = goodsDao.select().eq("idDel", false).exec();
//        List<Integer> goodsKeys = new ArrayList<>();
//        for (MktGoods g : exec)
//        {
//            map.put(g.getFarmer() + g.getTitle(), g);
//            goodsKeys.add(g.getPkey());
//        }
//        if (goodsKeys.size() > 0)
//        {
//            List<MktGoodsSpace> list = goodsSpaceDao.select().in("goods", goodsKeys.toArray()).exec();
//            for (MktGoodsSpace gs : list)
//            {
//                spaceMap.put(gs.getGoods() + gs.getSpace(), gs);
//            }
//        }
//        List<MktGoodsMain> goodsMain = goodsMainDao.select().eq("enabled", true).eq("idDel", false).exec();
//        
//        Map<Integer, String> gtypeMap = new HashMap<>();
//        List<MktGtype> gtype = gtypeDao.select().eq("enabled", true).eq("idDel", false).exec();
//        gtype.forEach(e -> {
//            gtypeMap.put(e.getPkey(), e.getName());
//        });
//        goodsMain.forEach(e -> {
//            if (gtypeMap.containsKey(e.getGtype()))
//            {
//                goodsMainMap.put(gtypeMap.get(e.getGtype()) + "/" + e.getName(), e);
//            }
//        });
//    }
//    
//    @Override
//    public void check(MktGoodsExcel2 data)
//    {
//        Integer pkey = null;
//        String marketPkey = CurrentSession.marketPkey();
//        log.info("marketPkey: {}", marketPkey);
//        MktGoods g = new MktGoods();
//        int kcNum = 0;
//        if (!map.containsKey(marketPkey + data.getTitle()))
//        {
//            g = putGoods(data, g);
//            map.put(g.getTitle(), g);
//            pkey = g.getPkey();
//        }
//        else
//        {
//            g = map.get(marketPkey + data.getTitle());
//            putGoods(data, g);
//            pkey = g.getPkey();
//            String space = pkey + data.getSpace();
//            if (spaceMap.containsKey(space))
//            {
//                MktGoodsSpace goodsSpace = spaceMap.get(space);
//                data.setPkey(goodsSpace.getPkey());
//            }
//            if(sapcesRepeat.containsKey(space))
//            {
//                throw TofocusException.of(LejiaErrCode.GOODSSPACES_NAMEREPEAT);
//            }
//            else
//                sapcesRepeat.put(space, 1);
//        }
//        if(data.getPriceMember() == null)
//            data.setPriceMember(BigDecimal.ZERO);
//        if(data.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
//        {
//            g.setExtendCon("member");
//            goodsDao.update(g);
//        }
//        
//        
//        data.setGoods(pkey);
//        data.setPoint(0);
//        data.setComm(BigDecimal.ZERO);
//    }
//    
//    private MktGoods putGoods(MktGoodsExcel2 data, MktGoods g)
//    {
//        String gooodsMainName = data.getGooodsMainName();
//        log.info("gooodsMainName: {}", gooodsMainName);
//        if (StringUtils.isBlank(gooodsMainName) || !gooodsMainName.contains("/"))
//        {
//            throw TofocusException.of(WsaleErrCode.DATE_ERR);
//        }
//        if (!goodsMainMap.containsKey(gooodsMainName)) throw TofocusException.of(WsaleErrCode.DATE_ERR);
//        MktGoodsMain goodsMain = goodsMainMap.get(gooodsMainName);
//        g.setGtype(goodsMain.getGtype());
//        g.setGoodsMain(goodsMain.getPkey());
//        g.setMType(MType.MARKET_GOODS);
//        g.setTitle(data.getTitle());
//        g.setSerialNumber(data.getSerialNumber());
//        g.setDescription(data.getDescription());
//        try
//        {
//            g.setStartDate(DateUtils.parseDate(data.getStartDate(), "yyyy-MM-dd"));
//            g.setEndDate(DateUtils.parseDate(data.getEndDate(), "yyyy-MM-dd"));
//        }
//        catch (ParseException e)
//        {
//            throw TofocusException.of(WsaleErrCode.DATA_FORMAT_ERR);
//        }
//        int viewCount = 0;
//        if (g != null && g.getViewCount() != null) viewCount = g.getViewCount();
//        if (System.currentTimeMillis() < g.getStartDate().getTime())
//        {
//            g.setEnabled(false);
//        }
//        else
//        {
//            g.setEnabled(true);
//        }
//        g.setViewCount(viewCount);
//        g.setXsNum(data.getXsNum());
//        g.setPurchaseNum(data.getPurchaseNum());
//        g.setPrice(data.getPrice());
//        if (data.getIsPostage().intValue() == 0)
//            g.setIsPostage(false);
//        else
//            g.setIsPostage(true);
//        if (data.getSort() != null)
//            g.setSort(data.getSort());
//        else
//            g.setSort(0);
//        g.setFarmer(CurrentSession.marketPkey());
//        g.setCompany(CurrentSession.companyPkey());
//        g.setEnabled(true);
//        g.setIdDel(false);
//        g.setRowVension(1);
//        MktGoods put = goodsDao.put(g);
//        return put;
//    }
//}
