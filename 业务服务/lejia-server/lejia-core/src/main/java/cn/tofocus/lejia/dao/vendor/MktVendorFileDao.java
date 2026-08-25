package cn.tofocus.lejia.dao.vendor;

import org.springframework.stereotype.Component;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import lombok.Getter;

/**
 * 商户文件表(MktVendorFile) 封装JPA的dao层
 * 
 * @author geshaojian
 * @since 2021-10-12 10:39:09
 */
@Component
@Getter
public class MktVendorFileDao extends JpaSpecificationDelegate<Integer, MktVendorFile>
{

    public String getHeadIcon(Integer vendor)
    {
        MktVendorFile file = this.selectOne().eq("vendorPkey", vendor).eq("type", VendorFileType.HEAD_ICON).exec();
        if(file == null)
            return "";
        return file.getUrl();
    }
    
}