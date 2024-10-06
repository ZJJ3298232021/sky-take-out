package com.sky.constant;

public class SqlExceptionConstant {
    // 表示尝试插入重复的条目
    public static final String DUPLICATE_ENTRY = "Duplicate entry";

    // 表示访问被拒绝，通常由于权限问题
    public static final String ACCESS_DENIED = "Access denied for user";

    // 表示SQL语法错误
    public static final String SQL_SYNTAX_ERROR = "You have an error in your SQL syntax";

    // 表示违反了完整性约束
    public static final String INTEGRITY_CONSTRAINT_VIOLATION = "Integrity constraint violation";

    // 表示尝试执行不支持的操作
    public static final String FUNCTION_NOT_SUPPORTED = "Function not supported";

    // 表示数据库死锁
    public static final String DEADLOCK = "Deadlock found when trying to get lock";

    // 表示数据库连接超时
    public static final String CONNECTION_TIMEOUT = "Connection timed out";

    // 表示查询超时
    public static final String QUERY_TIMEOUT = "Query timeout";

    // 表示无法将结果集转换为期望的类型
    public static final String DATA_CONVERSION_ERROR = "Data conversion error";

    // 表示尝试访问不存在的表
    public static final String UNKNOWN_TABLE = "Unknown table";

    // 表示尝试访问不存在的列
    public static final String UNKNOWN_COLUMN = "Unknown column";

    // 表示数据库查询返回的行数超过预期
    public static final String TOO_MANY_ROWS = "Too many rows";

    // 表示数据库查询返回的行数为零
    public static final String NO_ROWS_FOUND = "No rows found";

    // 可以根据需要继续添加其他异常常量...
}