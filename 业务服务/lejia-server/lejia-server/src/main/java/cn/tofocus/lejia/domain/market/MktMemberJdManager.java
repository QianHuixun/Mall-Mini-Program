//package cn.tofocus.lejia.domain.market;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.math.BigDecimal;
//import java.util.*;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.alibaba.excel.EasyExcel;
//import com.alibaba.excel.exception.ExcelAnalysisException;
//import com.google.common.collect.Lists;
//
//import cn.tofocus.common.util.BeanUtil;
//import cn.tofocus.common.util.StringUtil;
//import cn.tofocus.core.exception.SysErrCode;
//import cn.tofocus.core.exception.TofocusException;
//import cn.tofocus.core.page.PageResult;
//import cn.tofocus.db.redis.lock.RedisLockTemplate;
//import cn.tofocus.lejia.Constant;
//import cn.tofocus.lejia.bean.dto.app.market.MktAppMemberDetailsDTO;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdAdjustDTO;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdLineOnPage;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdOnPage;
//import cn.tofocus.lejia.bean.dto.market.jd.MktMemberJdTagDrop;
//import cn.tofocus.lejia.bean.entity.member.*;
//import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
//import cn.tofocus.lejia.bean.enums.JdOperationType;
//import cn.tofocus.lejia.core.CurrentSession;
//import cn.tofocus.lejia.dao.market.*;
//import cn.tofocus.lejia.dao.sys.SysFarmerDao;
//import cn.tofocus.lejia.excel.MktMemberJdExcel;
//import cn.tofocus.lejia.excel.MktMemberJdExcelTemplate;
//import cn.tofocus.lejia.excel.MktMemberJdLineExportExcel;
//import cn.tofocus.lejia.excel.listener.ImportMemberJdListener;
//import cn.tofocus.lejia.exception.LejiaErrCode;
//import cn.tofocus.lejia.util.ExportUtil;
//import cn.tofocus.lejia.utils.DateUtil;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Component
//public class MktMemberJdManager
//{
//    @Autowired
//    private MktMemberJdDao memberJdDao;
//    
//    @Autowired
//    private MktMemberJdLineDao memberJdLineDao;
//    
//    @Autowired
//    private MktMemberDao memberDao;
//    
//    @Autowired
//    private MktMemberTagDao memberTagDao;
//    
//    @Autowired
//    private MktTagDao tagDao;
//    
//    @Autowired
//    private SysFarmerDao sysFarmerDao;
//    
//    @Autowired
//    private MemberManager memberManger;
//    
//    @Autowired
//    private RedisLockTemplate lock;
//    
//    public List<MktMemberJdTagDrop> listTagDrop()
//    {
//        return memberJdDao.listTags(CurrentSession.ascriptionPkey());
//    }
//    
//    public PageResult<MktMemberJdOnPage> query(int page, int pagesize, String mobile, List<Integer> tags)
//    {
//        Integer ascription = CurrentSession.ascriptionPkey();
//        return memberJdDao.joinSelectPage()
//            .page(page)
//            .pagesize(pagesize)
//            .as(MktMemberJd.F.pkey, MktMemberJdOnPage.F.pkey)
//            .as(MktMemberJd.F.tag, MktMemberJdOnPage.F.tag)
//            .as(MktMemberJd.F.balance, MktMemberJdOnPage.F.balance)
//            .eq(MktMemberJd.F.ascription, ascription)
//            .in(MktMemberJd.F.tag, tags)
//            .join(MktMember.class, MktMemberJd.F.pkey, MktMember.F.pkey)
//            .as(MktMember.F.mobile, MktMemberJdOnPage.F.mobile)
//            .like(MktMember.F.mobile, mobile)
//            .endJoin()
//            .sort(MktMemberJd.F.pkey)
//            .exec(MktMemberJdOnPage.class);
//    }
//    
//    public boolean clearBalance(List<Integer> tags)
//    {
//        Integer ascription = CurrentSession.ascriptionPkey();
//        List<MktMemberJd> list = memberJdDao.listByTags(ascription, tags);
//        for (MktMemberJd bean : list)
//        {
//            // 清空余额，生成清空明细
//            clearMsdBalance(bean.getPkey());
//        }
//        return true;
//    }
//    
//    public boolean adjustBalance(MktMemberJdAdjustDTO dto)
//    {
//        Integer ascription = CurrentSession.ascriptionPkey();
//        MktMemberJd bean = memberJdDao.get(dto.getPkey(), ascription);
//        if (bean == null)
//            throw TofocusException.of(LejiaErrCode.DATA_INEXISTENCE, "找不到热力豆账户");
//        // 加/减余额，生成手动调整明细（其中减的已检查余额是否充足）
//        updJdBalance(bean.getPkey(),
//            bean.getTag(),
//            dto.getDirect(),
//            dto.getAmt(),
//            JdOperationType.MANUAL_ADJUST,
//            dto.getRemark(),
//            null,
//            ascription);
//        return true;
//    }
//    
//    public PageResult<MktMemberJdLineOnPage> queryLine(int page, int pagesize, String mobile, List<Integer> tags,
//        List<JdOperationType> operationTypes, String startDate, String endDate, String remark)
//    {
//        Integer ascription = CurrentSession.ascriptionPkey();
//        Date startTime = DateUtil.atStartOfDay(startDate);
//        Date endTime = DateUtil.atStartOfNextDay(endDate);
//        return memberJdLineDao.joinSelectPage()
//            .page(page)
//            .pagesize(pagesize)
//            .as(MktMemberJdLine.F.pkey, MktMemberJdLineOnPage.F.pkey)
//            .as(MktMemberJdLine.F.member, MktMemberJdLineOnPage.F.member)
//            .as(MktMemberJdLine.F.tag, MktMemberJdLineOnPage.F.tag)
//            .as(MktMemberJdLine.F.direct, MktMemberJdLineOnPage.F.direct)
//            .as(MktMemberJdLine.F.amt, MktMemberJdLineOnPage.F.amt)
//            .as(MktMemberJdLine.F.balance, MktMemberJdLineOnPage.F.balance)
//            .as(MktMemberJdLine.F.operationType, MktMemberJdLineOnPage.F.operationType)
//            .as(MktMemberJdLine.F.remark, MktMemberJdLineOnPage.F.remark)
//            .as(MktMemberJdLine.F.createdTime, MktMemberJdLineOnPage.F.createdTime)
//            .eq(MktMemberJdLine.F.ascription, ascription)
//            .in(MktMemberJdLine.F.tag, tags)
//            .in(MktMemberJdLine.F.operationType, operationTypes)
//            .ge(MktMemberJdLine.F.createdTime, startTime)
//            .lt(MktMemberJdLine.F.createdTime, endTime)
//            .like(MktMemberJdLine.F.remark, remark)
//            .join(MktMember.class, MktMemberJdLine.F.member, MktMember.F.pkey)
//            .as(MktMember.F.name, MktMemberJdLineOnPage.F.name)
//            .as(MktMember.F.mobile, MktMemberJdLineOnPage.F.mobile)
//            .like(MktMember.F.mobile, mobile)
//            .endJoin()
//            .sort(MktMemberJdLine.F.createdTime)
//            .sort(MktMemberJdLine.F.pkey)
//            .exec(MktMemberJdLineOnPage.class);
//    }
//    
//    public void downloadRechargeTemplate(HttpServletResponse response)
//    {
//        try
//        {
//            String excelName = "热力豆充值";
//            ExportUtil.exportData(MktMemberJdExcelTemplate.class,
//                Lists.newArrayList(new MktMemberJdExcelTemplate()),
//                response,
//                excelName,
//                excelName);
//        }
//        catch (Exception e)
//        {
//            log.error("下载充值模板异常", e);
//            throw TofocusException.of(LejiaErrCode.TEMPLATE_DOWNLOAD_ERROR);
//        }
//    }
//    
//    @Transactional(rollbackFor = Exception.class)
//    public void importRecharge(MultipartFile myfile, HttpServletResponse response)
//    {
//        Integer ascription = CurrentSession.ascriptionPkey();
//        Map<String, Integer> memberMobileMap = memberDao.map(ascription);
//        Map<String, Integer> tagNameMap = tagDao.map(ascription);
//        // 运营端下任意取一个启用的市场
//        SysFarmer farmer = sysFarmerDao.selectOne()
//            .notEq(SysFarmer.F.pkey, (Constant.Operation + ascription))
//            .eq(SysFarmer.F.ascription, ascription)
//            .eq(SysFarmer.F.enabled, true)
//            .eq(SysFarmer.F.idDel, false)
//            .sort(SysFarmer.F.pkey)
//            .exec();
//        if (farmer == null)
//            throw TofocusException.of(LejiaErrCode.MARKET_INEXISTENCE, "还未创建市场");
//        OutputStream out = null;
//        try (InputStream in = myfile.getInputStream())
//        {
//            List<MktMemberJdExcel> list = new ArrayList<>();
//            out = response.getOutputStream();
//            ExportUtil.setXlsxResponse(response, "错误数据.xlsx");
//            ImportMemberJdListener listener = new ImportMemberJdListener(list, memberMobileMap, tagNameMap, out);
//            EasyExcel.read(in, MktMemberJdExcel.class, listener).sheet().headRowNumber(1).doRead();
//            for (MktMemberJdExcel line : list)
//            {
//                // 如果手机号没有会员，新增一个会员
//                if (line.getMember() == null)
//                {
//                    log.info("手机号（{}）还没有注册会员，新增一条空会员", line.getMobile());
//                    MktAppMemberDetailsDTO entity = new MktAppMemberDetailsDTO();
//                    entity.setMobile(line.getMobile());
//                    entity.setName("会员用户");
//                    entity.setLastFarmer(farmer.getPkey());
//                    MktMember member = memberManger.insMember(entity, ascription);
//                    line.setMember(member.getPkey());
//                }
//                // 如果标签为空，在map里面再查一次，查不到新建一个标签
//                if (line.getTag() == null)
//                {
//                    Integer tag = tagNameMap.get(line.getTagName());
//                    if (tag == null)
//                    {
//                        MktTag tagBean = new MktTag();
//                        tagBean.setName(line.getTagName());
//                        tagBean.setAscription(ascription);
//                        tagBean = tagDao.add(tagBean);
//                        tag = tagBean.getPkey();
//                        tagNameMap.put(tagBean.getName(), tag);
//                    }
//                    line.setTag(tag);
//                }
//                // 查询原京东原标签
//                MktMemberJd oldBean = memberJdDao.get(line.getMember(), ascription);
//                // 加余额（如果没有京东账户，新建一个余额为0的账户），生成充值明细
//                updJdBalance(line.getMember(),
//                    line.getTag(),
//                    true,
//                    line.getAmt(),
//                    JdOperationType.RECHARGE,
//                    null,
//                    null,
//                    ascription);
//                if (oldBean == null || !Objects.equals(oldBean.getTag(), line.getTag()))
//                {
//                    if (oldBean != null)
//                    {
//                        // 京东账户改标签
//                        memberJdDao.updateTag(line.getMember(), ascription, line.getTag());
//                        // 用户去掉原标签
//                        MktMemberTag memberOldTag = memberTagDao.get(MktMemberTag.makePkey(line.getMember(), oldBean.getTag()));
//                        if (memberOldTag != null)
//                            memberTagDao.remove(memberOldTag);
//                    }
//                    // 如果该member还没有该标签，加上
//                    MktMemberTag memberTag = memberTagDao.get(MktMemberTag.makePkey(line.getMember(), line.getTag()));
//                    if (memberTag == null)
//                    {
//                        memberTag = new MktMemberTag();
//                        memberTag.setPkey(line.getMember(), line.getTag());
//                        memberTag.setAscription(ascription);
//                        memberTagDao.put(memberTag);
//                    }
//                }
//            }
//        }
//        catch (TofocusException e)
//        {
//            throw e;
//        }
//        catch (ExcelAnalysisException e)
//        {
//            if (e.getCause() != null)
//            {
//                if (e.getCause() instanceof TofocusException)
//                    throw e;
//                else
//                    throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e, "导入发生错误");
//                
//            }
//            else
//                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e, "导入发生错误");
//        }
//        catch (Exception e)
//        {
//            throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e, "导入发生错误");
//        }
//    }
//    
//    public void exportLine(String mobile, List<Integer> tags, List<JdOperationType> operationTypes, String startDate,
//        String endDate, String remark, HttpServletResponse response)
//    {
//        PageResult<MktMemberJdLineOnPage> res =
//            queryLine(0, 10000, mobile, tags, operationTypes, startDate, endDate, remark);
//        List<MktMemberJdLineExportExcel> list =
//            BeanUtil.beanListFrom(MktMemberJdLineExportExcel.class, res.getContent());
//        String excelName = "热力豆明细";
//        ExportUtil.exportData(MktMemberJdLineExportExcel.class, list, response, excelName, excelName);
//    }
//    
//    public void clearMsdBalance(int memberPkey)
//    {
//        try
//        {
//            lock.lock("zyysc", "zyysc-server", "memberJd" + memberPkey);// 业务锁
//            log.info("开始清空京东帐户（{}）", memberPkey);
//            MktMemberJd account = memberJdDao.get(memberPkey);
//            if (account == null)
//            {
//                log.info("京东账户（{}）找不到，无需清空", memberPkey);
//                return;
//            }
//            MktMemberJdLine line = new MktMemberJdLine();
//            line.setMember(memberPkey);
//            line.setDirect(Boolean.FALSE);
//            line.setOperationType(JdOperationType.CLEAR);
//            line.setTag(account.getTag());
//            line.setAmt(account.getBalance());
//            line.setAscription(account.getAscription());
//            account.setBalance(BigDecimal.ZERO);
//            account = memberJdDao.update(account);
//            line.setBalance(account.getBalance());
//            memberJdLineDao.add(line);
//            log.info("京东帐户（{}）清空完成", memberPkey);
//        }
//        finally
//        {
//            lock.unlock("zyysc", "zyysc-server", "memberJd" + memberPkey);
//        }
//    }
//    
//    public void updJdBalance(int memberPkey, Integer tag, boolean direct, BigDecimal amt,
//        JdOperationType operationType, String remark, String formId, Integer ascription)
//    {
//        try
//        {
//            lock.lock("zyysc", "zyysc-server", "memberJd" + memberPkey);// 业务锁
//            log.info("开始更新京东帐户（{}）", memberPkey);
//            if (StringUtil.isNotBlank(formId))
//            {
//                MktMemberJdLine old = memberJdLineDao.selectOne()
//                    .eq(MktMemberJdLine.F.formId, formId)
//                    .eq(MktMemberJdLine.F.operationType, operationType)
//                    .exec();
//                if (old != null)
//                    throw TofocusException.of(LejiaErrCode.WRONG_FORMID);
//            }
//            MktMemberJdLine line = new MktMemberJdLine();
//            line.setMember(memberPkey);
//            line.setTag(tag);
//            line.setDirect(direct);
//            line.setAmt(amt);
//            line.setOperationType(operationType);
//            line.setRemark(remark);
//            line.setFormId(formId);
//            line.setAscription(ascription);
//            MktMemberJd account = getMemberMsd(memberPkey, tag, ascription);
//            if (line.getDirect())
//            {
//                account.setBalance(account.getBalance().add(line.getAmt()));
//            }
//            else
//            {
//                if (account.getBalance().compareTo(line.getAmt()) < 0)
//                    throw TofocusException.of(LejiaErrCode.NO_MSD);
//                account.setBalance(account.getBalance().subtract(line.getAmt()));
//            }
//            memberJdDao.update(account);
//            if (line.getTag() == null)
//                line.setTag(account.getTag());
//            line.setBalance(account.getBalance());
//            memberJdLineDao.add(line);
//            log.info("京东帐户（{}）更新完成", memberPkey);
//        }
//        finally
//        {
//            lock.unlock("zyysc", "zyysc-server", "memberJd" + memberPkey);
//        }
//    }
//    
//    private MktMemberJd getMemberMsd(int pkey, Integer tag, int ascription)
//    {
//        MktMemberJd account = memberJdDao.get(pkey);
//        if (account == null)
//            account = initMemberJd(pkey, tag, ascription);
//        return account;
//    }
//    
//    private MktMemberJd initMemberJd(int pkey, Integer tag, int ascription)
//    {
//        MktMemberJd bean = new MktMemberJd();
//        bean.setPkey(pkey);
//        bean.setTag(tag);
//        bean.setBalance(BigDecimal.ZERO);
//        bean.setAscription(ascription);
//        bean = memberJdDao.add(bean);
//        return bean;
//    }
//}
