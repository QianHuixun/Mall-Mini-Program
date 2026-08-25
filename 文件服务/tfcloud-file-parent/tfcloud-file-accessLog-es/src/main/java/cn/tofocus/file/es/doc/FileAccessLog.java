package cn.tofocus.file.es.doc;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import cn.tofocus.common.cachemap.bean.HasPkey;
import cn.tofocus.db.ShardTime;
import cn.tofocus.file.bean.ThumbType;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Document(indexName = "#{@esIndexConfig.fileAccess}", shards = 5, createIndex = false)
@FieldNameConstants(innerTypeName = "F")
public class FileAccessLog implements HasPkey<Long>
{
    @Id
    @Field(name = "k_pkey", type = FieldType.Keyword)
    private Long pkey; //自增主键

    @Parameter(description = "文件Md5")
    @Field(name = "k_fileMd5", type = FieldType.Keyword)
    private String fileMd5;
    
    @Parameter(description = "文件Pkey")
    @Field(name = "l_filePkey", type = FieldType.Long)
    private long filePkey;

    @Parameter(description = "文件大小")
    @Field(name = "l_size", type = FieldType.Long)
    private long size; //文件大小

    @Field(name = "k_thumb", type = FieldType.Keyword)
    private ThumbType thumb;

    @Field(name = "k_ip", type = FieldType.Keyword)
    private String ip;

    @Field(name = "k_referer", type = FieldType.Keyword)
    private String referer;

    @Field(name = "k_deviceType", type = FieldType.Keyword)
    private String deviceType;

    @Field(name = "k_deviceName", type = FieldType.Keyword)
    private String deviceName;
    
    @Field(name = "k_os", type = FieldType.Keyword)
    private String os;

    @Field(name = "k_osVersion", type = FieldType.Keyword)
    private String osVersion;

    @Field(name = "k_agentType", type = FieldType.Keyword)
    private String agentType;

    @Field(name = "k_agentName", type = FieldType.Keyword)
    private String agentName;

    @Field(name = "k_agentVersion", type = FieldType.Keyword)
    private String agentVersion;

    @Field(name = "k_status", type = FieldType.Keyword)
    private String status;
    
    @ShardTime
    @Parameter(description = "访问时间")
    @Field(name = "dt_accessTime", type = FieldType.Date, format = DateFormat.epoch_millis)
    private Date accessTime;
}
