## hibernate框架介绍

是一个持久层框架，实现JDBC封装，是一个全自动ORM框架

**orm（对象 关系(表) 映射**

orm致力于将数据库操作转换成程序熟悉的对象操作

orm主要体现在两个方面：java程序通过jdbc与数据库程序交互，类似请求->响应

1. [^请求]: orm在请求时，由java程序向数据库传递sql时

   通过操作的对象对应操作的表

   通过操作对象的属性操作对应表的字段

   save(User) ---->orm:insert into user (name,pass) values (User.getName(),User.getPass());

2. [^响应]: 一般对应查询操作，且返回查询结果

   将ResultSet集合中装载的查询结果组装成对应的实体对象

   请求时orm：

   ```sql
   get(User.class)---->orm:select name,pass from user;
   ResultSet rs = pstat.excuteQuery();
   ```

   响应时orm：

   ```java
   User user = new User();
   user.setName(rs.get("name"));
   user.setPass(rs.get("pass"));
   ```

**全自动orm 和 半自动orm**

- Hibernate全自动orm

  - 在请求和响应过程中，都是用orm机制自动进行处理

    ```java
    session.save(user);
    User user = session.get(User.class,1);
    ```

- Mybatis半自动orm

  - 在请求过程中，操作对象与对应的表的关系，依然需要使用手动编写的sql体现

    ```java
    sqlSession.insert("insert",user);
    ---
    <insert id = "insert">
      	insert into user (name,pass) values (#{name},#{pass});
    </insert>
    ```

  - 在响应过程中，将查询结果自动组装成对象

    ```java
    User user = sqlSession.selectOne("findUser",1);
    ---
    <select resultType="user" id = "findUser">
    		select uname,upass from t_user where uno = #{uno}
    </select>
    ```

## Hibernate基本应用

1. 创建普通Maven程序

2. 引入依赖(Hibernate、mysql)

3. 创建配置文件，建议src/main/resources/hibernate.cfg.xml

   配置文件内容参考官方文档：http://hibernate.org/

   ```xml
   <?xml version='1.0' encoding='utf-8'?>
   <!DOCTYPE hibernate-configuration PUBLIC
   			"-//Hibernate/Hibernate Configuration DTD 3.0//EN"
   			"http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
   <hibernate-configuration>
   	<session-factory>
   			<!-- Database connection settings -->
   			<property name="connection.driver_class">com.mysql.cj.jdbc.Driver</property>
   			<property name="connection.url">jdbc:mysql://localhost:3306/hibernate?useUnicode=true&amp;characterEncoding=utf8&amp;serverTimezone=UTC</property>
   			<property name="connection.username">root</property>
   			<property name="connection.password">root</property>
   
   			<!-- SQL 方言，告诉hibernate按照哪个数据库语法生成sql语句 -->
   			<property name="dialect">org.hibernate.dialect.MySQL8Dialect</property>
   			<!-- 在控制台显示hibernate自动生成的sql -->
   			<property name="show_sql">true</property>
   			<mapper resource = "orm/User.orm.xml"/>
   	</session-factory>
   </hibernate-configuration>
   ```

4. 自定义实体类：User

   应为Hibernate是一个全自动orm框架，将对表的操作转换成对对象的操作

   ```java
   public class User implements Serializable {
       private Integer uid ;
       private String name ;
       private String sex ;
   }
   ```

5. 自定义orm配置文件：该文件作用是告诉hibernate实体与表的关系，建议放到resource/orm/User.orm.xml

   ```xml
   <?xml version="1.0"?>
   <!DOCTYPE hibernate-mapping PUBLIC
   			"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
   			"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
   <hibernate-mapping >
     <!-- 告诉hibernate 实体 与 表对应关系 -->
     <class name="com.hibernate.domain.Car" table="t_car">
       <id name="cno" column="cno" />
       <property name="cname" column="cname" />
       <property name="color" column="color" />
     </class>
   </hibernate-mapping>
   ```

   注意：

   - ​	需要将orm配置文件引入到核心配置文件

     ```xml
     <mapping resource="orm/User.orm.xml" />
     ```

   - 在指定类属性与表字段的对应关系时，必须使用<id>标签指定哪个对应是主键
   - 在hibernate启动时，就会根据映射关系，提前生成单标的CRUD语句，并且<id>指定的字段作为条件。

