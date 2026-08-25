package cn.tofocus.lejia.domain.jdvop.listener;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import cn.tofocus.core.Result;
import cn.tofocus.core.json.JsonUtil;
import cn.tofocus.core.msgpipe.queue.MsgListener;
import cn.tofocus.lejia.bean.entity.jd.JdAddress;
import cn.tofocus.lejia.dao.jd.JdAddressDao;
import cn.tofocus.lejia.domain.jdvop.bean.msg.JdVOPAddressChangeMsg;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdVOPAddressChangeListener implements MsgListener<JdVOPAddressChangeMsg, String>
{
    public static final String PIPE_NAME = "zyysc.jd.vop.msg.addressChange";
    
    @Autowired
    private JdAddressDao jdAddressDao;

    private static final String OPERATION_NAME = "地址变更";
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleMessage(String pipeId, String correlationId, JdVOPAddressChangeMsg msg)
        throws Exception
    {
        try
        {
            log.info("[京东VOP-消息队列]开始处理[{}]消息：{}", OPERATION_NAME, JsonUtil.toString(msg));
            // 新增/更新
            if (msg.getOperateType() == 1 || msg.getOperateType() == 2)
            {
                JdAddress bean = new JdAddress();
                bean.setAreaId(msg.getAreaId());
                bean.setAreaName(msg.getAreaName());
                bean.setAreaLevel(msg.getAreaLevel());
                bean.setParent(msg.getParentId());
                jdAddressDao.put(bean);
            }
            // 删除
            else if (msg.getOperateType() == 3)
            {
                JdAddress bean = jdAddressDao.get(msg.getAreaId());
                if (bean != null)
                {
                    List<JdAddress> children = new ArrayList<>();
                    jdAddressDao.listAllChildren(children, bean.getAreaId());
                    if (!children.isEmpty())
                        jdAddressDao.removeAll(children);
                }
                jdAddressDao.remove(bean);
            }
            return "ok";
        }
        catch (Exception e)
        {
            log.error("[京东VOP-消息队列]处理[{}]消息异常：{}", OPERATION_NAME, e.getMessage(), e);
            throw e;
        }
    }
    
    @Override
    public void handleResult(String pipeId, String correlationId, Result<String> result)
        throws Exception
    {
        
    }
}
