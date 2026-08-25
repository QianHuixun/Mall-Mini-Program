package cn.tofocus.lejia.domain.jd;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageParameter;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryHasChildrenDrop;
import cn.tofocus.lejia.bean.dto.market.jd.JdCategoryRelOnList;
import cn.tofocus.lejia.bean.entity.goods.MktGoodsMain;
import cn.tofocus.lejia.bean.entity.jd.JdCategory;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.goods.MktGoodsMainDao;
import cn.tofocus.lejia.dao.jd.JdCategoryDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东分类关联查询业务逻辑。
 */
@Slf4j
@Component
public class JdCategoryManager
{
    @Autowired
    private JdCategoryDao jdCategoryDao;

    @Autowired
    private MktGoodsMainDao mktGoodsMainDao;

    /**
     * 分页查询京东分类关联：列出 jd_category 第二级有效分类，按 pkey 倒序，
     * 京东父级分类名称与商城父级分类名称由 DTO 的 @JoinProperty 自动带出。
     *
     * @param page          页码
     * @param pagesize      每页大小
     * @param jdCategory    京东分类pkey，可为1级/2级中的任意一级，按其层级自动展开到2级
     * @param mallGtype     商城1级分类pkey（mkt_gtype），为空不限制
     * @param mallGoodsMain 商城2级分类pkey（mkt_goods_main），为空不限制
     */
    public PageResult<JdCategoryRelOnList> queryCategoryRel(Integer page, Integer pagesize, Long jdCategory,
        Integer mallGtype, Integer mallGoodsMain)
    {
        PageParameter pageParameter = new PageParameter(page, pagesize);

        // 京东分类筛选：传入任意一级，按层级展开为2级pkey集合
        Collection<Long> jdPkeys = resolveJdCategories(jdCategory);
        if (jdCategory != null && jdPkeys != null && jdPkeys.isEmpty())
        {
            return emptyPage(pageParameter);
        }

        // 商城分类筛选：1级/2级参数，展开为商城2级pkey集合
        Collection<Integer> mallCategories = resolveMallCategories(mallGtype, mallGoodsMain);
        if ((mallGtype != null || mallGoodsMain != null) && mallCategories != null && mallCategories.isEmpty())
        {
            return emptyPage(pageParameter);
        }

        return jdCategoryDao.queryValidTwo(page, pagesize, jdPkeys, mallCategories);
    }

