package cn.tofocus.lejia.dao.sys;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.tofocus.common.util.date.DateUtil;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.sys.SysDynamicAttribute;
import cn.tofocus.lejia.bean.entity.sys.SysDynamicAttribute.F;

@Component
public class SysDynamicAttributeDao extends JpaSpecificationDelegate<String, SysDynamicAttribute>
{
    /**
     * 获取系统配置
     * @param <T>
     * @param configClass 配置类
     * @return
     */
    public <T> T getSysAttribute(Class<T> configClass, Integer ascription)
    {
        List<SysDynamicAttribute> list = this.select()
            .eq(F.configClass, configClass.getSimpleName())
            .eq(F.ascription, ascription)
            .isNull(F.vendor)
            .isNull(F.farmer)
            .isNull(F.company)
            .exec();
        return listToConfig(configClass, list);
    }
    
    /**
     * 设置系统配置
     * @param <T>
     * @param config
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void setSysAttribute(T config, Integer ascription)
    {
        String configClass = config.getClass().getSimpleName();
        List<SysDynamicAttribute> newlist = configToList(config);
        newlist.forEach(e -> {
            e.setAscription(ascription);
        });
        //删除旧配置
        List<SysDynamicAttribute> list = this.select()
            .eq(F.configClass, configClass)
            .eq(F.ascription, ascription)
            .isNull(F.vendor)
            .isNull(F.farmer)
            .isNull(F.company)
            .exec();
        this.removeAll(list);
        
        //增加新配置
        this.addAll(newlist);
    }
    
    /**
     * 获取公司配置
     * @param <T>
     * @param configClass 配置类
     * @param ascription 归属类型
     * @param companyPkey 公司主键
     * @return
     */
    public <T> T getCompanyAttribute(Class<T> configClass, int ascription, String companyPkey)
    {
        List<SysDynamicAttribute> list = this.select()
            .eq(F.configClass, configClass.getSimpleName())
            .eq(F.ascription, ascription)
            .eq(F.company, companyPkey)
            .isNull(F.farmer)
            .isNull(F.vendor)
            .exec();
        return listToConfig(configClass, list);
    }
    
