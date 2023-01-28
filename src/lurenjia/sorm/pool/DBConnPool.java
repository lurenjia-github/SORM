package lurenjia.sorm.pool;

import lurenjia.sorm.core.DBManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接池：
 * 根据配置文件确定池中连接的最小值和最大值。
 * 使用连接池，可以在需要多次获取连接时大大节省资源。
 * @author lurenjia
 * @date 2022/12/3-22:51
 */
public class DBConnPool {
    /**
     * 连接池
     */
    private static  List<Connection> pool;

    /**
     * 连接池最大容量
     */
    private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMax();
    /**
     * 连接池最小容量
     */
    private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMin();

    public DBConnPool(){
        initPoll();
    }
    /**
     * 初始化连接池
     */
    public synchronized void initPoll(){
        if(pool==null){
            pool = new ArrayList<>();
        }
        while (pool.size()<POOL_MIN_SIZE){
            pool.add(DBManager.creatCon());
        }
    }

    /**
     * 从池中取出一个连接
     * @return 取出一个连接
     */
    public synchronized Connection getConnection(){
        if(pool.size()>0) {
            Connection c = pool.get(pool.size() - 1);
            pool.remove(pool.size() - 1);
            return c;
        }else {
            initPoll();
            Connection c = pool.get(pool.size() - 1);
            pool.remove(pool.size() - 1);
            return c;
        }
    }

    /**
     * 关闭一个连接：如果池中连接数量小于最大值则把连接放入池中
     * @param c 要关闭的连接
     */
    public static synchronized void close(Connection c){
        if(pool.size()>=POOL_MAX_SIZE){
            try{
                if(c!=null) {
                    c.close();
                }
            }catch (Exception e){e.printStackTrace();}
        }else {
            pool.add(c);
        }
    }

}
