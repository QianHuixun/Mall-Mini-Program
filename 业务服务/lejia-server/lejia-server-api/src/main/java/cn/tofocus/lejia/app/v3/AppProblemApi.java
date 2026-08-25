package cn.tofocus.lejia.app.v3;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.app.AppTags;
import cn.tofocus.lejia.bean.dto.v3.ProblemAppOnList;
import io.swagger.v3.oas.annotations.Operation;

public interface AppProblemApi
{
    @Operation(summary = "获取常见问题", tags = AppTags.mobileProblem)
    @PostMapping(value = "/list")
    public Result<List<ProblemAppOnList>> list();
}
