package cn.tofocus.lejia.util.mysql;


import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.excel.util.StringUtils;

public class MySQL57ExportToSQL {
    
    private Connection connection;
    private String outputFilePath;
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private boolean includeDropTable = false;  // 是否包含DROP TABLE语句
    private boolean includeCreateTable = false; // 是否包含CREATE TABLE语句
    
    public MySQL57ExportToSQL(String host, int port, String database, 
                             String username, String password, 
                             String outputFilePath) throws SQLException, ClassNotFoundException {
        // 注册MySQL 5.7驱动
        Class.forName(DRIVER_CLASS);
        
        // MySQL 5.7连接字符串
        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&useUnicode=true&characterEncoding=utf8", 
                                 host, port, database);
        
        this.connection = DriverManager.getConnection(url, username, password);
        this.outputFilePath = outputFilePath;
    }
    
    /**
     * 设置是否包含DROP TABLE语句
     */
    public void setIncludeDropTable(boolean includeDropTable) {
        this.includeDropTable = includeDropTable;
    }
    
    /**
     * 设置是否包含CREATE TABLE语句
     */
    public void setIncludeCreateTable(boolean includeCreateTable) {
        this.includeCreateTable = includeCreateTable;
    }
    
    /**
     * 导出所有表的INSERT语句到SQL文件
     */
    public void exportAllTablesToSQL() throws Exception {
        List<String> tables = getAllTables();
        System.out.println("找到 " + tables.size() + " 个表");
        
        // 确保输出目录存在
        File outputFile = new File(outputFilePath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        try (FileWriter fw = new FileWriter(outputFile);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter writer = new PrintWriter(bw)) {
            
            // 写入SQL文件头部信息
            writeSQLHeader(writer);
            
            int totalRows = 0;
            int processedTables = 0;
            
            for (String tableName : tables) {
                processedTables++;
                System.out.printf("处理表 %d/%d: %s%n", processedTables, tables.size(), tableName);
                
                try {
                    int rows = exportTableData(tableName, writer);
                    totalRows += rows;
                    System.out.printf("  -> 导出 %d 行%n", rows);
                } catch (SQLException e) {
                    System.err.printf("  -> 处理表 %s 时出错: %s%n", tableName, e.getMessage());
                    writer.println("-- ERROR: 处理表 " + tableName + " 失败 - " + e.getMessage());
                }
            }
            
            // 写入SQL文件尾部信息
            writeSQLFooter(writer, tables.size(), totalRows);
            
            System.out.println("\n导出完成!");
            System.out.println("总表数: " + tables.size());
            System.out.println("总行数: " + totalRows);
            System.out.println("SQL文件路径: " + outputFilePath);
            
        } catch (IOException e) {
            throw new Exception("写入文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 写入SQL文件头部信息
     */
    private void writeSQLHeader(PrintWriter writer) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(new Date());
        
        writer.println("-- ===========================================");
        writer.println("-- MySQL 5.7 数据库导出SQL文件");
        writer.println("-- 生成时间: " + currentTime);
        writer.println("-- 数据库: " + getDatabaseName());
        writer.println("-- ===========================================");
        writer.println();
        
        // 设置MySQL选项
//        writer.println("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;");
//        writer.println("/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;");
//        writer.println("/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;");
//        writer.println("/*!50503 SET NAMES utf8mb4 */;");
//        writer.println("/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;");
//        writer.println("/*!40103 SET TIME_ZONE='+00:00' */;");
//        writer.println("/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;");
//        writer.println("/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;");
//        writer.println("/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;");
//        writer.println("/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;");
        writer.println();
//        writer.println("SET NAMES utf8mb4;");
//        writer.println("SET FOREIGN_KEY_CHECKS = 0;");
        writer.println();
    }
    
    /**
     * 写入SQL文件尾部信息
     */
    private void writeSQLFooter(PrintWriter writer, int tableCount, int totalRows) {
        writer.println();
        writer.println("-- ===========================================");
        writer.println("-- 导出统计");
        writer.println("-- 表数量: " + tableCount);
        writer.println("-- 总行数: " + totalRows);
        writer.println("-- ===========================================");
        writer.println();
        
        // 恢复MySQL选项
//        writer.println("/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;");
//        writer.println();
//        writer.println("/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;");
//        writer.println("/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;");
//        writer.println("/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;");
//        writer.println("/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;");
//        writer.println("/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;");
//        writer.println("/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;");
//        writer.println("/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;");
        writer.println();
        writer.println("-- 导出完成");
    }
    
    /**
     * 导出单个表的数据到SQL
     */
    private int exportTableData(String tableName, PrintWriter writer) throws SQLException {
        // 获取表的列信息
        List<ColumnInfo> columns = getTableColumns(tableName);
        if (columns.isEmpty()) {
            return 0;
        }
        
        // 写入表注释
        writer.println();
        writer.println("--");
        writer.println("-- 表结构及数据: " + tableName);
        writer.println("--");
        writer.println();
        
        // 如果需要，包含DROP TABLE语句
        if (includeDropTable) {
            writer.println("DROP TABLE IF EXISTS `" + tableName + "`;");
        }
        
        // 如果需要，包含CREATE TABLE语句
        if (includeCreateTable) {
            String createTableSQL = getCreateTableSQL(tableName);
            writer.println(createTableSQL + ";");
            writer.println();
        }
        
        // 构建列名字符串
        StringBuilder columnNames = new StringBuilder();
        for (ColumnInfo col : columns) {
            columnNames.append("`").append(col.name).append("`,");
        }
        columnNames.deleteCharAt(columnNames.length() - 1);
        
        // 分批查询数据
        int rowCount = 0;
        int batchSize = 1000;
        int offset = 0;
        
        List<String> insertStatements = new ArrayList<>();
        
        while (true) {
            String query = String.format(
                "SELECT %s FROM `%s` LIMIT %d OFFSET %d",
                columnNames, tableName, batchSize, offset);
            if("flyway_schema_history".equals(tableName))
                break;
            // !"flyway_schema_history".equals(tableName) &&
            if( 
                !"sys_ascription".equals(tableName) &&
                !"third_pay_line".equals(tableName) &&
                !"h5_order".equals(tableName) &&
                !"mkt_activity_coupon".equals(tableName) &&
                !"mkt_activity_linshi".equals(tableName) &&
                !"mkt_member_index_advert".equals(tableName) &&
                !"mkt_order_code".equals(tableName) &&
                !"mkt_order_delivery_msg".equals(tableName) &&
                !"mkt_three_gtype_sort".equals(tableName) &&
                !"sys_farmer_extend".equals(tableName))
            {
                query = String.format(
                    "SELECT %s FROM `%s` where ascription = 13 LIMIT %d OFFSET %d",
                    columnNames, tableName, batchSize, offset);
            }
            
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                int batchRowCount = 0;
                while (rs.next()) {
                    // 构建INSERT语句
                    String insertStmt = buildInsertStatement(tableName, columns, rs);
                    insertStatements.add(insertStmt);
                    
                    rowCount++;
                    batchRowCount++;
                }
                
                // 如果这一批没有数据或少于batchSize，说明已经取完所有数据
                if (batchRowCount == 0 || batchRowCount < batchSize) {
                    break;
                }
                
                offset += batchSize;
                
            } catch (SQLException e) {
                if (e.getErrorCode() == 1146) { // Table doesn't exist
                    System.err.printf("表 %s 不存在，跳过%n", tableName);
                    break;
                } else {
                    throw e;
                }
            }
        }
        
        // 批量写入INSERT语句，每100条一个事务
        if (!insertStatements.isEmpty()) {
            writer.println("-- 开始插入数据，共 " + insertStatements.size() + " 行");
            
            // 开始事务
            writer.println("START TRANSACTION;");
            
            int batchCount = 0;
            for (String insertStmt : insertStatements) {
                writer.println(insertStmt);
                batchCount++;
                
                // 每100条提交一次，避免事务过大
                if (batchCount % 100 == 0) {
                    writer.println("COMMIT;");
                    writer.println("START TRANSACTION;");
                }
            }
            
            // 提交剩余的事务
            writer.println("COMMIT;");
            writer.println();
        } else {
            writer.println("-- 表 " + tableName + " 为空，无数据");
            writer.println();
        }
        
        return rowCount;
    }
    
    /**
     * 构建INSERT语句
     */
    private String buildInsertStatement(String tableName, List<ColumnInfo> columns, ResultSet rs) 
            throws SQLException {
        
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO `").append(tableName).append("` VALUES (");
        
        for (int i = 0; i < columns.size(); i++) {
            ColumnInfo col = columns.get(i);
            Object value = rs.getObject(col.name);
            
            if (rs.wasNull()) {
                sql.append("NULL");
            } else {
                String escapedValue = escapeValueForMySQL57(value, col.dataType);
                // 确定域名后 进行替换
//                if(StringUtils.isNotBlank(escapedValue))
//                {
//                    if(escapedValue.contains("https://small.xinanshizu.com"))
//                        escapedValue.replace("https://small.xinanshizu.com", "");
//                    if(escapedValue.contains("https://cloudtest.xinanshizu.com"))
//                        escapedValue.replace("https://cloudtest.xinanshizu.com", "");
//                }
                sql.append(escapedValue);
            }
            
            if (i < columns.size() - 1) {
                sql.append(", ");
            }
        }
        
        sql.append(");");
        return sql.toString();
    }
    
    /**
     * 获取数据库名称
     */
    private String getDatabaseName() {
        try {
            return connection.getCatalog();
        } catch (SQLException e) {
            return "未知数据库";
        }
    }
    
    /**
     * 获取所有表名（MySQL 5.7兼容）
     */
    private List<String> getAllTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        
        // MySQL 5.7获取表名的方法
        String sql = "SHOW FULL TABLES WHERE Table_type = 'BASE TABLE'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }
    
    /**
     * 获取表的CREATE TABLE语句
     */
    private String getCreateTableSQL(String tableName) throws SQLException {
        String sql = null;
        String query = "SHOW CREATE TABLE `" + tableName + "`";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                sql = rs.getString(2); // 第二列是CREATE TABLE语句
            }
        }
        
        return sql != null ? sql : "";
    }
    
    /**
     * 获取表的列信息（MySQL 5.7兼容）
     */
    private List<ColumnInfo> getTableColumns(String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();
        
        // MySQL 5.7获取列信息的方法
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_TYPE, " +
                    "CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE " +
                    "FROM INFORMATION_SCHEMA.COLUMNS " +
                    "WHERE TABLE_SCHEMA = ? " +
                    "AND TABLE_NAME = ? " +
                    "ORDER BY ORDINAL_POSITION";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, connection.getCatalog()); // 当前数据库
            pstmt.setString(2, tableName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ColumnInfo col = new ColumnInfo();
                    col.name = rs.getString("COLUMN_NAME");
                    col.dataType = rs.getString("DATA_TYPE").toUpperCase();
                    col.nullable = "YES".equals(rs.getString("IS_NULLABLE"));
                    col.columnType = rs.getString("COLUMN_TYPE");
                    columns.add(col);
                }
            }
        }
        return columns;
    }
    
    /**
     * 转义SQL值（MySQL 5.7兼容）
     */
    private String escapeValueForMySQL57(Object value, String dataType) {
        if (value == null) {
            return "NULL";
        }
        
        String strValue = value.toString();
        
        // 根据MySQL 5.7数据类型处理
        switch (dataType) {
            case "INT":
            case "INTEGER":
            case "BIGINT":
            case "MEDIUMINT":
            case "SMALLINT":
            case "TINYINT":
            case "DECIMAL":
            case "NUMERIC":
            case "FLOAT":
            case "DOUBLE":
            case "BIT":
                return strValue;
                
            case "DATE":
                return "'" + strValue + "'";
                
            case "DATETIME":
            case "TIMESTAMP":
                // MySQL 5.7时间格式处理
                if (strValue.contains(".")) {
                    // 包含微秒的时间戳
                    return "'" + strValue + "'";
                } else {
                    return "'" + strValue + "'";
                }
                
            case "TIME":
                return "'" + strValue + "'";
                
            case "YEAR":
                return strValue;
                
            case "CHAR":
            case "VARCHAR":
            case "TINYTEXT":
            case "TEXT":
            case "MEDIUMTEXT":
            case "LONGTEXT":
            case "ENUM":
            case "SET":
                // 转义特殊字符
                return "'" + escapeStringForSQL(strValue) + "'";
                
            case "BINARY":
            case "VARBINARY":
            case "TINYBLOB":
            case "BLOB":
            case "MEDIUMBLOB":
            case "LONGBLOB":
                // BLOB类型 - 转换为十六进制
                try {
                    if (value instanceof byte[]) {
                        byte[] bytes = (byte[]) value;
                        return "0x" + bytesToHex(bytes);
                    } else {
                        // 尝试作为字符串处理
                        return "'" + escapeStringForSQL(strValue) + "'";
                    }
                } catch (Exception e) {
                    return "NULL";
                }
                
            case "JSON":
                // MySQL 5.7.8+支持JSON类型
                return "'" + escapeStringForSQL(strValue) + "'";
                
            default:
                // 未知类型，按字符串处理
                return "'" + escapeStringForSQL(strValue) + "'";
        }
    }
    
    /**
     * 转义字符串中的特殊字符
     */
    private String escapeStringForSQL(String str) {
        if (str == null) return "";
        
        return str.replace("\\", "\\\\")
                  .replace("'", "''")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("\0", "\\0")
                  .replace("\b", "\\b")
                  .replace("\u001A", "\\Z"); // Ctrl+Z
    }
    
    /**
     * 将字节数组转为十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * 获取数据库信息
     */
    public void printDatabaseInfo() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT VERSION()")) {
            if (rs.next()) {
                System.out.println("MySQL版本: " + rs.getString(1));
            }
        }
        
        System.out.println("当前数据库: " + getDatabaseName());
    }
    
    /**
     * 测试数据库连接
     */
    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("数据库连接已关闭");
            }
        } catch (SQLException e) {
            System.err.println("关闭连接时出错: " + e.getMessage());
        }
    }
    
    /**
     * 列信息类
     */
    private static class ColumnInfo {
        String name;
        String dataType;
        boolean nullable;
        String columnType;
    }
    
    /**
     * 主方法 - 使用示例
     */
    public static void main(String[] args) {
        // 数据库配置（MySQL 5.7）
        String host = "192.168.128.94";
        int port = 3306;
        String database = "zdw_test";
        String username = "root";
        String password = System.getenv().getOrDefault("MYSQL_PASSWORD", "CHANGE_ME");
        
        // 输出SQL文件路径
        String outputFile = "D:/Users/29701/Desktop/aa/16_data.sql";
        
        MySQL57ExportToSQL exporter = null;
        
        try {
            System.out.println("开始导出MySQL 5.7数据库到SQL文件...");
            
            // 创建导出器
            exporter = new MySQL57ExportToSQL(host, port, database, 
                                             username, password, outputFile);
            
            // 设置选项
            exporter.setIncludeDropTable(false);     // 包含DROP TABLE语句
            exporter.setIncludeCreateTable(false);   // 包含CREATE TABLE语句
            
            // 测试连接
            if (!exporter.testConnection()) {
                System.err.println("数据库连接失败！");
                return;
            }
            
            // 打印数据库信息
            exporter.printDatabaseInfo();
            System.out.println("=");
            
            // 执行导出
            long startTime = System.currentTimeMillis();
            exporter.exportAllTablesToSQL();
            long endTime = System.currentTimeMillis();
            
            System.out.println("=");
            System.out.printf("导出总耗时: %.2f 秒%n", (endTime - startTime) / 1000.0);
            
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动未找到！");
            System.err.println("请将mysql-connector-java-5.1.xx.jar添加到classpath");
            System.err.println("下载地址: https://dev.mysql.com/downloads/connector/j/5.1.html");
        } catch (SQLException e) {
            System.err.println("数据库错误: " + e.getMessage());
            System.err.println("错误代码: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("导出失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (exporter != null) {
                exporter.close();
            }
        }
    }
}
