package cn.tofocus.lejia.api.v1.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.sys.SysLogOnList;
import cn.tofocus.lejia.domain.LogManager;


@RequestMapping("/v1/sys/log")
@RestController
public class LejiaLogApiImpl implements LejiaLogApi
{
	
	@Autowired
    private LogManager logManager;
	
	@Override
	public Result<PageResult<SysLogOnList>> queryLog(int page, int pagesize, String startTime, String endTime) {
		return new Result<>(logManager.queryLog(page, pagesize, startTime, endTime));
	}

}
