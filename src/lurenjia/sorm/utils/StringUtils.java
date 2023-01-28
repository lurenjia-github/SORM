package lurenjia.sorm.utils;

/**
 * 字符串操作工具：
 * 1、首字母转为大写。
 * @author lurenjia
 * @date 2022/12/2-15:40
 */
public class StringUtils {
    private StringUtils(){}

    /**
     * 把字符串首字母变成大写
     * @param str 传进来的字符串
     * @return 变为大写开头的字符串
     */
    public static String firstChar2UpperCase(String str){
        return str.toUpperCase().substring(0,1)+str.substring(1);
    }
}
