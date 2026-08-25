package cn.tofocus.lejia.util.mysql;


import java.io.*;
import java.sql.*;
import java.util.*;

public class MySQL57ExportToCSV {
    
    private Connection connection;
    private String outputFilePath;
    private static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    
    public MySQL57ExportToCSV(String host, int port, String database, 
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
     * 导出所有表的INSERT语句到CSV
     */
    public void exportAllTablesToCSV() throws Exception {
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
            
            // 写入CSV头部（MySQL 5.7兼容格式）
            writer.println("\"表名\",\"INSERT语句\"");
            
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
                    // 写入错误信息到CSV
                    writer.println(String.format("\"%s\",\"ERROR: %s\"", 
                        escapeForCSV(tableName), 
                        escapeForCSV("处理失败: " + e.getMessage())));
                }
            }
            
            System.out.println("\n导出完成!");
            System.out.println("总表数: " + tables.size());
            System.out.println("总行数: " + totalRows);
            System.out.println("文件路径: " + outputFilePath);
            
        } catch (IOException e) {
            throw new Exception("写入文件失败: " + e.getMessage(), e);
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
     * 导出单个表的数据
     */
    private int exportTableData(String tableName, PrintWriter writer) throws SQLException {
        // 获取表的列信息（MySQL 5.7兼容）
        List<ColumnInfo> columns = getTableColumns(tableName);
        if (columns.isEmpty()) {
            return 0;
        }
        
        // 构建列名字符串
        StringBuilder columnNames = new StringBuilder();
        for (ColumnInfo col : columns) {
            columnNames.append("`").append(col.name).append("`,");
        }
        columnNames.deleteCharAt(columnNames.length() - 1); // 删除最后一个逗号
        
        // 查询数据 - 使用LIMIT避免内存溢出
        int rowCount = 0;
        int batchSize = 1000;
        int offset = 0;
        
        while (true) {
            String query = String.format(
                "SELECT %s FROM `%s` LIMIT %d OFFSET %d",
                columnNames, tableName, batchSize, offset);
            if(!"flyway_schema_history".equals(tableName) && 
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
                    StringBuilder insertStmt = new StringBuilder();
                    insertStmt.append("INSERT INTO `").append(tableName)
                             .append("` (").append(columnNames).append(") VALUES (");
                    
                    // 处理每一列的值
                    for (int i = 0; i < columns.size(); i++) {
                        ColumnInfo col = columns.get(i);
                        Object value = rs.getObject(col.name);
                        
                        if (rs.wasNull()) {
                            insertStmt.append("NULL");
                        } else {
                            // 根据数据类型处理
                            String escapedValue = escapeValueForMySQL57(value, col.dataType);
                            insertStmt.append(escapedValue);
                        }
                        
                        if (i < columns.size() - 1) {
                            insertStmt.append(", ");
                        }
                    }
                    
                    insertStmt.append(");");
                    
                    // 写入CSV（转义双引号）
                    String escapedInsert = escapeForCSV(insertStmt.toString());
                    writer.println(String.format("\"%s\",\"%s\"", 
                        escapeForCSV(tableName), 
                        escapedInsert));
                    
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
        
        return rowCount;
    }
    
    /**
     * 获取表的列信息（MySQL 5.7兼容）
     */
    private List<ColumnInfo> getTableColumns(String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();
        
        // MySQL 5.7获取列信息的方法
        String sql = "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_TYPE " +
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
                // 转义单引号、反斜杠和特殊字符
                String escaped = strValue
                    .replace("\\", "\\\\")
                    .replace("'", "''")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                    .replace("\0", "\\0")
                    .replace("\b", "\\b")
                    .replace("\u001A", "\\Z"); // Ctrl+Z
                return "'" + escaped + "'";
                
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
                        return "'" + strValue.replace("'", "''") + "'";
                    }
                } catch (Exception e) {
                    return "NULL";
                }
                
            case "JSON":
                // MySQL 5.7.8+支持JSON类型
                return "'" + strValue.replace("'", "''") + "'";
                
            default:
                // 未知类型，按字符串处理
                return "'" + strValue.replace("'", "''") + "'";
        }
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
     * CSV转义（将双引号转义为两个双引号）
     */
    private String escapeForCSV(String value) {
        if (value == null) return "";
        // 转义双引号，并处理换行符
        return value.replace("\"", "\"\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
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
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DATABASE()")) {
            if (rs.next()) {
                System.out.println("当前数据库: " + rs.getString(1));
            }
        }
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
        String columnType; // 完整的列类型，如 INT(11)
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
        
        // 输出文件路径
        String outputFile = "D:/Users/29701/Desktop/aa/all_tables_inserts.csv";
        
        MySQL57ExportToCSV exporter = null;
        
        try {
            System.out.println("开始导出MySQL 5.7数据库...");
            
            // 创建导出器
            exporter = new MySQL57ExportToCSV(host, port, database, 
                                             username, password, outputFile);
            
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
            exporter.exportAllTablesToCSV();
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
