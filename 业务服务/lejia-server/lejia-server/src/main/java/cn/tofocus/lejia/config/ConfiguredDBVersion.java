package cn.tofocus.lejia.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import cn.tofocus.core.version.DBVersion;

/**
 * 允许部署配置显式指定应用应接受的数据库版本。
 *
 * <p>完整备份已包含源码仓库之外的 V84、V85，Flyway 仍负责校验仓库内
 * V1-V83 的历史 checksum；此类只把框架的二次“精确版本”门禁设置为 V85，
 * 不会跳过低版本、未知版本或 Flyway 校验失败。</p>
 */
@Component
@Primary
@ConditionalOnProperty(name = "tofocus.db.expected-version")
public class ConfiguredDBVersion extends DBVersion
{
    @Value("${tofocus.db.expected-version}")
    private String expectedVersion;

    @Override
    public String getExpectVersion()
    {
        return expectedVersion;
    }
}
