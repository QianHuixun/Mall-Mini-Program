package cn.tofocus.lejia.domain;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import cn.tofocus.common.util.StringUtil;
import com.alibaba.excel.exception.ExcelDataConvertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.jpa.dao.DaoHelper;
import cn.tofocus.lejia.bean.dto.MemberRechargeCardExcel;
import cn.tofocus.lejia.bean.dto.market.recharge.RechargeCardOnPage;
import cn.tofocus.lejia.bean.dto.market.recharge.RechargeCardSum;
import cn.tofocus.lejia.bean.entity.member.MktRechargeCard;
import cn.tofocus.lejia.bean.entity.member.MktTag;
import cn.tofocus.lejia.bean.enums.CommSourceType;
import cn.tofocus.lejia.bean.enums.RechargeStatus;
import cn.tofocus.lejia.bean.enums.member.RechargeCardType;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.market.MktRechargeCardDao;
import cn.tofocus.lejia.dao.market.MktTagDao;
import cn.tofocus.lejia.domain.market.MemberCommManager;
import cn.tofocus.lejia.exception.LejiaErrCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RechargeCardManager
{
    @Autowired
    private MktRechargeCardDao rechargeCardDao;
    
    @Autowired
    private MemberCommManager commManager;

    @Autowired
    private MktTagDao tagDao;

    private final static Integer MAX_ADD_NUM = 1000;
    
    private static SecureRandom secureRandom = new SecureRandom();
    @Autowired
    private DaoHelper daoHelper;
    
    public PageResult<RechargeCardOnPage> query(int page, int pagesize, List<String> types, String cardNumber,
        String status, String mobile, String createdStart, String createdEnd, String useStart, String useEnd)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if(StringUtils.isNotBlank(createdStart) && createdStart.length() < 11)
            createdStart = createdStart + " 00:00:00";
        if(StringUtils.isNotBlank(useStart) && useStart.length() < 11)
            useStart = useStart + " 00:00:00";

        if(StringUtils.isNotBlank(createdEnd) && createdEnd.length() < 11)
            createdEnd = createdEnd + " 23:59:59";
        if(StringUtils.isNotBlank(useEnd) && useEnd.length() < 11)
            useEnd = useEnd + " 23:59:59";
        
        PageResult<RechargeCardOnPage> res = rechargeCardDao.query(page,
            pagesize,
            types,
            cardNumber,
            status,
            mobile,
            createdStart,
            createdEnd,
            useStart,
            useEnd,
            ascription);
        return res;
    }
    
    public RechargeCardSum querySum(List<String> types, String cardNumber, String status, String mobile,
        String createdStart, String createdEnd, String useStart, String useEnd)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        if(StringUtils.isNotBlank(createdStart) && createdStart.length() < 11)
            createdStart = createdStart + " 00:00:00";
        if(StringUtils.isNotBlank(useStart) && useStart.length() < 11)
            useStart = useStart + " 00:00:00";

        if(StringUtils.isNotBlank(createdEnd) && createdEnd.length() < 11)
            createdEnd = createdEnd + " 23:59:59";
        if(StringUtils.isNotBlank(useEnd) && useEnd.length() < 11)
            useEnd = useEnd + " 23:59:59";
        List<RechargeCardSum> list = rechargeCardDao
            .querySum(types, cardNumber, status, mobile, createdStart, createdEnd, useStart, useEnd, ascription);
        RechargeCardSum res = new RechargeCardSum();
        if(list != null && !list.isEmpty())
        {
            list.forEach(e -> 
            {
                if(RechargeStatus.USED.equals(e.getStatus()))
                {
                    res.setUseNum(res.getUseNum() + e.getNum());
                    res.setSumUseCost(res.getSumUseCost().add(e.getSumCost()));
                }
                res.setNum(res.getNum() + e.getNum());
                res.setSumCost(res.getSumCost().add(e.getSumCost()));
            });
        }
        return res;
    }
    
    public Boolean add(BigDecimal cost, Integer num, Date deadline, RechargeCardType type, Integer tag)
    {
        if (num > MAX_ADD_NUM)
            throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "批量生成卡密数量一次不允许超过" + MAX_ADD_NUM);
        Integer ascription = CurrentSession.ascriptionPkey();
        String cPrefix = "BHMS";
        int cNum = 10;
        int pNum = 8;
        // 生成后查看一下 数据库是否重复 重复重新生成
        for (int i = 0; i < num; i++)
        {
            MktRechargeCard rc = new MktRechargeCard();
            rc.setType(type);
            rc.setStatus(RechargeStatus.UNUSED);
            rc.setCost(cost);
            rc.setDeadline(deadline);
            rc.setTag(tag);
            rc.setAscription(ascription);
            String cardNumber = cPrefix + generateRandomString(cNum, false);
            String cardPassword = generateRandomString(pNum, false);
            while (existCardNumber(cardNumber))
                cardNumber = generateRandomString(cNum, false);
            rc.setCardNumber(cardNumber);
            rc.setCardPassword(cardPassword);
            rechargeCardDao.add(rc);
        }
        return true;
    }

    private boolean existCardNumber(String cardNumber)
    {
        MktRechargeCard exist = rechargeCardDao.byCardNumber(cardNumber);
        return exist != null;
    }
    
