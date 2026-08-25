package cn.tofocus.file.es;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EsIndexConfig
{
    @Value("${tofocus.prefix}")
    private String prefix;

    public String getFileAccess()
    {
        return prefix.toLowerCase() + ".tf.fileacc";
    }
}
