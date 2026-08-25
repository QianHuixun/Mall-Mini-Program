package cn.tofocus.lejia.app.v1.market;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderCommentForAdd;
import cn.tofocus.lejia.bean.dto.app.market.AppOrderLineCommentDTO;
import cn.tofocus.lejia.domain.app.AppOrderCommentManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/v1/app/market/lm/order/comment")
@RestController
public class AppOrderCommentApiImpl implements AppOrderCommentApi
{
    @Autowired
    private AppOrderCommentManager appOrderCommentManager;
    
    @Override
    public Result<Boolean> add(@Valid AppOrderCommentForAdd dto)
    {
        boolean sign = appOrderCommentManager.add(dto);
        return new Result<>(sign);
    }
    
    @Override
    public Result<List<AppOrderLineCommentDTO>> listByOrder(Integer pkey)
    {
        List<AppOrderLineCommentDTO> res = appOrderCommentManager.listByOrder(pkey);
        return new Result<>(res);
    }
}
