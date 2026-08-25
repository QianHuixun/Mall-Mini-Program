package cn.tofocus.account;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import cn.tofocus.account.bean.application.ModelInfo;
import cn.tofocus.account.db.dao.application.MenuDao;
import cn.tofocus.core.data.TreeModel;
import cn.tofocus.core.enums.MenuType;
import cn.tofocus.domain.manager.MenuManager;
import cn.tofocus.domain.manager.ModelManager;
import lombok.Data;

@SpringBootTest
public class ManualMenuTest
{
    @Autowired
    private MenuManager menuManager;

    @Autowired
    private ModelManager modelManager;
    
    @Test
    public void testFunction()
    {
        List<TreeModel<String, MenuType>> list =
            menuManager.listMenuConfigByDept("farm", "1", "1", "farm_base", "farmCust-Web");
    }

    @Test
    public void testModel()
    {
        Map<String, ModelInfo> a =
            modelManager.activeModels("farm", null, null);
        System.out.println(a);
    }
}
