package cn.tofocus.lejia.domain.market.goods;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.gtype.GoodsMainInfo;
import cn.tofocus.lejia.bean.dto.gtype.GtypeInfo;
import cn.tofocus.lejia.bean.dto.market.MktGoodsMainOnList;
import cn.tofocus.lejia.bean.dto.market.MktGoodsMainThreeOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMainThree;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.goods.MktGoodsMainThreeDao;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.domain.GoodListQueryer;
import cn.tofocus.lejia.exception.WsaleErrCode;

@Component
public class GoodsMainManager
{
    @Autowired
    private MktGoodsMainDao goodsMainDao;
    
    @Autowired
    private MktGtypeDao gtypeDao;
    
    @Autowired
    private MktGoodsMainThreeDao goodsMainThreeDao;
    
    @Autowired
    private GoodListQueryer goodListQueryer;
    
    public Integer insGoodsMain(MktGoodsMainOnList entity)
    {
        MktGoodsMain goodsMain = BeanUtil.beanFrom(MktGoodsMain.class, entity);
        goodsMain.setRowVension(1);
        goodsMain.setIdDel(false);
        goodsMain.setFarmer(CurrentSession.marketPkey());
        goodsMain.setAscription(CurrentSession.ascriptionPkey());
        if (entity.getEnabled() == null) goodsMain.setEnabled(true);
        if (entity.getSort() == null) goodsMain.setSort(0);
        MktGoodsMain add = null;
        MktGoodsMain exec = goodsMainDao.selectOne()
            .eq("name", entity.getName())
            .eq("farmer", CurrentSession.marketPkey())
            .eq("ascription", CurrentSession.ascriptionPkey())
            .exec();
        if (exec == null)
            add = goodsMainDao.add(goodsMain);
        else
        {
            if (Boolean.TRUE.equals(exec.getIdDel()))
            {
                goodsMain.setPkey(exec.getPkey());
                add = goodsMainDao.update(goodsMain);
            }
            else
                throw TofocusException.of(WsaleErrCode.COMMODITY_LIBRARY_ALREADY_EXISTS);
        }
        return add.getPkey();
    }
    
    public PageResult<MktGoodsMainOnList> queryGoodsMain(Integer page, Integer pagesize, Integer gtype, String name,
        Boolean enabled)
    {
        PageResult<MktGoodsMain> pageResult =
            goodsMainDao.queryGoodsMain(page, pagesize, gtype, name, enabled, CurrentSession.ascriptionPkey());
        PageResult<MktGoodsMainOnList> result = BeanUtil.beanPageFrom(MktGoodsMainOnList.class, pageResult);
        getGtypeName(result.getContent());
        return result;
    }
    
    // 根据gtype获取其名称
    private void getGtypeName(List<MktGoodsMainOnList> list)
    {
        List<Integer> gtypePkeyList = new ArrayList<>();
        Iterator<MktGoodsMainOnList> iterator = list.iterator();
        while (iterator.hasNext())
        {
            gtypePkeyList.add(iterator.next().getGtype());
        }
        if (gtypePkeyList.isEmpty()) return;
        List<MktGtype> exec = gtypeDao.select()
            .in("pkey", gtypePkeyList.toArray())
            .eq("ascription", CurrentSession.ascriptionPkey())
            .eq("idDel", false)
            .exec();
        for (MktGoodsMainOnList bean : list)
        {
            for (MktGtype mg : exec)
            {
                if (bean.getGtype().equals(mg.getPkey())) bean.setGtypeName(mg.getName());
            }
        }
    }
    
