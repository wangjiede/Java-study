# Spring基础使用

## Spring概述

Spring是一个开源框架，为简化企业级应用开发而生，是一个 IOC(DI) 和 AOP 容器框架，具体描述：

- 轻量级：Spring 是非侵入性的 - 基于 Spring 开发的应用中的对象可以不依赖于 Spring 的 API
- 依赖注入(DI --- dependency injection、IOC)
- 面向切面编程(AOP --- aspect oriented programming)
- 容器： Spring 是一个容器, 因为它包含并且管理应用对象的生命周期
- 框架: Spring 实现了使用简单的组件配置组合成一个复杂的应用. 在 Spring 中可以使用 XML 和 Java 注解组合这些对象
- 一站式：在 IOC 和 AOP 的基础上可以整合各种企业应用的开源框架和优秀的第三方类库 （实际上 Spring 自身也提供了展现层的 SpringMVC 和 持久层的 Spring JDBC)

## IOC&DI概述

- IOC(Inversion of Control)：思想是反转资源获取的方向。传统方式是组件向容器发起请求查找资源，容器返回资源。而IOC是容器主动推送资源，组件选择合适的方式接收资源，也被称为查找的被动方式。
- spring的ApplicationContext 在初始化上下文时就实例化所有单例的 Bean，提供了两种类型的 IOC 容器实现：
  - BeanFactory：IOC 容器的基本实现，是 Spring 框架的基础设施，面向 Spring 本身
  - ApplicationContext：提供了更多的高级特性. 是 BeanFactory 的子接口，面向使用 Spring 框架的开发者，主要实现类：
    - ClassPathXmlApplicationContext：从 类路径下加载配置文件
    - FileSystemXmlApplicationContext：从文件系统中加载配置文件
    - ConfigurableApplicationContext ：扩展于 ApplicationContext，新增加两个主要方法：refresh() 和 close()， 让 ApplicationContext 具有启动、刷新和关闭上下文的能力
    - WebApplicationContext ：是专门为 WEB 应用而准备的，它允许从相对于 WEB 根目录的路径中完成初始化工作
- DI(Dependency Injection)：依赖注入，即依赖容器给组件注入参数、属性

## 配置Bean

### 基于XML

#### 实例Bean方式

XML中Id值说明：在 IOC 容器中必须是唯一的，若 id 没有指定，Spring 自动将权限定性类名作为 Bean 的名字，可以指定多个名字，名字之间可用逗号、分号、或空格分隔

- 通过全类名（反射）

  ```xml
  <bean id="helloWorld" class="com.spring.helloworld.HelloWorld">
  ```

- 通过工厂方法

  - 静态工厂：不需要实例化工厂就执行工厂的静态方法创建需要的对象

    ```java
    public class Factory {
        private static Map<String,Car> carMap = new HashMap<>();
        static {
            carMap.put("baoma",new Car("奔驰"));
            carMap.put("benci",new Car("宝马"));
        }
    
        public static Car getCar(String name){
            return carMap.get(name);
        }
    }
    ```

    ```xml
    <bean id="staticCar" class="com.just.Factory" factory-method="getCar">
      <constructor-arg value="baoma" type="java.lang.String"></constructor-arg>
    </bean>
    ```

  - 实例工厂：需要实例化工厂后执行工厂方法创建需要的对象

    ```java
    public class Factory {
        private Map<String,Car> carMap = new HashMap<>();
        {
            carMap.put("benci",new Car("奔驰"));
            carMap.put("baoma",new Car("宝马"));
        }
    
        public Car getCar(String name){
            return carMap.get(name);
        }
    }
    ```

    ```xml
    <bean id="factory" class="com.just.Factory"></bean>
    <bean id="dynamicCar" factory-bean="factory" factory-method="getCar">
      <constructor-arg value="baoma" type="java.lang.String"></constructor-arg>
    </bean>
    ```

- 自定义FactoryBean(静态工厂)

  ```java
  public class Factory implements FactoryBean<Car> {
      private String brand;
  
      @Override
      public Car getObject() throws Exception {
          return  new Car(brand);
      }
  
      @Override
      public Class<?> getObjectType() {
          return Car.class;
      }
  
      @Override
      public boolean isSingleton() {
          return false;
      }
  
      public void setBrand(String brand) {
          this.brand = brand;
      }
  }
  ```

  ```xml
  <!--实现了FactoryBean接口后，id可以不指定-->
  <bean id="beanCar" class="com.just.Factory">
    <property name="brand" value="baoma"></property>
  </bean>
  ```

#### 注入方式

- 属性注入：通过 setter 方法注入Bean 的属性值或依赖的对象，通常使用 \<property> 元素, 使用 name 属性指定 Bean 的属性名称，value 属性或\<value> 子节点指定属性值，如:

  ```xml
  <bean id="helloWorld" class="com.spring.helloworld.HelloWorld">
    <property name="user" value="Jerry"></property>
  </bean>
  ```

