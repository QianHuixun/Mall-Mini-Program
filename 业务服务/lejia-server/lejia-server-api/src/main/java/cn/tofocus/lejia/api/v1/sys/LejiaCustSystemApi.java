package cn.tofocus.lejia.api.v1.sys;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

public interface LejiaCustSystemApi {
	
	
	/***************** 
     * 角色
     ****************/
	@Operation(summary = "新增角色", tags = "CUST-角色")
    @PostMapping(value = "/role/ins")
    public Result<String> insRole(
    		@RequestParam(name = "name") String name, 
    		@RequestParam(name = "description", required = false) String description);
	
//	@Operation(summary = "查询角色", tags = "CUST-角色")
//    @PostMapping(value = "/role/query")
//    public Result<PageResult<RoleDetail>> queryRole(
//        @RequestParam(value = "page", defaultValue = "0") @Parameter(description = "页号") int page,
//        @RequestParam(value = "pagesize", defaultValue = "10") @Parameter(description = "每页大小") int pagesize);

    
    
}
