//package cn.tofocus.lejia.api.v3;
//
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
//import javax.websocket.OnClose;
//import javax.websocket.OnError;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//
//import org.springframework.stereotype.Component;
//
//import lombok.extern.slf4j.Slf4j;
//
//@ServerEndpoint("/websocket/{sid}")
//@Component
//@Slf4j
//public class WebsocketApiImpl
//{
//    //静态变量，用来记录当前在线连接数。应该把它设 计成线程安全的。
//    private static AtomicInteger onlineNum = new AtomicInteger();
//    
//    //concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
//    private static ConcurrentHashMap<Session, Set<String>> sessionPools = new ConcurrentHashMap<>();
//    
//    private static ConcurrentHashMap<String, Set<Session>> devicePools = new ConcurrentHashMap<>();
//    
//    //发送消息
//    public static void sendMessage(Session session, String message)
//        throws IOException
//    {
//        if (session != null)
//        {
//            synchronized (session)
//            {
//                log.info(session.getId() + "===发送消息给客户端=" + message);
//                session.getBasicRemote().sendText(message);
//            }
//        }
//    }
//    
//    //建立连接成功调用
//    @OnOpen
//    public void onOpen(Session session, @PathParam(value = "sid") String userName)
//    {
//        sessionPools.put(session, new HashSet<>());
//        addOnlineCount();
//        log.info(session.getId() + "===" + userName + "加入webSocket！当前人数为" + onlineNum);
//        try
//        {
//            sendMessage(session, "欢迎" + userName + "加入连接！");
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//    
//    //收到客户端信息
//    @OnMessage
//    public void onMessage(Session session, String message)
//        throws IOException
//    {
//        log.info(session.getId() + "===收到客户端消息=" + message);
//        Set<String> strings = sessionPools.get(session);
//        if (strings == null)
//        {
//            log.error(session.getId() + "===客户端连接后没有创建set集合");
//        }
//        log.info("[cancel]发送设备订阅消息=" + message);
//    }
//    
//    //关闭连接时调用
//    @OnClose
//    public void onClose(Session session, @PathParam(value = "sid") String userName)
//    {
//        subOnlineCount();
//        log.info(session.getId() + "===" + userName + "断开webSocket连接！当前人数为" + onlineNum);
//        //清除Session内容的方法， 自己根据业务需要编写
////        clearSession(session);
//        
//    }
//    
//    //错误时调用
//    @OnError
//    public void onError(Session session, Throwable throwable)
//    {
//        throwable.printStackTrace();
//        log.error(session.getId() + "===客户端异常", throwable);
//        subOnlineCount();
//        //清除Session内容的方法， 自己根据业务需要编写
////        clearSession(session);
//    }
//    
//    public static void addOnlineCount()
//    {
//        onlineNum.incrementAndGet();
//    }
//    
//    public static void subOnlineCount()
//    {
//        onlineNum.decrementAndGet();
//    }
//    
//}
