package cn.tofocus.lejia.app.v1.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberPointDTO;
import cn.tofocus.lejia.bean.dto.app.market.AppMemberPointLineOnList;
import cn.tofocus.lejia.bean.dto.app.market.AppMktVendorDTO;
import cn.tofocus.lejia.domain.market.mall.AppMemberPointManager;
import cn.tofocus.lejia.exception.LejiaErrCode;

@RequestMapping("/v1/app/market/lm/member/point")
@RestController
public class AppMemberPointApiImpl implements AppMemberPointApi {

    @Autowired
    private AppMemberPointManager poManager;

    @Override
    public Result<AppMktVendorDTO> loadIndex(String ecode) {
        
        try {
        	System.out.println(ecode);
            return new Result<>(poManager.loadIndex(ecode));
        } catch (Exception e) {
        	e.printStackTrace();
            throw TofocusException.of(LejiaErrCode.VENDOR_WRONG);
        }
    }

    @Override
    public Result<Boolean> payPoints(String ecode, int points) {
        
        poManager.payPoints(ecode, points);
        return new Result<>(true);
    }

    @Override
    public Result<AppMemberPointDTO> get() {
        return new Result<>(poManager.getPoints());
    }

    @Override
    public Result<PageResult<AppMemberPointLineOnList>> line(int page, int pagesize, Boolean direct) {
        return new Result<>(poManager.queryLine(page, pagesize, direct));
    }

}