6. 编写dao，使用hibernate实现与数据库的交互

   hibernate的session对象提供CRUD方法，每个session对象内置connection连接：
   		session.save()/update()/delete()/get()....

   hibernate的session对象需要用到sessionFactory对象来创建

   ​		Session session = sessionFactory.openSession();

   而sessionFactory对象需要hibernate的Configuration对象创建，所以最后获取session对象方式：

   ```java
   Configuration = cfg = new Configuration();
   //cfg.configure();默认读取resource/hibernate.cfg.xml
   cfg.configure("hibernate.cfg.xml");
   SessionFactory factory = cfg.buildSessionFactory();
   Session session = factory.openSession();
   //或者
   StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
   Configuration cfg = new Configuration();
   SessionFactory factory = cfg.buildSessionFactory(standardServiceRegistry);
   Session session = factory.openSession();
   ```

   注意：在实现增删改时，需要事务处理

   ```java
   session.beginTransaction();
   session.delete(User);
   session.getTransaction().commit();
   ```

7. Junit测试：如果配置了show_sql属性，并且为true，如果与数据库交互了会打印sql

## Hibernate对象状态

- **瞬时态(临时态)：**刚刚创建的对象数据临时态
  - jvm中存在，在session对象和数据库中不存在：如new User();
- **持久态：**jvm中存在，session中存在，数据库中存在
  - 操作jvm中对象，就会通过session自动更新数据库数据
  - save(User)、update(User)...是将瞬时态兑现转换成持久态
  - get()直接获取一个持久态对象。
  - 注意：delete删除的对象必须是持久态，删除后成为瞬时态。
- **游离态(脱管态)：**jvm中存在，数据库中存在，session中不存在
  - 可以通过session.close()、session.clear()关闭session数据就成为游离态了

## Hibernate的CRUD

补充：核心配置文件hibernate.cfg.xml文件中主要配置两部分内容

- <property>：主要配置属性信息，hibernate相关配置都会有hibernate前缀，在yml配置文件中需要加上。
- <mapper>：主要作用引入对象与数据表关系的orm配置文件

**session单记录查询：**session.get()、session.load()

```java
//根据主键ID查找User的一条记录
User user = session.get(User.class,1);
User user = session.load(User.class,1);
```

注意：get 和 load 特点

- 相同点：都是根据主键查找单条记录

- 不同点：

  - get方法会直接从数据库获取数据对象，如果没有数据，就返回null

  - load方法会先获取一个代理对象，在需要使用数据时再想数据库获取数据(懒加载)

    - 如果数据不存在，获取时抛出异常

    - load方法默认是懒加载查询，可以通过2种方式关闭懒 加载

      1. 在orm配置文件中，通过懒加载属性关闭

         <class ... lazy="false">

      2. 将实体类User用final关键字修饰

         load方法懒加载体现是获取当前实体类的代理对象，需要数据时代理对象查询。

         然后实现代理只有静态代理和动态代理，使用final修饰实体类后，该实体类就不能被继承，导致不能被代理，所以避免了懒加载。

- 懒加载：(时间换空间)

  - 懒加载不是不加载，而是加载主键ID，当需要使用数据时，再通过主键Id去与数据库交互查询

**session单记录增删改**

注意：增删改是，需要事务处理。

- 保存

  ```java
  User user = new User(1,"wj","nan");
  session.beginTransaction() ;
  session.save(user) ;
  session.getTransaction().commit();
  ```

- 修改

  ```java
  User user = new User(1,"wj","nan");
  session.beginTransaction() ;
  session.update(user);
  session.getTransaction().commit();
  * 对于持久态对象，修改对象就相当于修改数据。
  * 而不需要执行修改方法。
  User user = session.get(User.class,1);//持久态
  session.beginTransaction() ;
  session.getTransaction().commit();
  ```

- 删除

  注：正常情况删除通过主键ID就行，但hibernate对表的操作都是以对象的操作体现，所以也必须删除对象。

  ```java
  //对象可以只要主键ID的值，但是必须是一个持久态对象才能被删除。
  User user = new User();
  User.setUid(1);
  session.beginTransaction();
  session.delete(user);
  session.getTransaction().commit();
  ```

**拓展**

- 保存或修改：根据主键ID做查询，数据存在就修改，不存在就保存

  - session.saveOrUpdate(User)
  - session.merge(User)

- saveOrUpdate 与 merge 区别

  这两个方法的区别在于执行修改操作时：如果在session缓存中，之前已经存在了一个相同ID的持久态对象

  ​	update 方法会抛出异常：两个不同的对象却有相同的ID，修改失败

  ​	merge方法会修改成功：将两个对象合并，后者覆盖前者，merge修改的对象并没有变成持久态，而是覆盖到元持久态对象中。

- 保存时的主键生成机制

  在orm配置文件中，使用id的子标签\<generator>指定生成方式

  ```xml
  <id name="uid" column="uid">
    <generator class="assigned"></generator>
  </id>
  ```

  hibernate提供了多种主键生成机制，常用的有：

  - [^assigned]: 默认，手动提供主键值

  - [^uuid]: 主要是对于varchar(32)主键，生成32位16进制唯一数

  - [^identity]: mysql等int主键    自增，需要在建表时设置成自增主键

  - [^foreign]: 一对一关联时，由关联表外键充当主键值

  - [^native]: 使用当前数据库默认支持的主键生成防方式

    * 如果是mysql ， native === identity
    * 如果是oracle， native === sequence

  注意：使用主键生成机制时，使用save方法执行保存后，会以返回值的形式将生成的主键返回。

