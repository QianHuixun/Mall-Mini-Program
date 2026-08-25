package cn.tofocus.lejia.domain;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.lejia.bean.dto.goods.*;
import cn.tofocus.lejia.bean.dto.market.GoodsSellingPointDTO;
import cn.tofocus.lejia.bean.entity.goods.*;
import cn.tofocus.lejia.dao.goods.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.util.DateUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import cn.tofocus.common.excel.ExcelHelper;
import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdTagDrop;
import cn.tofocus.lejia.bean.entity.market.MktCard;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktWareLine;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.CouponExpireChoose;
import cn.tofocus.lejia.bean.enums.GiftType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.bean.enums.TagVisibleTargetType;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.bean.enums.v5.FarmerType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktCardDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktMemberMsdDao;
import cn.tofocus.lejia.dao.market.MktSupplierDao;
import cn.tofocus.lejia.dao.market.MktWareLineDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.domain.app.AppGoodsV4Manager2;
import cn.tofocus.lejia.domain.v2.GoodsV2Manager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.utils.DateUtil;

@Component
public class GoodsExcelManager
{
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsPresaleDao goodsPresaleDao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktWareLineDao wareLineDao;
    
    @Autowired
    private GoodsManager goodsManager;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private GoodsV2Manager goodsV2Manager;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Autowired
    private MktCardDao cardDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;

    @Autowired
    private MktVendorDao vendorDao;

    @Autowired
    private MktSupplierDao supplierDao;
    
    @Autowired
    private MktGoodsGiftDao goodsGiftDao;

    @Autowired
    private MktGoodsSellingPointDao goodsSellingPointDao;
    
    @Autowired
    private ExcelHelper excelHelper;
    
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private AppGoodsV4Manager2 appGoodsV4Manager;
    
    @Autowired
    private TagManager tagManager;
    
