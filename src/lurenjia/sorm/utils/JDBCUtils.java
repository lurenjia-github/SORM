package lurenjia.sorm.utils;

import java.sql.*;

/**
 * JDBC工具类：
 * 1、提供给sql语句设置参数的方法。
 * @author lurenjia
 * @date 2022/12/2-15:39
 */
public class JDBCUtils {
    private JDBCUtils(){}

    /**
     * 给sql语句设置参数
     * @param ps PreparedStatement对象
     * @param params 参数
     */
    public static void handleParams(PreparedStatement ps,Object[] params){
        if(params!=null){
            for(int i = 0;i<params.length;i++){
                try{
                    ps.setObject(i+1,params[i]);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
