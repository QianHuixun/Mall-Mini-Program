package cn.tofocus.lejia.dao.jd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdGoods.F;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdGoodsDao extends JpaSpecificationDelegate<Long, JdGoods>
{
    public List<Long> byNotGoods(List<Long> skus)
    {
        List<JdGoods> list = this.select().in(F.pkey, skus).exec();
        List<Long> keyList = CollectionUtil.keyList(list);
        return skus.stream()
            .filter(e -> !keyList.contains(e))
            .collect(Collectors.toList());
    }

    public List<JdGoods> byNotGoodsIdDel(List<Long> skus)
    {
        return this.select().in(F.pkey, skus).eq(F.idDel, true).exec();
    }
    
    public JdGoods bySpuId(Long spuId)
    {
        return this.selectOne().eq(F.spuId, spuId).eq(F.enabled, true).eq(F.idDel, false).exec();
    }
    
    public JdGoods bySkuId(Long skuId)
    {
        return this.selectOne().eq(F.pkey, skuId).eq(F.enabled, true).eq(F.idDel, false).exec();
    }

    public List<JdGoods> listBySpuId(Long spuId)
    {
        return this.select().eq(F.spuId, spuId).eq(F.idDel, false).exec();
    }
    
    public List<JdGoods> bySpuId(List<Long> spuIds)
    {
        return this.select().in(F.spuId, spuIds).eq(F.idDel, false).exec();
    }

    public List<JdGoods> byPkey(List<Long> pkeys)
    {
        return this.select().in(F.pkey, pkeys).exec();
    }

    /**
     * 按 pkey 批量精简投影：只查传入 DTO 的字段（避开 JdGoods 的 text 长文本列等不需要的列）。
     * 供 msd/search、mall/query 窗口（≤ pagesize）批量回填，clazz 决定投影列与返回类型，便于复用。
     */
    public <T> List<T> listBackfill(List<Long> pkeys, Class<T> clazz)
    {
        return this.select().in(F.pkey, pkeys).execDto(clazz);
    }

    public void delByBizPoolId(String bizPoolId)
    {
        this.select().strict(true).eq(F.bizPoolId, bizPoolId).update(F.idDel, true);
    }
    
    public JdGoods bySpuIdTitle(Long spuId, String title)
    {
        return this.selectOne()
            .eq(F.spuId, spuId)
            .like(F.title, title)
            .eq(F.enabled, true)
            .eq(F.idDel, false).exec();
    }
    
    public Map<Long,JdGoods> mapJdGoods(List<Long> pkeys)
    {
        List<JdGoods> list = this.select().in(F.pkey, pkeys).exec();
        Map<Long,JdGoods> map = new HashMap<>();
        list.forEach(e -> map.put(e.getPkey(), e));
        return map;
    }
    
    public List<JdGoods> notIdDel()
    {
        return this.select().eq(F.idDel, false).exec();
    }
    
    public List<JdGoods> salePriceIsNull()
    {
        return this.select().isNull(F.salePrice).eq(F.idDel, false).exec();
    }
    
    public void increaseXsNum(Long pkey, Integer num)
    {
        Integer xsNum = null;
        List<Integer> list = this.select().eq(F.pkey, pkey).execDto(F.xsNum, Integer.class);
        if (CollectionUtil.isNotEmpty(list))
            xsNum = list.get(0);
        if (xsNum == null)
            xsNum = 0;
        this.select().strict(true).eq(F.pkey, pkey).update(F.xsNum, xsNum + num);
    }
}
