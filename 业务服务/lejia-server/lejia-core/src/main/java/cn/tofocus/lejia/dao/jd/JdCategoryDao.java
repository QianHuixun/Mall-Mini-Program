package cn.tofocus.lejia.dao.jd;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryDrop;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryRelOnList;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryThreeDrop;
import cn.tofocus.lejia.bean.entity.jd.JdCategory;
import cn.tofocus.lejia.bean.entity.jd.JdCategory.F;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdCategoryDao extends JpaSpecificationDelegate<Long, JdCategory>
{
    public Map<Long, String> allMap()
    {
        Map<Long, String> map = new HashMap<>();
        List<JdCategory> list = this.findAll();
        list.forEach(e -> map.put(e.getPkey(), e.getCategoryName()));
        return map;
    }
    
    public List<JdCategoryDrop> listDrop()
    {
        return this.select().eq(F.categoryLevel, 0).sort(F.orderSort, false).execDto(JdCategoryDrop.class);
    }
    
    public List<JdCategoryThreeDrop> listThreeDrop(Long parentId, Integer level)
    {
        return this.select()
            .eq(F.parentId, parentId)
            .eq(F.categoryLevel, level)
            .sort(F.orderSort, false)
            .execDto(JdCategoryThreeDrop.class);
    }
    
    /**
     * 查询最多到指定层级的全部分类，供构建多级下拉树使用。
     */
    public List<JdCategory> listLtLevel(Integer level)
    {
        return this.select().lt(F.categoryLevel, level).sort(F.orderSort, false).exec();
    }
    
    public JdCategory getByPkey(Long pkey)
    {
        return this.selectOne().eq(F.pkey, pkey).exec();
    }
    
    /**
     * 查询指定父级分类下的某级分类pkey集合。
     *
     * @param parentIds 父分类pkey集合，为空不限制父级
     * @param level     分类级别（0=1级，1=2级，2=3级）
     */
    public List<Long> listPkeys(Collection<Long> parentIds, Integer level)
    {
        return this.select().in(F.parentId, parentIds).eq(F.categoryLevel, level).execDto("pkey", Long.class);
    }
    
    /**
     * 分页查询京东分类关联：列出 jd_category 第二级有效分类（categoryLevel=1、needShow=1），按 pkey 倒序，
     * 父级分类名称由 DTO 的 @JoinProperty 自动带出。
     *
     * @param page           页码
     * @param pagesize       每页大小
     * @param jdPkeys        京东2级分类pkey集合，为空不限制
     * @param mallCategories 关联的商城二级分类pkey集合（mkt_goods_main.pkey），为空不限制
     */
    public PageResult<JdCategoryRelOnList> queryValidTwo(Integer page, Integer pagesize, Collection<Long> jdPkeys,
        Collection<Integer> mallCategories)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.categoryLevel, 1)
            .eq(F.needShow, 1)
            .in(F.pkey, jdPkeys)
            .in(F.mallCategory, mallCategories)
            .sort(F.pkey, true)
            .execDto(JdCategoryRelOnList.class);
    }
}