## Hibernate多条查询

session并没有提供直接查询多条记录的方法，但提供了间接查找多条记录的对象。

- **Native sql**

  1. 通过session.createNativeQuery()方法获得Query对象

  2. 获得对象时需要预处理Sql

     ```java
     NativeQury query = session.createNativeQuery(select * from user where id = ?);
     //以上只是获得了query对象，并没有执行查询操作。
     ```

  3. 使用query.list()或者query.getSingleResult()执行sql语句

     query.list()返回list集合，为多条查询

     query.getSingleResult()为单条查询，使用单一对象装载

  4. 使用query.addEntity(User.class)告诉返回值类型。

  5. sql预处理传参，有两种方式

     方式一："?" 表示参数

     ```
     select * from user where id = ?;
     ---
     query.setParameter(1,1);
     ```

     方式二：":名" 表示参数

     ```
     select * from user where id = :id;
     ---
     query.setParameter("id",1);
     ```

  6. 分页查询

     ```java
     //如果分页，不需要用sql体现
     原始sql：select * from user limit start,length;
     现在sql：select * from user;
     //从第几条开始
     query.setFirstResult(start);
     //取几条记录
     query.setMaxResult(length);
     ```

  7. 拓展：使用NativeSql执行批量修改和批量删除时，需要使用query.excuteUpdate()方法，并注意事务。

- **QBC查询：Query By Criteria**

  语法与机制上都符合面向对象的特点，但在hibernate5.2之后，框架引入了JPA,就不推荐使用QBC查询了。

  ```java
  //通过createCriteria方法获取查询对象，并指定查询目标User
  //select * from user；
  Criteria query = session.createCriteria(User.class);
  List<User> userList = query.list();
  User user = query.getSingleResult();
  ```

  - 过滤查询

    ```java
    query.add(Restrictions.eq("id",1)) ;//  id = 1
    query.add(Restrictions.ne("id",1)) ;//  id != 1
    query.add(Restrictions.gt("id",1)) ;//  id > 1
    query.add(Restrictions.lt("id",1)) ;//  id < 1
    query.add(Restrictions.ge("id",1)) ;//  id >= 1
    query.add(Restrictions.lt("id",1)) ;//  id <= 1
    
    query.add(Restrictions.like("cname","%b%")) ; //cname like '%b%'
    
    query.add(Restrictions.between("price",200000,300000)); //price between 20w and 30w
    query.add(Restrictions.in("price",200000,250000,300000));//price in (20w,30w,25w)
    
    query.add(Restrictions.isNull("cname")) ;  //  cname is null
    query.add(Restrictions.isNotNull("cname")) ;//  cname is not null
    
    query.add(Restrictions.and(
      Restrictions.eq("color","red"),
      Restrictions.lt("price",300000)
    ));					// color='red' and price<30w
    query.add(Restrictions.or(
      Restrictions.eq("color","red"),
      Restrictions.lt("price",300000)
    ));					// color='red' or price<30w
    //注意： 连续使用两次add增加过滤条件，自动就是and。
    ```

  - 排序查询

    ```java
    query.addOrder(Order.desc("price")) ;// order by price desc
    query.addOrder(Order.asc("price")) ; // order by price asc
    // 注意： 设置两次排序，自然就是按两个条件排序。
    ```

  - 分页查询

    ```java
    query.setFirstResult(start) ;//起始索引
    query.setMaxResults(length); //分页长度
    ```

  - 分组查询

    - 字段投影：只查询表中部分字段的数据  select cname,color from user ;

      ```java
      query.setProjection(Projections.projectionList()
                          .add(Projections.property("cname"))
                          .add(Projections.property("color"))
                         ); 
      //select cname , color from user
      // 注意：投影之后，每条记录的字段信息就无法组成对象,最终组成Object[]
      ```

    - 聚合函数

      ```java
      query.setProjection(Projections.projectionList()
                          .add(Projections.count("id"))
                          .add(Projections.max("price"))
                         ); 
      //select count(id) , max(price) from user
      ```

    - 分组

      ```java
      query.setProjection(Projections.projectionList()
      				    .add(Projections.groupProperty("color"))
      				    .add(Projections.count("id"))
      				    .add(Projections.max("price"))
      				    .add(Projections.alias(Projections.min("price"),"minPrice"))
      				);
      				// select color,count(id),max(price),min(price) as minPrice from user group by color
      ```

  - 子查询

    ```java
    //离线查询
    Criteria query1 = session.createCriteria(User.class);
    DetachedCriteria query2 = DetachedCriteria.forClass(User.class);
    
    query2.setProjection(Projections.avg("price"));
    //select avg(price) from User ;
    
    
    query1.add(Property.forName("price").gt(query2));
    List<User> userList = query1.list();
    //select * from User where price > (select avg(price) from User )
    ```

