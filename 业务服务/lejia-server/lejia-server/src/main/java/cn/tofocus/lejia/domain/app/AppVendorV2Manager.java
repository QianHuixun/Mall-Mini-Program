package cn.tofocus.lejia.domain.app;

import cn.tofocus.common.util.BeanUtil;
import cn.tofocus.core.exception.SysErrCode;
import cn.tofocus.core.exception.TofocusException;
import cn.tofocus.lejia.bean.dto.app.AppVendorMerchant;
import cn.tofocus.lejia.bean.enums.VendorFileType;
import cn.tofocus.lejia.Constant;
import cn.tofocus.lejia.core.MobileSession;
import cn.tofocus.lejia.bean.dto.market.MktVendorFileDTO;
import cn.tofocus.lejia.bean.entity.vendor.MktVendor;
import cn.tofocus.lejia.bean.entity.vendor.MktVendorFile;
import cn.tofocus.lejia.dao.sys.SysConfigDao;
import cn.tofocus.lejia.dao.vendor.MktVendorBigdataDao;
import cn.tofocus.lejia.dao.vendor.MktVendorDao;
import cn.tofocus.lejia.dao.vendor.MktVendorFileDao;
import cn.tofocus.lejia.exception.LejiaErrCode;
import cn.tofocus.lejia.exception.WsaleErrCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class AppVendorV2Manager
{
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
     * mkt_vendor_bigdata 商户风采展示详情内容表
     */
    @Resource
    private MktVendorBigdataDao mktVendorBigdataDao;

    /**
     * sys_config dao层
     */
    @Resource
    private SysConfigDao sysConfigDao;

    /**
     * 简单判断有没有登录
     */
    public void judgeRight(){
        Integer vendorPkey = MobileSession.vendorPkey();
        if (Objects.isNull(vendorPkey))
        {
            throw TofocusException.of(SysErrCode.Auth.UNLOGIN);
        }
    }

    /**
     * 更新商户信息
     * @param param     参数
     * @return          是否成功
     */
    @Transactional(rollbackFor = Throwable.class)
    public Boolean upd(AppVendorMerchant param)
    {
        // 简单判断有没有登录
        judgeRight();

        Integer dtoPkey = param.getPkey();
        MktVendor old = null;
        // 更新
        if (Objects.nonNull(dtoPkey))
        {
            old = vendorDao.get(dtoPkey);
            if (old == null)
            {
                throw TofocusException.of(WsaleErrCode.NOT_INQUIRE);
            }
            else
            {
                BeanUtils.copyProperties(param, old);
            }

            // 校验手机号 - 和其他人的手机号比
            MktVendor valid = null;
            try
            {
                valid = vendorDao.selectOne()
                    .eq("mobile", param.getMobile())
                    .notEq("pkey", param.getPkey())
                    //.eq("idDel", false)
                    .exec();
            }
            catch (PersistenceException exception)
            {
                // 为什么会先执行update，导致抛出数据库校验异常？？？？
                throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
            }
            if (valid != null)
            {
                throw TofocusException.of(WsaleErrCode.MOBILE_REPEAT);
            }
        }
       
        // 修改商户
        MktVendor put = vendorDao.put(old);

        // 设置头像、视频、个性宣传
        Integer pkey = put.getPkey();
        List<MktVendorFile> oldFiles = mktVendorFileDao.select().eq("vendorPkey", put.getPkey()).exec();
        if(CollectionUtils.isNotEmpty(oldFiles)){
            mktVendorFileDao.removeAll(oldFiles);
        }
        List<MktVendorFileDTO> newFiles = param.getFiles();
        if(CollectionUtils.isNotEmpty(newFiles)){
            List<MktVendorFile> mktVendorFiles = BeanUtil.beanListFrom(MktVendorFile.class, newFiles);

            long headIcon = newFiles.stream().filter(f -> VendorFileType.HEAD_ICON.equals(f.getType())).count();
            long video = newFiles.stream().filter(f -> VendorFileType.VIDEO.equals(f.getType())).count();
            long propaganda = newFiles.stream().filter(f -> VendorFileType.PROPAGANDA.equals(f.getType())).count();
            if (headIcon > 1)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_OVERSIZE, "头像只能上传一张");
            }
            if (video > 1)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_OVERSIZE, "宣传视频只能上传一个");
            }
            if (propaganda > 5)
            {
                throw TofocusException.of(LejiaErrCode.PARAM_OVERSIZE, "个性宣传最多5张");
            }
            mktVendorFiles.forEach(file -> {
                file.setVendorPkey(pkey);
                file.setEnabled(true);
            });
            mktVendorFileDao.addAll(mktVendorFiles);
        }

        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    public Boolean isUnified()
    {
        // 简单判断有没有登录
        judgeRight();
        return sysConfigDao.getValue(Constant.SysConfig.VENDOR_MANAGER_DEPLOY, MobileSession.appid());
    }
}
