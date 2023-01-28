package lurenjia.sorm.bean;

/**
 * 封装了动态生成的源代码，包括javabean属性、set、get方法
 * @author lurenjia
 * @date 2022/12/2-21:14
 */
public class JavaFieldGetSet {
    /**
     * 内容为定义一个属性
     */
    private String fieldInfo;
    /**
     * 内容为属性的get方法
     */
    private String getInfo;
    /**
     * 内容为属性的set方法
     */
    private String setInfo;

    @Override
    public String toString() {
        return fieldInfo+"\n"+getInfo+"\n"+setInfo;
    }

    public JavaFieldGetSet() {
    }

    public JavaFieldGetSet(String fieldInfo, String getInfo, String setInfo) {
        this.fieldInfo = fieldInfo;
        this.getInfo = getInfo;
        this.setInfo = setInfo;
    }

    public String getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(String fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public String getGetInfo() {
        return getInfo;
    }

    public void setGetInfo(String getInfo) {
        this.getInfo = getInfo;
    }

    public String getSetInfo() {
        return setInfo;
    }

    public void setSetInfo(String setInfo) {
        this.setInfo = setInfo;
    }
}