- **HQL查询**

  类似于sql语法，面向兑现的机制查询的方式。简单理解为将sql中的表换成实体，字段换成属性。如：

  ```java
  原始：select id,name from user where id = ?
  现在：select id,name from User where id = ?
  ---------------------
  原始：select * from user where id = ?;
  现在：from User where id = ?
  //但是HQL中没有select * 这个说法，可以使用select查询部分属性。
  ```

  ```java
  //获取query对象，并指定查询对象
  Query query = session.createQuery("from User");
  //查询多条
  List<User> userList = query.list();
  //查询单条
  User user = query.getSingleSelect();
  
  //过滤查询：
  //方式一：
  from com.hibernate.domain.User where price > ?1 
  query.setParameter(1,300000);
  ----
  //方式二：
  from com.hibernate.domain.User where price > :price 
  query.setParameter("price",300000);
  
  
  //分页查询：
  query.setFirstResult(start);
  query.setMaxResults(length);
  
  //关联查询：
  //方式一：逻辑关联
  from Emp e , Dept d where e.dno = d.dno ;
  //注意：不能使用join关联语法，查询结果为List<Object[]{Emp,Dept}>;
  
  //方式二：物理关联，物理关联不需要在创建Query对象的时候给出关系，在orm配置文件指定，这里只用指定从哪里查
  from User；
  ```

  拓展：HQL的批量删除和批量修改，使用query.excuteUpdate()执行，注意事务。

- **JPA查询**

  1. 创建CriteriaBuilder对象，构建查询模式

     ```java
     CriteriaBuilder builder = session.getCriteriaBuilder();
     ```

  2. 获得查询对象CriteriaQuery

     ```java
     CriteriaQuery query = builder.createQuery(User.class);
     //注：User.class表示结果组成对象类型，既返回值类型。
     ```

  3. 获得目标对象Root

     ```java
     Root<User> table = query.from(User.class);
     //注：User.class表示查询的目标，根据orm中的配置User.class可以找到user表，既查询对象。
     ```

  4. 构建查询

     ```java
     query.select(table);
     //select * from user;
     ```

  5. 执行查询

     ```java
     List<User> userList = session.createQuery(query).list();
     User user = session.createQuery(query).getSingleResult();
     ```

  6. 构建查询-过滤查询

     ```java
     query.select(table).where(builder.equal(table.get("id"),1));//id = 1
     query.select(table).where(builder.gt(table.get("price"),30000)); //price > 30000;
     ```

  7. 构建查询-排序查询

     ```java
     query.orderBy(builder.desc(table.get("price")));//order by price desc；
     ```

  8. 执行查询-分页查询

     ```java
     session.createQuery(query).setFirstResult(start).setMaxResult(length).list();
     ```

  9. 构建查询-分组查询

     - 投影查询

       1. 投影查询不能组成完整的User对象，需要为投影字段专门定义一个类对象。如:

          class User1 {name,pass};

       2. 设置CriteriaQuery的泛型为新定义的类型User1

          CriteriaQuery<User1> query = builder.createQuery(User1.class);

       3. 构建查询时不能再使用select方法，而是使用multiselect方法，如：

          query.multiselect(table.get("name"),table.get("pass"));

     - 聚合查询

       ```java
       //select count(cno) , max(price) ,min(price) from user 
       //注意：上述聚合函数查询结果会自动为每一个字段起个别名，countCno,maxPrice,minPrice
       //当然，我们也可以手动为查询字段起别名。
       query.multiselect(builder.count(table.get("cno")).alias("countCno"),builder.max(table.<Integer>get("price")),builder.min(table.<Integer>get("price"))) ;
       ```

     - 分组查询

       ```java
       //select count(cno) , max(price) ,min(price) from user group by color
       query.groupBy(table.get("color")).multiselect(builder.count(table.get("cno")).alias("countCno"),builder.max(table.<Integer>get("price")),builder.min(table<Integer>get("price"))) ;
       ```

  10. 构建查询-子查询

      需要基于一个子查询对象Subquery

      ```java
      //select * from user where price > (select avg(price) from user) ;
      
      CriteriaQuery<User> query = builder.createQuery(User.class) ;
      Root<User> table = query.from(User.class);
      
      //select avg(price) from user
      Subquery<Double> query2 = query.subquery(Double);
      Root<User> table2 = query2.from(User.class) ;
      query2.select(builder.avg(table2.<Number>get("price"))) ;
      
      //select * from User where price > (query2)
      query.select(table).where(builder.gt(table.<Number>get("price") , query2)) ;
      
      session.createQuery(query).list();
      ```

