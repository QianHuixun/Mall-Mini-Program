package cn.tofocus.lejia.domain.jdvop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.jd.open.api.sdk.domain.vopkc.SkuInfoGoodsProvider.response.getNewStockById.GetStockByIdGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.CategoryInfoGoodsProvider.response.getCategoryInfoList.GetCategoryInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.request.getSkusAllSaleState.AreaBaseInfoGoodsReq;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.request.getSkusAllSaleState.GetStockByIdGoodsReq;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.request.getSkusAllSaleState.SkuNumBaseGoodsReq;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.checkAreaLimitList.CheckAreaLimitGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.checkSkuSaleList.CheckSkuSaleGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSellPrice.GetSellPriceGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSimilarSkuList.GetSimilarSkuGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuDetailInfo.GetSkuPoolInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuImageList.GetSkuImageGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkusAllSaleState.GetSkuCanSaleResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.querySkuAreaLimit.QuerySkuAreaLimitResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.getSkuPoolInfo.GetSkuPoolInfoItemGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuPoolGoodsProvider.response.querySkuByPage.OpenPagingResult;
import com.jd.open.api.sdk.request.vopkc.VopGoodsGetNewStockByIdRequest;
import com.jd.open.api.sdk.request.vopsp.*;
import com.jd.open.api.sdk.response.vopkc.VopGoodsGetNewStockByIdResponse;
import com.jd.open.api.sdk.response.vopsp.*;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPAreaInfo;
import cn.tofocus.lejia.domain.jdvop.bean.JdVOPSkuNum;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 京东VOP - 商品接口
 * （含库存接口）
 */
@Slf4j
@Component
public class JdVOPGoodsManager extends BaseJdVOPManager
{
    /**
     * 查询商品池编号
     * 一般第一次初始化客户商品库使用。与【根据商品池获取所有商品信息】搭配使用，可查询商品池内商品列表。
     * @return 商品池编号列表
     */
    public List<GetSkuPoolInfoItemGoodsResp> getSkuPoolInfo()
    {
        try
        {
            VopGoodsGetSkuPoolInfoRequest request = new VopGoodsGetSkuPoolInfoRequest();
            VopGoodsGetSkuPoolInfoResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            if (response.getOpenRpcResult().getResult() == null)
                return new ArrayList<>();
            return response.getOpenRpcResult().getResult().getSkuPoolList();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "查询商品池编号";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 根据商品池获取所有商品信息
     * @return 商品id列表(当remainPage=0时,商品池遍历完成)
     */
    public OpenPagingResult querySkuByPage(String bizPoolId, long offset, int pageSize)
    {
        try
        {
            VopGoodsQuerySkuByPageRequest request = new VopGoodsQuerySkuByPageRequest();
            request.setBizPoolId(bizPoolId);
            request.setOffset(offset);
            request.setPageSize(pageSize);
            VopGoodsQuerySkuByPageResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "根据商品池获取所有商品";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, wrapVopErrMsg(errMsg));
        }
    }
    
    /**
     * 获取商品详情及商品扩展信息
     * @param queryExtSet 查询扩展参数集合，可空。1：移动端商品详情大字段； 2：PC端商品详情大字段； 3：微信小程序商品详情大字段，仅提供图片地址，需要客户添加显示逻辑；5：商品附件查询资料下载。8:领货码商品属性。9:客户类目映射，最大支持100。10：供应商信息。11：商品类型，13：商品购买倍数，14：企业服务商品，15：汽车京保养类型
     * @return 商品详情
     */
    public GetSkuPoolInfoGoodsResp getSkuDetailInfo(long skuId, Set<Integer> queryExtSet)
    {
        try
        {
            VopGoodsGetSkuDetailInfoRequest request = new VopGoodsGetSkuDetailInfoRequest();
            request.setSkuId(skuId);
            if (queryExtSet != null)
                request.setQueryExtSet(collection2Str(queryExtSet));
            VopGoodsGetSkuDetailInfoResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "获取商品详情";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, wrapVopErrMsg(errMsg));
        }
    }
    