- 构造器注入：通过构造方法注入Bean 的属性值或依赖的对象，通过构造方法注入Bean 的属性值或依赖的对象，如：

  - 按索引匹配入参

    ```xml
    <bean id="car" class="com.spring.helloworld.Car">
      <constructor-arg value="KUGA" index="1"></constructor-arg>
      <constructor-arg value="ChangAnFord" index="0"></constructor-arg>
    </bean>
    ```

  - 按类型匹配入参

    ```xml
    <bean id="car" class="com.atguigu.spring.helloworld.Car">
      <constructor-arg value="KUGA" type="java.lang.String"></constructor-arg>
      <constructor-arg value="ChangAnFord" type="java.lang.String"></constructor-arg>
    </bean>
    ```

- 工厂方法注入（很少使用，不推荐）,参照**实例Bean方式**中的工厂方法

#### 基本类型注入

所有基本类型及基本类型包装类加String都可以通过value属性指定值，若指定的值中有特殊字符可以使用\<![CDATA[]]>，如：

```xml
<bean name='user' class="domain.User">
  <property name="name" value="张三"></property>
  <!--
	<constructor-arg name="name" value="张三" type="java.lang.String"></constructor-arg>
	-->
</bean>
```

#### 对象注入

可以通过 \<ref> 元素或 ref  属性为 Bean 的属性或构造器参数指定对 Bean 的引用，也可以在属性或构造器里包含 Bean 的声明, 这样的 Bean 称为内部 Bean,如：

```xml
<bean name="phone" class="domain.Phone"></bean>
<bean name='user' class="domain.User">
  <!--方式一-->
  <property name="phone" ref="phone"></property>
  <!--方式二-->
  <property name="phone">
  	<bean name="phone" class="domain.Phone"></bean>
  </property>
  <!--当指定的bean需要为一个null对象的时候,使用<null/>标签-->
  <property name="phone"><null/></property>
</bean>
```

#### 集合注入

```xml
<beans>
  <bean id="c1" class="domain.Computer"></bean>
  <bean id="c2" class="domain.Computer"></bean>
  <bean id="c3" class="domain.Computer"></bean>
  <bean id="c4" class="domain.Computer"></bean>
  
  <!--array-->
  <property name="computers">
    <array value-type="domain.Computer">
      <ref bean="c1"></ref>
      <ref bean="c2"></ref>
      <ref bean="c3"></ref>
      <ref bean="c4"></ref>
    </array>
  </property>
  
  <!--list-->
  <constructor-arg name="list" type="java.util.List">
    <list value-type="java.lang.String">
      <value>aaa</value>
      <value>bbb</value>
      <value>ccc</value>
      <value>ddd</value>
    </list>
  </constructor-arg>
  
  <!--set-->
  <constructor-arg name="set" type="java.util.Set">
    <set value-type="domain.Computer">
      <bean class="domain.Computer"></bean>
      <bean class="domain.Computer"></bean>
      <bean class="domain.Computer"></bean>
      <bean class="domain.Computer"></bean>
    </set>
  </constructor-arg>
  
  <!--map-->
  <property name="map">
    <map key-type="java.lang.Integer" value-type="java.lang.String">
      <entry key="1" value="aaa"></entry>
      <entry key="2" value="bbb"></entry>
      <entry key="3" value="ccc"></entry>
      <entry key="4" value="ddd"></entry>
    </map>
  </property>
  <property name="map">
    <map key-type="java.lang.String" value-type="domain.Computer">
      <entry key="a" value-ref="c1"></entry>
      <entry key="b" value-ref="c2"></entry>
      <entry key="c" value-ref="c3"></entry>
      <entry key="d" value-ref="c4"></entry>
    </map>
  </property>
  
  <!--props-->
  <constructor-arg name="properties">
    <props>
      <prop key="1">aaa</prop>
      <prop key="2">bbb</prop>
      <prop key="3">ccc</prop>
    </props>
  </constructor-arg>
</beans>
```

#### 命名空间

- p：代替property属性

  ```xml
  <bean name='user' class="domain.User" p:name="张三" p:age="18"></bean>
  ```

- c：代替constructor-arg属性

  ```xml
  <bean name="user" class="domain.User" c:name="张三" c:age="18"></bean>
  ```

- util：单纯的一个标签，通常用来定义集合模板

  ```xml
  <util:map id="computers">
    <entry key="1">
      <bean class="domain.Computer"></bean>
    </entry>
    <entry key="2">
      <bean class="domain.Computer"></bean>
    </entry>
  </util:map>
  <bean id="user" class="domain.User" p:name="张三" p:computers-ref="computer"></bean>
  ```

#### Bean关系

- 继承

  ```xml
  <bean id="base" p:computer1-ref="c1" p:computer2-ref="c2" p:computer3-ref="c3" abstract="true"></bean>
  <bean id="room1" class="domain.Room" parent="base"></bean>
  <!--也可以指定parent是一个具体的实例bean-->
  <bean id="room2" class="domain.Room" parent="room1"></bean>
  ```