## Hibernate关联查询

**关联关系**

- 一对一
- 一对多/多对一
- 多对多

**关联查询**

- 一对一
- 一对多

### 1. 一对一

- 建表

  一对一关系体现，可以使用量两张表的主键关联

- 建实体

  在两个实体中，分别都存在一个关联对象的属性。

- orm文件配置

  ```xml
  <class name="com.hibernate.domain.Person" table="t_person">
    <id name="pid" column="pid"></id>
    <one-to-one name="card" class="com.hibernate.domain.Card" />
  </class>
  ---
  <class name="com.hibernate.domain.Card" table="t_card">
    <id name="cid" column="cid" />
    <one-to-one name="person" class="com.hibernate.domain.Person" />
  </class>
  ```

- 级联保存

  在保存Person对象时同时保存Card对象，需要在orm配置文件中开启级联保存。

  ```xml
  <one-to-one name="card" class="com.hibernate.domain.Card" cascade="all" />
  //查询时级联自动的，不需要单独设置
  ```

- 关联查询

  不需要做任何的配置，只要配置了关系，每次查询都会将关联的数据取出。

  ```java
  public void find1(){
    Session session = HibernateUtil.getSession() ;
    Person p = session.get(Person.class,1);
    
    System.out.println(p.getPname()+","+p.getAge()); //业务查找
    System.out.println(p.getCard().getCno()+","+p.getCard().getAddress());//关系自动查找
  }
  ```

- 拓展

  使用foregin主键生成机制，确保两张表的主键值想等。

  其中一个对象(Person)的主键手动提供，或者自动生成。

  另一个主键的值，配置foregin主键生成器。

  ```xml
  <class name="Card" table="t_card">
    <id name="cid" column="cid" >
      <generator class="foreign">
        <param name="property">person</param>
      </generator>
    </id>
  <!-- 以上配置表示，t_card表的主键由Card实体里面的person属性对应的表的主键生成 -->
    ...
  </class>
  ```

  在编码时，需要将Person对象赋值给Card对象的person属性。

  ​	card.setPerson(person);

### 2. 一对多

- 建表

  一对多的关系可以体现，在many段多添加一个字段，与one段的主键 关联。

- 建实体

  在many端的实体中有一个one端对象属性，在one端的实体中有一个many端实体对象的set集合(推荐，可以去重)属性。

- orm文件配置

  ```xml
  <class name="Dept" table="t_dept">
    <id name="dno" column="dno"></id>
    
    <set name="emps">
      <key column="dno" />
      <one-to-many class="Emp" />
    </set>
  </class>	
  <!-- <set>,<one-to-many>
       * 当前Dept类有一个set集合属性,这个属性的名字叫name="emps"
       * 这个属性是1个 一对多关联属性
       * 关联的是class="Emp"类对应的表
       * 关联时，会默认使用当前One端Dept类的<id>主键值 与 指定的Emp的<key column="dno">外键值关联。
  -->
  -----------------------
  <class name="Emp" table="t_emp">
    <id name="eno" column="eno" />
    <property name="dno" column="dno" />
  
    <many-to-one name="dept" class="Dept" column="dno" />
  </class>
  <!-- <many-to-one>
       * 当前Emp类有1个 多对一关联属性,属性的名字叫name="dept"
       * 该属性关联的是class="Dept"类对应的表
       * 关联时使用当前Many端Emp类指定的外键字段column="dno" 与 实体Dept的<id>主键值关联 
  -->
  ```

- 级联保存

  保存Dept时，顺道保存多个Emp。

  1. 在dept的orm文件中开启级联保存

     ```xml
     <set name="emps" cascade="all">
     ```

  2. 将emp的set集合放到dept的属性中

     ```java
     dept.set(emps);
     ```

  **注意：**

  - 上述配置在级联保存时会抛出异常，错误原因是有两个不同的属性重复对应了同一个表的同一个字段。

    ```xml
    <property name="dno" column="dno" />
    <many-to-one name="dept" class="Dept" column="dno" />
    ```

    解决方案：

    - 去掉dno属性的对应配置。

    - 在dno属性的配置中加入insert = "false" update = "false"两项配置

      ```xml
      <property name="dno" column="dno" insert = "false" update = "false"/>
      ```

  - 上述配置及操作。保存成功后，还执行了n个修改语句

    这些修改语句使用来更新emp表的外键值，因为现在级联保存的主控在one端。

    主控的作用：用来维护外键，控制一对多两个关联表的关联属性值使用哪张表的。

    ​	主控默认在one端，所以保存后，需要将dept表的dno属性更新到emp中的dno属性中。

    ​	如果主控在many端，那么随着many端数据的保存，dno外键值就确定了，就不会再有更新语句了

    主控分配属性只能设置在one端的配置文件中。

    ```xml
    <set name="emps" inverse="false">
    <!--  inverse="false" (默认)主控不反转,one端是主控
    			inverse="true" 主控反转,many端是主控
    -->
    ```

  - 按照上述配置和操作。保存成功后，emp数据的外键值为null

    随着主控分配给many端，dno字段值由emp对象提供,,所以在保存emp对象时需要使用dept属性为dno字段赋值。

    ```java
    emp1.setDept(dept);
    emp2.setDept(dept);
    ```