    /**
     * 设置公司配置
     * @param <T>
     * @param config
     * @param ascription 归属类型
     * @param companyPkey
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void setCompanyAttribute(T config, int ascription, String companyPkey)
    {
        String configClass = config.getClass().getSimpleName();
        
        List<SysDynamicAttribute> newlist = configToList(config);
        newlist.forEach(e -> {
            e.setAscription(ascription);
            e.setCompany(companyPkey);
        });
        
        //删除旧配置
        List<SysDynamicAttribute> list = this.select()
            .eq(F.configClass, configClass)
            .eq(F.ascription, ascription)
            .eq(F.company, companyPkey)
            .isNull(F.farmer)
            .isNull(F.vendor)
            .exec();
        this.removeAll(list);
        
        //增加新配置
        this.addAll(newlist);
    }
    
    /**
     * 获取市场配置
     * @param <T>
     * @param configClass
     * @param ascription 归属类型
     * @param farmerPkey
     * @return
     */
    public <T> T getFarmerAttribute(Class<T> configClass, int ascription, String farmerPkey)
    {
        List<SysDynamicAttribute> list = this.select()
            .eq(F.configClass, configClass.getSimpleName())
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmerPkey)
            .isNull(F.vendor)
            .exec();
        return listToConfig(configClass, list);
    }
    
    /**
     * 设置市场配置
     * @param <T>
     * @param config
     * @param ascription 归属类型
     * @param companyPkey
     * @param farmerPkey
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void setFarmerAttribute(T config, int ascription, String companyPkey, String farmerPkey)
    {
        String configClass = config.getClass().getSimpleName();
        
        List<SysDynamicAttribute> newlist = configToList(config);
        newlist.forEach(e -> {
            e.setAscription(ascription);
            e.setCompany(companyPkey);
            e.setFarmer(farmerPkey);
        });
        
        //删除旧配置
        List<SysDynamicAttribute> list = this.select()
            .eq(F.configClass, configClass)
            .eq(F.ascription, ascription)
            .eq(F.farmer, farmerPkey)
            .isNull(F.vendor)
            .exec();
        this.removeAll(list);
        
        //增加新配置
        this.addAll(newlist);
    }
    
    /**
     * 获取商户属性
     * @param <T>
     * @param configClass
     * @param ascription 归属类型
     * @param vendorPkey
     * @return
     */
    public <T> T getVendorAttribute(Class<T> configClass, int ascription, int vendorPkey)
    {
        List<SysDynamicAttribute> list = this.select()
            .eq(F.configClass, configClass.getSimpleName())
            .eq(F.ascription, ascription)
            .eq(F.vendor, vendorPkey)
            .exec();
        return listToConfig(configClass, list);
    }
    
    /**
     * 设置商户属性
     * @param <T>
     * @param config
     * @param ascription 归属类型
     * @param companyPkey
     * @param farmerPkey
     * @param vendorPkey
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> void setMerchantAttribute(T config, int ascription, String companyPkey, String farmerPkey,
        int vendorPkey)
    {
        String configClass = config.getClass().getSimpleName();
        
        List<SysDynamicAttribute> newlist = configToList(config);
        newlist.forEach(e -> {
            e.setAscription(ascription);
            e.setCompany(companyPkey);
            e.setFarmer(farmerPkey);
            e.setVendor(vendorPkey);
        });
        
        //删除旧配置
        List<SysDynamicAttribute> list = this.select().eq(F.configClass, configClass).eq(F.vendor, vendorPkey).exec();
        this.removeAll(list);
        
        //增加新配置
        this.addAll(newlist);
    }
    
    public static <T> T listToConfig(Class<T> configClass, List<SysDynamicAttribute> list)
    {
        JSONObject json = new JSONObject();
        for (SysDynamicAttribute attr : list)
        {
            Object value;
            if (attr.getValue() != null && attr.getValue().startsWith("[") && attr.getValue().endsWith("]"))
            {
                try
                {
                    value = JSON.parseArray(attr.getValue());
                }
                catch (Exception e)
                {
                    value = attr.getValue();
                }
            }
            else
            {
                value = attr.getValue();
            }
            json.put(attr.getProperty(), value);
        }
        T t = JSONObject.parseObject(json.toString(), configClass);
        return t;
    }
    
    private static List<SysDynamicAttribute> configToList(Object config)
    {
        String configClass = config.getClass().getSimpleName();
        List<SysDynamicAttribute> newlist = new ArrayList<>();
        JSONObject json = (JSONObject)JSON.toJSON(config);
        for (String key : json.keySet())
        {
            Object o = json.get(key);
            if (o != null)
            {
                Class<?> clazz = o.getClass();
                String value = null;
                if (clazz.equals(String.class) || clazz.equals(Boolean.class) || clazz.equals(boolean.class)
                    || clazz.equals(Long.class) || clazz.equals(long.class) || clazz.equals(Integer.class)
                    || clazz.equals(int.class) || clazz.equals(Double.class) || clazz.equals(double.class)
                    || clazz.equals(Float.class) || clazz.equals(float.class) || clazz.equals(BigDecimal.class)
                    || clazz.isEnum())
                    value = o.toString();
                else if (clazz.equals(Date.class))
                    value = DateUtil.formatDate((Date)o);
                else if (clazz.equals(JSONArray.class))
                    value = JsonUtil.toString(o);
                else
                    continue;
                SysDynamicAttribute entity = new SysDynamicAttribute();
                entity.setConfigClass(configClass);
                entity.setProperty(key);
                entity.setValue(value);
                newlist.add(entity);
            }
        }
        return newlist;
    }
}
