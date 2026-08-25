package cn.tofocus.lejia.app.v3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.dto.v3.PromoteUpdDto;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.sys.MktPromoteDao;

@RequestMapping("/v3/app/promote")
@RestController
public class AppPromoteApiImpl implements AppPromoteApi
{
    @Autowired
    private MktPromoteDao promoteDao;
    
    @Override
    public Result<PromoteUpdDto> get()
    {
        PromoteUpdDto dto = promoteDao.getDto(MobileSession.appid(), MobileSession.farmerPkey());
//        if(dto == null)
//        {
//            dto = promoteDao.getDto(MobileSession.appid(), null);
//            if(dto == null)
//            {
//                dto = new PromoteUpdDto();
//                dto.setPhoto("");
//                dto.setContent(" ");
//                dto.setTitle(" ");
//            }
//        }
        return new Result<>(dto);
    }
    
}