    /**
     * 查询同类商品
     * @return 相似商品查询结果
     */
    public List<GetSimilarSkuGoodsResp> getSimilarSkuList(long skuId)
    {
        try
        {
            VopGoodsGetSimilarSkuListRequest request = new VopGoodsGetSimilarSkuListRequest();
            request.setSkuId(skuId);
            VopGoodsGetSimilarSkuListResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "查询同类商品";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, wrapVopErrMsg(errMsg));
        }
    }
    
    /**
     * 查询商品图片
     * @param skuIdList 商品id列表，最多100个
     * @return 商品列表
     */
    public List<GetSkuImageGoodsResp> getSkuImageList(List<Long> skuIdList)
    {
        try
        {
            VopGoodsGetSkuImageListRequest request = new VopGoodsGetSkuImageListRequest();
            request.setSkuId(collection2Str(skuIdList));
            VopGoodsGetSkuImageListResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "查询商品图片";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, wrapVopErrMsg(errMsg));
        }
    }
    
    /**
     * 查询商品全部购买限制区域
     * @param skuIdList 商品id列表，最多100个
     * @return 商品区域限制信息
     */
    public List<QuerySkuAreaLimitResp> querySkuAreaLimit(List<Long> skuIdList)
    {
        try
        {
            VopGoodsQuerySkuAreaLimitRequest request = new VopGoodsQuerySkuAreaLimitRequest();
            request.setSkuId(collection2Str(skuIdList));
            VopGoodsQuerySkuAreaLimitResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "查询商品全部购买限制区域";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, wrapVopErrMsg(errMsg));
        }
    }
    
    /**
     * 校验商品购买区域是否受限
     * @param skuIdList 商品id列表，最多100个
     * @param provinceId 京东一级地址编号
     * @param cityId 京东二级地址编号
     * @param countyId 京东三级地址编号
     * @param townId 可空，京东四级地址编号(如果该地区有四级地址，则必须传递四级地址，没有四级地址则传0)
     * @return 商品区域购买限制，true代表区域受限 false 区域不受限
     */
    public List<CheckAreaLimitGoodsResp> checkAreaLimitList(List<Long> skuIdList, Long provinceId, Long cityId,
        Long countyId, Long townId)
    {
        try
        {
            VopGoodsCheckAreaLimitListRequest request = new VopGoodsCheckAreaLimitListRequest();
            request.setSkuId(collection2Str(skuIdList));
            request.setProvinceId(provinceId);
            request.setCityId(cityId);
            request.setCountyId(countyId);
            request.setTownId(townId);
            VopGoodsCheckAreaLimitListResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "校验商品购买区域是否受限";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 批量查询商品售卖价
     * @param skuIdList 商品id列表，最多100个
     * @return 售价响应结果
     */
    public List<GetSellPriceGoodsResp> getSellPrice(List<Long> skuIdList)
    {
        try
        {
            VopGoodsGetSellPriceRequest request = new VopGoodsGetSellPriceRequest();
            request.setSkuId(collection2Str(skuIdList));
            VopGoodsGetSellPriceResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "批量查询商品售卖价";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 验证商品可售性
     * @param skuIdList 商品id列表，最多100个
     * @return 商品可售性校验结果
     */
    public List<CheckSkuSaleGoodsResp> checkSkuSaleList(List<Long> skuIdList)
    {
        try
        {
            VopGoodsCheckSkuSaleListRequest request = new VopGoodsCheckSkuSaleListRequest();
            request.setSkuId(collection2Str(skuIdList));
            VopGoodsCheckSkuSaleListResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "验证商品可售性";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 批量获取库存接口
     * @param skuNumInfoList sku和数量信息列表，最大支持100
     * @param areaInfo 区域信息
     * @return 商品可售性校验结果
     */
    public List<GetStockByIdGoodsResp> getNewStockById(List<JdVOPSkuNum> skuNumInfoList, JdVOPAreaInfo areaInfo)
    {
        try
        {
            VopGoodsGetNewStockByIdRequest request = new VopGoodsGetNewStockByIdRequest();
            com.jd.open.api.sdk.domain.vopkc.SkuInfoGoodsProvider.request.getNewStockById.GetStockByIdGoodsReq req =
                new com.jd.open.api.sdk.domain.vopkc.SkuInfoGoodsProvider.request.getNewStockById.GetStockByIdGoodsReq();
            req.setSkuNumInfoList(BeanUtil.beanListFrom(
                com.jd.open.api.sdk.domain.vopkc.SkuInfoGoodsProvider.request.getNewStockById.SkuNumBaseGoodsReq.class,
                skuNumInfoList));
            req.setAreaInfo(BeanUtil.beanFrom(
                com.jd.open.api.sdk.domain.vopkc.SkuInfoGoodsProvider.request.getNewStockById.AreaBaseInfoGoodsReq.class,
                areaInfo));
            request.setGetStockByIdGoodsReq(req);
            VopGoodsGetNewStockByIdResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "批量获取库存接口";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 商品可采校验接口（预占下单前调用）
     * @param skuNumInfoList sku和数量信息列表，最大支持100
     * @param areaInfo 区域信息
     * @return 商品可信结果
     */
    public List<GetSkuCanSaleResp> getSkusAllSaleState(List<JdVOPSkuNum> skuNumInfoList, JdVOPAreaInfo areaInfo)
    {
        try
        {
            VopGoodsGetSkusAllSaleStateRequest request = new VopGoodsGetSkusAllSaleStateRequest();
            GetStockByIdGoodsReq req = new GetStockByIdGoodsReq();
            req.setSkuNumInfoList(BeanUtil.beanListFrom(SkuNumBaseGoodsReq.class, skuNumInfoList));
            req.setAreaInfo(BeanUtil.beanFrom(AreaBaseInfoGoodsReq.class, areaInfo));
            request.setGetStockByIdGoodsReq(req);
            VopGoodsGetSkusAllSaleStateResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "商品可采校验接口";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
    
    /**
     * 根据分类id查询分类信息
     * @param categoryIdSet 类目ID集合，最大100条
     * @return 类目信息
     */
    public List<GetCategoryInfoGoodsResp> getCategoryInfoList(Set<Long> categoryIdSet)
    {
        try
        {
            VopGoodsGetCategoryInfoListRequest request = new VopGoodsGetCategoryInfoListRequest();
            request.setCategoryId(collection2Str(categoryIdSet));
            VopGoodsGetCategoryInfoListResponse response = jdClient().execute(request);
            if (!response.getOpenRpcResult().getSuccess())
            {
                printResponseFailedLog(log, request, response);
                throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR,
                    wrapVopErrMsg(response.getOpenRpcResult().getResultMessage()));
            }
            return response.getOpenRpcResult().getResult();
        }
        catch (TofocusException te)
        {
            throw te;
        }
        catch (Exception e)
        {
            String operate = "根据分类id查询分类信息";
            String errMsg = operate + "请求异常";
            log.error("[京东VOP]{}", errMsg, e);
            throw TofocusException.of(LejiaErrCode.REQUEST_OUT_ERROR, errMsg);
        }
    }
}
