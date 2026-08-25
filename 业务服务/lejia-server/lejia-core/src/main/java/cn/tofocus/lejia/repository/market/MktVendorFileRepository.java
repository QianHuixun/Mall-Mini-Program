package cn.tofocus.lejia.repository.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;

/**
 * 商户文件表(MktVendorFile) 原生JPA的dao层
 * 
 * @author geshaojian
 * @since 2021-10-12 10:39:46
 */
@Repository
public interface MktVendorFileRepository extends JpaRepository<MktVendorFile,Integer>{

}