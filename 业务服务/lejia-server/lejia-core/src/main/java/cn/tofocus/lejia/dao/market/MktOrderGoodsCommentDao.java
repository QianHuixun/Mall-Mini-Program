package cn.tofocus.lejia.dao.market;

import java.util.List;

import org.springframework.stereotype.Component;

import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.DataSourceWithFileUrl;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOrderGoodsComment;
import cn.tofocus.lejia.bean.entity.market.MktOrderGoodsComment.F;
import cn.tofocus.lejia.bean.enums.CommentApplyStatus;

@Component
@DataSourceWithFileUrl
public class MktOrderGoodsCommentDao extends JpaSpecificationDelegate<Integer, MktOrderGoodsComment>
{
    public Boolean existByOrder(Integer order)
    {
        return this.selectOne().eq(F.orderPkey, order).exec() != null;
    }
    
    public <T> List<T> listByOrder(Integer order, Class<T> clazz)
    {
        return this.select().eq(F.orderPkey, order).sort(F.pkey, false).execDto(clazz);
    }
    
    public <T> PageResult<T> queryByGoods(int page, int pagesize, Integer goods, Class<T> clazz)
    {
        return this.selectPage()
            .page(page)
            .pagesize(pagesize)
            .eq(F.goods, goods)
            .eq(F.applyStatus, CommentApplyStatus.APPLY)
            .sort(F.createdTime)
            .sort(F.pkey)
            .execDto(clazz);
    }
    
    public void updateApplyStatus(List<Integer> pkeys, Integer ascription, CommentApplyStatus applyStatus)
    {
        this.select().strict(true).eq(F.ascription, ascription).in(F.pkey, pkeys).update(F.applyStatus, applyStatus);
    }
}
