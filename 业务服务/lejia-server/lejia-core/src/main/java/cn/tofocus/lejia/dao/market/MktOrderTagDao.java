package cn.tofocus.lejia.dao.market;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import cn.tofocus.db.jpa.dao.JpaSpecificationDelegate;
import cn.tofocus.lejia.bean.entity.market.MktOrderTag;

@Component
public class MktOrderTagDao extends JpaSpecificationDelegate<String, MktOrderTag>
{
    public List<MktOrderTag> listTag(List<Integer> tags, Integer ascription)
    {
        return this.select().in("tag", tags).eq("ascription", ascription).exec();
    }

    public String getTagName(Integer orderPkey)
    {
        StringBuffer sb = new StringBuffer();
        List<MktOrderTag> list = this.select().eq("orderPkey", orderPkey).exec();
        list.forEach(e ->
        {
            sb.append(e.getTagName());
            sb.append(",");
        });
        if(sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 批量获取多个订单的标签名（逗号拼接），返回 orderPkey -> tagName。
     * 替代逐单调用 getTagName，避免 N+1 查询；无标签单不在 map 中，调用方用 getOrDefault(pkey, "")。
     */
    public Map<Integer, String> mapTagName(List<Integer> orderPkeys)
    {
        Map<Integer, String> map = new HashMap<>();
        if (orderPkeys == null || orderPkeys.isEmpty())
            return map;
        Map<Integer, StringBuffer> buf = new HashMap<>();
        // 框架 .in() 单次参数上限 10000，按 9000 分批查询后合并
        for (List<Integer> batch : Lists.partition(orderPkeys, 9000))
        {
            for (MktOrderTag t : this.select().in("orderPkey", batch).exec())
            {
                Integer orderPkey = t.getOrderPkey();
                StringBuffer sb = buf.get(orderPkey);
                if (sb == null)
                {
                    sb = new StringBuffer();
                    buf.put(orderPkey, sb);
                }
                sb.append(t.getTagName());
                sb.append(",");
            }
        }
        for (Map.Entry<Integer, StringBuffer> e : buf.entrySet())
        {
            StringBuffer sb = e.getValue();
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            map.put(e.getKey(), sb.toString());
        }
        return map;
    }

    public List<MktOrderTag> listOrderTag(Integer orderPkey)
    {
        return this.select().eq("orderPkey", orderPkey).exec();
    }
}
