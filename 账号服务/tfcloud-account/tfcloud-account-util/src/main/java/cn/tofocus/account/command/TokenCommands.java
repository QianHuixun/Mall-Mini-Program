package cn.tofocus.account.command;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import cn.tofocus.db.redis.RedisKeysUtil;

@ShellComponent
@ShellCommandGroup("Token命令")
public class TokenCommands extends BaseCommands
{
    @Autowired
    private RedisConnectionFactory connectionFactory;
    
    @Value("${spring.redis.prefix}")
    private String redisTokenPrefix;
    
    private static final String ACCESS = "access:";
    
    private static final String AUTH_TO_ACCESS = "auth_to_access:";
    
    private static final String AUTH = "auth:";
    
    private static final String REFRESH_AUTH = "refresh_auth:";
    
    private static final String ACCESS_TO_REFRESH = "access_to_refresh:";
    
    private static final String REFRESH = "refresh:";
    
    private static final String REFRESH_TO_ACCESS = "refresh_to_access:";
    
    private static final String CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    
    private static final String UNAME_TO_ACCESS = "uname_to_access:";
    
    @ShellMethod("清除账号服务的所有token")
    public String clearToken()
    {
        if (!confirmation())
            return "取消";
        
        clearToken(ACCESS_TO_REFRESH);
        clearToken(REFRESH_TO_ACCESS);
        clearToken(ACCESS);
        clearToken(AUTH_TO_ACCESS);
        clearToken(REFRESH);
        clearToken(AUTH);
        clearToken(REFRESH_AUTH);
        clearToken(UNAME_TO_ACCESS);
        clearToken(CLIENT_ID_TO_ACCESS);
        return "完成";
    }
    
    private int clearToken(String path)
    {
        RedisConnection connection = connectionFactory.getConnection();
        Set<byte[]> keys = RedisKeysUtil.keys(connection, redisTokenPrefix + ":" + path + "*");
        connection = connectionFactory.getConnection();
        connection.openPipeline();
        for (byte[] key : keys)
        {
            connection.del(key);
        }
        connection.closePipeline();
        System.out.println("清除 " + keys.size() + " 个 " + path);
        connection.close();
        return keys.size();
    }
}