//    private List<String> randomGenerate(Integer num)
//    {
//        List<String> res = new ArrayList<>();
//        for(int i = 0; i < num; i++)
//        {
//            String s = RandomStringUtils.randomAlphabetic(6);
//            res.add(s);
//        }
//        return res;
//    }
    
//    private String randomNum(int length) {
//        Random random = new Random();
//        int max = (int) Math.pow(10, length);
//        int tmp = random.nextInt(max);
//        while (tmp < max / 10)
//            tmp = random.nextInt(max);
//        return "" + tmp;
//    }
    
    
    
    public String generateRandomString(int length, boolean flag)
    {
        String charLower = "abcdefghijklmnopqrstuvwxyz";
        String charUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String dfrs;
        if(flag)
        {
            dfrs = charLower + charUpper + numbers;
        }
        else
            dfrs = numbers;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) 
        {
            int rndCharAt = secureRandom.nextInt(dfrs.length());
            char rndChar = dfrs.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }
    
    
    public Boolean cancel(List<String> keys)
    {
        if(keys == null || keys.isEmpty())
            return false;
        List<MktRechargeCard> list = rechargeCardDao.listPkey(keys, CurrentSession.ascriptionPkey());
        for(MktRechargeCard rc : list)
        {
            if(!RechargeStatus.UNUSED.equals(rc.getStatus()))
                throw TofocusException.of(LejiaErrCode.RECHARGECARD_UNUSED_CANCEL_ERROR);
            rc.setStatus(RechargeStatus.CANCEL);
        }
        rechargeCardDao.updateAll(list);
        return true;
    }
    
    public void importExcel(MultipartFile myfile, OutputStream out) throws Exception
    {
        ExcelReaderBuilder read;
        Integer ascription = CurrentSession.ascriptionPkey();
        List<MktRechargeCard> addList = new ArrayList<>();
        read = EasyExcel.read(myfile.getInputStream());
        read.head(MemberRechargeCardExcel.class);
        read.registerReadListener(new MemberRechargeCardListener(addList, out, ascription, rechargeCardDao, tagDao));
        read.headRowNumber(1);
        read.doReadAll();
        if (!addList.isEmpty())
        {
            rechargeCardDao.putAll(addList);
        }
    }

    @Slf4j
    static class MemberRechargeCardListener extends AnalysisEventListener<MemberRechargeCardExcel>
    {
        private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        
        public void validator(MemberRechargeCardExcel v)
        {
            try
            {
                Set<ConstraintViolation<MemberRechargeCardExcel>> set = validator.validate(v);
                if (set != null && !set.isEmpty())
                {
                    for (ConstraintViolation<MemberRechargeCardExcel> cv : set)
                    {
                        Field declaredField = v.getClass().getDeclaredField(cv.getPropertyPath().toString());
                        ExcelProperty annotation = declaredField.getAnnotation(ExcelProperty.class);
                        throw TofocusException.of(SysErrCode.REQUIRED_PRARAM_ERROR,
                            annotation.value()[0] + cv.getMessage());
                    }
                }
            }
            catch (NoSuchFieldException | SecurityException e)
            {
                throw TofocusException.of(SysErrCode.UNKNOW_INTER_FAIL, e);
            }
        }
        
        MemberRechargeCardListener(List<MktRechargeCard> addList, OutputStream out, 
            Integer ascription, MktRechargeCardDao rechargeCardDao, MktTagDao tagDao)
        {
            this.out = out;
            this.addList = addList;
            this.ascription = ascription;
            this.rechargeCardDao = rechargeCardDao;
            this.tagDao = tagDao;
        }
        
        List<MemberRechargeCardExcel> errList = new ArrayList<>();
        
        List<MktRechargeCard> addList;
        
        ExcelWriterBuilder errBuilder;
        
        Integer ascription;
        
        OutputStream out;

        MktRechargeCardDao rechargeCardDao;

        MktTagDao tagDao;

        Map<String, RechargeCardType> typeNameMap = RechargeCardType.nameMap();
        
        List<String> existNumber = new ArrayList<>();
        
        @Override
        public void invoke(MemberRechargeCardExcel data, AnalysisContext context)
        {
            try
            {
                validator(data);
                // 卡号查重
                if (existNumber.contains(data.getCardNumber()))
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卡号重复");
                MktRechargeCard exist = rechargeCardDao.byCardNumber(data.getCardNumber());
                if (exist != null)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "卡号重复");
                // 检查卡类型
                RechargeCardType type = typeNameMap.get(data.getTypeName());
                if (type == null)
                    throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "错误的卡类型");
                // 检查标签
                if (type == RechargeCardType.MSD && StringUtil.isBlank(data.getTagName()))
                    throw TofocusException.of(LejiaErrCode.PARAM_NULL_ERR, "热力豆充值的标签不能为空");
                Integer tagPkey = null;
                if (StringUtil.isNotBlank(data.getTagName()))
                {
                    MktTag tag = tagDao.getByName(data.getTagName(), ascription);
                    if (tag == null)
                        throw TofocusException.of(LejiaErrCode.PARAM_VALUE_ERR, "标签不存在");
                    tagPkey = tag.getPkey();
                }
                MktRechargeCard rc = BeanUtil.beanFrom(MktRechargeCard.class, data);
                rc.setType(type);
                rc.setTag(tagPkey);
                rc.setStatus(RechargeStatus.UNUSED);
                rc.setAscription(ascription);
                addList.add(rc);
                existNumber.add(rc.getCardNumber());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                String errmsg;
                if (e instanceof TofocusException)
                {
                    errmsg = e.getMessage();
                }
                else if (e instanceof ExcelDataConvertException)
                {
                    errmsg = "数据格式异常!";
                }
                else
                {
                    errmsg = e.getClass().getSimpleName() + ":" + e.getMessage();
                }
                data.setErrMsg(errmsg);
                errList.add(data);
            }
        }
        
        @Override
        public void doAfterAllAnalysed(AnalysisContext context)
        {
            if (!errList.isEmpty())
            {
                errBuilder = EasyExcel.write(out, MemberRechargeCardExcel.class);
                ExcelWriter errWriter = errBuilder.build();
                WriteSheet errSheet = EasyExcel.writerSheet("错误数据").build();
                errWriter.write(errList, errSheet);
                errWriter.finish();
            }
        }
    }
    
    public Boolean rechargeCard(String cardNumber, String cardPassword, int memberPkey, String mobile, Integer ascription)
    {
        MktRechargeCard card = rechargeCardDao.byCardNumber(cardNumber);
        if(card == null || card.getType() != RechargeCardType.NORMAL)
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_NUMBER_ERROR);
        Calendar cal = Calendar.getInstance();
        if(card.getDeadline().compareTo(cal.getTime()) < 0)
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_DEADLINE_ERROR);
        if(card.getAscription() != ascription)
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_ASCRIPTION_ERROR);
        if(!card.getCardPassword().equals(cardPassword))
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_CARDPASSWORD_ERROR);
        if(!RechargeStatus.UNUSED.equals(card.getStatus()))
            throw TofocusException.of(LejiaErrCode.RECHARGECARD_UNUSED_ERROR);
        card.setUseTime(cal.getTime());
        card.setStatus(RechargeStatus.USED);
        card.setMobile(mobile);
        rechargeCardDao.update(card);
        commManager.updComm(memberPkey, card.getCost(), true, CommSourceType.RECHARGE_CARD, card.getCardNumber(), ascription);
        return true;
    }
}
