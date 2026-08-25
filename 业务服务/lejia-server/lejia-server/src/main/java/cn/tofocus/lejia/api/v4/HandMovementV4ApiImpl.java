package cn.tofocus.lejia.api.v4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import cn.tofocus.core.Result;
import cn.tofocus.lejia.bean.entity.sys.SysFarmerExtend;
import cn.tofocus.lejia.domain.GoodsBoxManager;
import cn.tofocus.lejia.domain.v4.HandMovementV4Manager;

@RequestMapping("/v4/hand/movement")
@RestController
public class HandMovementV4ApiImpl
{
    @Autowired
    private HandMovementV4Manager manager;
    
    @Autowired
    private GoodsBoxManager goodsBoxManager;
    
    @PostMapping("/putFarmerExtend")
    public Result<Boolean> putFarmerExtend(@RequestBody SysFarmerExtend info)
    {
        return new Result<>(manager.putFarmerExtend(info));
    }
    
    @PostMapping("/runAddGoodsBoxSpace")
    public Result<Boolean> runAddGoodsBoxSpace()
    {
        goodsBoxManager.runAddGoodsBoxSpace();
        return new Result<>(true);
    }
    
    @PostMapping("/addGoodsBoxSpace")
    public Result<Boolean> addGoodsBoxSpace(@RequestParam(value = "goods") Integer goods)
    {
        Boolean res = goodsBoxManager.addGoodsBoxSpace(goods);
        return new Result<>(res);
    }
}