- 关联查询

  只要配置了关联关系，查询时就会自动的将关联的数据查出，不需要额外的配置。

  查询所有部门，以及部门下对应的员工。

  ```java
  public void find(){
    Session session = HibernateUtil.getSession() ;
    Criteria query = session.createCriteria(Dept.class);
    List<Dept> depts = query.list() ;
    for(Dept dept : depts){
      System.out.println(dept.getDname());
      for(Emp emp : dept.getEmps()){
        System.out.println("\t"+emp.getEname()+","+emp.getSal());
      }
    }
  }
  ```

  **注意：**

  - 在上述查询结果显示中，发现每要显示一个部门的员工信息时，就要执行一次查询

    因为一对多关联查询时，会自动开启懒加载查询，可以在orm的关系配置中关闭懒加载

    ```xml
    <set name="emps" lazy="false">
    ```

  - 关闭懒加载后，又出现了N+1问题

    可以在orm的关系配置中，设置关联数据抓取策略

    ```xml
    <set name="emps" fetch="join|subselect">
    <!-- * subselect:使用子查询
           select * from t_dept ;
           select * from emp where dno in (select * from t_dept )
          * join： 使用关联语句
           select * from t_dept d left join t_emp e where d.dno = e.dno
    -->
    ```

  - 使用关联查询后，部门信息会重复出现，可以将查询到的list结果装载到set集合中去去重。

    ```java
    List<Dept> depts = query.list() ;
    Set<Dept> set  = new HashSet<Dept>(depts);
    ```

  - 上述查询抓取策略对HQL无效

    HQL需通过编码实现关联查询

    ```java
    Query query = session.createQuery("from Dept d left join fetch d.emps");
    //表示 Dept类对应的表，与Dept中emps属性关联的类对应的表 关联查询。
    ```

### 3. 多对多

- 建表

  多对多关系体现，理论上应该是两张表的外键与外键直接关联，但是会产生大量冗余，所以出现了中间表，专门存储两张表的关联关系，该中间表分别与两张表的主键关联。

- 建实体

  在两个实体类中都分别存在关联实体的一个<set>集合。

- orm文件配置

  ```xml
  <class name="Teacher" table="t_teacher">
    <id name="tno" column="tno">
      <generator class="identity" ></generator>
    </id>
    
    <set name="ss" table="t_teacher_student">
      <key column="tno" />
      <many-to-many class="Student" column="sno" />
    </set>
  </class>
  <!-- <many-to-many>
    * 当前Teacher类有一个set集合属性
    * 属性的名字叫name="ss"
    * 该属性是一个多对多<many-to-many>关联属性
    * 关联的是class="Student"类对应的表。
    * 关联时会基于一张中间表table="t_teacher_tudent"
    * 会使用当前Teacher类<id>主键值，与中间表指定的<key column="tno">字段关联
    * 会使用中间表指定的另一个column="sno" 与Student类<id>主键值关联。
  -->
    ----
  <class name="Student" table="t_student">
    <id name="sno" column="sno">
      <generator class="native" />
    </id>
    
    <set name="ts" table="t_teacher_student">
      <key column="sno" />
      <many-to-many class="Teacher" column="tno"/>
    </set>
  </class>
  ```

- 级联保存

  保存多个老师的同时，顺道也保存多个学生

  在orm文件中开启级联保存

  ```xml
  <set name="Teacher" cascade="all">
  ```

  在Teacher中设置Student集合数据的值：teacher.setStudentSet(studentSet);

- 关联查询

  不用配置什么，默认会查询teacher下的所有学生，但是会产生N+1问题和懒加载，可以在orm文件中配置：

  ```xml
  <set name="ss" table="t_teacher_student" cascade = "all" fatch = "join">
    <key column="tno" />
    <many-to-many class="Student" column="sno" />
  </set>
  ```

## Hibernate注解编程

hibernate有两种xml配置文件，而 注解编程就是用来替代orm配置文件。

