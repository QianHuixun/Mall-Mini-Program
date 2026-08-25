package cn.tofocus.lejia.domain.market.goods;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.SelectPageBuilder;
import cn.tofocus.lejia.bean.dto.market.MktGoodsSpaceOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsSpace;
import cn.tofocus.lejia.dao.goods.MktGoodsDao;
import cn.tofocus.lejia.dao.goods.MktGoodsSpaceDao;
import cn.tofocus.lejia.repository.market.MktGoodsSpaceRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SpaceManager
{
    @Autowired
    private MktGoodsSpaceDao goodsSpaceDao;
    
    @Autowired
    private MktGoodsDao goodsDao;
    
    @Autowired
    private MktGoodsSpaceRepository goodsSpaceRepository;
    
    public MktGoodsSpaceOnList insSpace(MktGoodsSpaceOnList entity)
    {
        MktGoodsSpace goodsSpace = BeanUtil.beanFrom(MktGoodsSpace.class, entity);
        MktGoodsSpace add = goodsSpaceDao.add(goodsSpace);
        updGoodsPrice(add.getGoods());
        
        return BeanUtil.beanFrom(MktGoodsSpaceOnList.class, add);
    }
    
    public MktGoodsSpaceOnList getSpace(Integer pkey)
    {
        MktGoodsSpace goodsSpace = goodsSpaceDao.get(pkey);
        return BeanUtil.beanFrom(MktGoodsSpaceOnList.class, goodsSpace);
    }
    
    public PageResult<MktGoodsSpaceOnList> queryGtypr(int page, int pagesize)
    {
        SelectPageBuilder<Integer, MktGoodsSpace> builder =
            goodsSpaceDao.selectPage().page(page).pagesize(pagesize).sort("pkey", true);
        PageResult<MktGoodsSpace> pageResult = builder.exec();
        return BeanUtil.beanPageFrom(MktGoodsSpaceOnList.class, pageResult);
    }
    
    public Boolean delSpace(Integer pkey)
    {
        MktGoodsSpace space = goodsSpaceDao.get(pkey);
        boolean result = goodsSpaceDao.removeById(pkey);
        updGoodsPrice(space.getGoods());
        return result;
    }
    
    public MktGoodsSpaceOnList updSpace(Integer pkey, String space, BigDecimal weight, Integer kcNum, Integer xsNum,
        BigDecimal price, BigDecimal priceOld, Integer point, BigDecimal comm)
    {
        MktGoodsSpace goodsSpace = goodsSpaceDao.get(pkey);
        if (StringUtils.isNotBlank(space)) goodsSpace.setSpace(space);
        if (kcNum != null) goodsSpace.setKcNum(kcNum);
        if (xsNum != null) goodsSpace.setXsNum(xsNum);
        if (price != null) goodsSpace.setPrice(price);
        if (priceOld != null) goodsSpace.setPriceOld(priceOld);
        if (weight != null) goodsSpace.setWeight(weight);
        if (point != null) goodsSpace.setPoint(point);
        if (comm != null) goodsSpace.setComm(comm);
        MktGoodsSpace update = goodsSpaceDao.update(goodsSpace);
        updGoodsPrice(goodsSpace.getGoods());
        return BeanUtil.beanFrom(MktGoodsSpaceOnList.class, update);
    }
    
    public void updGoodsPrice(Integer goodsPkey)
    {
        BigDecimal minPrice = goodsSpaceRepository.minPrice(goodsPkey);
        MktGoods goods = goodsDao.get(goodsPkey);
        
        log.info("minPrice:", minPrice);
        log.info("goods:", goods.getPrice());
        if (goods.getPrice() != null && minPrice != null && goods.getPrice().compareTo(minPrice) == 1)
        {
            goods.setPrice(minPrice);
            goodsDao.update(goods);
        }
    }
    
}
