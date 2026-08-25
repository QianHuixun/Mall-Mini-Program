package cn.tofocus.lejia.domain.market;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import cn.tofocus.common.util.CollectionUtil;
import cn.tofocus.lejia.bean.enums.member.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.google.common.collect.Lists;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.common.util.StringUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.redis.lock.RedisLockTemplate;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdAdjustDTO;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdLineOnPage;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdOnPage;
import cn.tofocus.lejia.bean.dto.market.MktMemberMsdTagDrop;
import cn.tofocus.lejia.bean.entity.member.*;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.enums.MsdOperationType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.*;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.excel.MktMemberMsdExcel;
import cn.tofocus.lejia.excel.MktMemberMsdExcelTemplate;
import cn.tofocus.lejia.excel.MktMemberMsdExportExcel;
import cn.tofocus.lejia.excel.MktMemberMsdLineExportExcel;
import cn.tofocus.lejia.excel.listener.ImportMemberMsdListener;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import cn.tofocus.lejia.util.ExportUtil;
import cn.tofocus.lejia.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MktMemberMsdManager
{
    @Autowired
    private MktMemberMsdDao memberMsdDao;
    
    @Autowired
    private MktMemberMsdLineDao memberMsdLineDao;
    
    @Autowired
    private MktMemberDao memberDao;
    
    @Autowired
    private MktMemberTagDao memberTagDao;
    
    @Autowired
    private MktTagDao tagDao;
    
    @Autowired
    private SysFarmerDao sysFarmerDao;
    
    @Autowired
    private MemberManager memberManger;
    
    @Autowired
    private RedisLockTemplate lock;

    @Deprecated
    public List<MktMemberMsdTagDrop> listTagDrop()
    {
        return memberMsdDao.listTags(CurrentSession.ascriptionPkey());
    }
    
    public PageResult<MktMemberMsdOnPage> query(int page, int pagesize, String mobile, List<Integer> tags)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        return memberMsdDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktMemberMsd.F.pkey, MktMemberMsdOnPage.F.pkey)
            .as(MktMemberMsd.F.tag, MktMemberMsdOnPage.F.tag)
            .as(MktMemberMsd.F.balance, MktMemberMsdOnPage.F.balance)
            .eq(MktMemberMsd.F.ascription, ascription)
            .in(MktMemberMsd.F.tag, tags)
            .join(MktMember.class, MktMemberMsd.F.pkey, MktMember.F.pkey)
            .as(MktMember.F.mobile, MktMemberMsdOnPage.F.mobile)
            .like(MktMember.F.mobile, mobile)
            .endJoin()
            .sort(MktMemberMsd.F.pkey)
            .exec(MktMemberMsdOnPage.class);
    }
    
    public boolean clearBalance(List<Integer> tags)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktMemberMsd> list = memberMsdDao.listByTags(ascription, tags);
        for (MktMemberMsd bean : list)
        {
            // 清空余额，生成清空明细
            clearMsdBalance(bean.getPkey());
        }
        return true;
    }
    
    public boolean adjustBalance(MktMemberMsdAdjustDTO dto)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        MktMemberMsd bean = memberMsdDao.get(dto.getPkey(), ascription);
        if (bean == null)
            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到热力豆账户");
        // 加/减余额，生成手动调整明细（其中减的已检查余额是否充足）
        updMsdBalance(bean.getPkey(),
            bean.getTag(),
            dto.getDirect(),
            dto.getAmt(),
            MsdOperationType.MANUAL_ADJUST,
            dto.getRemark(),
            null,
            ascription,
            false);
        return true;
    }
    
    public PageResult<MktMemberMsdLineOnPage> queryLine(int page, int pagesize, String mobile, List<Integer> tags,
        List<MsdOperationType> operationTypes, String startDate, String endDate, String remark)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Date startTime = DateUtil.atStartOfDay(startDate);
        Date endTime = DateUtil.atStartOfNextDay(endDate);
        return memberMsdLineDao.joinSelectPage()
            .page(page)
            .pagesize(pagesize)
            .as(MktMemberMsdLine.F.pkey, MktMemberMsdLineOnPage.F.pkey)
            .as(MktMemberMsdLine.F.member, MktMemberMsdLineOnPage.F.member1)
            .as(MktMemberMsdLine.F.tag, MktMemberMsdLineOnPage.F.tag)
            .as(MktMemberMsdLine.F.direct, MktMemberMsdLineOnPage.F.direct)
            .as(MktMemberMsdLine.F.amt, MktMemberMsdLineOnPage.F.amt)
            .as(MktMemberMsdLine.F.balance, MktMemberMsdLineOnPage.F.balance)
            .as(MktMemberMsdLine.F.operationType, MktMemberMsdLineOnPage.F.operationType)
            .as(MktMemberMsdLine.F.remark, MktMemberMsdLineOnPage.F.remark)
            .as(MktMemberMsdLine.F.createdTime, MktMemberMsdLineOnPage.F.createdTime)
            .eq(MktMemberMsdLine.F.ascription, ascription)
            .in(MktMemberMsdLine.F.tag, tags)
            .in(MktMemberMsdLine.F.operationType, operationTypes)
            .ge(MktMemberMsdLine.F.createdTime, startTime)
            .lt(MktMemberMsdLine.F.createdTime, endTime)
            .like(MktMemberMsdLine.F.remark, remark)
            .join(MktMember.class, MktMemberMsdLine.F.member, MktMember.F.pkey)
            .as(MktMember.F.name, MktMemberMsdLineOnPage.F.name)
            .as(MktMember.F.mobile, MktMemberMsdLineOnPage.F.mobile)
            .like(MktMember.F.mobile, mobile)
            .endJoin()
            .sort(MktMemberMsdLine.F.createdTime)
            .sort(MktMemberMsdLine.F.pkey)
            .exec(MktMemberMsdLineOnPage.class);
    }
    
    public void downloadRechargeTemplate(HttpServletResponse response)
    {
        try
        {
            String excelName = "热力豆充值";
            ExportUtil.exportData(MktMemberMsdExcelTemplate.class,
                Lists.newArrayList(new MktMemberMsdExcelTemplate()),
                response,
                excelName,
                excelName);
        }
        catch (Exception e)
        {
            log.error("下载充值模板异常", e);
            throw TofocusException.of(LejiaErrCode.TEMPLATE_DOWNLOAD_ERROR);
        }
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void importRecharge(MultipartFile myfile, HttpServletResponse response)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        Map<String, Integer> memberMobileMap = memberDao.map(ascription);
        Map<String, Integer> tagNameMap = tagDao.map(ascription);
        // 运营端下任意取一个启用的市场
        SysFarmer farmer = sysFarmerDao.selectOne()
            .notEq(SysFarmer.F.pkey, (Constant.Operation + ascription))
            .eq(SysFarmer.F.ascription, ascription)
            .eq(SysFarmer.F.enabled, true)
            .eq(SysFarmer.F.idDel, false)
            .sort(SysFarmer.F.pkey)
            .exec();
        if (farmer == null)
            throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE, "还未创建市场");
        OutputStream out = null;
        try (InputStream in = myfile.getInputStream())
        {
            List<MktMemberMsdExcel> list = new ArrayList<>();
            Set<String> repeatMobiles = new HashSet<>();
            out = response.getOutputStream();
            ExportUtil.setXlsxResponse(response, "错误数据.xlsx");
            ImportMemberMsdListener listener = new ImportMemberMsdListener(list, memberMobileMap, tagNameMap, repeatMobiles, out);
            EasyExcel.read(in, MktMemberMsdExcel.class, listener).sheet().headRowNumber(1).doRead();
            // 表内手机号重复，报错
            if (!repeatMobiles.isEmpty())
                throw TofocusException.of(LejiaErrCode.IMPORT_ERROR,
                    "批量充值失败，表格内手机号重复：" + CollectionUtil.list2String(repeatMobiles, "、"));
            // 记录这次用到的非新增标签pkey，将标签类型设为民生豆标签
            Set<Integer> usedOldTagKeys = new HashSet<>();
            for (MktMemberMsdExcel line : list)
            {
                boolean isNewTag = false;
                // 如果手机号没有会员，新增一个会员
                if (line.getMember() == null)
                {
                    log.info("手机号（{}）还没有注册会员，新增一条空会员", line.getMobile());
                    MktAppMemberDetailsDTO entity = new MktAppMemberDetailsDTO();
                    entity.setMobile(line.getMobile());
                    entity.setName("会员用户");
                    entity.setLastFarmer(farmer.getPkey());
                    MktMember member = memberManger.insMember(entity, ascription);
                    line.setMember(member.getPkey());
                }
                // 如果标签为空，在map里面再查一次，查不到新建一个标签
                if (line.getTag() == null)
                {
                    Integer tag = tagNameMap.get(line.getTagName());
                    if (tag == null)
                    {
                        MktTag tagBean = new MktTag();
                        tagBean.setType(TagType.MSD);
                        tagBean.setName(line.getTagName());
                        tagBean.setAscription(ascription);
                        tagBean.setIdDel(false);
                        tagBean = tagDao.add(tagBean);
                        tag = tagBean.getPkey();
                        tagNameMap.put(tagBean.getName(), tag);
                        isNewTag = true;
                    }
                    line.setTag(tag);
                }
                // 查询原民生豆原标签
                MktMemberMsd oldBean = memberMsdDao.get(line.getMember(), ascription);
                // 加余额（如果没有民生豆账户，新建一个余额为0的账户），生成充值明细
                updMsdBalance(line.getMember(),
                    line.getTag(),
                    true,
                    line.getAmt(),
                    MsdOperationType.RECHARGE,
                    null,
                    null,
                    ascription,
                    false);
                if (oldBean == null || !Objects.equals(oldBean.getTag(), line.getTag()))
                {
                    if (oldBean != null)
                    {
                        // 民生豆账户改标签
                        memberMsdDao.updateTag(line.getMember(), ascription, line.getTag());
                        // 用户去掉原标签
                        if (oldBean.getTag() != null)
                        {
                            MktMemberTag memberOldTag =
                                memberTagDao.get(MktMemberTag.makePkey(line.getMember(), oldBean.getTag()));
                            if (memberOldTag != null)
                                memberTagDao.remove(memberOldTag);
                        }
                    }
                    // 如果该member还没有该标签，加上
                    MktMemberTag memberTag = memberTagDao.get(MktMemberTag.makePkey(line.getMember(), line.getTag()));
                    if (memberTag == null)
                    {
                        memberTag = new MktMemberTag();
                        memberTag.setPkey(line.getMember(), line.getTag());
                        memberTag.setAscription(ascription);
                        memberTagDao.put(memberTag);
                    }
                    if (!isNewTag) usedOldTagKeys.add(line.getTag());
                }
            }
            // 将这次用到的非新增标签的类型设为民生豆标签
            if (!usedOldTagKeys.isEmpty())
                tagDao.updateType(usedOldTagKeys, TagType.MSD);
        }
        catch (TofocusException e)
        {
            throw e;
        }
        catch (ExcelAnalysisException e)
        {
            if (e.getCause() != null)
            {
                if (e.getCause() instanceof TofocusException)
                    throw e;
                else
                    throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e, "导入发生错误");
                
            }
            else
                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e, "导入发生错误");
        }
        catch (Exception e)
        {
            throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e, "导入发生错误");
        }
    }
    
    public void export(String mobile, List<Integer> tags, HttpServletResponse response)
    {
        PageResult<MktMemberMsdOnPage> res = query(0, 10000, mobile, tags);
        List<MktMemberMsdExportExcel> list =
            BeanUtil.beanListFrom(MktMemberMsdExportExcel.class, res.getContent());
        String excelName = "热力豆账户";
        ExportUtil.exportData(MktMemberMsdExportExcel.class, list, response, excelName, excelName);
    }

    public void exportLine(String mobile, List<Integer> tags, List<MsdOperationType> operationTypes, String startDate,
        String endDate, String remark, HttpServletResponse response)
    {
        PageResult<MktMemberMsdLineOnPage> res =
            queryLine(0, 10000, mobile, tags, operationTypes, startDate, endDate, remark);
        List<MktMemberMsdLineExportExcel> list =
            BeanUtil.beanListFrom(MktMemberMsdLineExportExcel.class, res.getContent());
        String excelName = "热力豆明细";
        ExportUtil.exportData(MktMemberMsdLineExportExcel.class, list, response, excelName, excelName);
    }
    
    public void clearMsdBalance(int memberPkey)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberMsd" + memberPkey);// 业务锁
            log.info("开始清空民生豆帐户（{}）", memberPkey);
            MktMemberMsd account = memberMsdDao.get(memberPkey);
            if (account == null)
            {
                log.info("民生豆账户（{}）找不到，无需清空", memberPkey);
                return;
            }
            MktMemberMsdLine line = new MktMemberMsdLine();
            line.setMember(memberPkey);
            line.setDirect(Boolean.FALSE);
            line.setOperationType(MsdOperationType.CLEAR);
            line.setTag(account.getTag());
            line.setAmt(account.getBalance());
            line.setAscription(account.getAscription());
            account.setBalance(BigDecimal.ZERO);
            account = memberMsdDao.update(account);
            line.setBalance(account.getBalance());
            memberMsdLineDao.add(line);
            log.info("民生豆帐户（{}）清空完成", memberPkey);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberMsd" + memberPkey);
        }
    }
    
    // jdOrder ture 是京东京东
    public void updMsdBalance(int memberPkey, Integer tag, boolean direct, BigDecimal amt,
        MsdOperationType operationType, String remark, String formId, Integer ascription, Boolean jdOrder)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberMsd" + memberPkey);// 业务锁
            log.info("开始更新民生豆帐户（{}）", memberPkey);
            if (StringUtil.isNotBlank(formId) && Boolean.TRUE.equals(jdOrder))
            {
                MktMemberMsdLine old = memberMsdLineDao.selectOne()
                    .eq(MktMemberMsdLine.F.formId, formId)
                    .eq(MktMemberMsdLine.F.operationType, operationType)
                    .exec();
                if (old != null)
                    throw TofocusException.of(LejiaErrCode.WRONG_FORMID);
            }
            MktMemberMsdLine line = new MktMemberMsdLine();
            line.setMember(memberPkey);
            line.setTag(tag);
            line.setDirect(direct);
            line.setAmt(amt);
            line.setOperationType(operationType);
            line.setRemark(remark);
            line.setFormId(formId);
            line.setAscription(ascription);
            MktMemberMsd account = getMemberMsd(memberPkey, tag, ascription);
            if (line.getDirect())
            {
                account.setBalance(account.getBalance().add(line.getAmt()));
            }
            else
            {
                if (account.getBalance().compareTo(line.getAmt()) < 0)
                    throw TofocusException.of(LejiaErrCode.NO_MSD);
                account.setBalance(account.getBalance().subtract(line.getAmt()));
            }
            memberMsdDao.update(account);
            if (line.getTag() == null)
                line.setTag(account.getTag());
            line.setBalance(account.getBalance());
            memberMsdLineDao.add(line);
            log.info("民生豆帐户（{}）更新完成", memberPkey);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberMsd" + memberPkey);
        }
    }
    
    // 组合支付先锁金额 成功后扣除
    public void updLockMsd(int memberPkey, BigDecimal comm, Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberMsd" + memberPkey);// 业务锁
            log.info("组合支付锁定金额-开始更新民生豆帐户（{}）", memberPkey);
            MktMemberMsd msd = getMemberMsd(memberPkey, null, ascription);
            if (msd.getBalance().compareTo(comm) < 0)
            {
                throw TofocusException.of(WsaleErrCode.NO_COMMS);
            }
            msd.setBalance(msd.getBalance().subtract(comm));
            if(msd.getLockMsd() == null)
                msd.setLockMsd(BigDecimal.ZERO);
            msd.setLockMsd(msd.getLockMsd().add(comm));
            memberMsdDao.update(msd);
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberMsd" + memberPkey);
        }
    }
    
    // 组合支付成功后扣除
    public void updMsd(int memberPkey, BigDecimal comm, String formId, 
        Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberMsd" + memberPkey);// 业务锁
            System.out.println("组合支付成功-开始更新民生豆帐户");
           
            MktMemberMsdLine old = memberMsdLineDao.selectOne()
                .eq(MktMemberMsdLine.F.formId, formId)
                .eq(MktMemberMsdLine.F.operationType, MsdOperationType.CONSUME)
                .exec();
            if (old != null)
                throw TofocusException.of(LejiaErrCode.WRONG_FORMID);
            
            MktMemberMsdLine line = new MktMemberMsdLine();
            line.setMember(memberPkey);
            line.setTag(null);
            line.setDirect(false);
            line.setAmt(comm);
            line.setOperationType(MsdOperationType.CONSUME);
            line.setRemark(null);
            line.setFormId(formId);
            line.setAscription(ascription);
            MktMemberMsd msd = getMemberMsd(memberPkey, null, ascription);
            if (msd.getLockMsd().compareTo(line.getAmt()) < 0)
            {
                throw TofocusException.of(WsaleErrCode.NO_COMMS);
            }
            msd.setLockMsd(msd.getLockMsd().subtract(line.getAmt()));
            memberMsdDao.update(msd);
            line.setBalance(msd.getBalance());
            memberMsdLineDao.add(line);
            System.out.println("组合支付成功-民生豆帐户更新完成");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberMsd" + memberPkey);
        }
    }
    
    
    // 组合支付失败处理
    public void updMsdPayFail(int memberPkey, Integer tag, BigDecimal comm, 
        Integer ascription)
    {
        try
        {
            lock.lock("zyysc", "zyysc-server", "memberMsd" + memberPkey);// 业务锁
            System.out.println("组合支付失败处理-开始更新民生豆帐户");
            MktMemberMsd msd = getMemberMsd(memberPkey, tag, ascription);
            msd.setBalance(msd.getBalance().add(comm));
            msd.setLockMsd(msd.getLockMsd().subtract(comm));
            memberMsdDao.update(msd);
            System.out.println("组合支付失败处理-民生豆帐户更新完成");
        }
        finally
        {
            lock.unlock("zyysc", "zyysc-server", "memberMsd" + memberPkey);
        }
    }
    
    private MktMemberMsd getMemberMsd(int pkey, Integer tag, int ascription)
    {
        MktMemberMsd account = memberMsdDao.get(pkey);
        if (account == null)
            account = initMemberMsd(pkey, tag, ascription);
        return account;
    }
    
    private MktMemberMsd initMemberMsd(int pkey, Integer tag, int ascription)
    {
        MktMemberMsd bean = new MktMemberMsd();
        bean.setPkey(pkey);
        bean.setTag(tag);
        bean.setBalance(BigDecimal.ZERO);
        bean.setLockMsd(BigDecimal.ZERO);
        bean.setAscription(ascription);
        bean = memberMsdDao.add(bean);
        return bean;
    }
}
