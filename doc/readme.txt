准备：
1、在src下建立db.properties。配置信息
2、只支持有且只有一个主键的表操作。
3、po属性使用包装类，不使用基本数据类型。（便于查询是否为空）

使用：
1、建议先调用QueryFactory.createQuery().creatJavaBean();方法创建对应数据库的JavaBean类。
2、若要联表查询，需要自己创建目的Javabean对象。