- 依赖

  ```xml
  <!--room类依赖computer，在创建bean时，没有computerbean就会报错，依赖多个bean时，可以用逗号分隔-->
  <bean id="room" class="domain.Room" depends-on="computer"></bean>
  ```

#### SpEL

- ${}：主要是使用外部文件中定义好的key值，如在spring的配置文件中引入properties文件后：

  ```xml
  <context:property-placeholder location="classpath:xxx.properties"></context:property-placeholder>
  <property name="name" value="${name}"></property>
  ```

- #{}：

  - 基础类型属性值注入：#{'zzt'}  #{123}  #{123.45}  #{true}
  - 做运算：
    - 算术：+ - * / %等， ^在Java中是位运算，但^在Spring是幂运算
    - 比较：> >= < <= != ==对应gt ge lt le ne eq
    - 逻辑：没有与符号  ||  !	and or not
  - 对象类型属性赋值：#{beanID}
  - 操作对象属性：#{beanID.属性名}
  - 操作对象方法：#{beanID.方法名()}
  - 引入java中的类并执行方法或调用属性：#{2 * T(java.lang.Math).PI}

#### 自动装配

将对象中的对象类型的属性自动赋值，使用autowire

- byName：bean对象中的属性名与另一个bean对象的name或id一致即可
- byType：bean对象中的属性类型与另一个bean对象的class类型一致，当有多个类型的bean时，报错
- constructor：先按照类型匹配，如果类型发现不止一个对应，再按照属性名与bean的name或id匹配 成功匹配 就赋值

#### Bean作用域

- 在 Spring 中, 可以在 \<bean> 元素的 scope 属性里设置 Bean 的作用域，默认情况下值是singleton, Spring 只为每个在 IOC 容器里声明的 Bean 创建唯一一个实例, 整个 IOC 容器范围内都能共享该实例。

  | 类别      | 说明                                                         |
  | --------- | ------------------------------------------------------------ |
  | singleton | 单例模式                                                     |
  | prototype | 原型的，每次获取bean对象，都会创建新的对象                   |
  | request   | 每次Http请求都会创建一个新的bean，适合用于WebApplicationContext环境 |
  | session   | 同一个Http Seesion共享一个bean，适合用于WebApplicationContext环境 |

- spring底层默认采用立即加载、生命周期托管形式实现对象单例

  - 可以通过bean标签的scope属性修改是否为单例对象，默认singleton

    - scope="singleton"：单例模式
    - scope="prototype"：每次都new新对象

  - 可以通过bean标签的lazy-init属性修改是否延迟加载，默认default即false

    - lazy-init="default/false"：立即加载
    - lazy-init="true"：延迟加载

  - 以上两点也可以通过在\<beans>标签中设置，就不用针对每一个bean设置了

    ```xml
    <beans default-lazy-init="true" 
           default-autowire="byName">
      
    </beans>
    ```


#### IOC容器中Bean生命周期

- 默认生命周期管理过程，在 Bean 的声明里设置 init-method 和 destroy-method 属性, 为 Bean 指定初始化和销毁方法

  - 通过构造器或工厂方法创建 Bean 实例
  - 为 Bean 的属性设置值和对其他 Bean 的引用
  - 调用 Bean 的初始化方法
  - Bean 可以使用了
  - 当容器关闭时, 调用 Bean 的销毁方法

- 创建并配置bean后置处理器，如：

  ```xml
  <!---实现org.springframework.beans.factory.config.BeanPostProcessor接口并实现方法，在spring配置文件中配置-->
  <bean class="com.spring.ref.MyBeanPostProcessor"></bean>
  ```

  - 通过构造器或工厂方法创建 Bean 实例
  - 为 Bean 的属性设置值和对其他 Bean 的引用
  - 将 Bean 实例传递给 Bean 后置处理器的 postProcessBeforeInitialization 方法
  - 调用 Bean 的初始化方法
  - 将 Bean 实例传递给 Bean 后置处理器的 postProcessAfterInitialization方法
  - 使用bean
  - 当容器关闭时, 调用 Bean 的销毁方法

### 基于注解

- 使用context:component-scan标签进行包扫描

  - base-package属性：指定一个需要扫描的基类包，Spring 容器将会扫描这个基类包里及其子包中的所有类，当需要扫描多个包时, 可以使用逗号分隔
  - resource-pattern属性：扫描满足指定规则的类
  - \<context:include-filter>子节点：表示要包含的目标类
  - \<context:exclude-filter> 子节点：表示要排除在外的目标类

- Spring扫描组件有默认的命名策略，使用非限定类名, 首字母小写，也可以在注解中通过 value 属性值标识组件的名称，默认扫描组件包括，可以使用use-default-filters属性设置：

  - @Component: 基本注解, 标识了一个受 Spring 管理的组件
  - @Respository: 标识持久层组件
  - @Service: 标识服务层(业务层)组件
  - @Controller: 标识表现层组件