    public MktGoodsMainOnList updGoodsMain(Integer pkey, String name, Integer sort, Integer gtype, String remark)
    {
        MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(pkey);
        if (goodsMain == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (StringUtils.isNotBlank(name))
        {
            MktGoodsMain exec = goodsMainDao.selectOne()
                .eq("name", name)
                .eq("farmer", CurrentSession.marketPkey())
                .notEq("pkey", pkey)
                .eq("ascription", CurrentSession.ascriptionPkey())
                .exec();
            if (exec != null) throw TofocusException.of(WsaleErrCode.GOODS_MAIN_NAME_REPEAT);
            goodsMain.setName(name);
        }
        if (StringUtils.isNotBlank(remark)) goodsMain.setRemark(remark);
        if (sort != null) goodsMain.setSort(sort);
        if (gtype != null) goodsMain.setGtype(gtype);
        MktGoodsMain update = goodsMainDao.update(goodsMain);
        //        appGoodsV4Manager.openThread(null, CurrentSession.ascriptionPkey());
        return BeanUtil.beanFrom(MktGoodsMainOnList.class, update);
    }
    
    public Boolean delGoodsMain(Integer pkey)
    {
        MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(pkey);
        if (goodsMain.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        goodsMain.setIdDel(true);
        goodsMainDao.update(goodsMain);
        goodListQueryer.delGoodsMain(pkey);
        
        List<MktGoodsMainThree> gmtList =
            goodsMainThreeDao.listGeSort(null, null, pkey, goodsMain.getFarmer(), goodsMain.getAscription());
        for (MktGoodsMainThree gmt : gmtList)
        {
            gmt.setEnabled(false);
            gmt.setIdDel(true);
        }
        goodsMainThreeDao.updateAll(gmtList);
        return true;
    }
    
    public Boolean enabledGoodsMain(Integer pkey, Boolean flag)
    {
        MktGoodsMain goodsMain = goodsMainDao.getGoodsMain(pkey);
        goodsMain.setEnabled(flag);
        MktGoodsMain update = goodsMainDao.update(goodsMain);
        //        appGoodsV4Manager.openThread(null, CurrentSession.ascriptionPkey());
        if (update.getEnabled() == flag) return true;
        goodListQueryer.switchGoodsMain(pkey, flag);
        return false;
    }
    
    public List<PkeyNameDTO> listDrop(Boolean flag)
    {
        Integer gtypeKey = null;
        if (flag)
        {
            MktGtype gtype = gtypeDao.getGiftGtype(CurrentSession.ascriptionPkey());
            gtypeKey = gtype.getPkey();
        }
        else
        {
            MktGtype gtype = gtypeDao.getCouponGtype(CurrentSession.ascriptionPkey());
            gtypeKey = gtype.getPkey();
        }
        return goodsMainDao.listDto(gtypeKey, CurrentSession.ascriptionPkey(), PkeyNameDTO.class);
    }
    
    public Integer insGoodsMainThree(MktGoodsMainThreeOnList entity)
    {
        MktGoodsMainThree goodsMain = BeanUtil.beanFrom(MktGoodsMainThree.class, entity);
        goodsMain.setRowVension(1);
        goodsMain.setIdDel(false);
        goodsMain.setFarmer(CurrentSession.marketPkey());
        goodsMain.setAscription(CurrentSession.ascriptionPkey());
        if (entity.getEnabled() == null) goodsMain.setEnabled(true);
        if (entity.getSort() == null) goodsMain.setSort(0);
        if (entity.getTwoGtype() != null)
        {
            MktGoodsMain mktGoodsMain = goodsMainDao.get(entity.getTwoGtype());
            goodsMain.setGtype(mktGoodsMain.getGtype());
        }
        MktGoodsMainThree add = null;
        MktGoodsMainThree exec = goodsMainThreeDao.selectOne()
            .eq("name", entity.getName())
            .eq("farmer", CurrentSession.marketPkey())
            .eq("ascription", CurrentSession.ascriptionPkey())
            .exec();
        if (exec == null)
            add = goodsMainThreeDao.add(goodsMain);
        else
        {
            if (Boolean.TRUE.equals(exec.getIdDel()))
            {
                goodsMain.setPkey(exec.getPkey());
                add = goodsMainThreeDao.update(goodsMain);
            }
            else
                throw TofocusException.of(WsaleErrCode.COMMODITY_LIBRARY_ALREADY_EXISTS);
        }
        return add.getPkey();
    }
    
    public PageResult<MktGoodsMainThreeOnList> queryGoodsMainThree(Integer page, Integer pagesize, Integer twoGtype,
        String name, Boolean enabled)
    {
        PageResult<MktGoodsMainThree> pageResult =
            goodsMainThreeDao.queryGoodsMain(page, pagesize, twoGtype, name, enabled, CurrentSession.ascriptionPkey());
        PageResult<MktGoodsMainThreeOnList> result = BeanUtil.beanPageFrom(MktGoodsMainThreeOnList.class, pageResult);
        
        for (MktGoodsMainThreeOnList t : result.getContent())
        {
            MktGoodsMain mktGoodsMain = goodsMainDao.get(t.getTwoGtype());
            if (mktGoodsMain != null)
            {
                t.setTwoGtypeName(mktGoodsMain.getName());
            }
            MktGtype mktGtype = gtypeDao.get(t.getGtype());
            if (mktGtype != null) t.setGtypeName(mktGtype.getName());
        }
        return result;
    }
    
    public MktGoodsMainThreeOnList updGoodsMainThree(Integer pkey, String name, Integer sort, Integer twoGtype,
        String remark)
    {
        MktGoodsMainThree goodsMain = goodsMainThreeDao.getGoodsMain(pkey);
        if (goodsMain == null) throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
        if (StringUtils.isNotBlank(name))
        {
            MktGoodsMainThree exec = goodsMainThreeDao.selectOne()
                .eq("name", name)
                .eq("farmer", CurrentSession.marketPkey())
                .notEq("pkey", pkey)
                .eq("ascription", CurrentSession.ascriptionPkey())
                .exec();
            if (exec != null) throw TofocusException.of(WsaleErrCode.GOODS_MAIN_NAME_REPEAT);
            goodsMain.setName(name);
        }
        if (StringUtils.isNotBlank(remark)) goodsMain.setRemark(remark);
        if (sort != null) goodsMain.setSort(sort);
        if (twoGtype != null)
        {
            goodsMain.setTwoGtype(twoGtype);
            MktGoodsMain mktGoodsMain = goodsMainDao.get(twoGtype);
            if (mktGoodsMain != null)
            {
                goodsMain.setGtype(mktGoodsMain.getGtype());
                goodsMain.setGtypeName(mktGoodsMain.getGtypeName());
            }
        }
        
        MktGoodsMainThree update = goodsMainThreeDao.update(goodsMain);
        
        // 异步修改 缓存队列
        //        appGoodsV4Manager.openThread(null, CurrentSession.ascriptionPkey());
        return BeanUtil.beanFrom(MktGoodsMainThreeOnList.class, update);
    }
    
    public Boolean delGoodsMainThree(Integer pkey)
    {
        MktGoodsMainThree goodsMain = goodsMainThreeDao.getGoodsMain(pkey);
        if (goodsMain.getEnabled()) throw TofocusException.of(WsaleErrCode.NOT_DELETED);
        goodsMain.setIdDel(true);
        goodsMainThreeDao.update(goodsMain);
        goodListQueryer.delThreeGtype(pkey);
        return true;
    }
    
    public Boolean enabledGoodsMainThree(Integer pkey, Boolean flag)
    {
        MktGoodsMainThree goodsMain = goodsMainThreeDao.getGoodsMain(pkey);
        goodsMain.setEnabled(flag);
        MktGoodsMainThree update = goodsMainThreeDao.update(goodsMain);
        //        appGoodsV4Manager.openThread(null, CurrentSession.ascriptionPkey());
        goodListQueryer.switchThreeGtype(pkey, flag);
        if (update.getEnabled() == flag) return true;
        return false;
    }
    
    public List<GtypeInfo> listSys()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktGtype> quaryAppGtype = gtypeDao.quaryAppGtype(Constant.Operation + ascription, ascription);
        List<GtypeInfo> list = BeanUtil.beanListFrom(GtypeInfo.class, quaryAppGtype);
        for(GtypeInfo g : list)
        {
            List<GoodsMainInfo> gmList = new ArrayList<>();
            List<MktGoodsMain> glist = goodsMainDao.listSortFalse(g.getPkey(), true, Constant.Operation + ascription, ascription);
            gmList = BeanUtil.beanListFrom(GoodsMainInfo.class, glist);
            g.setGmList(gmList);
        }
        return list;
    }
    
}
