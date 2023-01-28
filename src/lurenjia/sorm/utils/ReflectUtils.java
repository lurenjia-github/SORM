package lurenjia.sorm.utils;

import java.lang.reflect.Method;

/**
 * 反射工具：
 * 1、通过反射获取JavaBean对象的值
 * 2、通过反射设置JavaBean对象的值
 * @author lurenjia
 * @date 2022/12/2-15:40
 */
public class ReflectUtils {
    /**
     * 调用obj对象对应属性的get方方法获取属性值
     * @param fieldName 属性名
     * @param obj 对象
     * @return 属性值
     */
    public static Object invokeGet(String fieldName,Object obj){
        try{
            Class c =obj.getClass();
            Method m = c.getMethod("get"+StringUtils.firstChar2UpperCase(fieldName),null);
            return m.invoke(obj,null);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 调用obj对象对应属性的set方方法设置属性值
     * @param fieldName 属性名
     * @param obj 对象
     * @param value  需要设置的值
     * @return 设置成功，返回1，失败放回-1
     */
    public static void invokeSet(String fieldName,Object obj,Object value){
        try{
            if(value!=null) {
                Class c = obj.getClass();
                Method m = c.getMethod("set" + StringUtils.firstChar2UpperCase(fieldName), value.getClass());
                m.invoke(obj, value);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
