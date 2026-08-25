package cn.tofocus.lejia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.collect.Lists;

import cn.tofocus.core.data.KeyValue;
import cn.tofocus.lejia.bean.entity.market.MktAddr;
import cn.tofocus.lejia.bean.enums.AddrType;
import cn.tofocus.lejia.dao.market.MktAddrDao;

@SpringBootTest
public class ManualAddrAplitTest
{
    
    @Autowired
    private MktAddrDao addrDao;
    
    @Test
    public void batchAddrHandle()
    {
        System.out.println("开始地址拆分地区处理...");
        int successNum = 0;
        List<MktAddr> addrList = addrDao.select().eq(MktAddr.F.type, AddrType.DELIVERY).exec();
        for (MktAddr addr : addrList)
        {
            String s = addr.getAddr();
            String pro = null;
            String city = null;
            String area = null;
            
            KeyValue<String, String> zxs = handleZXS(s); // 处理直辖市
            if (zxs != null)
            {
                pro = zxs.getKey();
                city = pro;
                s = zxs.getValue();
            }
            else
            {
                KeyValue<String, String> proKV = handlePro(s); // 处理省
                if (proKV != null)
                {
                    pro = proKV.getKey();
                    s = proKV.getValue();
                    KeyValue<String, String> cityKV = handleCity(s); // 处理市
                    if (cityKV != null)
                    {
                        city = cityKV.getKey();
                        s = cityKV.getValue();
                    }
                }
            }
            if (pro != null && city != null)
            {
                KeyValue<String, String> areaKV = handleArea(s); // 处理区
                if (areaKV != null)
                {
                    area = areaKV.getKey();
                    successNum++;
                }
            }
            if (pro == null && city == null)
            {
                if (s.startsWith("瓯海区") || s.startsWith("甌海區"))
                {
                    pro = "浙江省";
                    city = "温州市";
                    area = "瓯海区";
                    successNum++;
                }
                if (s.startsWith("鹿城区") || s.startsWith("鹿城") || s.startsWith("黎明西路") || s.startsWith("七都街道"))
                {
                    pro = "浙江省";
                    city = "温州市";
                    area = "鹿城区";
                    successNum++;
                }
                if (s.startsWith("海珠区"))
                {
                    pro = "广东省";
                    city = "广州市";
                    area = "海珠区";
                    successNum++;
                }
                if (s.startsWith("青云谱区"))
                {
                    pro = "江西省";
                    city = "南昌市";
                    area = "青云谱区";
                    successNum++;
                }
                if (s.startsWith("滨海新区"))
                {
                    pro = "天津市";
                    city = "天津市";
                    area = "滨海新区";
                    successNum++;
                }
            }
            if (pro != null && pro.length() <= 40) addr.setPro(pro);
            if (city != null && city.length() <= 40) addr.setCity(city);
            if (area != null && area.length() <= 40) addr.setArea(area);
            //addr.setAddr(addr.getAddr() + addr.getAddrDetail());
        }
        //addrDao.updateAll(addrList);
        System.out.println("完成地址批处理，共" + successNum + "条成功拆分地区，共" + (addrList.size() - successNum) + "条待人工处理");
    }
    
    private KeyValue<String, String> handleZXS(String s)
    {
        List<String> zxsList = Lists.newArrayList("北京市", "天津市", "上海市", "重庆市");
        for (String zxs : zxsList)
        {
            if (s.startsWith(zxs))
            {
                s = s.substring(3);
                if (s.startsWith(zxs)) s = s.substring(3);
                return new KeyValue<>(zxs, s);
            }
        }
        return null;
    }
    
    private KeyValue<String, String> handlePro(String s)
    {
        int index = s.indexOf("自治区");
        if (index >= 0)
        {
            String pro = s.substring(0, index + 3);
            s = s.substring(index + 3);
            return new KeyValue<>(pro, s);
        }
        index = s.indexOf("省");
        if (index >= 0)
        {
            String pro = s.substring(0, index + 1);
            s = s.substring(index + 1);
            return new KeyValue<>(pro, s);
        }
        return null;
    }
    
    private KeyValue<String, String> handleCity(String s)
    {
        int index = s.indexOf("自治州");
        if (index >= 0)
        {
            String city = s.substring(0, index + 3);
            s = s.substring(index + 3);
            return new KeyValue<>(city, s);
        }
        index = s.indexOf("市");
        if (index >= 0)
        {
            String city = s.substring(0, index + 1);
            s = s.substring(index + 1);
            return new KeyValue<>(city, s);
        }
        return null;
    }
    
    private KeyValue<String, String> handleArea(String s)
    {
        int index1 = s.indexOf("县");
        int index2 = s.indexOf("市");
        int index3 = s.indexOf("区");
        List<Integer> list = new ArrayList<>();
        if (index1 > 0) list.add(index1);
        if (index2 > 0) list.add(index2);
        if (index3 > 0)
        {
            String temp = s.substring(index3 - 1, index3);
            if (!"小".equals(temp) && !"社".equals(temp) && !"市".equals(temp)) list.add(index3);
        }
        if (!list.isEmpty())
        {
            int index = Collections.min(list);
            String area = s.substring(0, index + 1);
            s = s.substring(index + 1);
            return new KeyValue<>(area, s);
        }
        return null;
    }
}
