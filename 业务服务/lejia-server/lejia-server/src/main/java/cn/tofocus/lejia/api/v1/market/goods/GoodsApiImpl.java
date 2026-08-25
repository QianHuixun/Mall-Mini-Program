package cn.tofocus.lejia.api.v1.market.goods;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.goods.GoodsAdvertOnInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsRecommendInfo;
import cn.tofocus.lejia.bean.dto.goods.GoodsRecommendOnPage;
import cn.tofocus.lejia.bean.enums.AccountType;
import cn.tofocus.lejia.bean.enums.GoodsRecommendZone;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.util.WxDataBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.Result;
import cn.tofocus.core.log.LogApi;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.core.msgpipe.queue.MsgSenderTemplate;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant.SysConfig;
import cn.tofocus.lejia.api.v1.ApiTags;
import cn.tofocus.lejia.bean.dto.market.DropDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.MktGoodsOnList;
import cn.tofocus.lejia.bean.dto.market.MktGoodsUpdDTO;
import cn.tofocus.lejia.bean.entity.goods.MktGoods;
import cn.tofocus.lejia.bean.entity.sys.AccountEntity;
import cn.tofocus.lejia.bean.enums.MType;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.AccountDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.domain.GoodsExcelManager;
import cn.tofocus.lejia.domain.GoodsManager;
import cn.tofocus.lejia.domain.TagManager;
import cn.tofocus.lejia.domain.WxManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/v1/market/goods/manager")
@RestController
@Validated
@Slf4j
public class GoodsApiImpl implements GoodsApi
{
    
    @Autowired
    private GoodsManager goodsManager;
    
    @Autowired
    private GoodsExcelManager goodsExcelManager;
    
    @Autowired
    private TagManager tagManager;
    
    @Autowired
    private WxManager wxManager;
    
    @Autowired
    private MsgSenderTemplate msgSenderTemplate;
    
    @Autowired
    private SysConfigDao sysConfigDao;
    
    @Autowired
    private AccountDao accountDao;
    
    @Override
    @LogApi(operation = "新增商品", format = "新增商品,名称:{entity.title}", resultFormat = "")
    public Result<Integer> insGoods(MktGoodsDetailsDTO entity)
    {
        MktGoods goods = goodsManager.insGoods(entity);
        if (entity.isSendWechatMsg() && MType.SPECIAL_GOODS.equals(entity.getMType()))
        {
            try
            {
                msgSenderTemplate.put("", "", goods, new WxMsgSender());
            }
            catch (Exception e)
            {
                log.warn("发送秒杀商品通知异常", e);
            }
        }
        return new Result<>(goods.getPkey());
    }
    
    @Override
    @LogApi(operation = "修改商品", format = "修改商品 名称:{entity.title}")
    public Result<Integer> updGoods(MktGoodsUpdDTO entity)
    {
        MktGoods goods = goodsManager.updGoods(entity);
        if (entity.isSendWechatMsg() && MType.SPECIAL_GOODS.equals(entity.getMType()))
        {
            try
            {
                msgSenderTemplate.put("", "", goods, new WxMsgSender());
            }
            catch (Exception e)
            {
                log.warn("发送秒杀商品通知异常", e);
            }
        }
        return new Result<>(goods.getPkey());
    }
    
    class WxMsgSender implements MsgListener<MktGoods, Boolean>
    {
        @Override
        public Boolean handleMessage(String pipeId, String correlationId, MktGoods goods)
            throws Exception
        {
            Integer ascription = goods.getAscription();
            String templateid = sysConfigDao.getTemplate(SysConfig.TEMPLATE_SPECIAL_GOODS, ascription);
            if (templateid == null)
                return false;
            List<Integer> tags =
                MemberVisibleRange.TAG.equals(goods.getVisibleRange()) ? tagManager.getGoodsTags(goods.getPkey().longValue())
                    : null;
            List<String> openids = tagManager.listMemberOpenid(ascription, tags);
            
            JSONObject data = new WxDataBuilder().param("thing1")
                .value(goods.getTitle())
                .param("amount2")
                .value(goods.getPrice() + "元")
                .param("time3")
                .value(DateUtil.formatDate(goods.getStartDate(), "yyyy-MM-dd"))
                .param("thing4")
                .value("限时秒杀商品已上架，不要错过哦～")
                .build();
            
            for (String openid : openids)
            {
                AccountEntity account = accountDao.get(ascription, AccountType.USER);
                wxManager.sendWeappSubscribeMessage(account, openid, templateid, "pages/shouyeGroup/goodsDeatil/index?pkey=" + goods.getPkey(), data);
            }
            return true;
        }
        
        @Override
        public void handleResult(String pipeId, String correlationId, Result<Boolean> result)
            throws Exception
        {
            if (!result.isSuccess())
                log.warn("发送秒杀商品通知异常, {}", result.getMsg());
            else if(!result.getResult())
                log.warn("秒杀商品通知的模板未配置");
        }
    }
    
    @Override
    public Result<String> checkPricePurchase(Integer goodsPkey)
    {
        String res = "";
        //        res = goodsManager.checkPricePurchase(goodsPkey);
        return new Result<>(res);
    }
    
    @Override
    public Result<PageResult<MktGoodsDetailsDTO>> queryGoods(Integer page, Integer pagesize, MType mType, String title,
        Integer gtype, Boolean enabled, Integer status, String farmer)
    {
        return new Result<>(goodsManager.queryGoodsList(page, pagesize, mType, enabled, status, gtype, title, farmer));
    }
    
    @Override
    public Result<MktGoodsDetailsDTO> getGoods(Integer pkey)
    {
        return new Result<>(goodsManager.getGoods(pkey));
    }
    
