package cn.tofocus.lejia.domain.jdvop.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsUpdNotice;
import cn.tofocus.lejia.dao.jd.JdGoodsUpdNoticeDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.checkSkuSaleList.CheckSkuSaleGoodsResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSimilarSkuList.GetSimilarSkuGoodsResp;
import com.jd.open.api.sdk.domain.vopsp.SkuInfoGoodsProvider.response.getSkuDetailInfo.GetSkuPoolInfoGoodsResp;
import com.jd.open.api.sdk.domain.vopxx.MsgRecordProvider.response.queryTransByVopNormal.VopBizTransMessage;

import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.entity.jd.JdGoods;
import cn.tofocus.lejia.bean.entity.jd.JdGoodsSpace;
import cn.tofocus.lejia.bean.enums.MemberVisibleRange;
import cn.tofocus.lejia.dao.jd.JdCategoryDao;
import cn.tofocus.lejia.dao.jd.JdGoodsDao;
import cn.tofocus.lejia.dao.jd.JdGoodsSpaceDao;
import cn.tofocus.lejia.domain.jd.JdGoodsManager;
import cn.tofocus.lejia.domain.jdvop.JdVOPGoodsManager;
import cn.tofocus.lejia.domain.jdvop.bean.msg.*;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class JdVOPSkuListener implements MsgListener<VopBizTransMessage, String>
{
    public static final String PIPE_NAME = "zyysc.jd.vop.msg.sku";
    
    @Autowired
    private JdVOPGoodsManager jdVOPGoodsManager;
    
    @Autowired
    private JdGoodsManager jdGoodsManager;
    
    @Autowired
    private JdGoodsDao jdGoodsDao;
    
    @Autowired
    private JdCategoryDao jdCategoryDao;
    
    @Autowired
    private JdGoodsSpaceDao jdGoodsSpaceDao;

    @Autowired
    private JdGoodsUpdNoticeDao jdGoodsUpdNoticeDao;
    
    @Override
    public String handleMessage(String pipeId, String correlationId, VopBizTransMessage msg)
        throws Exception
    {
        switch (msg.getType())
        {
            case 2:
            {
                // 商品价格变更
                // {"id":推送id, "result":{"skuId" : 商品编号 }, "type": 2, "time":推送时间}
                JdVOPSkuPriceChangeMsg m = JsonUtil.getBean(msg.getContent(), JdVOPSkuPriceChangeMsg.class);
                skuPriceChangeMsg(m);
                break;
            }
            case 4:
            {
                // 商品上下架变更消息
                // {"id":推送id,"result":{"state":0,"skuId":商品编号},"type":4,"time":推送时间}
                // state:1代表在主站（jd.com）上架； state:0代表下架
                JdVOPSkuStateChangeMsg m = JsonUtil.getBean(msg.getContent(), JdVOPSkuStateChangeMsg.class);
                skuStateChangeMsg(m);
                break;
            }
            case 6:
            {
                // 商品池内商品添加、删除消息
                // {"id":推送id, "result":{"skuId": 商品编号, "page_num":商品池编号, "state":"1添加，2删除"}, "type" : 6, "time":消息推送时间}
                JdVOPSkuAddDelMsg m = JsonUtil.getBean(msg.getContent(), JdVOPSkuAddDelMsg.class);
                skuAddDelMsg(m);
                break;
            }
            case 16:
            {
                // 商品信息变更
                // {"id":推送id, "result":{"skuId" : 商品编号 } "type" : 16, "time":推送时间}
                // 包含： 商品名称，介绍，规格参数，商品图片变更
                JdVOPSkuChangeMsg m = JsonUtil.getBean(msg.getContent(), JdVOPSkuChangeMsg.class);
                skuChangeMsg(m);
                break;
            }
            case 48:
            {
                // 商品池添加、删除消息
                // {"id":推送id, "result":"{"poolType": "recommend", "page_num":"商品池编号", "state":"1添加，2删除"}", "type" :48, "time":推送时间}
                // poolType: p_skupool 用户的私有商品池； cate_pool 分类商品池 recommend 主推商品池；hot_sale 热销商品池； p_custom_skupool 用户的私有定制商品池；
                JdVOPSkuPoolAddDelMsg m = JsonUtil.getBean(msg.getContent(), JdVOPSkuPoolAddDelMsg.class);
                skuPoolAddDelMsg(m);
                break;
            }
            case 126:
            {
                // 商品可售状态(只限预约预售导致)
                // {"id":推送id, "result":{"skuId": 商品编号}, "type" : 126, "time":推送时间}
                JdVOPSkuSaleStateChangeMsg m = JsonUtil.getBean(msg.getContent(), JdVOPSkuSaleStateChangeMsg.class);
                skuSaleStateChangeMsg(m);
                break;
            }
        }
        return "ok";
    }
    
    private static final String SKU_PRICE_CHANGE_NAME = "商品价格变更";
    
    private void skuPriceChangeMsg(JdVOPSkuPriceChangeMsg msg)
    {
        try
        {
            startHandleLog(SKU_PRICE_CHANGE_NAME, msg);
            JdGoods jdGoods = jdGoodsDao.get(msg.getSkuId());
            if (jdGoods != null)
            {
                List<JdGoods> goodsList = Lists.newArrayList(jdGoods);
                // 价格
                jdGoodsManager.setPrice(goodsList);
                jdGoodsDao.put(jdGoods);
            }
        }
        catch (Exception e)
        {
            exceptionLog(SKU_PRICE_CHANGE_NAME, e);
        }
    }
    
    private static final String SKU_STATE_CHANGE_NAME = "商品上下架变更";
    
    private void skuStateChangeMsg(JdVOPSkuStateChangeMsg msg)
    {
        try
        {
            startHandleLog(SKU_STATE_CHANGE_NAME, msg);
            JdGoods jdGoods = jdGoodsDao.get(msg.getSkuId());
            if (jdGoods != null)
            {
                GetSkuPoolInfoGoodsResp s = jdVOPGoodsManager.getSkuDetailInfo(jdGoods.getPkey(), null);
                jdGoods.setSkuState(s.getSkuState());
                jdGoodsDao.put(jdGoods);
            }
        }
        catch (Exception e)
        {
            exceptionLog(SKU_STATE_CHANGE_NAME, e);
        }
    }
    
    private static final String SKU_ADD_DEL_NAME = "商品池内商品添加、删除";
    
    private void skuAddDelMsg(JdVOPSkuAddDelMsg msg)
    {
        try
        {
            startHandleLog(SKU_ADD_DEL_NAME, msg);
            if (msg.getState() == null)
                return;
            switch (msg.getState())
            {
                case 1:
                {
                    JdGoods jdGoods = jdGoodsDao.get(msg.getSkuId());
                    if (jdGoods == null)
                    {
                        jdGoods = new JdGoods();
                        jdGoods.setAscription(13);
                        jdGoods.setPkey(msg.getSkuId());
                        jdGoods.setBizPoolId(msg.getPageNum());
                    }
                    // 规格
                    Map<Long, JdGoodsSpace> gsMap = new HashMap<>();
                    List<GetSimilarSkuGoodsResp> list = jdVOPGoodsManager.getSimilarSkuList(jdGoods.getPkey());
                    for (int i = 0; i < list.size(); i++)
                    {
                        GetSimilarSkuGoodsResp gssgr = list.get(i);
                        jdGoodsManager.setSpace(i, jdGoods, gsMap, gssgr);
                    }
                    JdGoodsSpace space = gsMap.get(jdGoods.getPkey());
                    // 详情
                    GetSkuPoolInfoGoodsResp s =
                        jdVOPGoodsManager.getSkuDetailInfo(jdGoods.getPkey(), Sets.newHashSet(3));
                    String title = specialTitle(s.getSkuName());
                    jdGoods.setTitle(title);
                    jdGoods.setWeight(s.getWeight());
                    jdGoods.setSaleUnit(s.getSaleUnit());
                    jdGoods.setSeoModel(s.getSeoModel());
                    jdGoods.setIntroduce(s.getIntroduce());
                    jdGoods.setIntroducePc(s.getIntroducePc());
                    jdGoods.setIntroduceApp(s.getIntroduceApp());
                    jdGoods.setIntroduceWechat(s.getIntroduceWechat());
                    jdGoods.setSkuState(s.getSkuState());
                    jdGoods.setSpuId(s.getSpuId());
                    jdGoods.setSpuName(s.getSpuName());
                    jdGoods.setVisibleRange(MemberVisibleRange.ALL);
                    jdGoods.setSort(0);
                    jdGoods.setFarmer(Constant.Operation + "13");
                    jdGoods.setEnabled(false);
                    jdGoods.setIdDel(false);
                    List<JdGoods> goodsList = Lists.newArrayList(jdGoods);
                    // 图片
                    jdGoodsManager.setPhoto(goodsList);
                    // 价格
                    jdGoodsManager.setPrice(goodsList);
                    // 分类
                    Map<Long, String> cateMap = jdCategoryDao.allMap();
                    jdGoodsManager.setCate(s.getCategory(), jdGoods, cateMap);
                    jdGoodsDao.put(jdGoods);
                    if (space != null)
                        jdGoodsSpaceDao.put(space);
                    break;
                }
                case 2:
                {
                    JdGoods jdGoods = jdGoodsDao.get(msg.getSkuId());
                    if (jdGoods != null)
                    {
                        jdGoods.setIdDel(true);
                        jdGoodsDao.put(jdGoods);
                    }
                    break;
                }
                default:
                    log.warn("[京东VOP-消息队列]商品池内商品添加、删除消息的state不合法：{}", msg.getState());
            }
        }
        catch (Exception e)
        {
            exceptionLog(SKU_ADD_DEL_NAME, e);
        }
    }
    
    private String specialTitle(String title)
    {
        List<String> t = new ArrayList<>();
        t.add("鲜京采");
        t.add("京东京造");
        t.add("京觅");
        t.add("京东");
        t.add("京东自营");
        t.add("京东物流");
        t.add("京东专供");
        t.add("京东金榜");
        t.add("京东超市");
        t.add("京鲜生");
        t.add("1号会员店");
        for(String k : t)
        {
            boolean contains = title.contains(k);
            if(contains)
            {
                title = title.replace(k, "");
            }
        }
        return title;
    }
    
    private static final String SKU_CHANGE_NAME = "商品信息变更";
    
    private void skuChangeMsg(JdVOPSkuChangeMsg msg)
    {
        try
        {
            startHandleLog(SKU_CHANGE_NAME, msg);
            JdGoods jdGoods = jdGoodsDao.get(msg.getSkuId());
            if (jdGoods != null)
            {
                // 详情
                GetSkuPoolInfoGoodsResp s = jdVOPGoodsManager.getSkuDetailInfo(jdGoods.getPkey(), Sets.newHashSet(3));
                String title = specialTitle(s.getSkuName());
                jdGoods.setTitle(title);
                jdGoods.setWeight(s.getWeight());
                jdGoods.setSaleUnit(s.getSaleUnit());
                jdGoods.setSeoModel(s.getSeoModel());
                jdGoods.setIntroduce(s.getIntroduce());
                jdGoods.setIntroducePc(s.getIntroducePc());
                jdGoods.setIntroduceApp(s.getIntroduceApp());
                jdGoods.setIntroduceWechat(s.getIntroduceWechat());
                jdGoods.setSkuState(s.getSkuState());
                jdGoods.setSpuId(s.getSpuId());
                jdGoods.setSpuName(s.getSpuName());
                List<JdGoods> goodsList = Lists.newArrayList(jdGoods);
                // 图片
                jdGoodsManager.setPhoto(goodsList);
                // 价格
                jdGoodsManager.setPrice(goodsList);
                jdGoodsDao.put(jdGoods);
            }
        }
        catch (Exception e)
        {
            exceptionLog(SKU_CHANGE_NAME, e);
        }
    }
    
    private static final String SKU_POOL_ADD_DEL_NAME = "商品池添加、删除";
    
    private void skuPoolAddDelMsg(JdVOPSkuPoolAddDelMsg msg)
    {
        try
        {
            log.info("[京东VOP-消息队列]不处理[{}]消息（处理单商品添加、删除消息），仅打印：{}", SKU_POOL_ADD_DEL_NAME, JsonUtil.toString(msg));
        }
        catch (Exception e)
        {
            exceptionLog(SKU_POOL_ADD_DEL_NAME, e);
        }
    }
    
    private static final String SKU_SALE_STATE_CHANGE_NAME = "商品可售状态";
    
    private void skuSaleStateChangeMsg(JdVOPSkuSaleStateChangeMsg msg)
    {
        try
        {
            startHandleLog(SKU_SALE_STATE_CHANGE_NAME, msg);
            JdGoods jdGoods = jdGoodsDao.get(msg.getSkuId());
            if (jdGoods == null || jdGoods.getIdDel())
            {
                log.info("[京东VOP-消息队列]sku（{}）不存在或已删除，不处理可售状态变更", msg.getSkuId());
                return;
            }
            List<CheckSkuSaleGoodsResp> list = jdVOPGoodsManager.checkSkuSaleList(Lists.newArrayList(msg.getSkuId()));
            if (CollectionUtil.isEmpty(list)) throw TofocusException.of(LejiaErrCode.REMOTE_REQUEST_ERROR, "找不到商品可售性");
            for (CheckSkuSaleGoodsResp item : list)
            {
                // saleState：是否可售，1：是，0：否
                if (item.getSkuId() == msg.getSkuId() && item.getSaleState() != 1)
                {
                    jdGoods.setIdDel(true);
                    jdGoodsDao.put(jdGoods);
                    log.info("[京东VOP-消息队列]sku（{}）已变更为不可售，已逻辑删除", msg.getSkuId());
                    JdGoodsUpdNotice notice = JdGoodsUpdNotice.saleStateOf(jdGoods);
                    notice.setDescription("商品不可售");
                    jdGoodsUpdNoticeDao.add(notice);
                    break;
                }
            }
        }
        catch (Exception e)
        {
            exceptionLog(SKU_SALE_STATE_CHANGE_NAME, e);
        }
    }
    
    private void startHandleLog(String name, Object msg)
    {
        log.info("[京东VOP-消息队列]开始处理[{}]消息：{}", name, JsonUtil.toString(msg));
    }
    
    private void exceptionLog(String name, Exception e)
    {
        log.error("[京东VOP-消息队列]处理[{}]消息异常：{}", name, e.getMessage());
        log.error("堆栈：", e);
    }
    
    @Override
    public void handleResult(String pipeId, String correlationId, Result<String> result)
        throws Exception
    {
        
    }
}
