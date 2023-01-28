package lurenjia.sorm.core;

import lurenjia.sorm.bean.ColumnInfo;
import lurenjia.sorm.bean.TableInfo;
import lurenjia.sorm.utils.JavaFileUtils;
import lurenjia.sorm.utils.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库上下文：
 * 封装了指定数据库所有的表内信息。
 * 包括表名对应表信息TableInfo，Javabean对应表信息TableInfo。
 * @author lurenjia
 * @date 2022/12/2-15:37
 */
public class TableContext {

    /**
     * 表名为key,表信息对象为value。
     */
    public static Map<String, TableInfo> tables = new HashMap<>();

    /**
     * 将po的class对象和表信息关联起来，便于重用。
     */
    public static Map<Class,TableInfo> poClassTableMap = new HashMap<>();

    public TableContext(){}

    static {//初始化获取表的信息
        Connection con = null;
        try{
            con = DBManager.getCon();
            DatabaseMetaData dbmd = con.getMetaData();

            //把指定数据库的表信息存放到结果集
            ResultSet tableSet = dbmd.getTables(null,"%","%",new String[]{"TABLE"});

            //遍历表信息
            while (tableSet.next()){
                //获取表名
                String tableName = (String) tableSet.getObject("TABLE_NAME");

                //创建一个表信息
                TableInfo ti = new TableInfo(tableName,
                        new ArrayList<ColumnInfo>(),
                        new HashMap<String,ColumnInfo>());

                tables.put(tableName,ti);

                //根据表名获取字段信息（字段全部设置为普通键）
                ResultSet set = dbmd.getColumns(null,"%",tableName,"%");
                while(set.next()){
                    //创建一个字段信息
                    ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"),
                            set.getString("TYPE_NAME"),
                            0);
                    //把字段加入表信息中
                    ti.getColumns().put(set.getString("COLUMN_NAME"),ci);
                }

                //查询表中的主键
                ResultSet set2 = dbmd.getPrimaryKeys(null,"%",tableName);
                while(set2.next()){
                    ColumnInfo ci2 = (ColumnInfo) ti.getColumns().get(set2.getObject("COLUMN_NAME"));
                    ci2.setKeyType(1);//设置为主键类型
                    ti.getPriKey().add(ci2);
                }

                //获取唯一主键，如果是联合主键则为空。
                if(ti.getPriKey().size()>0){
                    ti.setOnlyPriKey(ti.getPriKey().get(0));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            DBManager.close(con);
        }
        //更新类结构
        updateJavaPoFile();
        //加载po包下的所有类信息
        loadPoTable();
    }

    /**
     * 根据表结构，更新配置文件中指定的po包下的javabean类文件。
     * 实现了表结构转换到类结构。
     */
    public static void updateJavaPoFile(){
        Map<String, TableInfo> map = TableContext.tables;//获取表信息
        for(TableInfo t: map.values()){
            JavaFileUtils.creatJavaPoFile(t,new MysqlTypeConvertor());
        }
    }

    /**
     * 把所有的JavaBean类加载到poClassTableMap容器中。
     */
    public static void loadPoTable(){
        for(TableInfo t:tables.values()){
            try{
                Class c = Class.forName(DBManager.getConf().getPoPackage()
                                        +"."+ StringUtils.firstChar2UpperCase(t.getName()));
                poClassTableMap.put(c, t);
            }catch (ClassNotFoundException e){
                System.out.println("成功创建对应的源文件，但是读取失败，请再次启动程序.....");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