    @Override
    @LogApi(operation = "删除商品", format = "删除商品")
    public Result<Boolean> delGoods(Integer pkey)
    {
        return new Result<>(goodsManager.delGoods(pkey));
    }
    
    @Override
    @LogApi(operation = "批量删除商品", format = "删除商品")
    public Result<Boolean> delListGoods(List<Integer> pkeys)
    {
        return new Result<>(goodsManager.delListGoods(pkeys));
    }
    
    @Override
    @LogApi(operation = "启动商品", format = "启动商品")
    public Result<Boolean> startGoods(Integer pkey)
    {
        return new Result<>(goodsManager.enabledGoods(pkey, true));
    }
    
    @Override
    @LogApi(operation = "停止商品", format = "停止商品")
    public Result<Boolean> stopGoods(Integer pkey)
    {
        return new Result<>(goodsManager.enabledGoods(pkey, false));
    }
    
    @Override
    public Result<List<MktGoodsOnList>> listGoods()
    {
        return new Result<>(goodsManager.listGoodsTitle());
    }
    
    @Override
    public Result<List<DropDTO>> listGoods(String title)
    {
        return new Result<>(goodsManager.listGoodsTitle(title));
    }
    
    @Deprecated
    @Operation(summary = "导出市场商品清单", tags = ApiTags.custGoods)
    @PostMapping(value = "/export/goods")
    public Result<Boolean> downOrder(
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "gtype", required = false) @Parameter(description = "分类id") Integer gtype,
        @RequestParam(value = "goodsMain", required = false) @Parameter(description = "商品库id") Integer goodsMain,
        @RequestParam(value = "enabled", required = false) @Parameter(description = "是否上架") Boolean enabled,
        @RequestParam(value = "status", defaultValue = "0") @Parameter(description = "发售状态") Integer status,
        @RequestParam(value = "mType", required = false, defaultValue = "MARKET_GOODS") MType mType,
        HttpServletResponse response)
    {
        OutputStream out = null;
        String marketPkey = CurrentSession.marketPkey();
        try
        {
            String fileName = new String("市场商品清单.xlsx".getBytes(), "iso-8859-1");
            response.setHeader("Content-disposition", "attachment; filename = " + fileName);
            out = response.getOutputStream();
            goodsExcelManager.exportGoods(title, gtype, goodsMain, enabled, status, mType, marketPkey, out);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Result<>(true);
    }
    
    @Operation(summary = "商品excle导入", tags = ApiTags.custGoods)
    @Transactional
    @PostMapping(value = "/importexcel")
    public Result<Boolean> importExcel(MultipartFile myfile,
        @RequestParam(value = "mType", required = false, defaultValue = "MARKET_GOODS") MType mType,
        HttpServletResponse response)
    {
        String marketPkey = CurrentSession.marketPkey();
        try (OutputStream out = response.getOutputStream())
        {
            goodsExcelManager.importGoods(myfile, mType, marketPkey, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new Result<>(true);
    }
    
    @Override
    public Result<Boolean> updRichTemp(String content)
    {
        goodsManager.updRichTemp(content);
        return new Result<>(true);
    }
    
    @Override
    public Result<String> getRichTemp()
    {
        return new Result<>(goodsManager.getRichTemp());
    }
    
    @Override
    public Result<Boolean> enableGuessLike(Integer pkey)
    {
        return new Result<>(goodsManager.enableGuessLike(pkey));
    }
    
    @Override
    public Result<Boolean> enableZoneRecommend(Integer pkey, Boolean enabled)
    {
        boolean sign = goodsManager.enableZoneRecommend(pkey, enabled);
        return new Result<>(sign);
    }
    
    @Override
    public Result<String> getZoneDisplayName(MType mType)
    {
        String res = goodsManager.getZoneDisplayName(mType);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> setZoneDisplayName(MType mType, String displayName)
    {
        boolean sign = goodsManager.setZoneDisplayName(mType, displayName);
        return new Result<>(sign);
    }
    
    @Override
    public Result<PageResult<GoodsRecommendOnPage>> queryRecommendGoods(Integer page, Integer pagesize,
        Integer sourceGoods, String goodsFarmer, MType mType, String vendor, String title, GoodsRecommendZone zone)
    {
        PageResult<GoodsRecommendOnPage> res =
            goodsManager.queryRecommendGoods(page, pagesize, sourceGoods, goodsFarmer, mType, vendor, title, zone);
        return new Result<>(res);
    }
    
    @Override
    public Result<GoodsRecommendInfo> getRecommendGoods(Integer pkey)
    {
        GoodsRecommendInfo res = goodsManager.getRecommendGoods(pkey);
        return new Result<>(res);
    }
    
    @Override
    public Result<Boolean> addRecommendGoods(GoodsRecommendInfo info)
    {
        boolean sign = goodsManager.saveRecommendGoods(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> updRecommendGoods(GoodsRecommendInfo info)
    {
        if (info.getPkey() == null)
            throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "主键不能为空");
        boolean sign = goodsManager.saveRecommendGoods(info);
        return new Result<>(sign);
    }
    
    @Override
    public Result<Boolean> delRecommendGoods(Integer pkey)
    {
        boolean sign = goodsManager.delRecommendGoods(pkey);
        return new Result<>(sign);
    }

    @Override
    public Result<PageResult<GoodsAdvertOnInfo>> queryAdvertGoods(int page, int pagesize, String farmer, String title)
    {
        PageResult<GoodsAdvertOnInfo> result = goodsManager.queryAdvertGoods(page, pagesize, farmer, title);
        return new Result<>(result);
    }
}
