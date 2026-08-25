package cn.tofocus.lejia.domain.v3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.v3.ProblemAppOnList;
import cn.tofocus.lejia.bean.dto.v3.ProblemOnInfo;
import cn.tofocus.lejia.bean.dto.v3.ProblemTypeOnInfo;
import cn.tofocus.lejia.bean.entity.applet.MktProblemEntity;
import cn.tofocus.lejia.bean.entity.applet.MktProblemTypeEntity;
import cn.tofocus.lejia.bean.entity.sys.SysAscription;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.applet.MktProblemDao;
import cn.tofocus.lejia.dao.applet.MktProblemTypeDao;
import cn.tofocus.lejia.dao.sys.SysAscriptionDao;
import cn.tofocus.lejia.exception.LejiaErrCode;

@Component
public class ProblemManager
{
    @Autowired
    private MktProblemDao problemDao;
    
    @Autowired
    private MktProblemTypeDao problemTypeDao;
    
    @Autowired
    private SysAscriptionDao ascriptionDao;
    
    
    public PageResult<ProblemOnInfo> queryProblem(int page, int pagesize, List<Integer> types, String content)
    {
        return problemDao.query(page, pagesize, types, content, CurrentSession.ascriptionPkey(), ProblemOnInfo.class);
    }
    
    public Boolean insProblem(ProblemOnInfo dto)
    {
        MktProblemEntity entity = BeanUtil.beanFrom(MktProblemEntity.class, dto);
        MktProblemTypeEntity problemType = problemTypeDao.get(dto.getType());
        if(Boolean.FALSE.equals(problemType != null))
            throw TofocusException.of(LejiaErrCode.PROBLEMTYPE_NOTEXIST);
        entity.setSort(problemType.getSort());
        entity.setEnabled(true);
        entity.setAscription(CurrentSession.ascriptionPkey());
        problemDao.add(entity);
        return true;
    }
    
    public Boolean updProblem(ProblemOnInfo dto)
    {
        if(dto.getPkey() == null)
            throw TofocusException.of();
        MktProblemEntity problem = problemDao.get(dto.getPkey());
        if(Boolean.TRUE.equals(problem.getIsDefault()))
            throw TofocusException.of(LejiaErrCode.DEFAULT_PROBLEMTYPE_ERROR);
        BeanUtils.copyProperties(dto, problem, "enabled");
        MktProblemTypeEntity problemType = problemTypeDao.get(dto.getType());
        if(Boolean.FALSE.equals(problemType != null))
            throw TofocusException.of(LejiaErrCode.PROBLEMTYPE_NOTEXIST);
        problem.setSort(problemType.getSort());
        problemDao.update(problem);
        return true;
    }
    
    public Boolean enabled(Integer pkey, Boolean enabled)
    {
        MktProblemEntity problem = problemDao.get(pkey);
        problem.setEnabled(enabled);
        problemDao.update(problem);
        return true;
    }
    
    public Boolean delProblem(Integer pkey)
    {
        MktProblemEntity problem = problemDao.get(pkey);
        if(Boolean.TRUE.equals(problem.getIsDefault()))
            throw TofocusException.of(LejiaErrCode.DEFAULT_PROBLEMTYPE_ERROR);
        return problemDao.removeById(pkey);
    }
    
    public PageResult<ProblemTypeOnInfo> queryProblemType(int page, int pagesize)
    {
        return problemTypeDao.query(page, pagesize, CurrentSession.ascriptionPkey(), ProblemTypeOnInfo.class);
    }
    
    public List<ProblemTypeOnInfo> listProblemType()
    {
        return problemTypeDao.list(CurrentSession.ascriptionPkey(), ProblemTypeOnInfo.class);
    }
    
    public Boolean insProblemType(ProblemTypeOnInfo dto)
    {
        String name = dto.getName();
        if("订单问题".equals(name) || "退款问题".equals(name) || "联系我们".equals(name))
            throw TofocusException.of(LejiaErrCode.PROBLEMTYPE_NAMEERROR);
        Integer ascription = CurrentSession.ascriptionPkey();
        Integer maxSort = problemTypeDao.getMaxSort(ascription);
        MktProblemTypeEntity entity = BeanUtil.beanFrom(MktProblemTypeEntity.class, dto);
        entity.setSort(maxSort + 1);
        entity.setAscription(ascription);
        problemTypeDao.add(entity);
        return true;
    }
    
    public Boolean updProblemType(ProblemTypeOnInfo dto)
    {
        String name = dto.getName();
        if("订单问题".equals(name) || "退款问题".equals(name) || "联系我们".equals(name))
            throw TofocusException.of(LejiaErrCode.PROBLEMTYPE_NOTUPDATED);
        MktProblemTypeEntity entity = problemTypeDao.get(dto.getPkey());
        BeanUtils.copyProperties(dto, entity, "sort");
        problemTypeDao.update(entity);
        return true;
    }
    
    public Boolean delProblemType(Integer pkey)
    {
        MktProblemTypeEntity entity = problemTypeDao.get(pkey);
        String name = entity.getName();
        if("订单问题".equals(name) || "退款问题".equals(name) || "联系我们".equals(name))
            throw TofocusException.of(LejiaErrCode.PROBLEMTYPE_NOTUPDATED);
        return problemTypeDao.removeById(pkey);
    }
    
