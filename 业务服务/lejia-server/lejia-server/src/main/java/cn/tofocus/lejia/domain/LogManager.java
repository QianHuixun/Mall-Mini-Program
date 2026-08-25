package cn.tofocus.lejia.domain;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.lejia.bean.dto.sys.SysLogOnList;
import cn.tofocus.lejia.bean.entity.sys.SysCompany;
import cn.tofocus.lejia.bean.entity.sys.SysFarmer;
import cn.tofocus.lejia.bean.entity.sys.SysLog;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.CurrentSession;
import cn.tofocus.lejia.dao.sys.SysCompanyDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.sys.SysLogDao;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LogManager
{
    @Autowired
    private SysLogDao sysLogDao;
    
    @Autowired
    private SysFarmerDao farmerDao;
    
    @Autowired
    private SysCompanyDao companyDao;
    
    public PageResult<SysLogOnList> queryLog(int page, int pagesize, String startTime, String endTime)
    {
        Integer ascription = CurrentSession.ascriptionPkey();
        PageResult<SysLog> pageResult = sysLogDao.queryLog(page, pagesize, startTime, endTime, ascription);
        PageResult<SysLogOnList> result = BeanUtil.beanPageFrom(SysLogOnList.class, pageResult);
        for (SysLogOnList bean : result.getContent())
        {
            if ((Constant.Operation + ascription).equals(bean.getMarket()))
            {
                bean.setMarket("");
                bean.setCompany("运营者");
            }
            else
            {
                if (StringUtils.isNotBlank(bean.getMarket()))
                {
                    // 设置市场名称
                    SysFarmer farmer = farmerDao.get(bean.getMarket());
                    if (farmer != null)
                    {
                        bean.setMarket(farmer.getName());
                        // 设置公司名称
                        SysCompany company = companyDao.get(farmer.getOrg());
                        if (company != null)
                        {
                            bean.setCompany(company.getName());
                        }
                    }

                }
                else
                {
                    // 设置公司名称
                    if (StringUtils.isNotBlank(bean.getCompany()))
                    {
                        SysCompany company = companyDao.get(bean.getCompany());
                        if (company != null)
                        {
                            bean.setCompany(company.getName());
                        }
                    }
                }
            }
        }
        return result;
    }
}
