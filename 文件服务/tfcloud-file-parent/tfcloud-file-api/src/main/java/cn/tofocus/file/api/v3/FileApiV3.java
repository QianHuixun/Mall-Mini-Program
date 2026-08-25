package cn.tofocus.file.api.v3;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import cn.tofocus.core.Result;
import cn.tofocus.core.data.EndDate;
import cn.tofocus.core.data.StartDate;
import cn.tofocus.core.exception.DefaultFallbackFactory;
import cn.tofocus.core.feign.FeignConfig;
import cn.tofocus.core.page.PageResult;
import cn.tofocus.db.file.RefChangeList;
import cn.tofocus.file.api.ApiTags;
import cn.tofocus.file.bean.BackupInfo;
import cn.tofocus.file.bean.DownloadReport;
import cn.tofocus.file.bean.FileAccessInfo;
import cn.tofocus.file.bean.FileDownloadReportItem;
import cn.tofocus.file.bean.FileInfoV3;
import cn.tofocus.file.bean.FileRefInfo;
import cn.tofocus.file.bean.FileRefLinkInfo;
import cn.tofocus.file.bean.FileReport;
import cn.tofocus.file.bean.OrphanFileRecord;
import cn.tofocus.file.bean.ReportItem;
import cn.tofocus.file.bean.ThumbType;
import cn.tofocus.file.bean.UpdateParam;
import cn.tofocus.file.bean.BackupStatus;
import cn.tofocus.file.bean.ClearStatus;
import cn.tofocus.file.bean.UploadType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@FeignClient(value = "file", path = "/v3", contextId = "v3", fallbackFactory = DefaultFallbackFactory.class, configuration = FeignConfig.class)
public interface FileApiV3
{
    /**
     * 上传图片
     * @param file
     * @param title
     * @param memo
     * @return
     */
    @Operation(summary = "上传图片", tags = ApiTags.UPLOAD)
    @PostMapping(value = "/image/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileInfoV3> uploadImage(@RequestPart("file") MultipartFile file, @RequestParam(value = "title") String title,
        @RequestParam(value = "memo") String memo);
    
    /**
     * 上传视频
     * @param file
     * @param title
     * @param memo
     * @return
     */
    @Operation(summary = "上传视频", tags = ApiTags.UPLOAD)
    @PostMapping(value = "/video/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileInfoV3> uploadVideo(@RequestPart("file") MultipartFile file, @RequestParam(value = "title") String title,
        @RequestParam(value = "memo") String memo);
    
    /**
     * 上传文档
     * @param file
     * @param title
     * @param memo
     * @return
     */
    @Operation(summary = "上传文档", tags = ApiTags.UPLOAD)
    @PostMapping(value = "/doc/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileInfoV3> uploadDoc(@RequestPart("file") MultipartFile file, @RequestParam(value = "title") String title,
        @RequestParam(value = "memo") String memo);
    
    /**
     * 上传文件
     * @param file
     * @param title
     * @param memo
     * @return
     */
    @Operation(summary = "上传文件", tags = ApiTags.UPLOAD)
    @PostMapping(value = "/file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<FileInfoV3> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam(value = "title") String title,
        @RequestParam(value = "memo") String memo);
    
    /**
     * 获取文件信息
     * @param file
     * @return
     */
    @Operation(summary = "获取文件信息")
    @PostMapping(value = "/info/get")
    Result<FileInfoV3> getFileInfo(@RequestParam("file") String file);
    
    /**
     * 引用文件
     * @param domain
     * @param db
     * @param table
     * @param refs
     * @return
     */
    @Operation(summary = "引用文件")
    @PostMapping(value = "/referencesFiles")
    Result<?> referencesFiles(@RequestBody @Valid RefChangeList ref);
    
    /***********************
     * 
     *        管理
     * 
     ***********************/
    
    /**
     * 重新产生缩略图
     * @param file
     * @param force
     * @return
     */
    @Operation(summary = "重新产生缩略图")
    @PostMapping(value = "/image/createThumb")
    Result<?> createImageThumb(@RequestParam("file") String file, @RequestParam(value = "thumb") ThumbType thumb,
        @RequestParam(value = "force", required = false) boolean force);
    