    public List<ProblemAppOnList> getAppProblem(Integer ascription)
    {
        List<ProblemAppOnList> list = problemTypeDao.list(ascription, ProblemAppOnList.class);
        Iterator<ProblemAppOnList> iter = list.iterator();
        while(iter.hasNext())
        {
             ProblemAppOnList pa = iter.next();
            List<ProblemOnInfo> listApp = problemDao.listApp(pa.getPkey(), ascription, ProblemOnInfo.class);
            if(!listApp.isEmpty())
            {
                pa.setContent(listApp);
            }
            else
                iter.remove();
        }
        return list;
    }
    
    // 4.1.3 上线初始化 跑一次
    public void runProblemType()
    {
        List<SysAscription> list = ascriptionDao.findAll();
//        List<MktProblemTypeEntity> ptList = new ArrayList<>();
        List<MktProblemEntity> proList = new ArrayList<>();
        for(SysAscription sa : list)
        {
            MktProblemTypeEntity pt1 = new MktProblemTypeEntity();
            pt1.setName("订单问题");
            pt1.setSort(0);
            pt1.setAscription(sa.getPkey());
            pt1 = problemTypeDao.add(pt1);
            MktProblemEntity pro1 = assembleProblem(pt1.getPkey(), pt1.getAscription(), pt1.getSort(), "配送地址无法填入", "系统会判断收货地址是否满足市场可配送距离范围。如不满足，点击地址时无法选中，并会提示“当前配送地址超出市场配送范围”。");
            MktProblemEntity pro2 = assembleProblem(pt1.getPkey(), pt1.getAscription(), pt1.getSort(), "配送订单何时送达", "配送订单可选择配送时间。配送时间数据会同时发送至配送员，配送时间无法超出市场营业时间。");
            MktProblemEntity pro3 = assembleProblem(pt1.getPkey(), pt1.getAscription(), pt1.getSort(), "自提订单提货时间", "自提订单可选择配送时间。自提时间无法超出市场营业时间。");
            MktProblemEntity pro4 = assembleProblem(pt1.getPkey(), pt1.getAscription(), pt1.getSort(), "订单被取消了", "除砍价商品外，待支付订单只有10分钟支付时间，超过则会自动取消订单。");
            MktProblemEntity pro5 = assembleProblem(pt1.getPkey(), pt1.getAscription(), pt1.getSort(), "订单状态变为已完成", "情况①是市场已发货、已到货订单，次日00:10订单将会自动变为“已完成”状态；情况②是积分商城已发货、已到货订单，从提交订单时开始计算，10天后到期订单就会自动变为“已完成”状态；情况③是消费者在我的订单页面，点击该条订单【确认收货】按钮，订单即刻变为“已完成”状态。");
            
            
            MktProblemTypeEntity pt2 = new MktProblemTypeEntity();
            pt2.setName("退款问题");
            pt2.setSort(1);
            pt2.setAscription(sa.getPkey());
            pt2 = problemTypeDao.add(pt2);
            MktProblemEntity pro6 = assembleProblem(pt2.getPkey(), pt2.getAscription(), pt2.getSort(), "订单无法退款", "市场商城订单无线上退款功能。订单点击【申请退款】按钮，商城人员将会与消费者联系，确认退款内容进行线下退款操作。");
            
            MktProblemTypeEntity pt3 = new MktProblemTypeEntity();
            pt3.setName("联系我们");
            pt3.setSort(2);
            pt3.setAscription(sa.getPkey());
            pt3 = problemTypeDao.add(pt3);
            MktProblemEntity pro7 = assembleProblem(pt3.getPkey(), pt3.getAscription(), pt3.getSort(), "联系市场", "进入小程序首页，点击图标“" + "<img&nbsp;src='https://small.xinanshizu.com/file/v3/image/download?file=3793.png&code=DBEDC0CF59FDA7A4AB683E1DC61E7180'>" + "”，点击后弹出联系我们窗口。点击电话联系，自动进行跳转拨号页面。");
            
            proList.add(pro1);
            proList.add(pro2);
            proList.add(pro3);
            proList.add(pro4);
            proList.add(pro5);
            proList.add(pro6);
            proList.add(pro7);
        }
        problemDao.addAll(proList);
        System.out.println("runProblemType运行结束");
//        System.out.println("runProblemType 运行结束,新增" + ptList.size() + "条数据");
//        List<MktProblemTypeEntity> addAll = problemTypeDao.addAll(ptList);
       
//        for(MktProblemTypeEntity pt : addAll)
//        {
//            assembleProblem(pt.getPkey(), pt.getAscription(), pt.getSort(), "", "");
//        }
        // https://small.xinanshizu.com/file/v3/image/download?file=3793.png&code=DBEDC0CF59FDA7A4AB683E1DC61E7180
    }

    private MktProblemEntity assembleProblem(Integer type, Integer ascription, Integer sort, String name, String answer)
    {
        MktProblemEntity pro = new MktProblemEntity();
        pro.setType(type);
        pro.setEnabled(true);
        pro.setSort(sort);
        pro.setAscription(ascription);
        pro.setName(name);
        pro.setAnswer(answer);
        pro.setIsDefault(true);
        return pro;
    }
}