- \<context:include-filter> 和 \<context:exclude-filter> 子节点支持类型表达式

  | 类别       | 示例                     | 说明                                                         |
  | ---------- | ------------------------ | ------------------------------------------------------------ |
  | annotation | com.spring.Controller    | 所有标注了@Controller的类                                    |
  | assinable  | com.spring.Service       | 所有继承或扩展了com.spring.Service的类                       |
  | aspectj    | com.spring..*Service+    | 所有以Service结束的类以及继承获取扩展它的类                  |
  | regex      | com\\.spring\\.anno\\..* | 所有com.spring.anno包下的类                                  |
  | custom     | com.spring.XxxTypeFilter | 通过实现org.springframework.core.type.filter.TypeFilter接口的类具体代码实现过滤。 |

- 组件装配

  \<context:component-scan> 元素还会自动注册 AutowiredAnnotationBeanPostProcessor 实例, 该实例可以自动装配具有 @Autowired 和 @Resource 、@Inject注解的属性.

  - @Autowired，可以使用在构造器、普通属性、具有参数的普通方法、数组、集合、map。
    - required属性：设置是否在IOC容器中必须存在。
    - @Qualifier注解：配和@Autowired使用，指定注入bean的名称
  - @Resource：要求提供一个 Bean 名称的属性，若该属性为空，则自动采用标注处的变量或方法名作为 Bean 的名称
  - @Inject：和@Autowired 注解一样也是按类型匹配注入的 Bean， 但没有 reqired 属性

## AOP

- 纵向切面：通过继承、或者方法内部执行相同的逻辑，有缺点：
  - 代码混乱：与原有业务不相关代码无关。
  - 代码分散：为了满足单一需求，在多个模块(方法)里面多次重复相同代码，如果需求发生变化，所有地方都需要改动。

- 横向切面：在不改变原有业务逻辑的情况下，从侧面切入，执行其他功能代码，可以使用动态代理方式解决。

### 简介

- AOP(Aspect-Oriented Programming, 面向切面编程): 是一种新的方法论, 是对传统 OOP(Object-Oriented Programming, 面向对象编程) 的补充
- AOP 的主要编程对象是切面(aspect), 而切面模块化横切关注点
- 优点
  - 每个事物逻辑位于一个位置, 代码不分散, 便于维护和升级
  - 业务模块更简洁, 只包含核心业务代码

### 术语

- 切面(Aspect):  横切关注点(跨越应用程序多个模块的功能)被模块化的特殊对象
- 通知(Advice):  切面必须要完成的工作，通知类型如下：
  - Before：前置通知, 在方法执行之前执行
  - After：后置通知, 在方法执行之后执行(获取不到方法返回值)
  - AfterRunning：返回通知, 在方法返回结果之后执行(可以获取方法返回结果)
  - AfterThrowing：异常通知, 在方法抛出异常之后
  - Around：环绕通知, 围绕着方法执行
- 目标(Target): 被通知的对象
- 代理(Proxy): 向目标对象应用通知之后创建的对象
- 连接点（Joinpoint）：程序执行的某个特定位置
- 切点（pointcut）：每个类都拥有多个连接点

### 使用AspectJ

#### 注解方式

- 启用注解支持：在Spring配置文件中配置：\<aop:aspectj-autoproxy>，作用自动为与 AspectJ 切面匹配的 Bean 创建代理

- 使用@AspectJ注解声明切面，前提是当前类被Spring管理，如：

  ```java
  @Aspect
  @Component
  public class LoggingAspect {}
  ```

- 声明方法配置切入点表达式,可以通过操作符 &&, ||, ! 结合起来，如：

  ```java
  @Pointcut("execution(public int com.atguigu.spring.aop.ArithmeticCalculator.*(..)) [|| execution]")
  public void declareJointPointExpression(){}
  ```

