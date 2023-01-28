package lurenjia.sorm.core;
import lurenjia.sorm.bean.ColumnInfo;
import lurenjia.sorm.bean.TableInfo;
import lurenjia.sorm.utils.JDBCUtils;
import lurenjia.sorm.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象类：
 * 负责对外提供服务的核心类。
 * 包括了增、删、改、查数据库信息。
 * 采用原型模式，支持被克隆。
 * @author lurenjia
 * @date 2022/12/2-15:02
 */
@SuppressWarnings("all")
public abstract class Query implements Cloneable{

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    /**
     * 模板方法模式：查询操作中，获取连接、参数，关闭连接是固定的。
     * @param sql 查询语句
     * @param params 参数
     * @param clazz 对应JavaBean类
     * @param back 回调函数
     * @return 结果集
     */
    public Object executeQueryTemplate(String sql,Object[] params,Class clazz,CallBack back){
        Connection conn = DBManager.getCon();
        java.util.List list =null;//存放结果
        PreparedStatement ps =null;
        ResultSet rs =null;
        try{
            ps = conn.prepareStatement(sql);
            JDBCUtils.handleParams(ps,params);
            rs = ps.executeQuery();

            return back.doexEcute(conn,ps,rs);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DBManager.close(conn,ps);
        }
    }
    /**
     * 执行一个DML语句
     * @param sql sql语句
     * @param params 参数
     * @return 影响记录的行数
     */
    private int executeDML(String sql, Object[] params) {
        Connection conn = DBManager.getCon();
        int count = 0;
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement(sql);
            //设置参数
            JDBCUtils.handleParams(ps,params);
            count = ps.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBManager.close(conn,ps);
        }
        return count;
    }
    /**
     * 将一个对象存入数据库中
     * @param obj 要存储的对象
     */
    public void insert(Object obj) {
        Class c = obj.getClass();
        java.util.List<Object> params = new ArrayList<>();//准备一个存放参数的容器
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);//获取对应的表信息
        StringBuilder sql = new StringBuilder("insert into "+tableInfo.getName()+" (");
        int countNotNullField = 0;//不为空的参数个数
        Field[] fs = c.getDeclaredFields();//获取所有参数
        for(Field f:fs){
            String fieldName =f.getName();//参数名
            Object fieldValue = ReflectUtils.invokeGet(fieldName,obj);//参数值

            if(fieldValue!=null){//参数值为空就不用加了
                countNotNullField++;
                sql.append(fieldName+",");
                params.add(fieldValue);
            }
        }

        sql.setCharAt(sql.length()-1,')');
        sql.append(" values (");
        for(int i=0;i<countNotNullField;i++){
            sql.append("?,");
        }
        sql.setCharAt(sql.length()-1,')');
        executeDML(sql.toString(),params.toArray());
    }
    /**
     * 删除clazz类对应的表中记录（通过指定主键值id）
     * @param clazz 和表对应的Class对象
     * @param id 主键的值
     */
    public void delect(Class clazz, Object id) {
        //获取表信息
        TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
        //获取主键
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
        //delete from table_name where id=?
        String sql = "delete from "+tableInfo.getName()+" where "+onlyPriKey.getName()+"=?";
        //执行sql语句
        executeDML(sql,new Object[]{id});
    }

    /**
     * 删除对象在数据库中对应的记录（对象所在的类对应数据表，对象的主键值对应到数据）
     * @param obj 要删除的对象
     */
    public void delect(Object obj) {
        Class c = obj.getClass();
        //获取表信息
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);
        //获取主键
        ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
        //获取主键的值
        Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
        //执行删除
        delect(c,priKeyValue);

    }

    /**
     * 更新对象对应的记录，并且只更新指定的字段
     * @param obj 需要更新的对象
     * @param fieldNames 更新的属性列表
     * @return 影响记录的行数
     */
    public int update(Object obj, String[] fieldNames) {
        Class c = obj.getClass();
        java.util.List<Object> params = new ArrayList<>();//准备一个存放参数的容器
        TableInfo tableInfo = TableContext.poClassTableMap.get(c);//获取对应的表信息
        ColumnInfo priKey = tableInfo.getOnlyPriKey();//获取主键信息
        //update table_name set name=?,pwd=? where id=?
        StringBuilder sql =new StringBuilder("update "+tableInfo.getName()+" set ");

        for(String fname:fieldNames){
            Object fvalue = ReflectUtils.invokeGet(fname,obj);//获取属性的值
            params.add(fvalue);
            sql.append(fname+"=?,");
        }
        sql.setCharAt(sql.length()-1,' ');
        sql.append(" where ");
        sql.append(priKey.getName()+"=? ");
        //根据主键来更新对象
        params.add(ReflectUtils.invokeGet(priKey.getName(), obj));
        return executeDML(sql.toString(),params.toArray());
    }

    /**
     * 查询返回多行记录,记录存储在list容器中，为数据表对应的JavaBean对象。
     * @param sql 查询语句
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询结果
     */
    public List queryRows(String sql, Class clazz, Object[] params) {
        return (List) executeQueryTemplate(sql,params,clazz,(con, ps, rs)->{
            try {
                List list = new ArrayList();
                ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    //创建一个bean对象
                    Object rowObj = clazz.newInstance();
                    for (int i = 0; i < metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnLabel(i + 1);
                        Object columnValue = rs.getObject(i + 1);
                        ReflectUtils.invokeSet(columnName, rowObj, columnValue);
                    }
                    list.add(rowObj);
                }
                return list;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * 查询返回一行记录，并且把记录封装到clazz类的对象中
     * @param sql 查询语句
     * @param clazz 封装数据的javabean类的Class对象
     * @param params sql的参数
     * @return 查询结果
     */
    public Object queryUniqueRows(String sql, Class clazz, Object[] params) {
        List list = queryRows(sql,clazz,params);
        return (list==null||list.size()>1)?null:list.get(0);
    }

    /**
     * 查询返回一个值，并且把记录封装到clazz类的对象中
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询结果
     */
    public Object queryValue(String sql, Object[] params) {
        return executeQueryTemplate(sql,null,null,(con,ps,rs)->{
            Object value = null;
            try {
                while (rs.next()) {
                    value = rs.getObject(1);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return value;
        });
    }

    /**
     * 查询返回一个数字，并且把记录封装到clazz类的对象中
     * @param sql 查询语句
     * @param params sql的参数
     * @return 查询结果
     */
    public Number queryNumber(String sql, Object[] params) {
        return (Number) queryValue(sql,params);
    }

    /**
     * 在配置文件的po包下，创建数据库对应的JavaBean类
     */
    public void creatJavaBean(){
        TableContext.updateJavaPoFile();
    }
}
