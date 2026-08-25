package cn.tofocus.lejia.domain.jdvop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.annotation.PostConstruct;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jd.open.api.sdk.domain.vopxx.MsgRecordProvider.response.queryTransByVopNormal.VopBizTransMessage;

import cn.tofocus.core.PreClose;
import cn.tofocus.core.msgpipe.queue.MQMsqComsumer;
import cn.tofocus.core.msgpipe.queue.MQMsqProducter;
import cn.tofocus.core.msgpipe.queue.MsgSender;
import cn.tofocus.lejia.domain.jdvop.bean.msg.JdVOPAddressChangeMsg;
import cn.tofocus.lejia.domain.jdvop.listener.JdVOPAddressChangeListener;
import cn.tofocus.lejia.domain.jdvop.listener.JdVOPAfsListener;
import cn.tofocus.lejia.domain.jdvop.listener.JdVOPOrderListener;
import cn.tofocus.lejia.domain.jdvop.listener.JdVOPSkuListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JdVOPMsgQueue
{
    @Autowired
    private ConnectionFactory connectionFactory;
    
    @Value("${tofocus.prefix}")
    protected String prefix;
    
    @Autowired
    private RabbitAdmin rabbitAdmin;
    
    @Autowired
    @Qualifier("msgSenderTemplate")
    private MsgSender sender;
    
    private MsgSender orderMsgSender;
    
    private MsgSender skuMsgSender;
    
    private MsgSender afsMsgSender;
    
    @Autowired
    private JdVOPOrderListener orderListener;
    
    @Autowired
    private JdVOPSkuListener skuListener;
    
    @Autowired
    private JdVOPAfsListener afsListener;
    
    @Autowired
    private JdVOPAddressChangeListener addressChangeListener;
    
    private MQMsqProducter<VopBizTransMessage> orderMsgProducter;
    
    private MQMsqComsumer<VopBizTransMessage, String> orderMsgComsumer;
    
    private MQMsqProducter<VopBizTransMessage> skuMsgProducter;
    
    private MQMsqComsumer<VopBizTransMessage, String> skuMsgComsumer;
    
    private MQMsqProducter<VopBizTransMessage> afsMsgProducter;
    
    private MQMsqComsumer<VopBizTransMessage, String> afsMsgComsumer;
    
    private MQMsqProducter<JdVOPAddressChangeMsg> addressChangeMsgProducter;
    
    private MQMsqComsumer<JdVOPAddressChangeMsg, String> addressChangeMsgComsumer;
    
    @PostConstruct
    public void init()
    {
        if (orderMsgSender == null)
        {
            orderMsgSender = new MsgSender(4, 1, "jdVopOrderMsgSender");
        }
        
        if (skuMsgSender == null)
        {
            skuMsgSender = new MsgSender(4, 1, "jdVopSkuMsgSender");
        }
        
        if (afsMsgSender == null)
        {
            afsMsgSender = new MsgSender(4, 1, "jdVopAfsMsgSender");
        }
        
        if (orderMsgProducter == null)
        {
            orderMsgProducter = new MQMsqProducter<>(connectionFactory, prefix, JdVOPOrderListener.PIPE_NAME, "0");
        }
        if (orderMsgComsumer == null)
        {
            orderMsgComsumer = new MQMsqComsumer<>(rabbitAdmin, connectionFactory, prefix, JdVOPOrderListener.PIPE_NAME,
                0, orderListener, orderMsgSender, false);
        }
        
        if (skuMsgProducter == null)
        {
            skuMsgProducter = new MQMsqProducter<>(connectionFactory, prefix, JdVOPSkuListener.PIPE_NAME, "0");
        }
        if (skuMsgComsumer == null)
        {
            skuMsgComsumer = new MQMsqComsumer<>(rabbitAdmin, connectionFactory, prefix, JdVOPSkuListener.PIPE_NAME, 0,
                skuListener, skuMsgSender, false);
        }
        
        if (afsMsgProducter == null)
        {
            afsMsgProducter = new MQMsqProducter<>(connectionFactory, prefix, JdVOPAfsListener.PIPE_NAME, "0");
        }
        if (afsMsgComsumer == null)
        {
            afsMsgComsumer = new MQMsqComsumer<>(rabbitAdmin, connectionFactory, prefix, JdVOPAfsListener.PIPE_NAME, 0,
                afsListener, afsMsgSender, false);
        }
        
        if (addressChangeMsgProducter == null)
        {
            addressChangeMsgProducter =
                new MQMsqProducter<>(connectionFactory, prefix, JdVOPAddressChangeListener.PIPE_NAME, "0");
        }
        if (addressChangeMsgComsumer == null)
        {
            addressChangeMsgComsumer = new MQMsqComsumer<>(rabbitAdmin, connectionFactory, prefix,
                JdVOPAddressChangeListener.PIPE_NAME, 0, addressChangeListener, sender, false);
        }
    }
    
    @PreClose
    public void close()
    {
        List<FutureTask<Integer>> closeTasklist = new ArrayList<>();
        
        addCloseTask(closeTasklist, orderMsgComsumer);
        addCloseTask(closeTasklist, skuMsgComsumer);
        addCloseTask(closeTasklist, afsMsgComsumer);
        addCloseTask(closeTasklist, addressChangeMsgComsumer);
        
        for (FutureTask<Integer> t : closeTasklist)
        {
            try
            {
                t.get();
            }
            catch (ExecutionException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        
        orderListener.close();
    }
    
    private void addCloseTask(List<FutureTask<Integer>> tasklist, final MQMsqComsumer comsumer)
    {
        FutureTask<Integer> task = new FutureTask<>(() -> {
            if (comsumer != null)
            {
                try
                {
                    comsumer.close();
                }
                catch (Exception e)
                {
                    log.error("关闭orderSplitMsgComsumer行为告警", e);
                }
            }
            return 1;
        });
        tasklist.add(task);
        Thread thread = new Thread(task);
        thread.start();
    }
    
    public String orderMsg(VopBizTransMessage msg)
    {
        return orderMsgProducter.produce(msg);
    }
    
    public String skuMsg(VopBizTransMessage msg)
    {
        return skuMsgProducter.produce(msg);
    }
    
    public String afsMsg(VopBizTransMessage msg)
    {
        return afsMsgProducter.produce(msg);
    }
    
    public String addressChangeMsg(JdVOPAddressChangeMsg msg)
    {
        return addressChangeMsgProducter.produce(msg);
    }
}