- 创建方法并配置通知类型，如：

  - 前置通知

    ```java
    @Before("declareJointPointExpression()")
    public void beforeMethod(JoinPoint joinPoint){
      String methodName = joinPoint.getSignature().getName();
      Object [] args = joinPoint.getArgs();
      System.out.println("The method " + methodName + " begins with " + Arrays.asList(args));
    }
    ```

  - 后置通知

    ```java
    @After("declareJointPointExpression()")
    public void afterMethod(JoinPoint joinPoint){
      String methodName = joinPoint.getSignature().getName();
      System.out.println("The method " + methodName + " ends");
    }
    ```

  - 返回通知

    ```java
    @AfterReturning(value="declareJointPointExpression()",
                    returning="result")
    public void afterReturning(JoinPoint joinPoint, Object result){
      String methodName = joinPoint.getSignature().getName();
      System.out.println("The method " + methodName + " ends with " + result);
    }
    ```

  - 异常通知

    ```java
    @AfterThrowing(value="declareJointPointExpression()",
                   throwing="e")
    public void afterThrowing(JoinPoint joinPoint, Exception e){
      String methodName = joinPoint.getSignature().getName();
      System.out.println("The method " + methodName + " occurs excetion:" + e);
    }
    ```

  - 环绕通知

    ```java
    @Around("execution(public int com.spring.aop.ArithmeticCalculator.*(..))")
    public Object aroundMethod(ProceedingJoinPoint pjd){
    
      Object result = null;
      String methodName = pjd.getSignature().getName();
    
      try {
        //前置通知
        System.out.println("The method " + methodName + " begins with " + Arrays.asList(pjd.getArgs()));
        //执行目标方法
        result = pjd.proceed();
        //返回通知
        System.out.println("The method " + methodName + " ends with " + result);
      } catch (Throwable e) {
        //异常通知
        System.out.println("The method " + methodName + " occurs exception:" + e);
        throw new RuntimeException(e);
      }
      //后置通知
      System.out.println("The method " + methodName + " ends");
    
      return result;
    }
    ```

- 指定切面顺序可以通过实现Ordered 接口的getOrder()方法或通过@Order注解，值越小越先执行，如：

  ```java
  @Order(2)
  @Aspect
  @Component
  public class LoggingAspect {}
  ```

#### XML方式

- 声明切面：在spring的配置文件中所有与切面相关的配置都必须在\<aop:config>内部,对于每个切面而言, 都要创建一个 \<aop:aspect> 元素来为具体的切面实现引用后端 Bean 实例,如：

  ```xml
  <aop:config>
    <!-- 配置切面及通知 -->
    <aop:aspect ref="loggingAspect" order="2"></aop:aspect>	
    <aop:aspect ref="vlidationAspect" order="1"></aop:aspect>
  </aop:config>
  ```
  
- 声明切入点：使用\<aop:pointcut> ，如：

  ```xml
  <!--
  定义在 <aop:aspect> 元素下: 只对当前切面有效
  定义在 <aop:config> 元素下: 对所有切面都有效
  -->
  <aop:pointcut expression="execution(* com.atguigu.spring.aop.xml.ArithmeticCalculator.*(int, int))" id="pointcut"/>
  ```

- 声明通知：使用 pointcut-ref来引用切入点, 或用 pointcut直接嵌入切入点表达式，method 属性指定切面类中通知方法的名称，如：

  ```xml
  <aop:aspect ref="loggingAspect" order="2">
  			<aop:before method="beforeMethod" pointcut-ref="pointcut"/>
  			<aop:after method="afterMethod" pointcut-ref="pointcut"/>
  </aop:aspect>
  ```

## 事务管理

### 编程式事务管理

将事务管理代码嵌入到业务方法中来控制事务的提交和回滚. 在编程式管理事务时, 必须在每个事务操作中包含额外的事务管理代码

### 声明式事务管理

将事务管理代码从业务方法中分离出来, 以声明的方式来实现事务管理. 事务管理作为一种横切关注点, 可以通过 AOP 方法模块化. Spring 通过 Spring AOP 框架支持声明式事务管理

```xml
<!-- 1. 配置事务管理器 -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
  <property name="dataSource" ref="dataSource"></property>
</bean>

<!-- 2. 配置事务属性 -->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
  <tx:attributes>
    <!-- 根据方法名指定事务的属性 -->
    <tx:method name="purchase" propagation="REQUIRES_NEW"/>
    <tx:method name="get*" read-only="true"/>
    <tx:method name="find*" read-only="true"/>
    <tx:method name="*"/>
  </tx:attributes>
</tx:advice>

<!-- 3. 配置事务切入点, 以及把事务切入点和事务属性关联起来 -->
<aop:config>
  <aop:pointcut expression="execution(* com.atguigu.spring.tx.xml.service.*.*(..))" 
                id="txPointCut"/>
  <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointCut"/>	
</aop:config>
```

- @Transactional ：标注需要使用事务的方法，方法必须是public修饰,需要在配置文件中增加，事务处理器名称默认transactionManager，可以不写

  ```xml
  <tx:annotation-driven transaction-manager="transactionManager"/>
  ```

### Spring支持传播行为

| 传播属性                  | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| Propagation.REQUIRED      | 如果有事务在运行，当前方法就在这个事务内运行，否则就自己开启一个新事务 |
| Propagation.REQUIRES_NEW  | 当前方法必须启动新事务，并在自己创建的事务内运行，如果有事务在运行，会将它挂起 |
| Propagation.SUPPORTS      | 如果有事务在运行，当前方法就在这个事务内运行，否则它可以不运行在事务中 |
| Propagation.NOT_SUPPORTED | 当前方法不运行在事务中，如果有运行的事务将它挂起             |
| Propagation.MANDATORY     | 当前方法必须运行在事务内部，如果没有正在运行的事务就，就抛出异常 |
| Propagation.NEVER         | 当前方法不运行在事务内部，如果有正在运行的事务就抛出异常     |
| Propagation.NESTED        | 如果有事务在运行，当前的方法就应该在这个事务的嵌套事务中运行，否者就启动一个新事务，并在新事务内运行。 |

