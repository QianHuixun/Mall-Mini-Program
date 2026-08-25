//package cn.tofocus.lejia.app.v3;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import cn.tofocus.core.Result;
//import cn.tofocus.core.page.PageResult;
//import cn.tofocus.lejia.bean.dto.market.MktGtypeOnList;
//import cn.tofocus.lejia.bean.dto.market.v3.MktVendorGoodsOnInfo;
//import cn.tofocus.lejia.domain.v3.AppGoodsVendorManager;
//
//@RequestMapping("/v3/app/vendor/goods")
//@RestController
//public class AppGoodsVendorApiImpl implements AppGoodsVendorApi
//{
//    @Autowired
//    private AppGoodsVendorManager manager;
//    
//    @Override
//    public Result<PageResult<MktVendorGoodsOnInfo>> queryGoods(int page, int pagesize, 
//        String title, Integer gtype, Boolean enabled, Integer status)
//    {
//        return new Result<>(manager.queryGoods(page, pagesize, title, gtype, enabled, status));
//    }
//
//    @Override
//    public Result<List<MktGtypeOnList>> queryGtype()
//    {
//        return new Result<>(manager.queryGtype());
//    }
//
//    @Override
//    public Result<Integer> insGoods(MktVendorGoodsOnInfo entity)
//    {
//        return new Result<>(manager.insGoods(entity));
//    }
//
//    @Override
//    public Result<Integer> updGoods(MktVendorGoodsOnInfo entity)
//    {
//        return new Result<>(manager.updGoods(entity));
//    }
//    
//}
