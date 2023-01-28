package lurenjia.sorm.utils;

import lurenjia.sorm.bean.ColumnInfo;
import lurenjia.sorm.bean.JavaFieldGetSet;
import lurenjia.sorm.bean.TableInfo;
import lurenjia.sorm.core.DBManager;
import lurenjia.sorm.core.TypeConvertor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Java源文件生成工具：
 * 1、生成数据库中表对应的JavaBean类文件。
 * @author lurenjia
 * @date 2022/12/2-15:41
 */
public class JavaFileUtils {
    private JavaFileUtils(){}

    /**
     * 根据字段生成java属性信息，及其set、get方法。
     * @param colum 字段信息
     * @param convertor 类型转换器
     * @return java代码（定义属性，属性对应的set、get方法）
     */
    private static JavaFieldGetSet creatFieldGetSetSrc(ColumnInfo colum, TypeConvertor convertor){
        JavaFieldGetSet jfgs = new JavaFieldGetSet();
        String javaFieldType = convertor.databaseType2JavaType(colum.getDataType());
        //定义变量:private String username
        jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+colum.getName()+";\n");

        //get方法：public String getUsername(){return username;}
        StringBuilder getSrc = new StringBuilder();
        getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(colum.getName())+"(){\n");
        getSrc.append("\t\treturn "+colum.getName()+";\n");
        getSrc.append("\t}");
        jfgs.setGetInfo(getSrc.toString());

        //set方法：public void setUsername(String username){this.username=username;}
        StringBuilder setSrc = new StringBuilder();
        setSrc.append("\tpublic void set"+StringUtils.firstChar2UpperCase(colum.getName())+"(");
        setSrc.append(javaFieldType+" "+colum.getName()+"){\n");
        setSrc.append("\t\tthis."+colum.getName()+"="+colum.getName()+";\n");
        setSrc.append("\t}");
        jfgs.setSetInfo(setSrc.toString());

        return jfgs;
    }

    /**
     * 根据表信息生成对应的JavaBean类源码。
     * @param tableInfo 表信息
     * @param convertor 类型转换器
     * @return JavaBean源码
     */
    private static String creatJavaSrc(TableInfo tableInfo,TypeConvertor convertor){
        Map<String,ColumnInfo> columns = tableInfo.getColumns();
        List<JavaFieldGetSet> javaFields = new ArrayList<>();

        //把表中字段信息全部生成对应的源码后，放入list集合中。
        for(ColumnInfo c:columns.values()){
            javaFields.add(creatFieldGetSetSrc(c,convertor));
        }

        StringBuilder sb = new StringBuilder();
        //package语句
        sb.append("package "+ DBManager.getConf().getPoPackage()+";\n\n");
        //import语句
        sb.append("import java.sql.*;\n");
        sb.append("import java.util.*;\n\n");
        //类声明语句
        sb.append("public class "+StringUtils.firstChar2UpperCase(tableInfo.getName())+" {\n\n");
        //属性
        for(JavaFieldGetSet f:javaFields){
            sb.append(f.getFieldInfo());
        }
        sb.append("\n\n");
        //get方法
        for(JavaFieldGetSet f:javaFields){
            sb.append(f.getGetInfo());
            sb.append("\n");
        }
        //set方法
        for(JavaFieldGetSet f:javaFields){
            sb.append(f.getSetInfo());
            sb.append("\n");
        }
        //结束  }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 生成表对应的JavaBean文件在配置文件指定的包中。
     * @param tableInfo 表信息
     * @param convertor 类型转换器
     */
    public static void creatJavaPoFile(TableInfo tableInfo,TypeConvertor convertor){
        
        String src = creatJavaSrc(tableInfo,convertor);

        String srcPath = DBManager.getConf().getSrcPath()+"/";
        String pacKage = DBManager.getConf().getPoPackage().replaceAll("\\.","/");

        File f = new File(srcPath+pacKage);
        if(!f.exists()){
            f.mkdir();//如果指定目录不存在，则建立它。
        }

        BufferedWriter bw = null;
        try{
            bw = new BufferedWriter(new FileWriter(f.getAbsolutePath()+"/"+StringUtils.firstChar2UpperCase(tableInfo.getName())+".java"));
            bw.write(src);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(null!=bw){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
