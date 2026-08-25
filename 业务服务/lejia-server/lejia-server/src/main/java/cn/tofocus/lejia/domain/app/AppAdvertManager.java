package cn.tofocus.lejia.domain.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.domain.AccountManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.db.SelectBuilder;
import cn.tofocus.lejia.bean.dto.app.market.AppAdvertOnList;

import cn.tofocus.lejia.bean.dto.market.MktFunMenuConfigOnList;
import cn.tofocus.lejia.bean.dto.market.MktIndexAdvertOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.market.MktActivity;
import cn.tofocus.lejia.bean.entity.market.MktAdvert;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.market.MktIndexAdvert;
import cn.tofocus.lejia.bean.entity.market.MktSpecialAdvert;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.entity.member.MktMemberIndexAdvert;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.enums.AdvertPosition;
import cn.tofocus.lejia.bean.enums.AdvertType;
import cn.tofocus.lejia.bean.enums.IndexAdvertSubject;
import cn.tofocus.lejia.bean.enums.LevelType;
import cn.tofocus.lejia.bean.enums.LinkType;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.market.MktActivityDao;
import cn.tofocus.lejia.dao.market.MktAdvertDao;
import cn.tofocus.lejia.dao.market.MktFunMenuConfigDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.market.MktIndexAdvertDao;
import cn.tofocus.lejia.dao.market.MktMemberDao;
import cn.tofocus.lejia.dao.market.MktMemberIndexAdvertDao;
import cn.tofocus.lejia.dao.market.MktMemberTagDao;
import cn.tofocus.lejia.dao.market.MktSpecialAdvertDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppAdvertManager
{
    
    private final AccountManager accountManager;
    
    @Autowired
    private MktAdvertDao mktAdvertDao;
    
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktIndexAdvertDao indexAdvertDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private MktSpecialAdvertDao mktSpecialAdvertDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktMemberIndexAdvertDao memberIndexAdvertDao;
    
    @Autowired
    private MktActivityDao activityDao;
    
    @Autowired
    private MktFunMenuConfigDao mktFunMenuConfigDao;
    
    @Autowired
    private MktVendorDao vendorDao;
    
    @Autowired
    private MktTagDao mktTagDao;
    
    @Autowired
    private MktMemberTagDao mktMemberTagDao;
    
    AppAdvertManager(AccountManager accountManager)
    {
        this.accountManager = accountManager;
    }
    
    public AppAdvertOnList getAdvert(Integer pkey)
    {
        return mktAdvertDao.selectOne().eq("pkey", pkey).eq("enabled", true).execDto(AppAdvertOnList.class);
        // return BeanUtil.beanFrom(AppAdvertOnList.class, advert);
    }
    
    public List<AppAdvertOnList> queryAppAdvert(AdvertPosition position, String positionObj)
    {
        Integer ascription = MobileSession.appid();
        Integer member = MobileSession.memberPkey();
        // 判断 是否走专区广告 
        Boolean value = sysConfigDao.getValue(Constant.SysConfig.ADVERTISE_MANAGER_DEPLOY, ascription);
        String farmerPkey = MobileSession.farmerPkey();
        log.info("farmerPkey: {}", farmerPkey);
        SelectBuilder<Integer, MktAdvert> builder =
            mktAdvertDao.select().eq("enabled", true).sort("sort", false).sort("pkey", true);
        if (position != null)
        {
            builder.eq("position", position);
            if (AdvertPosition.ascriptionPositions().contains(position))
                farmerPkey = Constant.Operation + ascription;
        }
        // 天津定制  预售广告 只走运营端 
        if (ascription != null && (22 == ascription || 13 == ascription)
            && AdvertPosition.ADVERT_POSITION_SALE.equals(position))
        {
            List<MktSpecialAdvert> exec =
                mktSpecialAdvertDao.select().eq("position", position).eq("farmer", farmerPkey).exec();
            List<Integer> keys = new ArrayList<>();
            exec.forEach(e -> keys.add(e.getAdvertKey()));
            if (keys.isEmpty())
                return new ArrayList<>();
            builder.eq("type", AdvertType.SPECIAL_AREA).eq("farmer", Constant.Operation + ascription).in("pkey", keys);
        }
        else if (position == AdvertPosition.ADVERT_POSITION_GOODS_MAIN)
        {
            if (positionObj == null)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择二级类目");
            positionObj = positionObj.replace("-", "");
            if (!StringUtil.isNumeric(positionObj))
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT);
            builder.eq(MktAdvert.F.positionObj, positionObj)
                .eq(MktAdvert.F.type, AdvertType.OWN)
                .eq(MktAdvert.F.farmer, farmerPkey);
        }
        else if (position == AdvertPosition.ADVERT_POSITION_MSD_GOODS_MAIN)
        {
            if (positionObj == null)
                throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "请选择二级类目");
            positionObj = positionObj.replace("-", "");
            if (!StringUtil.isNumeric(positionObj))
                throw TofocusException.of(WsaleErrCode.GOODSMAIN_CORRECT);
            builder.eq(MktAdvert.F.positionObj, positionObj)
                .eq(MktAdvert.F.type, AdvertType.OWN)
                .eq(MktAdvert.F.farmer, Constant.Operation + ascription);
        }
        else
        {
            if (value)
            {
                if (!(Constant.Operation + ascription).equals(farmerPkey))
                {
                    List<MktSpecialAdvert> exec =
                        mktSpecialAdvertDao.select().eq("position", position).eq("farmer", farmerPkey).exec();
                    List<Integer> keys = new ArrayList<>();
                    exec.forEach(e -> keys.add(e.getAdvertKey()));
                    if (keys.isEmpty())
                        return new ArrayList<>();
                    builder.eq("type", AdvertType.SPECIAL_AREA).in("pkey", keys);
                    
                }
                else
                    builder.eq("type", AdvertType.OWN).eq("farmer", farmerPkey);
            }
            else
            {
                builder.eq("type", AdvertType.OWN).eq("farmer", farmerPkey);
            }
        }
        //List<MktAdvert> listResult = builder.exec();
        //     List<AppAdvertOnList> result = BeanUtil.beanListFrom(AppAdvertOnList.class, listResult);
        
        List<AppAdvertOnList> result = builder.sort("sort", false).execDto(AppAdvertOnList.class);
        List<AppAdvertOnList> res = new ArrayList<>();
        for (AppAdvertOnList ad : result)
        {
            if (ad.getTargerKeys() != null && ad.getVisibleRange() != null
                && ad.getVisibleRange().equals(MemberVisibleRange.TAG) && !ad.getTargerKeys().isEmpty())
            {
                List<MktTag> mklist = mktTagDao.get(ad.getTargerKeys());
                
                List<Integer> listTag = mktMemberTagDao.listTag(member, ascription);
                if (!listTag.isEmpty())
                {
                    mklist = mklist.stream().filter(m -> listTag.contains(m.getPkey())).collect(Collectors.toList());
                    if (!mklist.isEmpty())
                    {
                        List<String> liststr = mklist.stream().map(MktTag::getName).collect(Collectors.toList());
                        ad.setVisibleRangeName(String.join(",", liststr));
                        res.add(ad);
                        
                    }
                    
                }
            }
            else
                res.add(ad);
            
        }
        
        setGoodsName(res);
        setJump(res);
        return res;
    }
    
    private void setJump(List<AppAdvertOnList> list)
    {
        Map<String, Long> gtypeMap = goodsDao.aggregation()
            .eq("farmer", MobileSession.farmerPkey())
            .eq("enabled", true)
            .eq("idDel", false)
            .eq("mType", MType.MARKET_GOODS)
            .execGroupByCount("gtype", "pkey");
        
        Map<String, Long> gmMap = goodsDao.aggregation()
            .eq("farmer", MobileSession.farmerPkey())
            .eq("enabled", true)
            .eq("idDel", false)
            .eq("mType", MType.MARKET_GOODS)
            .execGroupByCount("goodsMain", "pkey");
        
        for (AppAdvertOnList aa : list)
        {
            if (aa.getUrlType().equals(LinkType.GTYPE))
            {
                String objKey = aa.getObjKey();
                if (objKey.contains(","))
                {
                    String mainKey = objKey.split(",")[1];
                    if (!gmMap.containsKey(mainKey))
                        aa.setJump(false);
                }
                else
                {
                    if (!gtypeMap.containsKey(objKey))
                        aa.setJump(false);
                }
            }
        }
        
    }
    
    // 将链接是商品的 objKey的pkey 改成name 返回给前段
    private void setGoodsName(List<AppAdvertOnList> list)
    {
        for (AppAdvertOnList ad : list)
        {
            if (ad.getUrlType().getIndex() == 4 && StringUtils.isNotBlank(ad.getObjKey()))
            {
                MktGoodsMain goodsMain = goodsMainDao.get(Integer.valueOf(ad.getObjKey()));
                if (goodsMain != null)
                    ad.setGoodsName(goodsMain.getName());
            }
            else
                ad.setGoodsName("");
            
            if (ad.getUrlType().equals(LinkType.GTYPE))
            {
                String objKey = ad.getObjKey();
                if (objKey.contains(","))
                {
                    String[] split = objKey.split(",");
                    MktGoodsMain goodsMain = goodsMainDao.get(Integer.valueOf(split[1]));
                    if (goodsMain != null)
                        ad.setObjKeyName(goodsMain.getName());
                }
                else
                {
                    MktGtype gtype = gtypeDao.get(Integer.valueOf(objKey));
                    if (gtype != null)
                        ad.setObjKeyName(gtype.getName());
                }
            }
        }
    }
    
    public List<MktIndexAdvertOnList> listIndexAdvert()
    {
        Integer memberPkey = MobileSession.memberPkey();
        List<IndexAdvertSubject> subjectList = getSubjectList(memberPkey);
        Integer ascription = MobileSession.appid();
        List<MktIndexAdvert> list = indexAdvertDao.listIndexAdvert(subjectList, MobileSession.farmerPkey(), ascription);
        if (list.isEmpty())
            list = indexAdvertDao.listIndexAdvert(subjectList, null, ascription);
        return BeanUtil.beanListFrom(MktIndexAdvertOnList.class, list);
    }
    
    public List<MktIndexAdvertOnList> listIndexAdvertV2()
    {
        Integer memberPkey = MobileSession.memberPkey();
        List<Integer> keys = memberIndexAdvertDao.listIndexAdver(memberPkey);
        Integer ascription = MobileSession.appid();
        List<MktIndexAdvert> list = indexAdvertDao.listIndexAdvertMember(keys, MobileSession.farmerPkey(), ascription);
        return BeanUtil.beanListFrom(MktIndexAdvertOnList.class, list);
    }
    
    // 判断会员符合多少个弹窗广告
    private List<IndexAdvertSubject> getSubjectList(Integer member)
    {
        log.info("member: {}", member);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -90);
        Date date = calendar.getTime();
        
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DATE, -180);
        Date date2 = calendar.getTime();
        
        List<IndexAdvertSubject> subject = new ArrayList<>();
        int status = 0;
        Integer ascription = MobileSession.appid();
        while (status <= 7)
        {
            List<MktMember> exec = new ArrayList<>();
            switch (status)
            {
                case 0:
                    exec = memberDao.select()
                        .eq("ascription", ascription)
                        .eq("enabled", true)
                        .sort("createdTime", true)
                        .exec();
                    break;
                case 1:
                    exec = memberDao.select()
                        .eq("ascription", ascription)
                        .eq("enabled", true)
                        .eq("pkey", member)
                        .eq("level", LevelType.PAID_MEMBER)
                        .sort("createdTime", true)
                        .exec();
                    break;
                case 2:
                    exec = memberDao.select()
                        .eq("enabled", true)
                        .eq("ascription", ascription)
                        .eq("pkey", member)
                        .eq("level", LevelType.ORDINARY_MEMBER)
                        .sort("createdTime", true)
                        .exec();
                    break;
                case 3:
                    exec = memberDao.select()
                        .eq("ascription", ascription)
                        .eq("pkey", member)
                        .eq("enabled", true)
                        .ge("loginTime", date)
                        .sort("createdTime", true)
                        .exec();
                    break;
                case 4:
                    exec = memberDao.select()
                        .eq("ascription", ascription)
                        .eq("pkey", member)
                        .eq("enabled", true)
                        .le("loginTime", date)
                        .sort("createdTime", true)
                        .exec();
                    break;
                case 5:
                    exec = memberDao.select()
                        .eq("pkey", member)
                        .eq("ascription", ascription)
                        .eq("enabled", true)
                        .ge("createdTime", date)
                        .sort("createdTime", true)
                        .exec();
                    break;
                case 6:
                    exec = memberDao.select()
                        .eq("enabled", true)
                        .eq("ascription", ascription)
                        .eq("pkey", member)
                        .le("createdTime", date2)
                        .sort("createdTime", true)
                        .exec();
                    break;
                case 7:
                    exec = memberDao.getNotOrder(member, ascription);
                    break;
                default:
                    break;
            }
            if (!exec.isEmpty())
                subject.add(IndexAdvertSubject.fromIndex(status));
            status += 1;
        }
        return subject;
    }
    
    public Boolean notDisplayIndexAdvert(Integer pkey)
    {
        MktMemberIndexAdvert mia = new MktMemberIndexAdvert();
        mia.setMember(MobileSession.memberPkey());
        mia.setIndexAdvert(pkey);
        mia.setPkey(MobileSession.memberPkey() + "_" + pkey);
        memberIndexAdvertDao.add(mia);
        return true;
    }
    
    public List<MktFunMenuConfigOnList> listFunMenuConfig(String farmer, Integer member, Integer appid)
    {
        
        List<MktFunMenuConfigOnList> list =
            mktFunMenuConfigDao.listFunMenuConfig(Collections.singletonList(farmer), MktFunMenuConfigOnList.class);
        
        list = procesUrlType(list, member, appid);
        return list;
    }
    
    // 
    private List<MktFunMenuConfigOnList> procesUrlType(List<MktFunMenuConfigOnList> list, Integer member, Integer appid)
    {
        List<MktFunMenuConfigOnList> memberList = new ArrayList<>();
        for (MktFunMenuConfigOnList ad : list)
        {
            
            switch (ad.getUrlType())
            {
                case LINK:
                {
                    String objKey = ad.getObjKey();
                    String value = ad.getUrlType().getValue();
                    if (StringUtils.isNotBlank(objKey) && objKey.contains(value))
                        objKey = objKey.replace(value, "");
                    ad.setObjKey(objKey);
                    break;
                }
                case GOODS:
                {
                    if (StringUtils.isNotBlank(ad.getObjKey()))
                    {
                        MktGoods goods = goodsDao.get(Integer.valueOf(ad.getObjKey()));
                        if (goods != null)
                            ad.setGoodsName(goods.getTitle());
                    }
                    break;
                }
                case GTYPE:
                {
                    String objKey = ad.getObjKey();
                    if (StringUtils.isNotBlank(objKey))
                    {
                        if (objKey.contains(","))
                        {
                            String[] split = objKey.split(",");
                            MktGoodsMain goodsMain = goodsMainDao.get(Integer.valueOf(split[1]));
                            if (goodsMain != null)
                                ad.setObjKeyName(goodsMain.getName());
                        }
                        else
                        {
                            MktGtype gtype = gtypeDao.get(Integer.valueOf(objKey));
                            if (gtype != null)
                                ad.setObjKeyName(gtype.getName());
                        }
                    }
                    break;
                }
                case ACTIVITY:
                {
                    if (StringUtil.isNotBlank(ad.getObjKey()))
                    {
                        MktActivity activity = activityDao.get(Integer.valueOf(ad.getObjKey()));
                        if (activity != null)
                            ad.setActivityName(activity.getName());
                    }
                    break;
                }
                case VENDOR:
                {
                    if (StringUtil.isNotBlank(ad.getObjKey()))
                    {
                        
                        MktVendor vendor = vendorDao.get(Integer.valueOf(ad.getObjKey()));
                        if (vendor != null)
                        {
                            ad.setObjKeyName(vendor.getDisplayName());
                            
                        }
                    }
                }
                    break;
                default:
                
            }
            
            if (ad.getVisibleRange().equals(MemberVisibleRange.TAG) && !ad.getTargerKeys().isEmpty())
            {
                
                List<MktTag> mklist = mktTagDao.get(ad.getTargerKeys());
                
                List<Integer> listTag = mktMemberTagDao.listTag(member, appid);
                if (!listTag.isEmpty())
                { //log.info("listTag: {}", listTag);
                    mklist = mklist.stream().filter(m -> listTag.contains(m.getPkey())).collect(Collectors.toList());
                    //   log.info("mklist: {}", mklist);
                    if (!mklist.isEmpty())
                    {
                        List<String> liststr = mklist.stream().map(MktTag::getName).collect(Collectors.toList());
                        ad.setVisibleRangeName(String.join(",", liststr));
                        memberList.add(ad);
                        
                    }
                    
                }
                
            }
            else
                memberList.add(ad);
            
        }
        // log.info("memberList: {}", memberList);
        return memberList;
    }
    
}
