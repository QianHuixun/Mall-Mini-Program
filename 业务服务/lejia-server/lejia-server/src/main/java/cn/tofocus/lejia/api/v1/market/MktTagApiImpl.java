package cn.tofocus.lejia.api.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.market.DropIntegerDown;
import cn.tofocus.lejia.bean.dto.market.MktTagInfo;
import cn.tofocus.lejia.bean.dto.market.MktTagOnPage;
import cn.tofocus.lejia.bean.enums.member.TagType;
import cn.tofocus.lejia.domain.market.MktTagManager;

@RequestMapping("/v1/market/tag")
@RestController
public class MktTagApiImpl implements MktTagApi
{
    @Autowired
    private MktTagManager tagManager;
    
    @Override
    public Result<PageResult<MktTagOnPage>> query(int page, int pagesize, List<TagType> types, String name,
        String description)
    {
        PageResult<MktTagOnPage> result = tagManager.query(page, pagesize, types, name, description);
        return new Result<>(result);
    }
    
    @Override
    public Result<MktTagInfo> get(Integer pkey)
    {
        MktTagInfo result = tagManager.get(pkey);
        return new Result<>(result);
    }
    
    @Override
    public Result<Boolean> ins(@Valid MktTagInfo info)
    {
        boolean sign = tagManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> upd(@Valid MktTagInfo info)
    {
        boolean sign = tagManager.save(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> del(Integer pkey)
    {
        boolean sign = tagManager.del(pkey);
        return new Result<>(sign);
    }
    
    @Override
    public Result<List<DropIntegerDown>> listDrop(List<TagType> types)
    {
        return new Result<>(tagManager.listDrop(types));
    }
}