### 事务隔离级别

- 当同一个应用程序或者不同应用程序中的多个事务在同一个数据集上并发执行时, 可能会出现许多意外的问题，分为三种类型：
  - 脏读: 对于两个事物 T1, T2, T1  读取了已经被 T2 更新但 还没有被提交的字段。
  - 不可重复读:对于两个事物 T1, T2, T1  读取了一个字段, 然后 T2 更新了该字段. 之后, T1再次读取同一个字段, 值就不同了
  - 幻读:对于两个事物 T1, T2, T1  从一个表中读取了一个字段, 然后 T2 在该表中插入了一些新的行. 之后, 如果 T1 再次读取同一个表, 就会多出几行。

- 事务隔离级别：事务的隔离级别要得到底层数据库引擎的支持, 而不是应用程序或者框架的支持，Oracle 支持的 2 种事务隔离级别：READ_COMMITED , SERIALIZABLE，Mysql 支持 4 中事务隔离级别

  | 隔离级别        | 描述                                                         |
  | --------------- | ------------------------------------------------------------ |
  | DEFAULT         | 使用底层数据默认的事务隔离级别                               |
  | SERIALIZABLE    | 确保事务可以从一个表中读取相同的行，在这个事务执行期间，禁止其他事务对表进行更新操作，所有问题都可避免，但效率十分低 |
  | REPEATABLE_READ | 确保事务可以多次从一个字段中读取相同的值，在这个事务执行期间，禁止其他事务对该表进行更新，可以避免脏读和不可重复读，但幻读依旧存在 |
  | READ_COMMIT     | 只允许读取已被其他事务提交的变更，可以避免脏读，但幻读和不可重复读问题依旧存在 |
  | READ_UNCOMMIT   | 允许事务读取其他事务未被提交的数据，脏读，幻读和不可重复读问题都存在 |

- 设置事务隔离级别

  - @Transactional的isolation 属性指定
  - 事务配置\<tx:method> 元素中通过isolation 属性指定

- 设置事务回滚属性：默认情况RuntimeException和Error类型的异常都会回滚。

  - @Transactional 注解rollbackFor 和 noRollbackFor 属性来定义，两个类型都是数组，所以可以同时指定多个类型
    - rollbackFor：需要回滚的异常
    - noRollbackFor：不需要回滚的异常
  - 事务配置\<tx:method> 元素中通过rollbackFor 和 noRollbackFor属性指定，多个异常用逗号分隔

- 超时和只读属性

  - 由于事务可以在行和表上获得锁,  因此长事务会占用资源, 并对整体性能产生影响，需要配置超时事务属性timeout(单位秒)
  - 如果一个事物只读取数据但不做修改, 数据库引擎可以对这个事务进行优化，需要配只读事务属性readOnly

# Spring注解驱动

## 组件注册

- @Configuration：使用在类上，告知Spring这是一个配置类

  - 创建IOC容器

    ```java
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
    ```

- @Bean：给容器中注册一个Bean，类型为返回值的类型，id默认是用方法名作为id

- @ComponentScan：配置扫描包

  - value属性：指定扫描包

  - excludeFilters = Filter[] ：指定扫描的时候按照什么规则排除哪些组件

  - includeFilters = Filter[] ：指定扫描的时候只需要包含哪些组件

  - useDefaultFilters属性：是否扫描默认包含的标注类(@Controller、@Service、@Reposiroty、@Component)

  - excludeFilters注解参数@Filter：指定过滤类型及规则

    - FilterType.ANNOTATION：按照注解

    - FilterType.ASSIGNABLE_TYPE：按照给定的类型；

    - FilterType.ASPECTJ：使用ASPECTJ表达式

    - FilterType.REGEX：使用正则指定

    - FilterType.CUSTOM：使用自定义规则，需要实现org.springframework.core.type.filter.TypeFilter接口，如：

      ```java
      package com.config;
      import java.io.IOException;
      import org.springframework.core.io.Resource;
      import org.springframework.core.type.AnnotationMetadata;
      import org.springframework.core.type.ClassMetadata;
      import org.springframework.core.type.classreading.MetadataReader;
      import org.springframework.core.type.classreading.MetadataReaderFactory;
      import org.springframework.core.type.filter.TypeFilter;
      
      public class MyTypeFilter implements TypeFilter {
          /**
           * metadataReader：读取到的当前正在扫描的类的信息
           * metadataReaderFactory:可以获取到其他任何类信息的
           * 返回true表示匹配成功
           */
          @Override
          public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                  throws IOException {
              // TODO Auto-generated method stub
              //获取当前类注解的信息
              AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
              //获取当前正在扫描的类的类信息
              ClassMetadata classMetadata = metadataReader.getClassMetadata();
              //获取当前类资源（类的路径）
              Resource resource = metadataReader.getResource();
      
              String className = classMetadata.getClassName();
              System.out.println("--->" + className);
              if (className.contains("er")) {
                  return true;
              }
              return false;
          }
      }
      
      ```

