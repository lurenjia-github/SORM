package lurenjia.sorm.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 具体查询操作的统一接口。
 * @author lurenjia
 * @date 2022/12/3-16:36
 */
public interface CallBack {
    /**
     * 具体的查询操作
     * @param conn 连接
     * @param ps Statement对象
     * @param rs 结果集
     * @return 结果
     */
    public Object doexEcute(Connection conn, PreparedStatement ps, ResultSet rs);
}
