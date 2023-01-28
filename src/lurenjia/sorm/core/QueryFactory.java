package lurenjia.sorm.core;

/**
 * Query工厂，根据配置信息创建Query对象
 * @author lurenjia
 * @date 2022/12/2-15:31
 */
public class QueryFactory {

    /**
     * 工厂持有一个Query类的原型对象
     */
    private static Query prototypeObj;//原型对象

    /**
     * 加载指定的query类
     */
    static {
        try {
            Class c =Class.forName(DBManager.getConf().getQueryClass());
            prototypeObj = (Query) c.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private QueryFactory(){ }

    /**
     * 原型模式：通过克隆获取Query实例
     * @return Query实例
     */
    public static Query createQuery(){
        try {
            return (Query) prototypeObj.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
