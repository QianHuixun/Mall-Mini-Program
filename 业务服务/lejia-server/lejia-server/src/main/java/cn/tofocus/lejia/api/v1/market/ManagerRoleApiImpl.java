package cn.tofocus.lejia.api.v1.market;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.sys.ManagerOnPage;
import cn.tofocus.lejia.bean.enums.ManagerRole;
import cn.tofocus.lejia.domain.ManagerRoleManager;
import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("/v1/market/manager")
@RestController
public class ManagerRoleApiImpl
{
    @Autowired
    private ManagerRoleManager manager;
    
    @Operation(summary = "新增", tags = ApiTags.MANAGER_ROLE_MANAGER)
    @PostMapping("/ins")
    public Result<Boolean> ins(@RequestBody ManagerOnPage info)
    {
        return new Result<>(manager.ins(info));
    }
    
    @Operation(summary = "编辑", tags = ApiTags.MANAGER_ROLE_MANAGER)
    @PostMapping("/upd")
    public Result<Boolean> upd(@RequestBody ManagerOnPage info)
    {
        return new Result<>(manager.upd(info));
    }
    
    @Operation(summary = "删除", tags = ApiTags.MANAGER_ROLE_MANAGER)
    @PostMapping("/del")
    public Result<Boolean> del(@RequestParam(value = "pkey")Integer pkey)
    {
        return new Result<>(manager.del(pkey));
    }
    
    @Operation(summary = "查询列表", tags = ApiTags.MANAGER_ROLE_MANAGER)
    @PostMapping("/query")
    public Result<PageResult<ManagerOnPage>> query(@RequestParam(value = "page", defaultValue = "0", required = false)int page, 
        @RequestParam(value = "pagesize", defaultValue = "10", required = false)int pagesize, 
        @RequestParam(value = "mobile", required = false)String mobile, 
        @RequestParam(value = "role", required = false)ManagerRole role)
    {
        PageResult<ManagerOnPage> res = manager.query(page, pagesize, mobile, role);
        return new Result<>(res);
    }
    
}
