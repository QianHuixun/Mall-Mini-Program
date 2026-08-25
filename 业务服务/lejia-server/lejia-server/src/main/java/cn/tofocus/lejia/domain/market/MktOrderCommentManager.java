package cn.tofocus.lejia.domain.market;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.config.AscriptionConfig;
import cn.tofocus.lejia.bean.dto.market.MktOrderCommentConfigDTO;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentInfo;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentOnList;
import cn.tofocus.lejia.bean.dto.market.MktOrderGoodsCommentReplyDTO;
import cn.tofocus.lejia.bean.entity.market.MktOrder;
import cn.tofocus.lejia.bean.entity.market.MktOrderGoodsComment;
import cn.tofocus.lejia.bean.entity.member.MktMember;
import cn.tofocus.lejia.bean.enums.CommentApplyStatus;
import cn.tofocus.lejia.bean.enums.CommentReplyStatus;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktOrderGoodsCommentDao;
import cn.tofocus.lejia.dao.sys.SysDynamicAttributeDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MktOrderCommentManager
{
    @Autowired
    private SysDynamicAttributeDao dynamicAttributeDao;
    
    @Autowired
    private MktOrderGoodsCommentDao orderGoodsCommentDao;
    
    public MktOrderCommentConfigDTO getConfig()
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if (ascription == null)
            throw TofocusException.of(LejiaErrCode.NOT_RIGHT);
        AscriptionConfig ascriptionConfig =
            dynamicAttributeDao.getFarmerAttribute(AscriptionConfig.class, ascription, Constant.Operation + ascription);
        if (ascriptionConfig == null)
            ascriptionConfig = new AscriptionConfig();
        MktOrderCommentConfigDTO res = new MktOrderCommentConfigDTO();
        res.setEnableComment(ascriptionConfig.getEnableComment());
        return res;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean setConfig(MktOrderCommentConfigDTO dto)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String companyPkey = CurrentSession.companyPkey();
        String farmerPkey = CurrentSession.marketPkey();
        if (ascription == null || farmerPkey == null)
            throw TofocusException.of(LejiaErrCode.MEMBER_NOT_LOGIN);
        AscriptionConfig ascriptionConfig =
            dynamicAttributeDao.getFarmerAttribute(AscriptionConfig.class, ascription, Constant.Operation + ascription);
        if (ascriptionConfig == null)
            ascriptionConfig = new AscriptionConfig();
        ascriptionConfig.setEnableComment(dto.getEnableComment());
        dynamicAttributeDao.setFarmerAttribute(ascriptionConfig, ascription, companyPkey, farmerPkey);
        return true;
    }
    
    public PageResult<MktOrderGoodsCommentOnList> query(int page, int pagesize, String memberMobile, String orderCode,
        String goodsName, CommentReplyStatus replyStatus, CommentApplyStatus applyStatus)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        String currentFarmer = CurrentSession.marketPkey();
        return orderGoodsCommentDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktOrderGoodsComment.F.pkey)
            .as(MktOrderGoodsComment.F.goodsName)
            .as(MktOrderGoodsComment.F.score)
            .as(MktOrderGoodsComment.F.content)
            .as(MktOrderGoodsComment.F.photo)
            .as(MktOrderGoodsComment.F.replyStatus)
            .as(MktOrderGoodsComment.F.applyStatus)
            .as(MktOrderGoodsComment.F.createdTime)
            .eq(MktOrderGoodsComment.F.ascription, ascription)
            .eq(MktOrderGoodsComment.F.farmer, currentFarmer)
            .like(MktOrderGoodsComment.F.goodsName, goodsName)
            .eq(MktOrderGoodsComment.F.replyStatus, replyStatus)
            .eq(MktOrderGoodsComment.F.applyStatus, applyStatus)
            .join(MktOrder.class, MktOrderGoodsComment.F.orderPkey, MktOrder.F.pkey)
                .as(MktOrder.F.code, MktOrderGoodsCommentOnList.F.orderCode)
                .like(MktOrder.F.code, orderCode)
            .join(MktMember.class, MktOrderGoodsComment.F.member, MktMember.F.pkey)
                .as(MktMember.F.mobile, MktOrderGoodsCommentOnList.F.memberMobile)
                .like(MktMember.F.mobile, memberMobile)
            .endJoin()
            .sort(MktOrderGoodsComment.F.createdTime)
            .sort(MktOrderGoodsComment.F.pkey)
            .exec(MktOrderGoodsCommentOnList.class);
    }
    
    public MktOrderGoodsCommentInfo get(Integer pkey)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktOrderGoodsCommentInfo res = orderGoodsCommentDao.selectOne()
            .eq(MktOrderGoodsComment.F.pkey, pkey)
            .eq(MktOrderGoodsComment.F.ascription, ascription)
            .execDto(MktOrderGoodsCommentInfo.class);
        if (res == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到评价");
        return res;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean reply(MktOrderGoodsCommentReplyDTO dto)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktOrderGoodsComment bean = orderGoodsCommentDao.selectOne()
            .eq(MktOrderGoodsComment.F.pkey, dto.getPkey())
            .eq(MktOrderGoodsComment.F.ascription, ascription)
            .exec();
        if (bean == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到评价");
        bean.setReplyContent(dto.getReplyContent());
        bean.setReplyStatus(CommentReplyStatus.REPLIED);
        bean.setReplyTime(new Date());
        orderGoodsCommentDao.put(bean);
        return true;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public boolean batchApply(List<Integer> pkeys, CommentApplyStatus applyStatus)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        orderGoodsCommentDao.updateApplyStatus(pkeys, ascription, applyStatus);
        return true;
    }
}