- orm

  1. 在实体类上使用 @Entity注解 ，表示当前类 与 同名表有对应关系

     ```java
     //<class name="Car" table="Car" >
     @Entity
     public class Car {}
     //扩展: 使用 @Table注解 指定实体类对应的表名
     //<class name="Car" table="t_car" >
     @Entity
     @Table(name="t_car")
     public class Car {}
     注意： 
     	随着实体与表对应
     	默认实体中所有的属性 都有与之 数量相同，名称相同的表字段。
     ```

  2. 在主键属性或get方法上使用 @Id注解，表示当前属性是主键属性

     ```java
     @Id
     private Integer cno ;   // <id name="cno" column="cno" />
     ```

  3. 在属性或get方法上使用 @Column注解 设置属性与字段具体的映射信息

     ```java
     @Column(name="carname",length = 8)
     private String cname ; // <property name="cname" column="carname" />
     ```

  4. 在属性上使用 @Transient注解，表示当前属性不与表字段对应。

     ```java
     @Transient
     private Integer price ;
     ```

  注意：orm实体设置完成后，需要将其关联到核心配置文件中

  ```xml
  <mapping class="com.hibernate.domain.Car" />
  ```

- 主键生成机制

  - 情景一

    ```java
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cno ;
    
    * GenerationType.IDENTITY mysql 表自增
    * GenerationType.SEQUENCE oracle序列
    * GenerationType.AUTO 根据数据库自动选择(上述2种)
    ```

  - 情景二

    ```java
    @Id
    @SequenceGenerator(name="seq",sequenceName="seq1",initialValue=1,allocationSize=1)
    @GeneratedValue(generator = "seq")
    private Integer cno ;
    
    * sequenceName 数据序列的名称
    * initialValue 序列初始值
    * allocationSize 序列递增值
    ```

  - 情景三

    ```java
    @Id
    GenericGenerator(name="aaa",strategy = "uuid" )
    @GeneratedValue(generator = "aaa")
    private Integer cno ; 
    ```

- 关联关系

  - 一对一

    ```java
    @Entity
    @Table(name="t_person")
    public class Person {
      @Id
      private Integer pid ;
      private String pname ;
      private Integer age ;
    
      /*<one-to-one name="card" class="Card">*/
      @OneToOne(cascade = CascadeType.ALL) //表示是一个一对一关联属性,并设置级联保存
      @PrimaryKeyJoinColumn //表示使用主键关联
      private Card card ;
    }
    ----------
    @Entity
    @Table(name="t_card")
    public class Card {
      @Id
      private Integer cid ;
      private String cno ;
      private String address ;
    
      @OneToOne
      @PrimaryKeyJoinColumn
      private Person person ;
    }
    ```

  - 一对多

    ```java
    @Entity
    @Table(name="t_dept")
    public class Dept {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Integer dno ;
      private String dname ;
      /*<set name="emps">
    				<key column="dno" />
    				<one-to-many class="Emp" />
    		</set>*/
      @OneToMany  //表示当前属性是一个 一对多关联属性
      @JoinColumn(name="dno")  //表示使用该表主键与emp表的dno字段关联
      private Set<Emp> emps ;
    }
    /*
    @OneToMany(
      cascade = CascadeType.ALL,	//级联操作
      mappedBy = "dept",		//设置主控(在many端)
      fetch = FetchType.EAGER		//设置懒加载及抓取策略
    ) 
    						
    * 注意： mappedBy 用来指定主控在many端。
      关联many端Emp时，关联条件参考Emp端dept属性上的@JoinCollumn
    * Dept 如何关联 Emp 要看 Emp是如何关联Dept,所以many端是主控。
    * one端不能再使用 @JoinColunn注解。
    * 注意：fetch 有2个值
      FetchType.EAGER	 关闭懒加载 和 关联语句查询(迫切抓取)
      FetchType.LAZY   懒加载 和 N+1查询
    * @JoinColumn 表示关联的外键字段
    */
    
    @Entity
    @Table(name="t_emp")
    public class Emp {
      @Id
      @GenericGenerator(name="aaa",strategy = "uuid")
      @GeneratedValue(generator = "aaa")
      private String eno ;
      private String ename ;
      private Integer sal ;
    
      /*<many-to-one name="dept" class="Dept" column="dno" />*/
      @ManyToOne //表示是一个多对一关联属性
      @JoinColumn(name="dno") //表示使用该表的dno字段与dept表的主键关联
      private Dept dept ;
    }
    ```

  - 多对多

    ```java
    @Entity
    @Table(name="t_teacher")
    public class Teacher {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Integer tno ;
      private String tname ;
      private Integer sal ;
    
      /*
    		<set name="ss" table="t_teacher_student>
    				<key column="tno" />
    				<many-to-many class="Student" column="sno"/>
    		</set>
    		*/
      @ManyToMany(cascade = CascadeType.ALL)  //表示是一个多对多关联属性，并开启级联操作
      @JoinTable(
        name="t_teacher_student",  //关联中间表
        joinColumns = {@JoinColumn(name="tno")},  //Teacher.id 与中间表tno关联
        inverseJoinColumns = {@JoinColumn(name="sno")}  //Student.id与 中间表sno关联
      )
      private Set<Student> ss ;
    }
    
    
    @Entity
    @Table(name="t_student")
    public class Student {
    
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Integer sno ;
      private String sname ;
      private Integer age ;
    
      @ManyToMany
      @JoinTable(
        name="t_teacher_student",
        joinColumns = {@JoinColumn(name="sno")},
        inverseJoinColumns = {@JoinColumn(name="tno")}
      )
      private Set<Teacher> ts ;
    }
    ```

