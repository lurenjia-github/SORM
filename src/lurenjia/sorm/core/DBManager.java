package lurenjia.sorm.core;

import lurenjia.sorm.bean.Configuration;
import lurenjia.sorm.pool.DBConnPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * 管理数据库的连接，包括读取配置信息，获取连接，释放连接。
 * @author lurenjia
 * @date 2022/12/2-15:38
 */
public class DBManager {
    /**
     * 配置信息
     */
    private static Configuration conf;
    /**
     * 连接池
     */
    private static DBConnPool pool;

    static {//只会加载一次，获取配置信息
        Properties pros = new Properties();
        try{
            pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
        }catch (Exception e){
            e.printStackTrace();
        }
        conf = new Configuration();
        conf.setDriver(pros.getProperty("driver"));
        conf.setPoPackage(pros.getProperty("poPackage"));
        conf.setPwd(pros.getProperty("pwd"));
        conf.setSrcPath(pros.getProperty("srcPath"));
        conf.setUrl(pros.getProperty("url"));
        conf.setUser(pros.getProperty("user"));
        conf.setUsingDB(pros.getProperty("usingDB"));
        conf.setQueryClass(pros.getProperty("queryClass"));
        conf.setPoolMax(Integer.parseInt(pros.getProperty("poolMax")));
        conf.setPoolMin(Integer.parseInt(pros.getProperty("poolMin")));
        //加载表信息
        try {
            TableContext s =TableContext.class.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对外提供获取资源信息的方法。
     * @return 存有资源信息的Configuration对象
     */
    public static Configuration getConf() {
        return conf;
    }

    /**
     * 创建一个数据库连接。
     * @return 数据库连接。
     */
    public static Connection creatCon(){
        try {
            Class.forName(conf.getDriver());
            return DriverManager.getConnection(
                    conf.getUrl(),
                    conf.getUser(),
                    conf.getPwd());
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
    /**
     * 获取一个数据库连接,从连接池中取出一个连接。
     * @return 数据库连接
     */
    public static Connection getCon(){
        if(pool==null){//第一次获取连接，初始化连接池
            pool=new DBConnPool();
        }
        return pool.getConnection();
    }
    /**
     * 释放资源，把连接放回池中。
     * @param c Connection对象
     * @param s Statement对象
     */
    public static void close(Connection c, Statement s){
        if(s!=null){
            try {
                s.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        close(c);
    }

    /**
     * 把连接放回连接池中
     * @param c 连接
     */
    public static void close(Connection c){
        pool.close(c);
    }
}
