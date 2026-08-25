package cn.tofocus.account;

import java.util.List;

import javax.persistence.Entity;

import org.junit.jupiter.api.Test;

import cn.tofocus.account.db.entity.application.AppLoginCheckEntity;
import cn.tofocus.account.db.entity.application.MenuEntity;
import cn.tofocus.account.db.entity.domain.ModelEntity;
import cn.tofocus.account.db.entity.org.DeptMenuEntity;
import cn.tofocus.account.db.entity.org.DeptModelEntity;
import cn.tofocus.account.db.entity.org.OrgMenuEntity;
import cn.tofocus.account.db.entity.org.OrgModelEntity;
import cn.tofocus.account.db.entity.role.RoleMenuEntity;
import cn.tofocus.account.db.entity.user.AccessInstance;
import cn.tofocus.account.db.entity.user.RoleInstance;
import cn.tofocus.common.excel.ExcelUtil;
import cn.tofocus.common.util.EntityUtil;
import cn.tofocus.db.DBUtil;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  wyw
 * @version  [版本号, 2018年4月2日]
 */

public class ManualDBTest
{
    
    @Test
    public void test()
    {
        //找到所有实体
        List<Class<?>> entityClassList = DBUtil.findClass("cn.tofocus.domain", Entity.class);
        entityClassList.addAll(DBUtil.findClass("cn.tofocus.account.db.entity.domain", Entity.class));
        ExcelUtil.exportTableDoc(entityClassList, "D://数据字典//account.xlsx", "account");
    }
    
    @Test
    public void testSql()
    {
        System.out.println(EntityUtil.entity2Sql(ModelEntity.class,
            OrgModelEntity.class,
            DeptModelEntity.class,
            OrgMenuEntity.class,
            DeptMenuEntity.class,
            RoleMenuEntity.class,
            AppLoginCheckEntity.class));
        System.out.println(EntityUtil.entityAddColSql(MenuEntity.class, "modelId", "createdTime", "updatedTime"));
    }
}
