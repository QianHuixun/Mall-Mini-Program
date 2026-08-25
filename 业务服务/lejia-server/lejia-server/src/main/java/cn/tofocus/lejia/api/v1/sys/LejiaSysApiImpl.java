package cn.tofocus.lejia.api.v1.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.sys.SysCompanyOnList;
import cn.tofocus.lejia.domain.CompanyManager;


@RequestMapping("/v1/sys/company")
@RestController
public class LejiaSysApiImpl implements LejiaSysApi 
{
	@Autowired
    private CompanyManager companyManager;

	@Override
	@LogApi(operation = "新增公司", format = "新增公司,名称:{entity.name},联系方式:{mobile}", resultFormat = "")
	public Result<SysCompanyOnList> insCompany(SysCompanyOnList entity) {
		SysCompanyOnList company = companyManager.insCompany(entity);
		return new Result<>(company);
	}

	@Override
	public Result<SysCompanyOnList> getCompany(String pkey) {
		return new Result<>(companyManager.getCompany(pkey));
	}

	@Override
	public Result<PageResult<SysCompanyOnList>> queryCompany(int page, int pagesize, String companyName) {
		return new Result<>(companyManager.queryCompany(page, pagesize, companyName));
	}

	@Override
	@LogApi(operation = "修改公司", format = "修改公司,名称:{name}, 地址:{addr}")
	public Result<SysCompanyOnList> updCompany(String pkey, String name, String addr) {
		return new Result<>(companyManager.updCompany(pkey, name, addr));
	}

	@Override
	@LogApi(operation = "删除公司", format = "删除公司")
	public Result<Boolean> delCompany(String pkey) {
		return new Result<>(companyManager.delCompany(pkey));
	}

	@Override
	@LogApi(operation = "启动公司", format = "启动公司")
	public Result<Boolean> startCompany(String pkey) {
		return new Result<>(companyManager.enableCompany(pkey, true));
	}

	@Override
	@LogApi(operation = "停止公司", format = "停止公司")
	public Result<Boolean> stopCompany(String pkey) {
		return new Result<>(companyManager.enableCompany(pkey, false));
	}





}
