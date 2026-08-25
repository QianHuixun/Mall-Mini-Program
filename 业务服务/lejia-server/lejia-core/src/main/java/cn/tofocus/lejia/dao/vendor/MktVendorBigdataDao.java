package cn.tofocus.lejia.dao.vendor;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorBigData;
import lombok.Getter;

/**
 * 商户大数据表(MktVendorBigdata) 封装JPA的dao层
 * 
 * @author geshaojian
 * @since 2021-10-12 10:47:56
 */
@Component
@Getter
public class MktVendorBigdataDao extends JpaSpecificationDelegate<Integer, MktVendorBigData>
{
    /**
     * 注入JPA原生的dao层接口
     */
    //    @Resource
    //    private MktVendorBigdataRepository jpaDao;
    //    
    //    @Override
    //    protected JpaRepository<MktVendorBigData, Integer> getRepository()
    //    {
    //        return jpaDao;
    //    }
}