## Hibernate知识点

### 自动建表

在hibernate核心配置文件中增加 hbm2ddl 属性

```xml
<!--
    根据orm文件，生成对应表
    create 每次启动框架，都会删除之前的表，创建新表
    update 当orm文件的结构改变时，修改表结构
    所以实际使用时，第一次create，以后update，直接使用update,当有不存在的表会执行create操作
-->
<property name="hbm2ddl.auto">create</property>
```

### 二级缓存

缓存：将数据存入内存中，然后获取数据直接从内存中获取，相比直接从数据库中获取效率更高。

- 一级缓存

  hibernate提供的session对象就是一级缓存，在使用同一个session对象读取相同数据时，第一次会执行sql，后面会直接从缓存中获取数据，不会执行sql与数据库交互。

- 二级缓存

  二级缓存是sessionFactory级别的缓存，需要额外引入。

  1. 引入二级缓存依赖：ehcache、hibernate-ehcache

  2. 定义二级缓存的配置文件：建议参考官方文档。

  3. 在核心配置文件中配置启用了二级缓存，并且是哪种二级缓存

     ```xml
      <!--告诉hibernate开启二级缓存-->
     <property name="hibernate.cache.use_second_level_cache">true</property>
     <!--告诉hibernate使用哪一种二级缓存-->
     <property name="hibernate.cache.region.factory_class">
       org.hibernate.cache.ehcache.internal.EhcacheRegionFactory
     </property>
     ```

  4. 在核心配置文件或者orm文件中指定哪类数据放入二级缓存

     ```xml
     <!--hibernate.cfg.xml-->
     <class-cache class="com.hibernate.domain.User" usage="read-only" />
     <!--Car.orm.xml-->
     <class name="com.hibernate.domain.User" table="User">
       <cache usage="read-only" />
       ...
     </class>
     ```

  5. 注意：上述二级缓存对get/load单记录查询时生效的，但对于多记录查询是无效的，需要额外配置。

     get数据查询过程：一级缓存session-------->二级缓存cache-------->数据库

     1. 在核心配置文件中增加属性配置

        ```xml
        <!--开启查询缓存，将查询的多记录数据也允许装入缓存-->
        <property name="hibernate.cache.use_query_cache" >true</property>
        ```

     2. java代码执行查询时，调用方法实现数据装入二级缓存。

        ```java
        session1.createQuery("from User").setCacheable(true).list();
        session2.createQuery("from User").setCacheable(true).list();
        session3.createQuery("from User").setCacheable(true).list();
        ```

### 锁

在多事务并发操作时，确保共享数据的安全性，hibernate支持乐观锁和悲观锁。

- [^悲观锁]: 是一种数据库锁机制，会认为要使用的数据在应用过程中会被其他事务操作使用，会将数据锁住。

  可以使用sql开启悲观锁：select * from table for update;

  特点：确保数据操作的安全性，避免数据的脏读、幻读。

  ​			执行效率低。

  实现：

  ```java
  session.beginTransaction() ;
  //Car car = session.get(User.class,1, LockMode.PESSIMISTIC_WRITE) ;
  Query query = session.createQuery(User.class)
  query.setLockMode(LockModeType.PESSIMISTIC_WRITE).list() ;
  Thread.sleep(30000);
  session.getTransaction().commit();
  ```

  注意：锁一般在查询时使用，使用时会伴随着事务，随着事务处理完毕，锁被释放。

- [^乐观锁]: 认为要使用的数据在应用过程中不会被其他事务操作使用，不用上(悲观)锁。

  hibernate实现乐观锁是基于记录版本号的方式实现。

  特点：确保其他事务处理数据的安全性，忽略自身。

  ​			整体运行效率高

  实现：

  1. 建表，建实体时需要增加一个字段存储版本号

  2. 在orm文件中指定版本号字段，在保存和修改数据时，hibernate会自动更新该条数据版本号

     ```xml
     <version name="version" column="version" />
     ```

  3. 正常编码实现添加或者修改逻辑即可。

  4. 乐观锁机制

     查询一条数据，记录该条数据版本号，按业务实现相应操作修改。

     修改数据时，会用该条数据之前的版本号，与数据库里该条数据当前版本号做对比

     ​	匹配：更新成功，版本号+1

     ​	不匹配：证明被其他事务操作，此次更新失败，抛出异常。

  ​			

  