    /**
     * 重新校准引用信息
     * @param domain
     * @param db
     * @param table
     * @param refs
     * @return
     */
    @Operation(summary = "重新校准引用信息")
    @PostMapping(value = "/manage/referencesAllbyTable")
    Result<?> referencesAllbyTable(@RequestBody @Valid RefChangeList ref);
    
    /**
     * 升级文件
     * @param file
     * @param domain
     * @param db
     * @param table
     * @param pkey
     * @return
     */
    @Operation(summary = "升级文件")
    @PostMapping(value = "/manage/update")
    Result<FileInfoV3> update(@RequestParam("file") String file, @RequestParam(value = "domain") String domain,
        @RequestParam(value = "db") String db, @RequestParam(value = "table") String table,
        @RequestParam(value = "pkey") String pkey, @RequestParam(value = "org", required = false) String org,
        @RequestParam(value = "dept", required = false) String dept, @RequestParam(value = "upType") UploadType upType);
    
    /**
     * 升级文件
     * @param file
     * @param domain
     * @param db
     * @param table
     * @param pkey
     * @return
     */
    @Operation(summary = "升级文件")
    @PostMapping(value = "/manage/updateList")
    Result<List<FileInfoV3>> updateList(@RequestBody @Valid List<UpdateParam> list);
    
    /***********************
     * 
     *        清理
     * 
     ***********************/
    
    /**
     * 检查当前没有任何引用并且创建时间大于30天的文件记录
     * @return
     */
    @Operation(summary = "检查当前没有任何引用并且创建时间大于30天的文件记录", tags = ApiTags.CLEAR)
    @PostMapping(value = "/manage/clear/checkOrphanFileRecord")
    Result<OrphanFileRecord> checkOrphanFileRecord();
    
    /**
     * 删除当前没有任何引用并且创建时间大于30天的文件记录及文件
     * @return
     */
    @Operation(summary = "删除当前没有任何引用并且创建时间大于30天的文件记录及文件", tags = ApiTags.CLEAR)
    @PostMapping(value = "/manage/clear/orphanFileRecord")
    Result<String> clearOrphanFileRecord();
    
    /**
     * 删除没有数据库文件记录的文件
     * @return
     */
    @Operation(summary = "删除没有数据库文件记录的文件", tags = ApiTags.CLEAR)
    @PostMapping(value = "/manage/clear/orphanFile")
    Result<String> clearOrphanFile();
    
    /**
     * 清理进度
     * @return
     */
    @Operation(summary = "清理进度", tags = ApiTags.CLEAR)
    @PostMapping(value = "/manage/clear/staus")
    Result<ClearStatus> clearStaus();
    
    /***********************
     * 
     *        备份
     * 
     ***********************/
    
    /**
     * 备份列表
     * @return
     */
    @Operation(summary = "备份列表", tags = ApiTags.BACKUP)
    @PostMapping(value = "/manage/backup/list")
    Result<PageResult<BackupInfo>> listBackup(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize);
    
    /**
     * 备份
     * @param year
     * @param month
     * @return
     */
    @Operation(summary = "备份", tags = ApiTags.BACKUP)
    @PostMapping(value = "/manage/backup/start")
    Result<String> backup(@RequestParam(value = "year") int year,
        @RequestParam(value = "month", required = false) Integer month);
    
    /**
     * 恢复
     * @param year
     * @param month
     * @return
     */
    @Operation(summary = "恢复", tags = ApiTags.BACKUP)
    @PostMapping(value = "/manage/restore/start")
    Result<String> restore(@RequestParam(value = "year") int year,
        @RequestParam(value = "month", required = false) Integer month);
    
    /**
     * 备份进度
     * @return
     */
    @Operation(summary = "备份进度", tags = ApiTags.BACKUP)
    @PostMapping(value = "/manage/backup/staus")
    Result<BackupStatus> backupStaus();
    
    /***********************
     * 
     *        统计报表
     * 
     ***********************/
    
