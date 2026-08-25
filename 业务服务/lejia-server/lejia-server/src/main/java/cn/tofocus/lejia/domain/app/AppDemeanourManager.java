package cn.tofocus.lejia.domain.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.dto.DtoEnhance;
import cn.tofocus.lejia.bean.dto.PkeyNameDTO;
import cn.tofocus.lejia.bean.dto.app.AppDemeanourPageDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorFileDTO;
import cn.tofocus.lejia.bean.dto.market.MktVendorQueryParamDTO;
import cn.tofocus.lejia.bean.entity.market.MktGtype;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBigData;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.dao.market.MktGtypeDao;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.sys.SysFarmerDao;
import cn.tofocus.lejia.dao.vendor.MktVendorBigdataDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;

@Component
public class AppDemeanourManager
{
    /**
     * mkt_gtype 一级分类表
     */
    @Resource
    private MktGtypeDao mktGtypeDao;
    
    /**
     * mkt_vendor 商户表
     */
    @Resource
    private MktVendorDao vendorDao;
    
    /**
     * mkt_vendor_file 商户文件表
     */
    @Resource
    private MktVendorFileDao mktVendorFileDao;
    
    /**
     * mkt_vendor_bigdata 商户介绍
     */
    @Resource
    private MktVendorBigdataDao mktVendorBigdataDao;
    
    /**
     * sys_config 配置表
     */
    @Resource
    private SysConfigDao sysConfigDao;
    
    /**
     * sys_farmer 市场表
     */
    @Resource
    private SysFarmerDao sysFarmerDao;
    
    /**
     * dto增强对象类
     */
    @Resource
    private DtoEnhance dtoEnhance;
    
    /**
     * 简单判断有没有登录
     */
    public void judgeRight()
    {
        Integer memberPkey = MobileSession.memberPkey();
        if (Objects.isNull(memberPkey))
        {
            throw TofocusException.of(SysErrCode.Auth.UNLOGIN);
        }
    }
    
    /**
     * 商户风采：一级分类列表
     * @return   结果
     */
    public List<PkeyNameDTO> gtypePkeyNameList()
    {
        Integer ascription = MobileSession.appid();
        List<MktGtype> mktGtypes = mktGtypeDao.listGtype(MobileSession.farmerPkey(), ascription);
        List<PkeyNameDTO> res = new ArrayList<>();
        List<MktVendor> list = vendorDao.select()
            .eq("idDel", false)
            .eq("enabled", true)
            .eq("farmer", MobileSession.farmerPkey())
            .eq("ascription", ascription)
            .exec();
        Map<Integer, Integer> map = new HashMap<>();
        for (MktVendor v : list)
        {
            String bs = v.getBusinessScope();
            if (StringUtils.isNotBlank(bs))
            {
                String[] scopes = bs.split(",");
                for (int i = 0; i < scopes.length; i++)
                {
                    int key = Integer.parseInt(scopes[i]);
                    if (!map.containsKey(key)) map.put(key, 0);
                    map.put(key, map.get(key) + 1);
                }
            }
        }
        for (MktGtype g : mktGtypes)
        {
            if ("优惠券".equals(g.getName()) || "礼券".equals(g.getName()) || !map.containsKey(g.getPkey())) continue;
            PkeyNameDTO dto = BeanUtil.beanFrom(PkeyNameDTO.class, g);
            res.add(dto);
        }
        return res;
    }
    
    /**
     * 商户风采分页数据
     * @param paramDTO 商户名称
     * @return         结果
     */
    public PageResult<AppDemeanourPageDTO> pageList(MktVendorQueryParamDTO paramDTO)
    {
        // 查询获得结果
        PageResult<MktVendor> pageResult = vendorDao.queryVendor(paramDTO, MobileSession.appid());
        
        // 转换并处理结果
        PageResult<AppDemeanourPageDTO> result = BeanUtil.beanPageFrom(AppDemeanourPageDTO.class, pageResult);
        dtoEnhance.deal(AppDemeanourPageDTO.class, result);
        
        // 经营范围
        Map<Integer, String> gtypePkeyNameList = mktGtypeDao.listGtype(MobileSession.farmerPkey(), MobileSession.appid())
            .stream()
            .collect(Collectors.toMap(MktGtype::getPkey, MktGtype::getName));
        
        // 头像、视频、个性宣传
        // 商户pkey集合
        List<Integer> pkey = pageResult.getContent().stream().map(MktVendor::getPkey).collect(Collectors.toList());
        // 仅仅返回头像
        List<MktVendorFile> files =
            mktVendorFileDao.select().in("vendorPkey", pkey).eq("type", VendorFileType.HEAD_ICON).exec();
        
        // 风采展示详情内容
        List<MktVendorBigData> bigDatas = mktVendorBigdataDao.select().in("pkey", pkey).exec();
        
        // 不在循环里进行查询
        for (AppDemeanourPageDTO bean : result.getContent())
        {
            bean.setName(bean.getDisplayName());
            // 设置经营范围
            // 13,14 -> 蔬菜豆类,新鲜水果
            List<String> businessScopesName = new ArrayList<>();
            String businessScope = bean.getBusinessScope();
            if (StringUtils.isNotBlank(businessScope))
            {
                String[] scopes = businessScope.split(",");
                for (int i = 0; i < scopes.length; i++)
                {
                    int scopePkey = 0;
                    String name = "";
                    
                    try
                    {
                        scopePkey = Integer.parseInt(scopes[i]);
                    }
                    // 转换失败处理
                    catch (NumberFormatException e)
                    {
                        name += "未知";
                    }
                    // 一级范围名称
                    String v = gtypePkeyNameList.get(scopePkey);
                    if (Objects.nonNull(v))
                    {
                        name += v;
                    }
                    else
                    {
                        name += "未知";
                    }
                    businessScopesName.add(name);
                }
                bean.setBusinessScopesName(businessScopesName);
            }
            // 头像、视频、个性宣传
            // 当前商户的数据
            List<MktVendorFile> fileList =
                files.stream().filter(file -> file.getVendorPkey().equals(bean.getPkey())).collect(Collectors.toList());
            List<MktVendorFileDTO> fileDtos = BeanUtil.beanListFrom(MktVendorFileDTO.class, fileList);
            bean.setFiles(fileDtos);
            
            // 风采展示详情内容
            List<MktVendorBigData> mktVendorBigDatas =
                bigDatas.stream().filter(item -> item.getPkey().equals(bean.getPkey())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(mktVendorBigDatas))
            {
                bean.setMktVendorBigData(mktVendorBigDatas.get(0));
            }
            else
            {
                bean.setMktVendorBigData(new MktVendorBigData());
            }
        }
        return result;
    }
    
}