- @Scope：配置作用域

  - prototype：多实例的：ioc容器启动并不会去调用方法创建对象放在容器中，每次获取的时候才会调用方法创建对象；
  * singleton：单实例的（默认值），ioc容器启动会调用方法创建对象放到ioc容器中
  * request：同一次请求创建一个实例
  * session：同一个session创建一个实例

- @Lazy：懒加载

- @Conditional({Condition}) ： 按照一定的条件进行判断，满足条件给容器中注册bean，如系统是linux才创建某个bean，如：

  ```java
  package com.condition;
  
  import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
  import org.springframework.beans.factory.support.BeanDefinitionRegistry;
  import org.springframework.context.annotation.Condition;
  import org.springframework.context.annotation.ConditionContext;
  import org.springframework.core.env.Environment;
  import org.springframework.core.type.AnnotatedTypeMetadata;
  
  //判断是否linux系统
  public class LinuxCondition implements Condition {
      /**
       * ConditionContext：判断条件能使用的上下文（环境）
       * AnnotatedTypeMetadata：注释信息
       */
      @Override
      public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
          // TODO是否linux系统
          //1、能获取到ioc使用的beanfactory
          ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
          //2、获取类加载器
          ClassLoader classLoader = context.getClassLoader();
          //3、获取当前环境信息
          Environment environment = context.getEnvironment();
          //4、获取到bean定义的注册类
          BeanDefinitionRegistry registry = context.getRegistry();
  
          String property = environment.getProperty("os.name");
  
          //可以判断容器中的bean注册情况，也可以给容器中注册bean
          boolean definition = registry.containsBeanDefinition("person");
          if (property.contains("linux")) {
              return true;
          }
          return false;
      }
  }
  
  @Configuration
  public class CustomConfig{
    @Conditional({LinuxCondition.class})
    @Bean("linus")
  	public Person person02(){
  		return new Person("linus", 48);
  	}
  }
  ```

- @Import：快速导入，如：

  ```java
  @Configuration
  @Import({Color.class,Red.class,MyImportSelector.class,MyImportBeanDefinitionRegistrar.class})
  public class CustomConfig{}
  ```

  - @Import(要导入到容器中的组件)；容器中就会自动注册这个组件，id默认是全类名

  - ImportSelector:返回需要导入的组件的全类名数组，如：

    ```java
    package com.condition;
    
    import org.springframework.context.annotation.ImportSelector;
    import org.springframework.core.type.AnnotationMetadata;
    
    //自定义逻辑返回需要导入的组件
    public class MyImportSelector implements ImportSelector {
    
        //返回值，就是到导入到容器中的组件全类名
        //AnnotationMetadata:当前标注@Import注解的类的所有注解信息
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            //方法不要返回null值
            return new String[]{"com.bean.Blue", "com.bean.Yellow"};
        }
    }
    ```

  - ImportBeanDefinitionRegistrar:手动注册bean到容器中，如：

    ```java
    package com.condition;
    
    import org.springframework.beans.factory.support.BeanDefinitionRegistry;
    import org.springframework.beans.factory.support.RootBeanDefinition;
    import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
    import org.springframework.core.type.AnnotationMetadata;
    import com.bean.RainBow;
    
    public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    	/**
    	 * AnnotationMetadata：当前类的注解信息
    	 * BeanDefinitionRegistry:BeanDefinition注册类；
    	 * 		把所有需要添加到容器中的bean；调用
    	 * 		BeanDefinitionRegistry.registerBeanDefinition手工注册进来
    	 */
    	@Override
    	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    		
    		boolean definition = registry.containsBeanDefinition("com.atguigu.bean.Red");
    		boolean definition2 = registry.containsBeanDefinition("com.atguigu.bean.Blue");
    		if(definition && definition2){
    			//指定Bean定义信息；（Bean的类型，Bean。。。）
    			RootBeanDefinition beanDefinition = new RootBeanDefinition(RainBow.class);
    			//注册一个Bean，指定bean名
    			registry.registerBeanDefinition("rainBow", beanDefinition);
    		}
    	}
    }
    ```

- 使用FactoryBean注册组件

  - 实现org.springframework.beans.factory.FactoryBean接口，默认调用getObject()方法创建bean实例，并返回。
  - 通过ApplicationContext获取到的默认是getObject()方法返回的对象。
  - 要获取工厂Bean本身，我们需要给id前面加一个&，如：getBean("&colorFactoryBean");

## 生命周期

