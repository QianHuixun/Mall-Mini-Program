package cn.tofocus.lejia.domain.market.goods;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.MktWareLineInsDTO;
import cn.tofocus.lejia.bean.dto.market.MktWareLineOnList;
import cn.tofocus.lejia.bean.dto.market.WareAggreDTO;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.bean.entity.goods.MktSpaceKc;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktWareLine;
import cn.tofocus.lejia.bean.enums.WareType;
import cn.tofocus.lejia.cache.SpaceKcCache;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.dao.goods.MktSpaceKcDao;
import cn.tofocus.lejia.dao.market.MktOrderDao;
import cn.tofocus.lejia.dao.market.MktWareLineDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class WareManager
{
    @Autowired
    private MktWareLineDao dao;
    
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktOrderDao orderDao;
    
    @Autowired
    private SpaceKcCache spaceKcCache;
    
    @Autowired
    private MktSpaceKcDao spaceKcDao;
    
    @Transactional
    public Integer insWare(MktWareLineInsDTO entity)
    {
        MktWareLine add = new MktWareLine();
        BeanUtils.copyProperties(entity, add, "goodsName", "spaceName");
        MktGoods goods = goodsDao.get(entity.getGoods());
        MktGoodsSpace space = goodsSpaceDao.get(entity.getSpace());
        if (goods == null || space == null) throw TofocusException.of(WsaleErrCode.NOT_GOODS);
        add.setGoodsName(goods.getTitle());
        add.setSpaceName(space.getSpace());
        if (entity.getWareType().getIndex() == 1) add.setNum(entity.getNum() - space.getKcNum());
        add.setActualNum(space.getKcNum() + add.getNum());
        add.setAscription(CurrentSession.ascriptionPkey());
        MktWareLine wareLine = dao.add(add);
        // 修改规格表库存
        if(wareLine.getActualNum() < 0)
            throw TofocusException.of(LejiaErrCode.KC_ZERO_ERROR);
        space.setKcNum(wareLine.getActualNum());
        goodsSpaceDao.update(space);
        MktSpaceKc kc = BeanUtil.beanFrom(MktSpaceKc.class, space);
        spaceKcDao.put(kc);
        spaceKcCache.set(String.valueOf(kc.getPkey()), Long.valueOf(kc.getKcNum()));
        return wareLine.getPkey();
    }
    
    public PageResult<MktWareLineOnList> queryWare(int page, int pagesize, int goodsPkey, WareType type)
    {
        PageResult<MktWareLine> pageResult = dao.queryWare(page, pagesize, type, goodsPkey);
        PageResult<MktWareLineOnList> result = BeanUtil.beanPageFrom(MktWareLineOnList.class, pageResult);
        for (MktWareLineOnList dto : result.getContent())
            dto.setWareTypeName(dto.getWareType().getName());
        return result;
    }
    
    public List<WareAggreDTO> queryWareSum(int goodsPkey)
    {
        List<WareAggreDTO> result = new ArrayList<>();
        WareAggreDTO dto1 = new WareAggreDTO();
        dto1.setNum(0);
        dto1.setWareType(WareType.WAREHOUSING);
        dto1.setTypeName(WareType.WAREHOUSING.getName());
        
        WareAggreDTO dto2 = new WareAggreDTO();
        dto2.setNum(0);
        dto2.setWareType(WareType.SALES);
        dto2.setTypeName(WareType.SALES.getName());
        
        result.add(dto1);
        result.add(dto2);
        
        List<WareAggreDTO> list = dao.listWareAggre(goodsPkey);
        for (WareAggreDTO dto : result)
        {
            for (WareAggreDTO wa : list)
            {
                if (dto.getWareType().getIndex() == wa.getWareType().getIndex()) dto.setNum(wa.getNum());
            }
        }
        
        return result;
    }
    
    // 销售时,新增一条库存记录
    public void insWare(int spacePkey, int num, int orderPkey)
    {
        log.info("销售新增库存记录   spacePkey: {}, num: {}", spacePkey, num);
        MktOrder order = orderDao.get(orderPkey);
        MktGoodsSpace space = goodsSpaceDao.get(spacePkey);
        MktGoods goods = goodsDao.get(space.getGoods());
        MktWareLine add = new MktWareLine();
        add.setWareType(WareType.SALES);
        add.setGoods(goods.getPkey());
        add.setGoodsName(goods.getTitle());
        add.setSpace(space.getPkey());
        add.setSpaceName(space.getSpace());
        add.setRemark(order.getCode());
        add.setPrice(space.getPrice());
        add.setNum(-num);
        add.setActualNum(space.getKcNum());
        add.setAscription(MobileSession.appid());
        dao.add(add);
    }
    
    // 规格过期,新增一条库存记录
    public void insWare(int spacePkey, int num)
    {
        log.info("销售新增库存记录   spacePkey: {}, num: {}", spacePkey, num);
        MktGoodsSpace space = goodsSpaceDao.get(spacePkey);
        MktGoods goods = goodsDao.get(space.getGoods());
        MktWareLine add = new MktWareLine();
        add.setWareType(WareType.EXPIRE);
        add.setGoods(goods.getPkey());
        add.setGoodsName(goods.getTitle());
        add.setSpace(space.getPkey());
        add.setSpaceName(space.getSpace());
        add.setRemark("过期");
        add.setPrice(space.getPrice());
        add.setNum(-num);
        add.setActualNum(space.getKcNum());
        add.setAscription(MobileSession.appid());
        dao.add(add);
    }
    
    // 新增商品时 新增库存记录
    public void insWare(MktGoods goods, List<MktGoodsSpace> spaces)
    {
        List<MktWareLine> addAll = new ArrayList<>();
        for (MktGoodsSpace gs : spaces)
        {
            MktWareLine add = new MktWareLine();
            add.setWareType(WareType.WAREHOUSING);
            add.setGoods(goods.getPkey());
            add.setGoodsName(goods.getTitle());
            add.setSpace(gs.getPkey());
            add.setSpaceName(gs.getSpace());
            add.setNum(gs.getKcNum());
            add.setActualNum(gs.getKcNum());
            add.setAscription(CurrentSession.ascriptionPkey());
            addAll.add(add);
        }
        dao.addAll(addAll);
    }
    
    // 删除规格 将按盘点的方式 去掉库存
    public void delWare(MktGoods goods, List<MktGoodsSpace> spaces)
    {
        List<MktWareLine> addAll = new ArrayList<>();
        for (MktGoodsSpace gs : spaces)
        {
            MktWareLine add = new MktWareLine();
            add.setWareType(WareType.INVENTORY);
            add.setGoods(goods.getPkey());
            add.setGoodsName(goods.getTitle());
            add.setSpace(gs.getPkey());
            add.setSpaceName(gs.getSpace());
            add.setNum(-gs.getKcNum());
            add.setActualNum(0);
            add.setAscription(CurrentSession.ascriptionPkey());
            addAll.add(add);
        }
        dao.addAll(addAll);
    }
}
