package cn.tofocus.lejia.app.v1.member;

import cn.tofocus.lejia.bean.dto.app.market.AppMemberAddrFourArea;
import cn.tofocus.lejia.bean.dto.app.market.JdAddressOption;
import cn.tofocus.lejia.bean.enums.AddrType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppMktAddrOnList;
import cn.tofocus.lejia.domain.app.AppMemberAddrManager;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;


@RequestMapping("/v1/app/market/lm/member/addr")
@RestController
public class AppMemberAddrApiImpl implements AppMemberAddrApi
{
	@Autowired
    private AppMemberAddrManager addrManager;
	
	@Override
	public Result<AppMktAddrOnList> insAddr(@Valid AppMktAddrOnList entity) {
		
		return new Result<>(addrManager.insAddr(entity));
	}

	@Override
	public Result<AppMktAddrOnList> getAddr(Integer pkey) {
		
		return new Result<>(addrManager.getAddr(pkey));
	}

	@Override
	public Result<PageResult<AppMktAddrOnList>> queryAddr(int page, int pagesize, AddrType type) {
		
		return new Result<>(addrManager.queryAddr(page, pagesize, type));
	}

	@Override
	public Result<AppMktAddrOnList> updAddr(@Valid AppMktAddrOnList entity) {
		
		return new Result<>(addrManager.updAddr(entity));
	}

	@Override
	public Result<Boolean> delAddr(Integer pkey) {
		
		return new Result<>(addrManager.delAddr(pkey));
	}

	@Override
	public Result<Boolean> defaultAddr(Integer pkey) {
		
		return new Result<>(addrManager.defaultAddr(pkey));
	}
    
    @Override
    public Result<List<JdAddressOption>> listTown(String pro, String city, String area)
    {
        return new Result<>(addrManager.listTown(pro, city, area));
    }
    
    @Override
    public Result<AppMemberAddrFourArea> convertFourAreaByLatLng(BigDecimal longitude, BigDecimal latitude)
    {
        return new Result<>(addrManager.convertFourAreaByLatLng(longitude, latitude));
    }
}
