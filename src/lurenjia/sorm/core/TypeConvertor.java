package lurenjia.sorm.core;

/**
 * 类型转换接口：
 * 负责Java数据类型与数据库类型的相互转换。
 * @author lurenjia
 * @date 2022/12/2-15:33
 */
public interface TypeConvertor {
    /**
     * 将数据库类型转化成java数据类型
     * @param columnType 数据库字段的数据类型
     * @return java的数据类型
     */
    public String databaseType2JavaType(String columnType);

    /**
     * 将java数据类型装为数据库数据类型
     * @param javaDataType Java数据类型
     * @return 数据库的数据类型
     */
    public String javaType2DatabaseType(String javaDataType);
}