    public void exportGoods(String title, Integer gtype, Integer goodsMain, Boolean enabled, Integer status,
        MType mType, String marketPkey, OutputStream out)
    {
        try
        {
            Class<?> model = GoodsMarketExportExcel.class;
            List<?> list = new ArrayList<>();
            SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
            Boolean typeFlag = false;
            if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
                typeFlag = true;
            if (mType.equals(MType.COUPON_GOODS))
            {
                model = GoodsCouponExportExcel.class;
                list = goodsV2Manager.queryGoodsExcel(title, goodsMain, enabled);
            }
            else
            {
                PageResult<MktGoodsDetailsDTO> result =
                    goodsManager.queryGoodsList(0, 100000, mType, enabled, status, gtype, title, "");
                list = assemblyList(result.getContent(), mType);
            }
            if(typeFlag)
                model = GoodsMarketVendorExportExcel.class;
            if (mType.equals(MType.SPECIAL_GOODS) || mType.equals(MType.POVERTY_ALLEVIATION_GOODS))
            {
                model = MktGoodsOtherExportExcel.class;
                if(typeFlag)
                    model = MktGoodsOtherVendorExportExcel.class;
            }   
            if (mType.equals(MType.PRESALE_GOODS))
            {
                model = MktGoodsPresaleExportExcel.class;
            }
            if (mType.equals(MType.SHARE_GOODS))
            {
                model = MktGoodsShareExportExcel.class;
            }
            if (mType.equals(MType.CUT_GOODS))
            {
                model = MktGoodsCutExportExcel.class;
            }
            if (mType.equals(MType.COLLAGE_GOODS))
            {
                model = MktGoodsCollageExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_GOODS))
            {
                model = MktGoodsIntegralExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_PRESALE_GOODS))
            {
                model = MktGoodsIntegralPresaleExportExcel.class;
            }
            if (mType.equals(MType.INTEGRAL_BNYP_GOODS))
            {
                model = MktGoodsIntegralBnypExportExcel.class;
            }
            if (mType.equals(MType.GIFT_GOODS))
            {
                model = MktGoodsGiftExportExcel.class;
            }
            excelHelper.exportExcel(BeanUtil.beanListFrom(model, list), "Sheet1", out, model, null);
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void importGoods(MultipartFile myfile, MType mType, String marketPkey, OutputStream out)
    {
        Map<String, MktGoods> goodsMap = new HashMap<>();
//        Map<String, MktGoodsMain> goodsMainMap = new HashMap<>();
        Map<String, MktGoodsMainThree> goodsMainThreeMap = new HashMap<>();
        Map<String, MktGoodsSpace> spaceMap = new HashMap<>();
        Map<String, String> famerMap = new HashMap<>();
        Map<String, Integer> vendorMap = new HashMap<>();
        Map<String, Integer> supplierMap = new HashMap<>();
        Map<String, Integer> msdTagMap = new HashMap<>();
        SysFarmer sysFarmer = sysFarmerDao.get(marketPkey);
        assemblyImportMap(marketPkey,
            goodsMap,
            goodsMainThreeMap,
            spaceMap,
            famerMap,
            vendorMap,
            supplierMap,
            msdTagMap,
            mType,
            sysFarmer.getAscription());
        Map<String, Integer> sapcesRepeat = new HashMap<>();
        Class<?> clazz = getClass(mType, sysFarmer.getType());
        if (clazz == null)
        {
            ExcelReaderBuilder read;
            List<GoodsCouponExcel> addList = new ArrayList<>();
            try
            {
                Map<String, MktGoods> goodsAllMap = goodsDao.getMarketGoodsAllMap();
                Map<String, String> farmerMap = sysFarmerDao.findPkeyMap(CurrentSession.ascriptionPkey());
                Map<String, MktGtype> gtypeMap = gtypeDao.getMarketNameGtype(CurrentSession.ascriptionPkey());
                read = EasyExcel.read(myfile.getInputStream());
                read.head(GoodsCouponExcel.class);
                read.registerReadListener(new CouponGoodsListener(sapcesRepeat, goodsMap, goodsMainThreeMap, spaceMap,
                    addList, gtypeMap, goodsAllMap, farmerMap, out));
                read.headRowNumber(1);
                read.doReadAll();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            processCouponExcelData(addList, spaceMap, false);
        }
        else if(FarmerType.VENDOR_SHOPPING_MALL.equals(sysFarmer.getType()))
        {
            List<MktGoods> exec = goodsDao.select().eq("farmer", marketPkey).eq("mType", mType).eq("idDel", false).exec();
            Map<String, MktGoods> goodsMap2 = new HashMap<>();
            Map<Integer, MktVendor> map = vendorDao.getMapVendorGoods(marketPkey);
            exec.forEach(e -> {
                if(e.getVendor() != null && map.containsKey(e.getVendor()))
                {
                    MktVendor vendor = map.get(e.getVendor());
                    goodsMap2.put(e.getTitle() + "::" + vendor.getDisplayName(), e);
                }
            });
            processUniversalVendorData(myfile, mType, out, goodsMap2, goodsMainThreeMap, spaceMap, sapcesRepeat, 
                marketPkey, vendorMap, famerMap, clazz);
        }
        else
        {
            processUniversalData(myfile,
                mType,
                out,
                goodsMap,
                goodsMainThreeMap,
                spaceMap,
                sapcesRepeat,
                famerMap,
                vendorMap,
                supplierMap,
                msdTagMap,
                clazz);
        }
        if(!(Constant.Operation + CurrentSession.ascriptionPkey()).equals(marketPkey))
            appGoodsV4Manager.openThread(marketPkey, null);
    }
    
    private Class<?> getClass(MType mType, FarmerType farmerType)
    {
        Class<?> clazz = null;
        if(FarmerType.VENDOR_SHOPPING_MALL.equals(farmerType))
        {
            switch (mType)
            {
                case MARKET_GOODS:
                    clazz = GoodsMarketVendorExcel.class;
                    break;
                case SHARE_GOODS:
                    clazz = MktGoodsShareVendorExcel.class;
                    break;
                case CUT_GOODS:
                    clazz = MktGoodsCutVendorExcel.class;
                    break;
                case COLLAGE_GOODS:
                    clazz = MktGoodsCollageVendorExcel.class;
                    break;
                case INTEGRAL_GOODS:
                    clazz = MktGoodsIntegralVendorExcel.class;
                    break;
                case GIFT_GOODS:
                    clazz = MktGoodsGiftVendorExcel.class;
                    break;
                case PRESALE_GOODS:
                    clazz = MktGoodsPresaleVendorExcel.class;
                    break;
                case COUPON_GOODS:
                    break;
                default:
                    clazz = MktGoodsOtherVendorExcel.class;
                    break;
            }
        }
        else
        {
            switch (mType)
            {
                case MARKET_GOODS:
                    clazz = GoodsMarketExcel.class;
                    break;
                case SHARE_GOODS:
                    clazz = MktGoodsShareExcel.class;
                    break;
                case CUT_GOODS:
                    clazz = MktGoodsCutExcel.class;
                    break;
                case COLLAGE_GOODS:
                    clazz = MktGoodsCollageExcel.class;
                    break;
                case INTEGRAL_GOODS:
                    clazz = MktGoodsIntegralExcel.class;
                    break;
                case GIFT_GOODS:
                    clazz = MktGoodsGiftExcel.class;
                    break;
                case PRESALE_GOODS:
                    clazz = MktGoodsPresaleExcel.class;
                    break;
                case COUPON_GOODS:
                    break;
                case INTEGRAL_PRESALE_GOODS:
                    clazz = MktGoodsIntegralPresaleExcel.class;
                    break;
                case INTEGRAL_BNYP_GOODS:
                    clazz = MktGoodsIntegralBnypExcel.class;
                    break;
                case INTEGRAL_MSD_GOODS:
                    clazz = MktGoodsIntegralMsdExcel.class;
                    break;
                default:
                    clazz = MktGoodsOtherExcel.class;
                    break;
            }
        }
        return clazz;
    }
    
    private <T extends MktGoodsVendorExcel> void processUniversalVendorData(MultipartFile myfile, MType mType, OutputStream out,
        Map<String, MktGoods> goodsMap, Map<String, MktGoodsMainThree> goodsMainMap, Map<String, MktGoodsSpace> spaceMap,
        Map<String, Integer> sapcesRepeat, String famer, Map<String, Integer> vendorMap, Map<String, String> famerMap, Class<?> clazz)
    {
        
        List<T> addList = new ArrayList<>();
        ExcelReaderBuilder read;
        try
        {
            read = EasyExcel.read(myfile.getInputStream());
            read.head(clazz);
            read.registerReadListener(new GoodsVendorListener<T>(sapcesRepeat, goodsMap, goodsMainMap, spaceMap, famer,
                vendorMap, mType, famerMap, addList, out, clazz));
            read.headRowNumber(1);
            read.doReadAll();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        processExcelVendorData(addList, spaceMap, mType);
    }
    
    private <T extends MktGoodsExcel> void processUniversalData(MultipartFile myfile, MType mType, OutputStream out,
        Map<String, MktGoods> goodsMap, Map<String, MktGoodsMainThree> goodsMainMap,
        Map<String, MktGoodsSpace> spaceMap, Map<String, Integer> sapcesRepeat, Map<String, String> famerMap,
        Map<String, Integer> vendorMap, Map<String, Integer> supplierMap, Map<String, Integer> msdTagMap, Class<?> clazz)
    {
        
        List<T> addList = new ArrayList<>();
        ExcelReaderBuilder read;
        try
        {
            read = EasyExcel.read(myfile.getInputStream());
            read.head(clazz);
            read.registerReadListener(new GoodsListener<T>(sapcesRepeat, goodsMap, goodsMainMap, spaceMap, famerMap,
                vendorMap, supplierMap, msdTagMap, mType, addList, out, clazz));
            read.headRowNumber(1);
            read.doReadAll();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        processExcelData(addList, spaceMap, msdTagMap, mType);
    }
    
    private void assemblyImportMap(String marketPkey, Map<String, MktGoods> goodsMap,
        Map<String, MktGoodsMainThree> goodsMainThreeMap, Map<String, MktGoodsSpace> spaceMap,
        Map<String, String> famerMap, Map<String, Integer> vendorMap, Map<String, Integer> supplierMap, 
        Map<String, Integer> msdTagMap, MType mType, Integer ascription)
    {
        List<MktGoods> exec = goodsDao.select().eq("farmer", marketPkey).eq("mType", mType).eq("idDel", false).exec();
        List<Integer> goodsKeys = new ArrayList<>();
        for (MktGoods g : exec)
        {
            goodsMap.put(g.getTitle(), g);
            goodsKeys.add(g.getPkey());
        }
        if (!goodsKeys.isEmpty())
        {
            List<MktGoodsSpace> list = goodsSpaceDao.select().in("goods", goodsKeys.toArray()).exec();
            for (MktGoodsSpace gs : list)
            {
                spaceMap.put(gs.getGoods() + ":" + gs.getSpace(), gs);
            }
        }
        List<MktGoodsMainThree> goodsMainThree = goodsMainThreeDao.select().eq("farmer", CurrentSession.marketPkey()).eq("idDel", false).eq("ascription", ascription).exec();
        List<MktGoodsMain> goodsMain = goodsMainDao.select().eq("farmer", CurrentSession.marketPkey()).eq("idDel", false).eq("ascription", ascription).exec();
        List<MktGtype> gtype = gtypeDao.select().eq("farmer", CurrentSession.marketPkey()).eq("idDel", false).eq("ascription", ascription).exec();
        Map<Integer, String> gtypeMap = new HashMap<>();
        Map<Integer, String> goodsMainMap = new HashMap<>();
        gtype.forEach(e -> 
            gtypeMap.put(e.getPkey(), e.getName())
        );
        goodsMain.forEach(e -> 
            goodsMainMap.put(e.getPkey(), e.getName())
        );
        goodsMainThree.forEach(e -> {
            String gName = gtypeMap.get(e.getGtype());
            String tName = goodsMainMap.get(e.getTwoGtype());
            goodsMainThreeMap.put(gName + "/" + tName + "/" + e.getName(), e);
        });
        
        if (mType.equals(MType.GIFT_GOODS))
        {
            famerMap.putAll(sysFarmerDao.findPkeyMap(CurrentSession.ascriptionPkey()));
        }
        else if (mType.equals(MType.INTEGRAL_GOODS) || mType.equals(MType.INTEGRAL_PRESALE_GOODS) 
            || mType.equals(MType.INTEGRAL_BNYP_GOODS) || mType.equals(MType.INTEGRAL_MSD_GOODS))
        {
            supplierMap.putAll(supplierDao.findPkeyMap(CurrentSession.ascriptionPkey()));
        }
        if(mType.equals(MType.INTEGRAL_MSD_GOODS))
        {
            List<MktMemberMsdTagDrop> list = memberMsdDao.listTags(CurrentSession.ascriptionPkey());
            list.forEach(e -> msdTagMap.put(e.getName(), e.getPkey()));
        }
        vendorMap.putAll(vendorDao.findPkeyMap());
        
    }
    
    @Transactional
    private <T extends MktGoodsExcel> void processExcelData(List<T> addList, Map<String, MktGoodsSpace> spaceMap,
        Map<String, Integer> msdTagMap, MType mType)
    {
        List<MktGoodsSpace> insSpace = new ArrayList<>();
        List<MktGoodsSpace> updSpace = new ArrayList<>();
        List<MktGoodsGift> putGift = new ArrayList<>();
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        Map<Integer, String> goodsMap = new HashMap<>();
        for (T ge : addList)
        {
            MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, ge);
            if(space.getWeight() == null)
                space.setWeight(BigDecimal.ZERO);
            if(space.getXsNum() == null)
                space.setXsNum(0);
            space.setAscription(CurrentSession.ascriptionPkey());
            if (ge.getPkey() == null)
            {
                goodsMap.put(ge.getGoods(), ge.getTitle());
                insSpace.add(space);
            }
            else
            {
                updSpace.add(space);
                MktGoodsSpace updaSpaceModel = goodsSpaceDao.get(ge.getPkey());
                space.setXsNum(updaSpaceModel.getXsNum());
                if (updaSpaceModel.getKcNum().intValue() != ge.getKcNum().intValue())
                {
                    MktWareLine add = new MktWareLine();
                    add.setWareType(WareType.INVENTORY);
                    add.setGoods(ge.getGoods());
                    add.setGoodsName(ge.getTitle());
                    add.setSpace(ge.getPkey());
                    add.setSpaceName(ge.getSpace());
                    add.setNum(ge.getKcNum() - updaSpaceModel.getKcNum());
                    add.setActualNum(ge.getKcNum());
                    add.setAscription(CurrentSession.ascriptionPkey());
                    addWareLineAll.add(add);
                }
            }
            // 只有礼品券 需要插入其他表格
            if (mType.equals(MType.GIFT_GOODS))
            {
                MktGoodsGiftExcel gift = (MktGoodsGiftExcel)ge;
                Date giftStartDate = gift.getGiftStartDate();
                Date giftEndDate = gift.getGiftEndDate();
                CouponExpireChoose expireChoose = CouponExpireChoose.DATE_RANGE;
                if (giftStartDate == null && giftEndDate == null) expireChoose = CouponExpireChoose.LONG_TERM;
                String giftTitle = gift.getTitle() + gift.getSpace();
                String giftContent = "积分商城礼券，请于商户【" + gift.getUserVendorName() + "】核销。";
                MktGoodsGift addGift =
                    new MktGoodsGift(space.getGoods(), giftTitle, giftContent, expireChoose, gift.getUserFarmerKey(),
                        gift.getUserVendorKey(), giftStartDate, giftEndDate, GiftType.INTEGRAL_BUY);
                addGift.setAscription(CurrentSession.ascriptionPkey());
                putGift.add(addGift);
            }
        }
        List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(insSpace);
        addAll.forEach(sk -> {
            spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
        });
        spaceKcDao.addAll(BeanUtil.beanListFrom(MktSpaceKc.class, addAll));
        for (MktGoodsSpace gs : addAll)
        {
            if (goodsMap.containsKey(gs.getGoods()))
            {
                String title = goodsMap.get(gs.getGoods());
                MktWareLine add = new MktWareLine();
                add.setWareType(WareType.WAREHOUSING);
                add.setGoods(gs.getGoods());
                add.setGoodsName(title);
                add.setSpace(gs.getPkey());
                add.setSpaceName(gs.getSpace());
                add.setNum(gs.getKcNum());
                add.setActualNum(gs.getKcNum());
                add.setAscription(CurrentSession.ascriptionPkey());
                addWareLineAll.add(add);
            }
        }
        List<MktGoodsSpace> updateAll = goodsSpaceDao.updateAll(updSpace);
        spaceKcDao.updateAll(BeanUtil.beanListFrom(MktSpaceKc.class, updateAll));
        updateAll.forEach(sk -> {
            spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
        });
        wareLineDao.addAll(addWareLineAll);
        addAll.addAll(updateAll);
        if (!putGift.isEmpty()) goodsGiftDao.putAll(putGift);
        // 只有市场商品 才有会员价
        if (mType.equals(MType.MARKET_GOODS))
        {
            List<Integer> keys = new ArrayList<>();
            List<Integer> updKeys = new ArrayList<>();
            Map<Integer, Boolean> map = new HashMap<>();
            Map<Integer, Boolean> unMap = new HashMap<>();
            for (MktGoodsSpace s : addAll)
            {
                if (s.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
                {
                    map.put(s.getGoods(), true);
                }
                else
                    unMap.put(s.getGoods(), false);
            }
            for (Integer key : map.keySet())
            {
                if (!unMap.containsKey(key)) keys.add(key);
            }
            unMap.keySet().forEach(e -> updKeys.add(e));
            if (!keys.isEmpty())
            {
                List<MktGoods> exec = goodsDao.select().in("pkey", keys.toArray()).exec();
                for (MktGoods g : exec)
                {
                    g.setExtendCon("member");
                }
                goodsDao.updateAll(exec);
            }
            if (!updKeys.isEmpty())
            {
                List<MktGoods> exec = goodsDao.select().in("pkey", updKeys.toArray()).exec();
                for (MktGoods g : exec)
                {
                    g.setExtendCon(null);
                }
                goodsDao.updateAll(exec);
            }
        }
    }
    
    @Transactional
    private <T extends MktGoodsVendorExcel> void processExcelVendorData(List<T> addList, Map<String, MktGoodsSpace> spaceMap,
        MType mType)
    {
        List<MktGoodsSpace> insSpace = new ArrayList<>();
        List<MktGoodsSpace> updSpace = new ArrayList<>();
        List<MktGoodsGift> putGift = new ArrayList<>();
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        Map<Integer, String> goodsMap = new HashMap<>();
        for (T ge : addList)
        {
            MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, ge);
            if(space.getWeight() == null)
                space.setWeight(BigDecimal.ZERO);
            if(space.getXsNum() == null)
                space.setXsNum(0);
            space.setAscription(CurrentSession.ascriptionPkey());
            if (ge.getPkey() == null)
            {
                goodsMap.put(ge.getGoods(), ge.getTitle());
                insSpace.add(space);
            }
            else
            {
                updSpace.add(space);
                MktGoodsSpace updaSpaceModel = goodsSpaceDao.get(ge.getPkey());
                space.setXsNum(updaSpaceModel.getXsNum()); //显示数量导入不修改
                if (updaSpaceModel.getKcNum().intValue() != ge.getKcNum().intValue())
                {
                    MktWareLine add = new MktWareLine();
                    add.setWareType(WareType.INVENTORY);
                    add.setGoods(ge.getGoods());
                    add.setGoodsName(ge.getTitle());
                    add.setSpace(ge.getPkey());
                    add.setSpaceName(ge.getSpace());
                    add.setNum(ge.getKcNum() - updaSpaceModel.getKcNum());
                    add.setActualNum(ge.getKcNum());
                    add.setAscription(CurrentSession.ascriptionPkey());
                    addWareLineAll.add(add);
                }
            }
        }
        List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(insSpace);
        addAll.forEach(sk -> {
            spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
        });
        spaceKcDao.addAll(BeanUtil.beanListFrom(MktSpaceKc.class, addAll));
        for (MktGoodsSpace gs : addAll)
        {
            if (goodsMap.containsKey(gs.getGoods()))
            {
                String title = goodsMap.get(gs.getGoods());
                MktWareLine add = new MktWareLine();
                add.setWareType(WareType.WAREHOUSING);
                add.setGoods(gs.getGoods());
                add.setGoodsName(title);
                add.setSpace(gs.getPkey());
                add.setSpaceName(gs.getSpace());
                add.setNum(gs.getKcNum());
                add.setActualNum(gs.getKcNum());
                add.setAscription(CurrentSession.ascriptionPkey());
                addWareLineAll.add(add);
            }
        }
        List<MktGoodsSpace> updateAll = goodsSpaceDao.updateAll(updSpace);
        spaceKcDao.updateAll(BeanUtil.beanListFrom(MktSpaceKc.class, updateAll));
        updateAll.forEach(sk -> {
            spaceKcCache.set(String.valueOf(sk.getPkey()), Long.valueOf(sk.getKcNum()));
        });
        wareLineDao.addAll(addWareLineAll);
        addAll.addAll(updateAll);
        if (!putGift.isEmpty()) goodsGiftDao.putAll(putGift);
        // 只有市场商品 才有会员价
        if (mType.equals(MType.MARKET_GOODS))
        {
            List<Integer> keys = new ArrayList<>();
            List<Integer> updKeys = new ArrayList<>();
            Map<Integer, Boolean> map = new HashMap<>();
            Map<Integer, Boolean> unMap = new HashMap<>();
            for (MktGoodsSpace s : addAll)
            {
                if (s.getPriceMember().compareTo(BigDecimal.ZERO) > 0)
                {
                    map.put(s.getGoods(), true);
                }
                else
                    unMap.put(s.getGoods(), false);
            }
            for (Integer key : map.keySet())
            {
                if (!unMap.containsKey(key)) keys.add(key);
            }
            unMap.keySet().forEach(e -> updKeys.add(e));
            if (!keys.isEmpty())
            {
                List<MktGoods> exec = goodsDao.select().in("pkey", keys.toArray()).exec();
                for (MktGoods g : exec)
                {
                    g.setExtendCon("member");
                }
                goodsDao.updateAll(exec);
            }
            if (!updKeys.isEmpty())
            {
                List<MktGoods> exec = goodsDao.select().in("pkey", updKeys.toArray()).exec();
                for (MktGoods g : exec)
                {
                    g.setExtendCon(null);
                }
                goodsDao.updateAll(exec);
            }
        }
        Map<Integer,List<MktGoodsSpace>> map = new HashMap<>();
        for (MktGoodsSpace s : addAll)
        {
            Integer goods = s.getGoods();
            if(!map.containsKey(goods))
            {
                List<MktGoodsSpace> v = new ArrayList<>();
                map.put(goods, v);
            }
            map.get(goods).add(s);
        }
        map.entrySet();
        for(Map.Entry<Integer,List<MktGoodsSpace>> entry : map.entrySet())
        {
            MktGoods mktGoods = goodsDao.get(entry.getKey());
            goodsManager.insertSupply(mktGoods.getFarmer(), entry.getKey(), mktGoods.getVendor(), mktGoods.getMType(), entry.getValue());
        }
    }
    
    // 组合导出的list
    private List<?> assemblyList(List<MktGoodsDetailsDTO> content, MType mType)
    {
        if (mType.equals(MType.COLLAGE_GOODS))
        {
            List<MktGoodsCollageExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsCollageExportExcel e = BeanUtil.beanFrom(MktGoodsCollageExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    String extendCon = dto.getExtendCon();
                    e.setCollageNum(StringUtils.isBlank(extendCon) ? 0 : Integer.valueOf(extendCon));
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType.equals(MType.CUT_GOODS))
        {
            List<MktGoodsCutExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsCutExportExcel e = BeanUtil.beanFrom(MktGoodsCutExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    if (dto.getExtendConList() != null && !dto.getExtendConList().isEmpty())
                    {
                        for (int i = 0; i < dto.getExtendConList().size(); i++)
                        {
                            String str = dto.getExtendConList().get(i);
                            int j = i + 1;
                            String[] split = str.split(",");
                            switch (j)
                            {
                                case 1:
                                    e.setCutLow1(split[0]);
                                    e.setCutUpon1(split[1]);
                                    break;
                                case 2:
                                    e.setCutLow2(split[0]);
                                    e.setCutUpon2(split[1]);
                                    break;
                                case 3:
                                    e.setCutLow3(split[0]);
                                    e.setCutUpon3(split[1]);
                                    break;
                                case 4:
                                    e.setCutLow4(split[0]);
                                    e.setCutUpon4(split[1]);
                                    break;
                                case 5:
                                    e.setCutLow5(split[0]);
                                    e.setCutUpon5(split[1]);
                                    break;
                                case 6:
                                    e.setCutLow6(split[0]);
                                    e.setCutUpon6(split[1]);
                                    break;
                                case 7:
                                    e.setCutLow7(split[0]);
                                    e.setCutUpon7(split[1]);
                                    break;
                                case 8:
                                    e.setCutLow8(split[0]);
                                    e.setCutUpon8(split[1]);
                                    break;
                                case 9:
                                    e.setCutLow9(split[0]);
                                    e.setCutUpon9(split[1]);
                                    break;
                                case 10:
                                    e.setCutLow10(split[0]);
                                    e.setCutUpon10(split[1]);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType.equals(MType.SHARE_GOODS))
        {
            List<MktGoodsShareExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsShareExportExcel e = BeanUtil.beanFrom(MktGoodsShareExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setComm(s.getComm());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType.equals(MType.GIFT_GOODS))
        {
            List<MktGoodsGiftExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsGiftExportExcel e = BeanUtil.beanFrom(MktGoodsGiftExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName());
                    e.setUserFarmerName(dto.getUserFarmerName());
                    e.setUserVendorName(dto.getUserVendorName());
                    e.setPoint(s.getPoint());
                    e.setGiftStartDate(dto.getGiftStartDate());
                    e.setGiftEndDate(dto.getGiftEndDate());
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setKcNum(s.getKcNum());
                    list.add(e);
                }
            }
            return list;
        }
        else if (mType.equals(MType.INTEGRAL_PRESALE_GOODS))
        {
            List<MktGoodsIntegralPresaleExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    MktGoodsIntegralPresaleExportExcel e = BeanUtil.beanFrom(MktGoodsIntegralPresaleExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName() + "/" + dto.getThreeGtypeName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    e.setPoint(s.getPoint());
                    MktGoodsPresale goodsPresale = goodsPresaleDao.get(e.getPkey());
                    if(goodsPresale != null)
                    {
                        e.setPresaleStartDate(DateUtil.formatDate(goodsPresale.getStartDate(), "yyyy-MM-dd"));
                        e.setPresaleEndDate(DateUtil.formatDate(goodsPresale.getEndDate(), "yyyy-MM-dd"));
                    }
                    if (CollectionUtil.isNotEmpty(dto.getSellingPoints()))
                    {
                        GoodsSellingPointDTO sellingPoint1 = dto.getSellingPoints().get(0);
                        e.setSellingPointName1(sellingPoint1.getName());
                        e.setSellingPointContent1(sellingPoint1.getContent());
                        if (dto.getSellingPoints().size() > 1)
                        {
                            GoodsSellingPointDTO sellingPoint2 = dto.getSellingPoints().get(1);
                            e.setSellingPointName2(sellingPoint2.getName());
                            e.setSellingPointContent2(sellingPoint2.getContent());
                        }
                        if (dto.getSellingPoints().size() > 2)
                        {
                            GoodsSellingPointDTO sellingPoint3 = dto.getSellingPoints().get(2);
                            e.setSellingPointName3(sellingPoint3.getName());
                            e.setSellingPointContent3(sellingPoint3.getContent());
                        }
                        if (dto.getSellingPoints().size() > 3)
                        {
                            GoodsSellingPointDTO sellingPoint4 = dto.getSellingPoints().get(3);
                            e.setSellingPointName4(sellingPoint4.getName());
                            e.setSellingPointContent4(sellingPoint4.getContent());
                        }
                    }
                    list.add(e);
                }
            }
            return list;
        }
        else
        {
            List<GoodsMarketExportExcel> list = new ArrayList<>();
            for (MktGoodsDetailsDTO dto : content)
            {
                for (MktGoodsSpaceOnList s : dto.getSpaces())
                {
                    GoodsMarketExportExcel e = BeanUtil.beanFrom(GoodsMarketExportExcel.class, dto);
                    e.setGooodsMainName(dto.getGtypeName() + "/" + dto.getName() + "/" + dto.getThreeGtypeName());
                    if (dto.getIsPostage())
                        e.setIsPostage(1);
                    else
                        e.setIsPostage(0);
                    e.setSpace(s.getSpace());
                    e.setPriceOld(s.getPriceOld());
                    e.setPrice(s.getPrice());
//                    e.setPriceMember(s.getPriceMember());
                    e.setWeight(s.getWeight());
                    e.setKcNum(s.getKcNum());
                    e.setPoint(s.getPoint());
                    if (CollectionUtil.isNotEmpty(dto.getSellingPoints()))
                    {
                        GoodsSellingPointDTO sellingPoint1 = dto.getSellingPoints().get(0);
                        e.setSellingPointName1(sellingPoint1.getName());
                        e.setSellingPointContent1(sellingPoint1.getContent());
                        if (dto.getSellingPoints().size() > 1)
                        {
                            GoodsSellingPointDTO sellingPoint2 = dto.getSellingPoints().get(1);
                            e.setSellingPointName2(sellingPoint2.getName());
                            e.setSellingPointContent2(sellingPoint2.getContent());
                        }
                        if (dto.getSellingPoints().size() > 2)
                        {
                            GoodsSellingPointDTO sellingPoint3 = dto.getSellingPoints().get(2);
                            e.setSellingPointName3(sellingPoint3.getName());
                            e.setSellingPointContent3(sellingPoint3.getContent());
                        }
                        if (dto.getSellingPoints().size() > 3)
                        {
                            GoodsSellingPointDTO sellingPoint4 = dto.getSellingPoints().get(3);
                            e.setSellingPointName4(sellingPoint4.getName());
                            e.setSellingPointContent4(sellingPoint4.getContent());
                        }
                    }
                    list.add(e);
                }
            }
            return list;
        }
    }
    
    // 新写监听器  监听传进来的参数
    class GoodsListener<T extends MktGoodsExcel> extends AnalysisEventListener<T>
    {
        private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        public void validator(T v)
        {
            try
            {
                Set<ConstraintViolation<T>> set = validator.validate(v);
                if (set != null && !set.isEmpty())
                {
                    for (ConstraintViolation<T> cv : set)
                    {
                        Class<? extends MktGoodsExcel> clzss = v.getClass();
                        String name = "";
                        Field declaredField;
                        try
                        {
                            declaredField = clzss.getDeclaredField(cv.getPropertyPath().toString());
                            name = declaredField.getName();
                        }
                        catch (NoSuchFieldException e)
                        {
                            declaredField = clzss.getSuperclass().getDeclaredField(cv.getPropertyPath().toString());
                       
                        }
                        ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                        String[] value = annotation.value();
                        if(value.length > 0)
                            name = value[0];
                        throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR,
                            name + cv.getMessage());
                    }
                }
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
            }
        }
        
        ExcelWriterBuilder errBuilder;
        
        Map<String, Integer> sapcesRepeat = new HashMap<>();
        
        Map<String, MktGoods> goodsMap = new HashMap<>();
        
        Map<String, MktGoodsMainThree> goodsMainMap = new HashMap<>();
        
        Map<String, MktGoodsSpace> spaceMap = new HashMap<>();
        
        Map<String, String> famerMap = new HashMap<>();
        
        Map<String, Integer> vendorMap = new HashMap<>();

        Map<String, Integer> supplierMap = new HashMap<>();
        
        Map<String, Integer> msdTagMap = new HashMap<>();
        
        Class<?> errorModel;
        
        MType mType;
        
        OutputStream out;
        
        List<T> errList = new ArrayList<>();
        
        Map<Integer,String> typeErr = new HashMap<>();
        
        List<T> addList = new ArrayList<>();
        
        GoodsListener(Map<String, Integer> sapcesRepeat, Map<String, MktGoods> goodsMap,
            Map<String, MktGoodsMainThree> goodsMainMap, Map<String, MktGoodsSpace> spaceMap, Map<String, String> famerMap,
            Map<String, Integer> vendorMap, Map<String, Integer> supplierMap, Map<String, Integer> msdTagMap,
            MType mType, List<T> addList, OutputStream out, Class<?> errorModel)
        {
            this.sapcesRepeat = sapcesRepeat;
            this.goodsMap = goodsMap;
            this.goodsMainMap = goodsMainMap;
            this.spaceMap = spaceMap;
            this.mType = mType;
            this.out = out;
            this.addList = addList;
            this.errorModel = errorModel;
            this.famerMap = famerMap;
            this.vendorMap = vendorMap;
            this.supplierMap = supplierMap;
            this.msdTagMap = msdTagMap;
        }
        
        @Override
        public void onException(Exception exception, AnalysisContext context) throws Exception {
            ReadRowHolder holder = context.readRowHolder();
        
            if(exception instanceof ExcelDataConvertException)
            {
                ExcelDataConvertException e = (ExcelDataConvertException)exception;
                Integer rowIndex = e.getRowIndex();
                Integer columnIndex = e.getColumnIndex();
                String errStr = e.getCellData().getStringValue();
                typeErr.put(holder.getRowIndex(), "第" + rowIndex + "行,第" + columnIndex + "列数据异常,异常数据: " + errStr);
//                throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR, "第" + rowIndex + "行,第" + columnIndex + "列数据异常,异常数据: " + errStr);
            }
           
//            throw exception;
        }

        
        
        @Override
        public void invoke(T data, AnalysisContext context)
        {
            try
            {
                validator(data);
                checkGoodsExcelDate(data, famerMap, vendorMap, supplierMap, msdTagMap, mType);
                putGoods(data, goodsMainMap, goodsMap, mType);
                String space = data.getGoods() + ":" + data.getSpace();
                if (spaceMap.containsKey(space))
                {
                    MktGoodsSpace goodsSpace = spaceMap.get(space);
                    data.setPkey(goodsSpace.getPkey());
                }
                if (sapcesRepeat.containsKey(space))
                {
                    throw TofocusException.of(LejiaErrCode.GOODSSPACES_NAMEREPEAT);
                }
                else
                    sapcesRepeat.put(space, 1);
                if (data.getGuessLike() == null) data.setGuessLike(false);
                if (data.getPriceMember() == null) data.setPriceMember(BigDecimal.ZERO);
                if (data.getComm() == null) data.setComm(BigDecimal.ZERO);
                addList.add(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                String errmsg;
                if (e instanceof TofocusException)
                {
                    errmsg = e.getMessage();
                }
                else if (e instanceof ParseException)
                {
                    errmsg = "时间格式有问题!";
                }
                else
                {
                    errmsg = e.getClass().getSimpleName() + ":" + e.getMessage();
                }
                data.setErrMsg(errmsg);
                errList.add(data);
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context)
        {
            if(!typeErr.keySet().isEmpty())
            {
                List<MktTypeErrExcel> l = new ArrayList<>();
                for(Integer key : typeErr.keySet())
                {
                    MktTypeErrExcel ge = new MktTypeErrExcel();
                    ge.setErrMsg(typeErr.get(key));
                    l.add(ge);
                }
                errBuilder = EasyExcel.write(out, MktTypeErrExcel.class);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(l, errSheet);
                errWriter.finish();
            }
            if (!errList.isEmpty())
            {
                errBuilder = EasyExcel.write(out, errorModel);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(errList, errSheet);
                errWriter.finish();
            }

        }
    }
    
    // 校验导入的数据
    private <T extends MktGoodsExcel> void checkGoodsExcelDate(T data, Map<String, String> famerMap,
        Map<String, Integer> vendorMap, Map<String, Integer> supplierMap, Map<String, Integer> msdTagMap, 
        MType mType)
        throws ParseException
    {
        if (StringUtils.isBlank(data.getGooodsMainName()))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品库不能为空");
        if (StringUtils.isBlank(data.getTitle())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品名称不能为空");
//        if (StringUtils.isBlank(data.getStartTime()))
//            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "上架时间不能为空");
//        if (StringUtils.isBlank(data.getEndTime())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "下架时间不能为空");
        
        Date startDate = null;
        Date endDate = null;
        if(StringUtils.isNotBlank(data.getStartTime()))
        {
            if (data.getStartTime().contains("-"))
            {
                startDate = DateUtils.parseDate(data.getStartTime(), "yyyy-MM-dd");
            }
            else
            {
                startDate = DateUtils.parseDate(data.getStartTime(), "yyyy/MM/dd");
            }
        }
        if(StringUtils.isNotBlank(data.getEndTime()))
        {
            if (data.getEndTime().contains("-"))
            {
                endDate = DateUtils.parseDate(data.getEndTime(), "yyyy-MM-dd");
            }
            else
            {
                endDate = DateUtils.parseDate(data.getEndTime(), "yyyy/MM/dd");
            }
        }
        if(data.getStartTime() != null && data.getEndDate() != null && startDate.compareTo(endDate) > 0)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "上架时间不能大于下架时间"); 
        
        data.setStartDate(startDate);
        data.setEndDate(endDate);
        
        if (StringUtils.isBlank(data.getSpace())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品规格不能为空");
        if (data.getPriceOld() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "原价不能为空");
        if (data.getPrice() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "现价不能为空");
        if (data.getKcNum() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "库存不能为空");
        if (data.getGuessLike() == null) data.setGuessLike(false);
        if (data.getXsNum() == null) data.setXsNum(0);
        
        if(mType.equals(MType.MARKET_GOODS))
        {
            GoodsMarketExcel ge = (GoodsMarketExcel)data;
            if (ge.getTag() != null && ge.getTag().length() > 6)
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
            ge.validExcelHasSellingPoints();
            if (StringUtil.isNotEmpty(ge.getPhoto1()))
            {
                try
                {
                    JsonUtil.getBean(ge.getPhoto1(), List.class);
                }
                catch (Exception e)
                {
                    throw TofocusException.of(LejiaErrCode.IMPORT_PHOTO_IS_ERROR, "商品轮播图格式不对");
                }
            }
        }
        if (mType.equals(MType.SPECIAL_GOODS))
        {
            MktGoodsOtherExcel ge = (MktGoodsOtherExcel) data;
            if (ge.getTag() != null && ge.getTag().length() > 6)
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
            ge.validExcelHasSellingPoints();
        }
        if (mType.equals(MType.GIFT_GOODS))
        {
            MktGoodsGiftExcel ge = (MktGoodsGiftExcel)data;
            if (famerMap.containsKey(ge.getUserFarmerName()))
            {
                data.setUserFarmerKey(famerMap.get(ge.getUserFarmerName()));
                String key = data.getUserFarmerKey() + ":" + ge.getUserVendorName();
                if (vendorMap.containsKey(key))
                {
                    data.setUserVendorKey(vendorMap.get(key));
                }
                else
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该商户不存在");
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该市场不存在");
            if(ge.getGiftStartDate() != null && ge.getGiftEndDate() != null
                && ge.getGiftStartDate().compareTo(ge.getGiftEndDate()) > 0)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "兑换有效期开始日期不能大于兑换有效期到期日期"); 
            }
            ge.setWeight(BigDecimal.ZERO);
        }
        if (mType.equals(MType.COLLAGE_GOODS) && data instanceof MktGoodsCollageExcel)
        {
            data.setExtendCon(String.valueOf(((MktGoodsCollageExcel)data).getCollageNum()));
            if (Integer.valueOf(data.getExtendCon()) <= 0)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "成团人数必填,且大于0");
        }
        if (mType.equals(MType.PRESALE_GOODS))
        {
            if (data instanceof MktGoodsPresaleExcel)
            {
                MktGoodsPresaleExcel gp = (MktGoodsPresaleExcel)data;
                if (gp.getPresaleStartDate() == null || gp.getPresaleEndDate() == null)
                    throw TofocusException.of(LejiaErrCode.PRESALEGOODS_DELIVERY_TIME_ERROR);
                Date psd = null;
                if (gp.getPresaleStartDate().contains("-"))
                {
                    psd = DateUtils.parseDate(gp.getPresaleStartDate(), "yyyy-MM-dd");
                }
                else
                {
                    psd = DateUtils.parseDate(gp.getPresaleStartDate(), "yyyy/MM/dd");
                }
                if (psd.compareTo(gp.getStartDate()) < 0)
                    throw TofocusException.of(LejiaErrCode.PRESALEGOODS_TIME_ERROR);
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
        if (mType.equals(MType.CUT_GOODS))
        {
            if (data instanceof MktGoodsCutExcel)
            {
                MktGoodsCutExcel cut = (MktGoodsCutExcel)data;
                Integer l1 = 0;
                Integer u1 = 0;
                try
                {
                    l1 = Integer.valueOf(cut.getCutLow1());
                    u1 = Integer.valueOf(cut.getCutUpon1());
                }
                catch (Exception e)
                {
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "砍价参数填写错误");
                }
                if (l1 > u1) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "砍价下限不能比砍价上限高");
                List<String> extendConList = new ArrayList<>();
                extendConList.add(l1 + "," + u1);
                checkCut(cut.getCutLow2(), cut.getCutUpon2(), extendConList);
                checkCut(cut.getCutLow3(), cut.getCutUpon3(), extendConList);
                checkCut(cut.getCutLow4(), cut.getCutUpon4(), extendConList);
                checkCut(cut.getCutLow5(), cut.getCutUpon5(), extendConList);
                checkCut(cut.getCutLow6(), cut.getCutUpon6(), extendConList);
                checkCut(cut.getCutLow7(), cut.getCutUpon7(), extendConList);
                checkCut(cut.getCutLow8(), cut.getCutUpon8(), extendConList);
                checkCut(cut.getCutLow9(), cut.getCutUpon9(), extendConList);
                checkCut(cut.getCutLow10(), cut.getCutUpon10(), extendConList);
                data.setExtendCon(JsonUtil.toString(extendConList));
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
        if (mType == MType.INTEGRAL_GOODS)
        {
            if (data instanceof MktGoodsIntegralExcel)
            {
                MktGoodsIntegralExcel integral = (MktGoodsIntegralExcel)data;
                if (supplierMap.containsKey(integral.getSupplierName()))
                {
                    integral.setSupplier(supplierMap.get(integral.getSupplierName()));
                }
                else
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该供应商不存在");
                if (integral.getTag() != null && integral.getTag().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
                integral.validExcelHasSellingPoints();
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
        if (mType == MType.INTEGRAL_BNYP_GOODS)
        {
            if (data instanceof MktGoodsIntegralBnypExcel)
            {
                MktGoodsIntegralBnypExcel integral = (MktGoodsIntegralBnypExcel)data;
                if (supplierMap.containsKey(integral.getSupplierName()))
                {
                    integral.setSupplier(supplierMap.get(integral.getSupplierName()));
                }
                else
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该供应商不存在");
                if (integral.getTag() != null && integral.getTag().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
                integral.validExcelHasSellingPoints();
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
        if (mType == MType.INTEGRAL_MSD_GOODS)
        {
            if (data instanceof MktGoodsIntegralMsdExcel)
            {
                MktGoodsIntegralMsdExcel integral = (MktGoodsIntegralMsdExcel)data;
                if (supplierMap.containsKey(integral.getSupplierName()))
                {
                    integral.setSupplier(supplierMap.get(integral.getSupplierName()));
                }
                else
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该供应商不存在");
                if (integral.getTag() != null && integral.getTag().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
                integral.validExcelHasSellingPoints();
                String msdTags = integral.getMsdTags();
                integral.setVisibleRange(MemberVisibleRange.ALL);
                if(StringUtils.isNotBlank(msdTags))
                {
                    String[] split = msdTags.split(",");
                    if("全部用户".equals(split[0]))
                    {
                        if(split.length == 1)
                            integral.setVisibleRange(MemberVisibleRange.ALL);
                        else
                            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "可见用户只能选择全部用户或者其他指定标签");
                    }
                    else
                    {
                        integral.setVisibleRange(MemberVisibleRange.TAG);
                        List<Integer> msdTagKeys = new ArrayList<>();
                        for(int i = 0; i <= split.length - 1; i++)
                        {
                            if(msdTagMap.containsKey(split[i]))
                            {
                                msdTagKeys.add(msdTagMap.get(split[i]));
                            }
                            else
                                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, split[i] + " 该标签不存在");
                        }
                        integral.setMsdTagKeys(msdTagKeys);
                    }
                }
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
        if (mType == MType.INTEGRAL_PRESALE_GOODS)
        {
            if (data instanceof MktGoodsIntegralPresaleExcel)
            {
                MktGoodsIntegralPresaleExcel integral = (MktGoodsIntegralPresaleExcel)data;
                if (supplierMap.containsKey(integral.getSupplierName()))
                {
                    integral.setSupplier(supplierMap.get(integral.getSupplierName()));
                }
                else
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该供应商不存在");
                
                if (integral.getPresaleStartDate() == null)
                    throw TofocusException.of(LejiaErrCode.PRESALEGOODS_DELIVERY_TIME_ERROR);
                if (integral.getStartDate() == null)
                    throw TofocusException.of(LejiaErrCode.INTEGRALPRESALEGOODS_TIME_ERROR);
                if (integral.getEndDate() == null)
                    throw TofocusException.of(LejiaErrCode.INTEGRALPRESALEGOODS_TIME_ERROR2);

                Date psd = null;
                if (integral.getPresaleStartDate().contains("-"))
                {
                    psd = DateUtils.parseDate(integral.getPresaleStartDate(), "yyyy-MM-dd");
                }
                else
                {
                    psd = DateUtils.parseDate(integral.getPresaleStartDate(), "yyyy/MM/dd");
                }
//                if (integral.getPresaleEndDate().contains("-"))
//                {
//                    psd = DateUtils.parseDate(integral.getPresaleEndDate(), "yyyy-MM-dd");
//                }
//                else
//                {
//                    psd = DateUtils.parseDate(integral.getPresaleEndDate(), "yyyy/MM/dd");
//                }
                if (psd.compareTo(integral.getStartDate()) < 0)
                    throw TofocusException.of(LejiaErrCode.PRESALEGOODS_TIME_ERROR);
                if (integral.getTag() != null && integral.getTag().length() > 6)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
                integral.validExcelHasSellingPoints();
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
    }
    
    // 新写监听器  监听传进来的参数
    class GoodsVendorListener<T extends MktGoodsVendorExcel> extends AnalysisEventListener<T>
    {
        private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        public void validator(T v)
        {
            try
            {
                Set<ConstraintViolation<T>> set = validator.validate(v);
                if (set != null && !set.isEmpty())
                {
                    for (ConstraintViolation<T> cv : set)
                    {
                        Field declaredField = v.getClass().getDeclaredField(cv.getPropertyPath().toString());
                        throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR,
                            declaredField.getName() + cv.getMessage());
                    }
                }
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
            }
        }
        
        ExcelWriterBuilder errBuilder;
        
        Map<String, Integer> sapcesRepeat = new HashMap<>();
        
        Map<String, MktGoods> goodsMap = new HashMap<>();
        
        Map<String, MktGoodsMainThree> goodsMainMap = new HashMap<>();
        
        Map<String, MktGoodsSpace> spaceMap = new HashMap<>();
        
        Map<String, String> famerMap = new HashMap<>();
        String farmer;
        
        Map<String, Integer> vendorMap = new HashMap<>();
        
        Class<?> errorModel;
        
        MType mType;
        
        OutputStream out;
        
        List<T> errList = new ArrayList<>();
        
        List<T> addList = new ArrayList<>();
        
        GoodsVendorListener(Map<String, Integer> sapcesRepeat, Map<String, MktGoods> goodsMap,
            Map<String, MktGoodsMainThree> goodsMainMap, Map<String, MktGoodsSpace> spaceMap, String farmer,
            Map<String, Integer> vendorMap, MType mType, Map<String, String> famerMap, List<T> addList, OutputStream out, Class<?> errorModel)
        {
            this.sapcesRepeat = sapcesRepeat;
            this.goodsMap = goodsMap;
            this.goodsMainMap = goodsMainMap;
            this.spaceMap = spaceMap;
            this.mType = mType;
            this.out = out;
            this.addList = addList;
            this.famerMap = famerMap;
            this.errorModel = errorModel;
            this.farmer = farmer;
            this.vendorMap = vendorMap;
        }
        
        @Override
        public void invoke(T data, AnalysisContext context)
        {
            try
            {
                validator(data);
                checkGoodsVendorExcelDate(data, vendorMap, mType, farmer, famerMap);
                putGoodsVendor(data, goodsMainMap, goodsMap, mType);
                String space = data.getGoods() + ":" + data.getSpace();
                if (spaceMap.containsKey(space))
                {
                    MktGoodsSpace goodsSpace = spaceMap.get(space);
                    data.setPkey(goodsSpace.getPkey());
                }
                if (sapcesRepeat.containsKey(space))
                {
                    throw TofocusException.of(LejiaErrCode.GOODSSPACES_NAMEREPEAT);
                }
                else
                    sapcesRepeat.put(space, 1);
                if (data.getGuessLike() == null) data.setGuessLike(false);
                if (data.getPriceMember() == null) data.setPriceMember(BigDecimal.ZERO);
                if (data.getComm() == null) data.setComm(BigDecimal.ZERO);
                addList.add(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                String errmsg;
                if (e instanceof TofocusException)
                {
                    errmsg = e.getMessage();
                }
                else if (e instanceof ParseException)
                {
                    errmsg = "时间格式有问题!";
                }
                else
                {
                    errmsg = e.getClass().getSimpleName() + ":" + e.getMessage();
                }
                data.setErrMsg(errmsg);
                errList.add(data);
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context)
        {
            if (!errList.isEmpty())
            {
                errBuilder = EasyExcel.write(out, errorModel);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(errList, errSheet);
                errWriter.finish();
            }
        }
    }
    
    // 校验导入的数据
    private <T extends MktGoodsVendorExcel> void checkGoodsVendorExcelDate(T data, 
        Map<String, Integer> vendorMap, MType mType, String farmer, Map<String, String> famerMap)
            throws ParseException
    {
        if (StringUtils.isBlank(data.getGooodsMainName()))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品库不能为空");
        if (StringUtils.isBlank(data.getTitle())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品名称不能为空");
        //20240415 更改为默认为0
//        if (data.getXsNum() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "显示销量不能为空");
        if (StringUtils.isBlank(data.getVendorName()))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商户名称不能为空");
//        if (StringUtils.isBlank(data.getStartTime()))
//            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "上架时间不能为空");
//        if (StringUtils.isBlank(data.getEndTime())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "下架时间不能为空");
        
        Date startDate = null;
        Date endDate = null;
        if(StringUtils.isNotBlank(data.getStartTime()))
        {
            if (data.getStartTime().contains("-"))
            {
                startDate = DateUtils.parseDate(data.getStartTime(), "yyyy-MM-dd");
            }
            else
            {
                startDate = DateUtils.parseDate(data.getStartTime(), "yyyy/MM/dd");
            }
        }
        if(StringUtils.isNotBlank(data.getEndTime()))
        {
            if (data.getEndTime().contains("-"))
            {
                endDate = DateUtils.parseDate(data.getEndTime(), "yyyy-MM-dd");
            }
            else
            {
                endDate = DateUtils.parseDate(data.getEndTime(), "yyyy/MM/dd");
            }
        }
        if(data.getStartTime() != null && data.getEndDate() != null && startDate.compareTo(endDate) > 0)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "上架时间不能大于下架时间"); 
        
        data.setStartDate(startDate);
        data.setEndDate(endDate);
        
        if (StringUtils.isBlank(data.getSpace())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品规格不能为空");
        if (data.getPriceOld() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "原价不能为空");
        if (data.getPrice() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "现价不能为空");
        if (data.getKcNum() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "库存不能为空");
        if (data.getGuessLike() == null) data.setGuessLike(false);
        
        if(mType.equals(MType.MARKET_GOODS))
        {
            GoodsMarketVendorExcel ge = (GoodsMarketVendorExcel)data;
            String key = farmer + ":" + ge.getVendorName();
            if (vendorMap.containsKey(key))
            {
                data.setUserVendorKey(vendorMap.get(key));
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该商户不存在");
            if (ge.getTag() != null && ge.getTag().length() > 6)
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
            ge.validExcelHasSellingPoints();
            if (StringUtil.isNotEmpty(ge.getPhoto1())) {
                try {
                    JsonUtil.getBean(ge.getPhoto1(), List.class);
                }catch(Exception e) {
                    throw TofocusException.of(LejiaErrCode.IMPORT_PHOTO_IS_ERROR, "商品轮播图格式不对");
                }
            }
        }
        if(mType.equals(MType.SPECIAL_GOODS))
        {
            MktGoodsOtherVendorExcel ge = (MktGoodsOtherVendorExcel)data;
            String key = farmer + ":" + ge.getVendorName();
            if (vendorMap.containsKey(key))
            {
                data.setUserVendorKey(vendorMap.get(key));
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该商户不存在");
            if (ge.getTag() != null && ge.getTag().length() > 6)
                throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "商品标签不允许超过6个字");
            ge.validExcelHasSellingPoints();
        }
        
        if (mType.equals(MType.GIFT_GOODS))
        {
            MktGoodsGiftVendorExcel ge = (MktGoodsGiftVendorExcel)data;
            if (famerMap.containsKey(ge.getUserFarmerName()))
            {
                data.setUserFarmerKey(famerMap.get(ge.getUserFarmerName()));
                String key = data.getUserFarmerKey() + ":" + ge.getUserVendorName();
                if (vendorMap.containsKey(key))
                {
                    data.setUserVendorKey(vendorMap.get(key));
                }
                else
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该商户不存在");
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "该市场不存在");
            if(ge.getGiftStartDate() != null && ge.getGiftEndDate() != null
                && ge.getGiftStartDate().compareTo(ge.getGiftEndDate()) > 0)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "兑换有效期开始日期不能大于兑换有效期到期日期"); 
            }
            ge.setWeight(BigDecimal.ZERO);
        }
        if (mType.equals(MType.COLLAGE_GOODS) && data instanceof MktGoodsCollageVendorExcel)
        {
            data.setExtendCon(String.valueOf(((MktGoodsCollageVendorExcel)data).getCollageNum()));
            if (Integer.valueOf(data.getExtendCon()) <= 0)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "成团人数必填,且大于0");
        }
        if (mType.equals(MType.PRESALE_GOODS))
        {
            if (data instanceof MktGoodsPresaleVendorExcel)
            {
                MktGoodsPresaleVendorExcel gp = (MktGoodsPresaleVendorExcel)data;
                if (gp.getPresaleStartDate() == null || gp.getPresaleEndDate() == null)
                    throw TofocusException.of(LejiaErrCode.PRESALEGOODS_DELIVERY_TIME_ERROR);
                Date psd = null;
                if (data.getStartTime().contains("-"))
                {
                    psd = DateUtils.parseDate(gp.getPresaleStartDate(), "yyyy-MM-dd");
                }
                else
                {
                    psd = DateUtils.parseDate(gp.getPresaleStartDate(), "yyyy/MM/dd");
                }
                if (psd.compareTo(gp.getStartDate()) < 0)
                    throw TofocusException.of(LejiaErrCode.PRESALEGOODS_TIME_ERROR);
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
        if (mType.equals(MType.CUT_GOODS))
        {
            if (data instanceof MktGoodsCutVendorExcel)
            {
                MktGoodsCutVendorExcel cut = (MktGoodsCutVendorExcel)data;
                Integer l1 = 0;
                Integer u1 = 0;
                try
                {
                    l1 = Integer.valueOf(cut.getCutLow1());
                    u1 = Integer.valueOf(cut.getCutUpon1());
                }
                catch (Exception e)
                {
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "砍价参数填写错误");
                }
                if (l1 > u1) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "砍价下限不能比砍价上限高");
                List<String> extendConList = new ArrayList<>();
                extendConList.add(l1 + "," + u1);
                checkCut(cut.getCutLow2(), cut.getCutUpon2(), extendConList);
                checkCut(cut.getCutLow3(), cut.getCutUpon3(), extendConList);
                checkCut(cut.getCutLow4(), cut.getCutUpon4(), extendConList);
                checkCut(cut.getCutLow5(), cut.getCutUpon5(), extendConList);
                checkCut(cut.getCutLow6(), cut.getCutUpon6(), extendConList);
                checkCut(cut.getCutLow7(), cut.getCutUpon7(), extendConList);
                checkCut(cut.getCutLow8(), cut.getCutUpon8(), extendConList);
                checkCut(cut.getCutLow9(), cut.getCutUpon9(), extendConList);
                checkCut(cut.getCutLow10(), cut.getCutUpon10(), extendConList);
                data.setExtendCon(JsonUtil.toString(extendConList));
            }
            else
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "导入excel有问题");
        }
    }
    
    private void checkCut(String low, String upon, List<String> extendConList)
    {
        boolean lb1 = StringUtils.isBlank(low);
        boolean ub1 = StringUtils.isBlank(upon);
        if ((lb1 && !ub1) || (!lb1 && ub1))
        {
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "砍价下限和砍价上限需要一起填写");
        }
        if (!lb1 && !ub1)
        {
            Integer l2 = 0;
            Integer u2 = 0;
            try
            {
                l2 = Integer.valueOf(low);
                u2 = Integer.valueOf(upon);
            }
            catch (Exception e)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "砍价参数填写错误");
            }
            if (l2 > u2) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "砍价下限不能比砍价上限高");
            extendConList.add(low + "," + upon);
        }
    }
    
    // 导入商品表使用
    private <T extends MktGoodsExcel> MktGoods putGoods(T data, Map<String, MktGoodsMainThree> goodsMainMap,
        Map<String, MktGoods> goodsMap, MType mType)
    {
        MktGoods g = new MktGoods();
        List<MktGoodsSellingPoint> sellingPoints = null;
        boolean flag = true;
        if (goodsMap.containsKey(data.getTitle()))
        {
            BeanUtils.copyProperties(goodsMap.get(data.getTitle()), g);
            flag = false;
        }
        String gooodsMainName = data.getGooodsMainName();
        if (StringUtils.isBlank(gooodsMainName) || !gooodsMainName.contains("/"))
        {
            throw TofocusException.of(WsaleErrCode.DATE_ERR);
        }
        if (!goodsMainMap.containsKey(gooodsMainName)) throw TofocusException.of(WsaleErrCode.DATE_ERR);
        MktGoodsMainThree mktGoodsMainThree = goodsMainMap.get(gooodsMainName);
//        MktGoodsMain goodsMain = goodsMainMap.get(gooodsMainName);
        g.setGtype(mktGoodsMainThree.getGtype());
        g.setGoodsMain(mktGoodsMainThree.getTwoGtype());
        g.setThreeGtype(mktGoodsMainThree.getPkey());
        if (data instanceof GoodsMarketExcel)
        {
            GoodsMarketExcel excel = (GoodsMarketExcel)data;
            Boolean guessLike = excel.getGuessLike();
            g.setGuessLike(guessLike == null ? false : guessLike);
            Integer guessSort = excel.getGuessSort();
            g.setGuessSort(guessSort == null ? 0 : guessSort);
            Integer sort = excel.getSort();
            g.setSort(sort == null ? 0 : sort);
            //导入轮播图
            if (StringUtil.isNotBlank(excel.getPhoto1()))
                g.setPhoto1(JsonUtil.getBean(excel.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(excel.getContent2()))
                g.setContent2(excel.getContent2());
            g.setTag(excel.getTag());
            sellingPoints = excel.convertSellingPoints2List();
        }
        if (data instanceof MktGoodsOtherExcel)
        {
            MktGoodsOtherExcel excel = (MktGoodsOtherExcel) data;
            g.setSort(excel.getSort() == null ? 0 : excel.getSort());
            //导入轮播图
            if (StringUtil.isNotBlank((excel.getPhoto1())))
                g.setPhoto1(JsonUtil.getBean(excel.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(excel.getContent2()))
                g.setContent2(excel.getContent2());
            g.setTag(excel.getTag());
            sellingPoints = excel.convertSellingPoints2List();
        }
        if (data instanceof MktGoodsIntegralExcel)
        {
            MktGoodsIntegralExcel excel = (MktGoodsIntegralExcel)data;
            g.setSupplier(excel.getSupplier());
            g.setSort(excel.getSort() == null ? 0 : excel.getSort());
            //导入轮播图
            if (StringUtil.isNotBlank((excel.getPhoto1())))
                g.setPhoto1(JsonUtil.getBean(excel.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(excel.getContent2()))
                g.setContent2(excel.getContent2());
            g.setTag(excel.getTag());
            sellingPoints = excel.convertSellingPoints2List();
        }
        if (data instanceof MktGoodsIntegralBnypExcel)
        {
            MktGoodsIntegralBnypExcel excel = (MktGoodsIntegralBnypExcel)data;
            g.setSupplier(excel.getSupplier());
            g.setSort(excel.getSort() == null ? 0 : excel.getSort());
            //导入轮播图
            if (StringUtil.isNotBlank((excel.getPhoto1())))
                g.setPhoto1(JsonUtil.getBean(excel.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(excel.getContent2()))
                g.setContent2(excel.getContent2());
            g.setTag(excel.getTag());
            sellingPoints = excel.convertSellingPoints2List();
        }
        if (data instanceof MktGoodsIntegralMsdExcel)
        {
            MktGoodsIntegralMsdExcel excel = (MktGoodsIntegralMsdExcel)data;
            g.setSupplier(excel.getSupplier());
            g.setSort(excel.getSort() == null ? 0 : excel.getSort());
            //导入轮播图
            if (StringUtil.isNotBlank((excel.getPhoto1())))
                g.setPhoto1(JsonUtil.getBean(excel.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(excel.getContent2()))
                g.setContent2(excel.getContent2());
            g.setTag(excel.getTag());
            sellingPoints = excel.convertSellingPoints2List();
            g.setVisibleRange(excel.getVisibleRange());
        }
        if (data instanceof MktGoodsIntegralPresaleExcel)
        {
            MktGoodsIntegralPresaleExcel excel = (MktGoodsIntegralPresaleExcel)data;
            g.setSupplier(excel.getSupplier());
            //导入轮播图
            if (StringUtil.isNotBlank((excel.getPhoto1())))
                g.setPhoto1(JsonUtil.getBean(excel.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(excel.getContent2()))
                g.setContent2(excel.getContent2());
            g.setTag(excel.getTag());
            sellingPoints = excel.convertSellingPoints2List();
        }
        if (mType.equals(MType.CUT_GOODS))
        {
            g.setExtendCon(data.getExtendCon());
        }
        if (mType.equals(MType.COLLAGE_GOODS))
        {
            g.setExtendCon(data.getExtendCon());
        }
        if (!mType.equals(MType.MARKET_GOODS))
        {
            g.setGuessLike(false);
        }
        if (data.getPoint() == null) data.setPoint(0);
        if (mType.equals(MType.GIFT_GOODS))
            g.setIsPostage(true);
        else
        {
            if (data.getIsPostage() != null && data.getIsPostage().intValue() == 1)
                g.setIsPostage(true);
            else
                g.setIsPostage(false);
        }
        g.setMType(mType);
        g.setTitle(data.getTitle());
        g.setSerialNumber(data.getSerialNumber());
        g.setDescription(data.getDescription());
        g.setStartDate(data.getStartDate());
        g.setEndDate(data.getEndDate());
        int viewCount = 0;
        if (g != null && g.getViewCount() != null) viewCount = g.getViewCount();
        if (flag)
        {
            if(g.getStartDate() == null || g.getEndDate() == null)
                g.setEnabled(false);
            else if (System.currentTimeMillis() < g.getStartDate().getTime()
                || System.currentTimeMillis() > g.getEndDate().getTime())
            {
                g.setEnabled(false);
            }
            else
            {
                g.setEnabled(true);
            }
            //仅新增导入显示数量，修改不更新
            if (data.getXsNum() == null)
                g.setXsNum(0);
            else
                g.setXsNum(data.getXsNum());
        }
        g.setViewCount(viewCount);
        g.setXsNum(data.getXsNum());
        g.setPurchaseNum(data.getPurchaseNum());
        if(g.getPurchaseNum() == null)
            g.setPurchaseNum(0);
        g.setPrice(data.getPrice());
        
        if (g.getSort() == null) g.setSort(0);
        g.setFarmer(CurrentSession.marketPkey());
        g.setCompany(CurrentSession.companyPkey());
        g.setAscription(CurrentSession.ascriptionPkey());
        g.setIdDel(false);
        g.setRowVension(1);
        MktGoods put = goodsDao.put(g);
        if (flag) goodsMap.put(data.getTitle(), put);
        
        if(MType.INTEGRAL_MSD_GOODS.equals(put.getMType()) 
            && MemberVisibleRange.TAG.equals(g.getVisibleRange())
            && data instanceof MktGoodsIntegralMsdExcel)
        {
            MktGoodsIntegralMsdExcel excel = (MktGoodsIntegralMsdExcel)data;
            List<Integer> msdTagKeys = excel.getMsdTagKeys();
            tagManager.putTagVisibles(TagVisibleTargetType.INTEGRAL_MSD_GOODS,
                g.getPkey().longValue(),
                msdTagKeys,
                g.getAscription());
        }
        
        goodsSellingPointDao.removeByGoods(put.getPkey(), put.getAscription());
        if (sellingPoints != null)
        {
            for (MktGoodsSellingPoint sellingPoint : sellingPoints)
            {
                sellingPoint.setGoods(put.getPkey());
                sellingPoint.setAscription(put.getAscription());
            }
            goodsSellingPointDao.addAll(sellingPoints);
        }
        data.setGoods(put.getPkey());
        if (mType.equals(MType.PRESALE_GOODS) && data instanceof MktGoodsPresaleExcel)
        {
            MktGoodsPresaleExcel gpe = (MktGoodsPresaleExcel)data;
            MktGoodsPresale gp = new MktGoodsPresale();
            gp.setPkey(put.getPkey());
            gp.setAscription(put.getAscription());
            try
            {
                gp.setStartDate(DateUtil.formatDateStr(gpe.getPresaleStartDate()));
                gp.setEndDate(DateUtil.formatDateStr(gpe.getPresaleEndDate()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            goodsPresaleDao.add(gp);
        }
        if (mType.equals(MType.INTEGRAL_PRESALE_GOODS) && data instanceof MktGoodsIntegralPresaleExcel)
        {
            MktGoodsIntegralPresaleExcel gpe = (MktGoodsIntegralPresaleExcel)data;
            MktGoodsPresale gp = new MktGoodsPresale();
            gp.setPkey(put.getPkey());
            gp.setAscription(put.getAscription());
            try
            {
                gp.setStartDate(DateUtil.formatDateStr(gpe.getPresaleStartDate()));
                gp.setEndDate(DateUtil.formatDateStr(gpe.getPresaleEndDate()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            goodsPresaleDao.add(gp);
        }
        if (mType.equals(MType.INTEGRAL_MSD_GOODS) && data instanceof MktGoodsIntegralMsdExcel)
        {
            MktGoodsIntegralMsdExcel gpe = (MktGoodsIntegralMsdExcel)data;
            
            if(gpe.getPresaleStartDate() != null || gpe.getPresaleEndDate() != null)
            {
                MktGoodsPresale gp = new MktGoodsPresale();
                gp.setPkey(put.getPkey());
                gp.setAscription(put.getAscription());
                try
                {
                    if(gpe.getPresaleStartDate() != null)
                        gp.setStartDate(DateUtil.formatDateStr(gpe.getPresaleStartDate()));
                    if(gpe.getPresaleEndDate() != null)
                        gp.setEndDate(DateUtil.formatDateStr(gpe.getPresaleEndDate()));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                goodsPresaleDao.add(gp);
            }
        }
        return put;
    }
    // 导入商品表使用
    private <T extends MktGoodsVendorExcel> MktGoods putGoodsVendor(T data, Map<String, MktGoodsMainThree> goodsMainMap,
        Map<String, MktGoods> goodsMap, MType mType)
    {
        MktGoods g = new MktGoods();
        List<MktGoodsSellingPoint> sellingPoints = null;
        boolean flag = true;
        String goodskey = data.getTitle() + "::" + data.getVendorName(); 
        if (goodsMap.containsKey(goodskey))
        {
            BeanUtils.copyProperties(goodsMap.get(goodskey), g);
            flag = false;
        }
        String gooodsMainName = data.getGooodsMainName();
        if (StringUtils.isBlank(gooodsMainName) || !gooodsMainName.contains("/"))
        {
            throw TofocusException.of(WsaleErrCode.DATE_ERR);
        }
        if (!goodsMainMap.containsKey(gooodsMainName)) throw TofocusException.of(WsaleErrCode.DATE_ERR);
        MktGoodsMainThree mktGoodsMainThree = goodsMainMap.get(gooodsMainName);
//      MktGoodsMain goodsMain = goodsMainMap.get(gooodsMainName);
        g.setGtype(mktGoodsMainThree.getGtype());
        g.setGoodsMain(mktGoodsMainThree.getTwoGtype());
        g.setThreeGtype(mktGoodsMainThree.getPkey());
        if (data instanceof GoodsMarketVendorExcel)
        {
//            Boolean guessLike = ((GoodsMarketVendorExcel)data).getGuessLike();
//            g.setGuessLike(guessLike == null ? false : guessLike);
//            Integer guessSort = ((GoodsMarketVendorExcel)data).getGuessSort();
//            g.setGuessSort(guessSort == null ? 0 : guessSort);
            g.setGuessLike(false);
            g.setGuessSort(0);
            GoodsMarketVendorExcel gdata = (GoodsMarketVendorExcel)data;
            Integer sort = gdata.getSort();
            g.setSort(sort == null ? 0 : sort);
            //导入轮播图
            if (StringUtil.isNotBlank(gdata.getPhoto1()))
                g.setPhoto1(JsonUtil.getBean(gdata.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(gdata.getContent2()))
                g.setContent2(gdata.getContent2());
            g.setTag(((GoodsMarketVendorExcel) data).getTag());
            sellingPoints = ((GoodsMarketVendorExcel) data).convertSellingPoints2List();
        }
        if (data instanceof MktGoodsOtherVendorExcel)
        {
            MktGoodsOtherVendorExcel gdata = (MktGoodsOtherVendorExcel)data;
            Integer sort = gdata.getSort();
            g.setSort(sort == null ? 0 : sort);
            //导入轮播图
            if (StringUtil.isNotEmpty(gdata.getPhoto1()))
                g.setPhoto1(JsonUtil.getBean(gdata.getPhoto1(), List.class));
            if (StringUtil.isNotBlank(gdata.getContent2()))
                g.setContent2(gdata.getContent2());
            g.setTag(((MktGoodsOtherVendorExcel) data).getTag());
            sellingPoints = ((MktGoodsOtherVendorExcel) data).convertSellingPoints2List();
        }
        if (mType.equals(MType.CUT_GOODS))
        {
            g.setExtendCon(data.getExtendCon());
        }
        if (mType.equals(MType.COLLAGE_GOODS))
        {
            g.setExtendCon(data.getExtendCon());
        }
        if (!mType.equals(MType.MARKET_GOODS))
        {
            g.setGuessLike(false);
        }
        if (data.getPoint() == null) data.setPoint(0);
        if (mType.equals(MType.GIFT_GOODS))
            g.setIsPostage(true);
        else
        {
            if (data.getIsPostage() != null && data.getIsPostage().intValue() == 1)
                g.setIsPostage(true);
            else
                g.setIsPostage(false);
        }
        g.setMType(mType);
        g.setTitle(data.getTitle());
        g.setSerialNumber(data.getSerialNumber());
        g.setDescription(data.getDescription());
        g.setStartDate(data.getStartDate());
        g.setEndDate(data.getEndDate());
        g.setVendor(data.getUserVendorKey());
        int viewCount = 0;
        if (g != null && g.getViewCount() != null) viewCount = g.getViewCount();
        if (flag)
        {
            if(g.getStartDate() == null || g.getEndDate() == null)
                g.setEnabled(false);
            else if (System.currentTimeMillis() < g.getStartDate().getTime()
                || System.currentTimeMillis() > g.getEndDate().getTime())
            {
                g.setEnabled(false);
            }
            else
            {
                g.setEnabled(true);
            }
            //新增导入显示数量
            if (data.getXsNum() == null)
                g.setXsNum(0);
            else
                g.setXsNum(data.getXsNum());
        }else {
            //显示数量为空不修改
            if (data.getXsNum() != null)
                g.setXsNum(data.getXsNum());
        }
        g.setViewCount(viewCount);
        g.setPurchaseNum(data.getPurchaseNum());
        if(g.getPurchaseNum() == null)
            g.setPurchaseNum(0);
        g.setPrice(data.getPrice());
        if (g.getSort() == null) g.setSort(0);
        g.setFarmer(CurrentSession.marketPkey());
        g.setCompany(CurrentSession.companyPkey());
        g.setAscription(CurrentSession.ascriptionPkey());
        g.setIdDel(false);
        g.setRowVension(1);
        System.out.println("yx14>>"+g);
        MktGoods put = goodsDao.put(g);
        if (flag) goodsMap.put(data.getTitle() + "::" + data.getVendorName(), put);
        goodsSellingPointDao.removeByGoods(put.getPkey(), put.getAscription());
        if (sellingPoints != null)
        {
            for (MktGoodsSellingPoint sellingPoint : sellingPoints)
            {
                sellingPoint.setGoods(put.getPkey());
                sellingPoint.setAscription(put.getAscription());
            }
            goodsSellingPointDao.addAll(sellingPoints);
        }
        data.setGoods(put.getPkey());
        return put;
    }
    
    @Transactional
    private void processCouponExcelData(List<GoodsCouponExcel> data, Map<String, MktGoodsSpace> spaceMap, Boolean flag)
    {
        List<MktGoodsSpace> insSpace = new ArrayList<>();
        List<MktGoodsSpace> updSpace = new ArrayList<>();
        List<MktCard> updCard = new ArrayList<>();
        Map<Integer,String> updGoods = new HashMap<>();
        
        List<MktWareLine> addWareLineAll = new ArrayList<>();
        Map<Integer, String> goodsMap = new HashMap<>();
        Integer ascription = CurrentSession.ascriptionPkey();
        for (GoodsCouponExcel ge : data)
        {
            MktGoodsSpace space = BeanUtil.beanFrom(MktGoodsSpace.class, ge);
            space.setWeight(BigDecimal.ZERO);
            space.setAscription(CurrentSession.ascriptionPkey());
            MktCard card = new MktCard();
            BeanUtils.copyProperties(ge, card, "content", "startDate", "endDate", "title", "ascription");
            card.setStartDate(ge.getCardStartDate());
            card.setEndDate(ge.getCardEndDate());
            card.setTitle(space.getSpace());
            card.setCount(space.getKcNum());
            card.setRowVension(3);
            card.setAscription(CurrentSession.ascriptionPkey());
            
            space.setPriceOld(BigDecimal.ZERO);
            space.setPriceMember(BigDecimal.ZERO);
            space.setComm(BigDecimal.ZERO);
            if (ge.getPkey() == null)
            {
                if (space.getXsNum() == null) space.setXsNum(0);
                goodsMap.put(ge.getGoods(), ge.getTitle());
                insSpace.add(space);
                card.setFarmer(Constant.Operation + ascription);
                card.setCompany(Constant.Operation + ascription);
                card.setIdDel(false);
                card.setInvalid(false);
                card.setEnabled(true);
                card.setIssuedNum(0);
                card.setUsedNum(0);
                Integer cardKey = cardDao.add(card).getPkey();
                ge.setExtendCon(String.valueOf(cardKey));
                updGoods.put(ge.getGoods(), String.valueOf(cardKey));
            }
            else
            {
                MktCard mktCard = cardDao.get(Integer.valueOf(ge.getExtendCon()));
                BeanUtils.copyProperties(mktCard, card, "startDate", "endDate", "title", "count", "ascription");
                updCard.add(card);
                MktGoodsSpace updaSpaceModel = goodsSpaceDao.get(ge.getPkey());
                space.setXsNum(updaSpaceModel.getXsNum());
                updSpace.add(space);
                if (updaSpaceModel.getKcNum().intValue() != ge.getKcNum().intValue())
                {
                    MktWareLine add = new MktWareLine();
                    add.setWareType(WareType.INVENTORY);
                    add.setGoods(ge.getGoods());
                    add.setGoodsName(ge.getTitle());
                    add.setSpace(ge.getPkey());
                    add.setSpaceName(ge.getSpace());
                    add.setNum(ge.getKcNum() - updaSpaceModel.getKcNum());
                    add.setActualNum(ge.getKcNum());
                    add.setAscription(CurrentSession.ascriptionPkey());
                    addWareLineAll.add(add);
                }
            }
        }
        List<MktGoodsSpace> addAll = goodsSpaceDao.addAll(insSpace);
        addAll.forEach(sk -> {
            spaceKcCache.set(String.valueOf(sk.getPkey()), (long)sk.getKcNum());
        });
        spaceKcDao.addAll(BeanUtil.beanListFrom(MktSpaceKc.class, addAll));
        for (MktGoodsSpace gs : addAll)
        {
            if (goodsMap.containsKey(gs.getGoods()))
            {
                String title = goodsMap.get(gs.getGoods());
                MktWareLine add = new MktWareLine();
                add.setWareType(WareType.WAREHOUSING);
                add.setGoods(gs.getGoods());
                add.setGoodsName(title);
                add.setSpace(gs.getPkey());
                add.setSpaceName(gs.getSpace());
                add.setNum(gs.getKcNum());
                add.setActualNum(gs.getKcNum());
                add.setAscription(CurrentSession.ascriptionPkey());
                addWareLineAll.add(add);
            }
        }
        List<MktGoodsSpace> updateAll = goodsSpaceDao.updateAll(updSpace);
        updateAll.forEach(sk -> spaceKcCache.set(String.valueOf(sk.getPkey()), (long)sk.getKcNum()));
        spaceKcDao.updateAll(BeanUtil.beanListFrom(MktSpaceKc.class, updateAll));
        wareLineDao.addAll(addWareLineAll);
        if(!updGoods.isEmpty())
            goodsDao.updCoupon(updGoods);
        addAll.addAll(updateAll);
        // 处理卡券
        cardDao.updateAll(updCard);
        
    }
    
    // 新写监听器  监听传进来的参数
    class CouponGoodsListener extends AnalysisEventListener<GoodsCouponExcel>
    {
        private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        public void validator(GoodsCouponExcel v)
        {
            try
            {
                Set<ConstraintViolation<GoodsCouponExcel>> set = validator.validate(v);
                if (set != null && !set.isEmpty())
                {
                    for (ConstraintViolation<GoodsCouponExcel> cv : set)
                    {
                        Field declaredField = v.getClass().getDeclaredField(cv.getPropertyPath().toString());
                        throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR,
                            declaredField.getName() + cv.getMessage());
                    }
                }
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
            }
        }
        
        ExcelWriterBuilder errBuilder;
        
        Map<String, Integer> sapcesRepeat = new HashMap<>();
        
        Map<String, MktGoods> goodsMap = new HashMap<>();
        
        Map<String, MktGoodsMainThree> goodsMainMap = new HashMap<>();
        
        Map<String, MktGoodsSpace> spaceMap = new HashMap<>();
        
        Map<String, MktGoods> goodsAllMap = new HashMap<>();
        
        Map<String, String> farmerMap = new HashMap<>();
        
        Map<String, MktGtype> gtypeMap = new HashMap<>();
        
        OutputStream out;
        
        List<GoodsCouponExcel> errList = new ArrayList<>();
        
        List<GoodsCouponExcel> addList = new ArrayList<>();
        
        CouponGoodsListener(Map<String, Integer> sapcesRepeat, Map<String, MktGoods> goodsMap,
            Map<String, MktGoodsMainThree> goodsMainMap, Map<String, MktGoodsSpace> spaceMap, List<GoodsCouponExcel> addList,
            Map<String, MktGtype> gtypeMap, Map<String, MktGoods> goodsAllMap, Map<String, String> farmerMap,
            OutputStream out)
        {
            this.sapcesRepeat = sapcesRepeat;
            this.goodsMap = goodsMap;
            this.goodsMainMap = goodsMainMap;
            this.spaceMap = spaceMap;
            this.out = out;
            this.addList = addList;
            this.goodsAllMap = goodsAllMap;
            this.farmerMap = farmerMap;
            this.gtypeMap = gtypeMap;
        }
        
        @Override
        public void invoke(GoodsCouponExcel data, AnalysisContext context)
        {
            try
            {
                validator(data);
                checkCouponGoodsExcelDate(data, gtypeMap, goodsAllMap, farmerMap);
                putCouponGoods(data, goodsMainMap, goodsMap);
                String space = data.getGoods() + data.getSpace();
                if (data.getCardStartDate() == null && data.getCardEndDate() == null && data.getEffective() == null)
                {
                    throw TofocusException.of(LejiaErrCode.CARD_EFFECTIVE_ONE);
                }
                if (spaceMap.containsKey(space))
                {
                    MktGoodsSpace goodsSpace = spaceMap.get(space);
                    data.setPkey(goodsSpace.getPkey());
                }
                if (sapcesRepeat.containsKey(space))
                {
                    throw TofocusException.of(LejiaErrCode.GOODSSPACES_NAMEREPEAT);
                }
                else
                    sapcesRepeat.put(space, 1);
                data.setAscription(CurrentSession.ascriptionPkey());
                addList.add(data);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                String errmsg;
                if (e instanceof TofocusException)
                {
                    errmsg = e.getMessage();
                }
                else if (e instanceof ParseException)
                {
                    errmsg = "时间格式有问题!";
                }
                else
                {
                    errmsg = e.getClass().getSimpleName() + ":" + e.getMessage();
                }
                data.setErrMsg(errmsg);
                errList.add(data);
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context)
        {
            if (!errList.isEmpty())
            {
                errBuilder = EasyExcel.write(out, GoodsCouponExcel.class);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(errList, errSheet);
                errWriter.finish();
            }
        }
    }
    
    // 校验导入的优惠券商品数据
    private void checkCouponGoodsExcelDate(GoodsCouponExcel data, Map<String, MktGtype> gtypeMap,
        Map<String, MktGoods> goodsAllMap, Map<String, String> farmerMap)
        throws ParseException
    {
        if (StringUtils.isBlank(data.getGooodsMainName()))
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品库不能为空");
        if (StringUtils.isBlank(data.getTitle())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品名称不能为空");
//        if (StringUtils.isBlank(data.getStartTime()))
//            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "上架时间不能为空");
//        if (StringUtils.isBlank(data.getEndTime())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "下架时间不能为空");
        
        Date startDate = null;
        Date endDate = null;
        if(StringUtils.isNotBlank(data.getStartTime()))
        {
            if (data.getStartTime().contains("-"))
            {
                startDate = DateUtils.parseDate(data.getStartTime(), "yyyy-MM-dd");
            }
            else
            {
                startDate = DateUtils.parseDate(data.getStartTime(), "yyyy/MM/dd");
            }
        }
        if(StringUtils.isNotBlank(data.getEndTime()))
        {
            if (data.getEndTime().contains("-"))
            {
                endDate = DateUtils.parseDate(data.getEndTime(), "yyyy-MM-dd");
            }
            else
            {
                endDate = DateUtils.parseDate(data.getEndTime(), "yyyy/MM/dd");
            }
        }
        if(data.getStartTime() != null && data.getEndDate() != null && startDate.compareTo(endDate) > 0)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "上架时间不能大于下架时间"); 
        data.setStartDate(startDate);
        data.setEndDate(endDate);
        if (StringUtils.isNotBlank(data.getUserFarmerName()))
        {
            if (farmerMap.containsKey(data.getUserFarmerName()))
            {
                data.setUserFarmer(farmerMap.get(data.getUserFarmerName()));
            }
            else
                throw TofocusException.of(LejiaErrCode.GOODS_FARMER_ERROR);
        }
        if (StringUtils.isNotBlank(data.getUserTypeName()))
        {
            if (gtypeMap.containsKey(data.getUserTypeName()))
            {
                data.setUserType(gtypeMap.get(data.getUserTypeName()).getPkey());
            }
            else
                throw TofocusException.of(LejiaErrCode.GOODS_GTYPE_ERROR);
        }
        if (StringUtils.isNotBlank(data.getUserGoodsName()))
        {
            if (goodsAllMap.containsKey(data.getUserGoodsName()))
            {
                MktGoods mktGoods = goodsAllMap.get(data.getUserGoodsName());
                if (StringUtils.isNotBlank(data.getUserFarmer()) && !mktGoods.getFarmer().equals(data.getUserFarmer()))
                {
                    throw TofocusException.of(LejiaErrCode.GOODS_GTYPE_ERROR);
                }
                if (data.getUserType() != null && !mktGoods.getGtype().equals(data.getUserType()))
                {
                    throw TofocusException.of(LejiaErrCode.GOODS_GTYPE_ERROR);
                }
                data.setUserGoods(mktGoods.getPkey());
            }
            else
                throw TofocusException.of(LejiaErrCode.GOODS_GTYPE_ERROR);
        }
        
        if (StringUtils.isBlank(data.getSpace())) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "商品规格不能为空");
        if (data.getPrice() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "现价不能为空");
        if (data.getKcNum() == null) throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "库存不能为空");
        
        if(data.getCardStartDate() != null && data.getCardEndDate() != null
            && data.getCardStartDate().compareTo(data.getCardEndDate()) > 0)
        {
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "有效期-开始日期不能大于有效期-到期日期"); 
        }
        
    }
    
    // 导入商品表使用
    private MktGoods putCouponGoods(GoodsCouponExcel data, Map<String, MktGoodsMainThree> goodsMainMap,
        Map<String, MktGoods> goodsMap)
    {
        MktGoods g = new MktGoods();
        boolean flag = true;
        if (goodsMap.containsKey(data.getTitle()))
        {
            BeanUtils.copyProperties(goodsMap.get(data.getTitle()), g);
            flag = false;
        }
        String gooodsMainName = data.getGooodsMainName();
        if (StringUtils.isBlank(gooodsMainName))
        {
            throw TofocusException.of(WsaleErrCode.DATE_ERR);
        }
        gooodsMainName = "优惠券/" + gooodsMainName;
        if (!goodsMainMap.containsKey(gooodsMainName)) throw TofocusException.of(WsaleErrCode.DATE_ERR);
        MktGoodsMainThree mktGoodsMainThree = goodsMainMap.get(gooodsMainName);
//      MktGoodsMain goodsMain = goodsMainMap.get(gooodsMainName);
        g.setGtype(mktGoodsMainThree.getGtype());
        g.setGoodsMain(mktGoodsMainThree.getTwoGtype());
        g.setThreeGtype(mktGoodsMainThree.getPkey());
//        MktGoodsMain goodsMain = goodsMainMap.get(gooodsMainName);
//        g.setGtype(goodsMain.getGtype());
//        g.setGoodsMain(goodsMain.getPkey());
        g.setMType(MType.COUPON_GOODS);
        g.setTitle(data.getTitle());
        g.setStartDate(data.getStartDate());
        g.setEndDate(data.getEndDate());
        if (StringUtils.isNotBlank(data.getExtendCon())) g.setExtendCon(data.getExtendCon());
        int viewCount = 0;
        if (g.getViewCount() != null) viewCount = g.getViewCount();
        if (flag)
        {
            if(g.getStartDate() == null || g.getEndDate() == null)
                g.setEnabled(false);
            else if (System.currentTimeMillis() < g.getStartDate().getTime()
                || System.currentTimeMillis() > g.getEndDate().getTime())
            {
                g.setEnabled(false);
            }
            else
            {
                g.setEnabled(true);
            }
        }
        if(g.getGuessLike() == null)
            g.setGuessLike(false);
        g.setViewCount(viewCount);
        if (g.getXsNum() == null) g.setXsNum(0);
        g.setPurchaseNum(data.getPurchaseNum());
        if (g.getPurchaseNum() == null) g.setPurchaseNum(0);
        g.setPrice(data.getPrice());
        g.setIsPostage(true);
        if (g.getSort() == null) g.setSort(0);
        g.setFarmer(CurrentSession.marketPkey());
        g.setCompany(CurrentSession.companyPkey());
        g.setAscription(CurrentSession.ascriptionPkey());
        g.setIdDel(false);
        g.setRowVension(3);
        MktGoods put = goodsDao.put(g);
        if (flag) goodsMap.put(data.getTitle(), put);
        
        if (StringUtils.isNotBlank(g.getExtendCon()) && cardDao.checkInvalid(Integer.valueOf(g.getExtendCon())))
            throw TofocusException.of(WsaleErrCode.CARD_INVALID);
        data.setGoods(put.getPkey());
        data.setExtendCon(put.getExtendCon());
        return put;
    }
    
}