    /**
     * 编辑京东分类与商城分类的关联。
     *
     * @param jdTwoPkey   京东二级分类pkey，必须存在且为二级分类（categoryLevel=1）
     * @param mallTwoPkey 商城二级分类pkey（mkt_goods_main），为空表示取消关联（写null）
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updCategoryRel(Long jdTwoPkey, Integer mallTwoPkey)
    {
        if (jdTwoPkey == null)
        {
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "京东分类pkey不能为空");
        }
        // 校验京东分类存在且为二级分类
        JdCategory jdCategory = jdCategoryDao.getByPkey(jdTwoPkey);
        if (jdCategory == null || jdCategory.getNeedShow() != 1 || !Integer.valueOf(1).equals(jdCategory.getCategoryLevel()))
        {
            throw TofocusException.of(LejiaErrCode.JD_CATEGORY_TWO_ERROR);
        }
        // 商城分类非空时校验其存在（getGoodsMain 已过滤 idDel=false）
        if (mallTwoPkey != null && mktGoodsMainDao.getGoodsMain(mallTwoPkey) == null)
        {
            throw TofocusException.of(LejiaErrCode.MALL_CATEGORY_TWO_ERROR);
        }
        jdCategory.setMallCategory(mallTwoPkey);
        jdCategoryDao.update(jdCategory);
        return true;
    }

    /**
     * 查询京东多级分类下拉树。
     *
     * @param levels 需要的层级数：1=仅一级，2=一级+二级，3=一级+二级+三级；为空默认3，越界约束到 [1,3]
     */
    public List<JdCategoryHasChildrenDrop> listMultiDrop(Integer levels)
    {
        if (levels == null)
        {
            levels = 3;
        }
        if (levels < 1)
        {
            levels = 1;
        }
        if (levels > 3)
        {
            levels = 3;
        }

        List<JdCategory> all = jdCategoryDao.listLtLevel(levels);

        // 先按 pkey 建立全部节点，再按 parentId 串成树
        Map<Long, JdCategoryHasChildrenDrop> nodeMap = new LinkedHashMap<>();
        for (JdCategory c : all)
        {
            JdCategoryHasChildrenDrop node = new JdCategoryHasChildrenDrop();
            node.setPkey(c.getPkey());
            node.setCategoryName(c.getCategoryName());
            nodeMap.put(c.getPkey(), node);
        }

        List<JdCategoryHasChildrenDrop> roots = new ArrayList<>();
        for (JdCategory c : all)
        {
            JdCategoryHasChildrenDrop node = nodeMap.get(c.getPkey());
            Long parentId = c.getParentId();
            if (parentId == null || !nodeMap.containsKey(parentId))
            {
                roots.add(node);
            }
            else
            {
                JdCategoryHasChildrenDrop parent = nodeMap.get(parentId);
                if (parent.getChildren() == null)
                {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(node);
            }
        }
        return roots;
    }

    /**
     * 京东分类筛选：传入的pkey可能是1级(categoryLevel=0)/2级(1)，按层级展开为2级pkey集合。
     * 返回 null 表示不限制；返回空集合表示无匹配。
     */
    private Collection<Long> resolveJdCategories(Long jdCategory)
    {
        if (jdCategory == null)
        {
            return null;
        }
        JdCategory cat = jdCategoryDao.getByPkey(jdCategory);
        if (cat == null || cat.getCategoryLevel() == null)
        {
            return Collections.emptySet();
        }
        int level = cat.getCategoryLevel();
        if (level == 1)
        {
            // 本身就是2级
            return Collections.singleton(jdCategory);
        }
        if (level == 0)
        {
            // 1级：取其下2级
            return new HashSet<>(jdCategoryDao.listPkeys(Collections.singleton(jdCategory), 1));
        }
        // 其它（3级）：无对应2级关联
        return Collections.emptySet();
    }

    /**
     * 商城分类筛选：按1级(gtype)/2级(goodsMain)参数展开为商城2级pkey集合。
     * 返回 null 表示不限制；返回空集合表示无匹配。
     */
    private Collection<Integer> resolveMallCategories(Integer mallGtype, Integer mallGoodsMain)
    {
        boolean hasGtype = mallGtype != null;
        boolean hasMain = mallGoodsMain != null;
        if (!hasGtype && !hasMain)
        {
            return null;
        }

        Integer ascription = CurrentSession.ascriptionPkey();
        Set<Integer> mainSet = null;

        // 1级：展开为该级下所有2级
        if (hasGtype)
        {
            mainSet = new HashSet<>();
            List<MktGoodsMain> mains = mktGoodsMainDao.listDto(mallGtype, ascription, MktGoodsMain.class);
            for (MktGoodsMain m : mains)
            {
                mainSet.add(m.getPkey());
            }
        }

        // 2级：直接收窄，与上级结果取交集
        if (hasMain)
        {
            Set<Integer> single = new HashSet<>();
            single.add(mallGoodsMain);
            mainSet = intersect(mainSet, single);
        }
        return mainSet;
    }

    private Set<Integer> intersect(Set<Integer> base, Set<Integer> other)
    {
        if (base == null)
        {
            return other;
        }
        base.retainAll(other);
        return base;
    }

    private PageResult<JdCategoryRelOnList> emptyPage(PageParameter pageParameter)
    {
        return new PageResult<>(new ArrayList<>(), pageParameter, 0);
    }
}