1. 指定初始化和销毁方法：通过@Bean指定init-method和destroy-method
2. Bean实现接口：实现InitializingBean(定义初始化逻辑)、DisposableBean(定义销毁逻辑)
3. 使用JSR250
   - @PostConstruct：在bean创建完成并且属性赋值完成；来执行初始化方法
   - @PreDestroy：在容器销毁bean之前通知我们进行清理工作
4. bean的后置处理器：实现BeanPostProcessor接口
   - postProcessBeforeInitialization：在初始化之前工作
   - postProcessAfterInitialization：在初始化之后工作

## 属性赋值

- @PropertySource：引入外部配置文件.properties

- @Value：给属性，方法入参赋值，可以使用SpEl表达式

## 自动装配

- @Autowired：默认按照类型到容器中匹配，如果只有一个，就注入，有多个就按照名称去匹配，默认标注属性必须装配有值。

  - @Qualifier注解：一般与Autowired配合使用，指定使用什么名称到容器中匹配
  - required属性：指定该属性是否必须被装配。
  - @Primary注解：让Spring进行自动装配的时候，默认使用首选的bean，也可以继续使用@Qualifier指定需要装配的bean的名字
  - 放在方法上：@Bean标注的方法，方法参数值从容器中获取，可以不写@Autowired
  - 放在构造方法上：如果组件只有一个有参构造器，这个有参构造器的@Autowired可以省略，参数位置的组件还是可以自动从容器中获取。

- java规范注解

  - @Resource(JSR250)：和@Autowired一样，默认按照组件名称装配，不支持@Primary注解功能，没有required=false的功能
  - @Inject(JSR330)：需要导入javax.inject的包，和Autowired的功能一样。没有required=false的功能

- 注入Spring底层组件：当需要使用Spring底层一些组件时，可以实现xxxAware接口，在创建对象时，Spring会调用接口规定的方法，注入对应的组件，如实现ApplicationContextAware接口，Spring会调用ApplicationContextAwareProcessor注入applicationContext对象。

- 根据运行环境装配：如有多数据源时，根据不同的运行环境，选择不同的数据源

  - @Profile注解：指定组件在哪个环境的情况下才能被注册到容器中，不指定，任何环境下都能注册这个组件，如：

    ```java
    @PropertySource("classpath:/dbconfig.properties")
    @Configuration
    public class MainConfigOfProfile implements EmbeddedValueResolverAware{
    	
    	@Value("${db.user}")
    	private String user;
    	
    	private StringValueResolver valueResolver;
    	
    	private String  driverClass;
    
    	@Bean
    	public Yellow yellow(){
    		return new Yellow();
    	}
    	
    	@Profile("test")
    	@Bean("testDataSource")
    	public DataSource dataSourceTest(@Value("${db.password}")String pwd) throws Exception{
    		ComboPooledDataSource dataSource = new ComboPooledDataSource();
    		dataSource.setUser(user);
    		dataSource.setPassword(pwd);
    		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
    		dataSource.setDriverClass(driverClass);
    		return dataSource;
    	}
    	
    	
    	@Profile("dev")
    	@Bean("devDataSource")
    	public DataSource dataSourceDev(@Value("${db.password}")String pwd) throws Exception{
    		ComboPooledDataSource dataSource = new ComboPooledDataSource();
    		dataSource.setUser(user);
    		dataSource.setPassword(pwd);
    		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/ssm_crud");
    		dataSource.setDriverClass(driverClass);
    		return dataSource;
    	}
    	
    	@Profile("prod")
    	@Bean("prodDataSource")
    	public DataSource dataSourceProd(@Value("${db.password}")String pwd) throws Exception{
    		ComboPooledDataSource dataSource = new ComboPooledDataSource();
    		dataSource.setUser(user);
    		dataSource.setPassword(pwd);
    		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/scw_0515");
    		
    		dataSource.setDriverClass(driverClass);
    		return dataSource;
    	}
    
    	@Override
    	public void setEmbeddedValueResolver(StringValueResolver resolver) {
    		// TODO Auto-generated method stub
    		this.valueResolver = resolver;
    		driverClass = valueResolver.resolveStringValue("${db.driverClass}");
    	}
    }
    ```

    - 加了环境标识的bean，只有这个环境被激活的时候才能注册到容器中。默认是default环境
    - 写在配置类上，只有是指定的环境的时候，整个配置类里面的所有配置才能开始生效
    - 没有标注环境标识的bean在，任何环境下都是加载的

  - 切换运行环境方式

    1. 使用命令行动态参数: 在虚拟机参数位置加载 -Dspring.profiles.active=test

    2. 代码的方式激活某种环境，如：

       ```java
       //1、创建一个applicationContext
       AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
       //2、设置需要激活的环境
       applicationContext.getEnvironment().setActiveProfiles("dev");
       //3、注册主配置类
       applicationContext.register(MainConfigOfProfile.class);
       //4、启动刷新容器
       applicationContext.refresh();
       ```

## AOP



## 声明式事务