    /**
     * 按文件类型统计
     * @return
     */
    @Operation(summary = "按文件类型统计", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggFileByMime")
    Result<FileReport> aggFileByMime(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 按应用统计
     * @return
     */
    @Operation(summary = "按应用统计", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggRefByApp")
    Result<FileReport> aggRefByApp(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 按扩展名统计
     * @return
     */
    @Operation(summary = "按扩展名统计", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggRefByExt")
    Result<FileReport> aggRefByExt(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 统计每个域使用的空间
     * @return
     */
    @Operation(summary = "统计每个域使用的空间", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggLinkByDomain")
    Result<FileReport> aggByDomain(
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 统计每个数据库使用的空间
     * @return
     */
    @Operation(summary = "统计每个数据库使用的空间", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggLinkByDb")
    Result<FileReport> aggByDb(@RequestParam(value = "domain") @Parameter(description = "域") String domain,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 统计每个表使用的空间
     * @return
     */
    @Operation(summary = "统计每个表使用的空间", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggLinkByTable")
    Result<FileReport> aggByTable(@RequestParam(value = "domain") @Parameter(description = "域") String domain,
        @RequestParam(value = "db") @Parameter(description = "数据库") String db,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 统计每个公司使用的空间
     * @return
     */
    @Operation(summary = "统计每个公司使用的空间", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggLinkByOrg")
    Result<FileReport> aggByOrg(@RequestParam(value = "domain") @Parameter(description = "域") String domain,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 统计每个市场使用的空间
     * @return
     */
    @Operation(summary = "统计每个市场使用的空间", tags = ApiTags.REPORT)
    @PostMapping(value = "/report/aggLinkByDept")
    Result<FileReport> aggByDept(@RequestParam(value = "domain") @Parameter(description = "域") String domain,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /***********************
     * 
     *        查询
     * 
     ***********************/
    
    /**
     * 文件查询
     * @return
     */
    @Operation(summary = "文件查询", tags = ApiTags.QUERY)
    @PostMapping(value = "/query/ref")
    Result<PageResult<FileRefInfo>> queryRef(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "md5", required = false) String md5,
        @RequestParam(value = "fileName", required = false) @Parameter(description = "文件名") String fileName,
        @RequestParam(value = "extName", required = false) @Parameter(description = "扩展名") String extName,
        @RequestParam(value = "title", required = false) @Parameter(description = "标题") String title,
        @RequestParam(value = "memo", required = false) @Parameter(description = "备注") String memo,
        @RequestParam(value = "appid", required = false) String appid,
        @RequestParam(value = "userkey", required = false) @Parameter(description = "创建者主键") Integer userkey,
        @RequestParam(value = "type", required = false) @Parameter(description = "上传类型") UploadType type,
        @RequestParam(value = "minSize", required = false) @Parameter(description = "最小文件大小") Long minSize,
        @RequestParam(value = "maxSize", required = false) @Parameter(description = "最大文件大小") Long maxSize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 关联记录查询
     * @return
     */
    @Operation(summary = "关联记录查询", tags = ApiTags.QUERY)
    @PostMapping(value = "/query/link")
    Result<PageResult<FileRefLinkInfo>> queryLink(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "domain", required = false) @Parameter(description = "域") String domain,
        @RequestParam(value = "db", required = false) @Parameter(description = "数据库") String db,
        @RequestParam(value = "table", required = false) @Parameter(description = "表") String table,
        @RequestParam(value = "filePkey", required = false) @Parameter(description = "文件主键") Long filePkey,
        @RequestParam(value = "org", required = false) @Parameter(description = "机构/公司") String org,
        @RequestParam(value = "dept", required = false) @Parameter(description = "部门/市场") String dept,
        @RequestParam(value = "minSize", required = false) @Parameter(description = "最小文件大小") Long minSize,
        @RequestParam(value = "maxSize", required = false) @Parameter(description = "最大文件大小") Long maxSize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /***********************
     * 
     *        文件访问记录
     * 
     ***********************/
    
    /**
     * 文件访问记录查询
     * @return
     */
    @Operation(summary = "文件访问记录查询", tags = ApiTags.ACCESS)
    @PostMapping(value = "/access/query")
    Result<PageResult<FileAccessInfo>> queryAccess(
        @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(value = "pagesize", required = false, defaultValue = "10") Integer pagesize,
        @RequestParam(value = "md5", required = false) String md5,
        @RequestParam(value = "filePkey", required = false) @Parameter(description = "文件Pkey") Long filePkey,
        @RequestParam(value = "thumbType", required = false) @Parameter(description = "缩略图类型") ThumbType thumbType,
        @RequestParam(value = "ip", required = false) @Parameter(description = "ip") String ip,
        @RequestParam(value = "referer", required = false) @Parameter(description = "referer") String referer,
        @RequestParam(value = "deviceType", required = false) @Parameter(description = "设备类型") String deviceType,
        @RequestParam(value = "deviceName", required = false) @Parameter(description = "设备名") String deviceName,
        @RequestParam(value = "os", required = false) @Parameter(description = "系统") String os,
        @RequestParam(value = "osVersion", required = false) @Parameter(description = "系统版本") String osVersion,
        @RequestParam(value = "agentType", required = false) @Parameter(description = "Agent类型") String agentType,
        @RequestParam(value = "agentName", required = false) @Parameter(description = "Agent名称") String agentName,
        @RequestParam(value = "agentVersion", required = false) @Parameter(description = "Agent版本") String agentVersion,
        @RequestParam(value = "status", required = false) @Parameter(description = "状态") String status,
        @RequestParam(value = "minSize", required = false) @Parameter(description = "最小文件大小") Long minSize,
        @RequestParam(value = "maxSize", required = false) @Parameter(description = "最大文件大小") Long maxSize,
        @RequestParam(value = "startTime", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endTime", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 文件访问流量总计
     * @return
     */
    @Operation(summary = "文件访问流量总计", tags = ApiTags.ACCESS)
    @PostMapping(value = "/access/report")
    Result<DownloadReport> accessReport(
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 按文件统计
     * @return
     */
    @Operation(summary = "按文件统计", tags = ApiTags.ACCESS)
    @PostMapping(value = "/access/reportByFile")
    Result<List<FileDownloadReportItem>> accessReportByFile(
        @RequestParam(value = "orderBySize", required = false, defaultValue = "false") @Parameter(description = "按流量排序") boolean orderBySize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 按来源统计
     * @return
     */
    @Operation(summary = "按IP统计", tags = ApiTags.ACCESS)
    @PostMapping(value = "/access/reportByRef")
    Result<List<ReportItem>> accessReportByRef(
        @RequestParam(value = "filePkey", required = false) @Parameter(description = "文件Pkey") Long filePkey,
        @RequestParam(value = "orderBySize", required = false, defaultValue = "false") @Parameter(description = "按流量排序") boolean orderBySize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 按IP统计
     * @return
     */
    @Operation(summary = "按IP统计", tags = ApiTags.ACCESS)
    @PostMapping(value = "/access/reportByIp")
    Result<List<ReportItem>> accessReportByIp(
        @RequestParam(value = "filePkey", required = false) @Parameter(description = "文件Pkey") Long filePkey,
        @RequestParam(value = "orderBySize", required = false, defaultValue = "false") @Parameter(description = "按流量排序") boolean orderBySize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 按设备统计
     * @return
     */
    @Operation(summary = "按设备统计", tags = ApiTags.ACCESS)
    @PostMapping(value = "/access/reportByDevice")
    Result<List<ReportItem>> accessReportByDevice(
        @RequestParam(value = "filePkey", required = false) @Parameter(description = "文件Pkey") Long filePkey,
        @RequestParam(value = "orderBySize", required = false, defaultValue = "false") @Parameter(description = "按流量排序") boolean orderBySize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") EndDate endDate);
    
    /**
     * 按系统统计
     * @return
     */
    @Operation(summary = "按系统统计", tags = ApiTags.ACCESS)
    @PostMapping(value = "/access/reportByOs")
    Result<List<ReportItem>> accessReportByOs(
        @RequestParam(value = "filePkey", required = false) @Parameter(description = "文件Pkey") Long filePkey,
        @RequestParam(value = "orderBySize", required = false, defaultValue = "false") @Parameter(description = "按流量排序") boolean orderBySize,
        @RequestParam(value = "startDate", required = false) @Parameter(description = "开始时间") StartDate startDate,
        @RequestParam(value = "endDate", required = false) @Parameter(description = "结束时间") EndDate endDate);
}
