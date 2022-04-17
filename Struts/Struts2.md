## 介绍

Struts2框架：是mvc结构的封装框架，实现请求-响应过程中的处理，可以充当vc两层。

mvc框架：struts1、struts2、springMVC、(webwork)

struts1+webwork = struts2

## 搭建、应用

- 创建maven结构web程序

  实现一个web程序，初始化处理

  - 引入servlet依赖
  - 替换一个高版本的web.xml文件(3.0+)，才能支持注解编程
  - 自定义src/main/java和src/main/resources文件夹。

- 使用maven引入struts框架依赖

  在https://mvnrepository.com 网站搜索struts

  ```xml
  <!-- https://mvnrepository.com/artifact/org.apache.struts/struts2-core -->
  <dependency>
      <groupId>org.apache.struts</groupId>
      <artifactId>struts2-core</artifactId>
      <version>2.5.22</version>
  </dependency>
  ```

- 在web.xml文件中配置struts框架核心入口(过滤器)

  ```xml
  <filter>
    <filter-name>struts2</filter-name>
    <filter-class>
      org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter
    </filter-class>
  </filter>
  <filter-mapping>
    <filter-name>struts2</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
  ```

- 创建src/main/resources/struts.xml核心配置文件

## 配置文件

- web.xml

  作用配置一个过滤器，作为struts框架的核心入口

  原来：浏览器发送请求->服务器接收请求->参考web.xml找到servlet->调用servlet.service方法

  现在：浏览器发送请求->服务器接收请求->参考web.xml将对应请求交给struts框架->struts参考struts.xml找到action->执行action.execute方法

- struts-default.xml

  struts框架核心配置文件，在struts2-core.jar文件中。

  常见的配置标签：

  - bean：配置一些类，在框架启动时创建的对象(框架使用)，将来可以通过该配置来改装框架。

  - package：将一些公共的配置打包，集中管理，可以被其他package标签继承。

    - interceptors->interceptor：声明拦截器

      - 拦截器是struts2框架的核心功能。
        1. 每一个拦截器对象都有一个相应的功能
        2. struts在请求响应过程中的一些处理都是由拦截器对象完成的
        3. 常用的拦截器功能，如：接收参数、国际化处理、异常处理、文件上传等
      - 拦截器是AOP应用的体现(切面对象)
        1. aop：面向切面编程(横切面编程)
        2. 特点：在目标之前或之后，执行一些公用性的功能，如：编码处理，登录认证等
        3. 优点：可以动态增减功能，与实际业务解耦

    - interceptors->interceptor-stack：拦截器栈，拦截器功能的组合

      默认拦截器栈(19项功能)，每次经过struts的请求都会执行一遍

    - result-types->result-type：请求处理之后的结果处理方式(响应方式)

- default.properties

  struts框架的公共属性，在struts-core.jar/org.apache.struts2包中

  - 常用属性

    - struts.i18n.encoding=UTF-8：设置编码字符集
    - struts.multipart.maxSize=2097152：设置文件上传最大值
    - struts.action.extension=action,,：设置框架处理请求的后缀
    - struts.enable.DynamicMethodInvocation=false：设置框架是否允许动态方法调用
    - struts.configuration.xml.reload=true：设置struts.xml文件每次请求重新加载(开发时使用)
    - struts.devMode=false：设置开发者模式 
    - struts.custom.i18n.resources=testmessages,testmessages2：设置国际化文件名称

  - 属性定义方式

    - 方式一：在struts.xml文件中使用<constant>标签重置属性

      ```xml
      <struts>
        <constant name="struts.action.extension" value="action,," />
        。。。
      </struts>
      ```

    - 方式二：创建src/struts.properties文件，集中修改

- struts-plugin.xml

  struts框架除了自身提供了很多功能，还提供了许多插件功能(注解编程、json处理、ss整合等)

  这些功能插入框架后，可能会改变框架原有的对象，也可能会增加一些新对象功能

  这些内容都在该配置文件中配置，该配置文件作用是为了插件配置。

- struts.xml

  映射配置文件，主要作用是实现请求-响应关系配置

  常用标签：

  - bean：改造框架

  - constant：重置属性

  - include：引入struts的子配置文件

  - package：做一些集中的配置，可以通过继承，使用其他package中的功能，也可以被其他package使用

    - 属性：

      - name：为包命名，其他package可以通过该名称继承
      - extends，继承其他包
      - namespace：请求路径设置，默认/

    - 标签：

      - action：配置请求映射关系

        ```xml
        <!-- 配置test请求，由TestAction的t1方法处理 -->
        <action name="test" class="com.action.TestAction" method="t1">
        ```

        - result：配置响应处理，该配置可以通过String返回值来确定响应内容和方式

          假如请求方法返回一个"success"字符串

          ```xml
          <!--返回的字符串如果是success，那么指定返回方式为服务器转发到main.jsp页面-->
          <result name = "success" type = "dispatcher">main.jsp</result>
          ```

      - interceptors：自定义拦截器功能

      - results-type：自定义响应类型

      - global-results：设置公共响应映射

        ```xml
        <!--所有返回error字符串的都直接转到error.jsp页面-->
        <global-results>
          <result name="error">error.jsp</result>
        </global-results>
        ```

## 请求-响应

- 接收请求

  浏览器向服务器发送请求，服务器根据web.xml文件将请求交个struts框架，struts框架根据struts.xml文件中配置的struts.action.extension属性确定接收什么样后缀的请求。

- 映射请求

  struts根据struts.xml文件中action配置,确定请求的类及方法

  ```xml
  <!--  框架接收的test1请求，由com.action.TestAction对象的t1方法处理 -->
  <action name="test1" class="com.action.TestAction" method="t1"></action>
  ```

  - 注意：
    - 映射请求时在action的配置中name不用带后缀；
    - 请求的类可以是一个普通类；
    - 没有配置method属性，默认请求execute方法；
    - 处理请求的方法不能有任何参数;
    - 方法返回值只能是string或者void

  - 3中请求映射方式

    - 方式一：一个<action>配置对应一个请求

      ```xml
      <action name="test1" class="com.action.TestAction" method="t1"></action>
      <action name="test2" class="com.action.TestAction" method="t2"></action>
      ```

    - 方式二：动态方法调用，一个<action>配置对应多个请求

      - 浏览器发送请求需要符合以下规则

        /test!t3.do
        /test!t4.do

        上述两个请求表示发送test请求，执行t3、t4方法

      - 在struts.xml配置文件中重置属性，开启动态方法调用，并配置请求映射

        ```xml
        <constant name="struts.enable.DynamicMethodInvocation" value="true" />
        <package>
          <action name="test" class="com.action.TestAction"></action>
        </package>
        ```

      - 在struts.xml的<package>中，指定允许动态方法调用的方法名

        ```xml
        <global-allowed-methods>t3,t4</global-allowed-methods>
        ```

    - 方式三：通配符匹配，一个<action>配置对应多个请求

      - 浏览器发送请求时要符合一定的规则(自定义)，如：

        /Test_t5.do
        /Test_t6.do

      - 使用通配符*配置<action>

        ```xml
        <action name="Test_*" class="com.action.TestAction" method="{1}"></action>
        <!--{1} 表示第1个*代表的位置-->
        <!--
        	/Test_User_t5.do
        	/Test_Book_t6.do
        -->
        <action name="Test_*_*" class="com.action.{1}Action" method="{2}"></action>
        ```

      - 在struts.xml的<package>中，指定允许动态方法调用的方法名。

        ```xml
        <global-allowed-methods>t3,t4</global-allowed-methods>
        ```

- 请求处理

  - 接收参数

    - 接收初始化参数

      - 在struts.xml文件中使用action的子标签param指定初始化参数

        ```xml
        <param name="size" >10</param>
        ```

      - 在Action中定义与上述参数同名的属性，属性类型与参数值类型匹配，并实现该属性的set方法。

        struts框架在初始化Action时，会自动调用set方法为属性赋值(DI)

    - 接收独立参数

      在Action中定义与参数同名的属性，属性类型与参数值类型匹配，并实现该属性的set方法。

    - 接收对象参数(常规)

      对象参数：传递了多个参数，但是这些参数刚好组成一个对象。

      - 在浏览器请求传参时，需要为没一个参数提供一个统一的对象名前缀，如：

        /test3.do?user.uname=dmc&user.upass=123&user.age=18&user.uno=1001

      - 定义装载参数的类，类属性名称必须与参数名称一样，并实现get、set方法。

    - 接收对象参数(模型驱动)

      - 浏览器正常传参，不需要提供对象前缀，如：

        /test4.do?uname=dmc&upass=123&age=18&uno=1001

      - Action事项ModelDriven<User>接口，并指定泛型，并重写方法getModel()

      - Action类中定义User类型的属性，并创建对象，在getModel方法中返回该属性。

        ```java
        User user = new User();
        public User getModel() {
          return user;
        }
        ```

      - 模型驱动机制：struts框架在初始化Action时，检查Action是否实现了ModelDriven接口，实现了就会调用getModel方法获取对象，然后将获取到的请求参数装到值栈顶(默认请求的action)的user对象中

      - 拓展：值栈

        - 执行每次请求时，会创建一个值栈对象，用来存放请求响应过程中的一些数据，如：request、response、session、application等
        - 值栈栈顶装载的就是当前请求的action对象。
        - struts有一个params拦截器，在接收参数后，会获取值栈栈顶的对象，为其set方法赋值
        - 如果发现Action实现了ModelDrive接口，ModelDriven拦截器通过getModel方法获得对象，并将获得的对象压入栈顶，，params拦截器赋值时取得的栈顶对象就是刚刚压入的。

  - 获取servlet相关对象

    servlet对象：HttpServletRequest、HttpServletResponse、HttpSession、ServletContext等

    - 方式一：通过ActionContext获得一个具有session存储功能的Map对象

      ```java
      Map<String,Object> session = ActionContext.getContext().getSession();
      session.put("uname",uname) ;//相当于session.setAttribute("uname",uname);
      //扩展：
      ActionContext.getContext().put("k","v");//相当于request.setAttribute(k,v);
      ActionContext.getContext().getApplication().put("k","v");//相当于application.setAttribute(k,v);
      ```

    - 方式二：通过SerlvetActionCotnext获得原生servlet相关对象

      ```java
      HttpServletRequest request = ServletActionContext.getRequest();
      HttpSession session = request.getSession();	
      ServletContext application = request.getServletContext();
      HttpServletResponse response = ServletActionContext.getResponse();
      ```

    - 方式三：通过实现接口，配合拦截器，使用DI方式获得原生servlet相关对象

      Action实现SessionAware接口，并重写getSession方法，再定义一个session属性

      ```java
      private Map<String,Object> session ;
      public void setSessioin(Map<String,Object> map){
        this.session = map ;
      }
      ```

      机制：struts的servletConfig拦截器发现Action实现了SessionAware，就会根据set方法注入session对象

      拓展：

      ​		ServletRequestAware 接口 获得HttpServletRequest
      ​		ServletResponseAware 接口 获得HttpServletResponse

    注意：以上三种方式的源码分析发现所获得的对象都是来自ActionContext。

- 响应处理

  在Action的方法处理完成后，有可能会返回一个字符串，struts根据该字符串到struts.xml文件中action的子标签result配置的name属性匹配，得到result标签的type属性值，根据该属性的值实现具体的响应。

  ```xml
  <!--服务器内部转发,类似于：request.getRequestDispatcher("main.jsp").forward(req,resp)-->
  <!--转发即可以再次访问struts框架的action对象，也可以访问其他资源文件-->
  <result type="dispatcher" >main.jsp</result>
  
  <!--框架内部放行(转发)，struts框架内部只有action对象资源，转发只能访问其他的action-->
  <result type="chain" >test2</result>
  <!-- 重定向,类似于 response.sendRedirect("main.jsp")-->
  <result type="redirect" >main.jsp</result>
  <!--跳转到指定的Action-->
  <result type="redirectAction" >main.jsp</result>
  <!--文件下载使用-->
  <result type="stream"></result> 
  <!--响应网站源码-->
  <result type="plainText" />
  ```

  拓展：转发携带参数

  ​		只需要在（值栈栈顶的）action对象中定义想用携带的数据对应的属性，并实现get方法

  ```java
  private String a = "dmc" ;
  private String b = "zhangan" ;
  public String getA(){
    return a;
  }
  public String getB(){
    return b;
  }
  //在转发时，会通过get方法将这些参数装到request对象中。
  ```

- 框架内部请求-响应

  - 回顾：
    - AOP特点：可以动态增减功能，不需要改变其他功能源代码。
    - AOP相关对象：
      - 起始对象：动作发起者，类比浏览器
      - 目标对象：动作的完成着
      - 切面对象：在目标之前或之后完成一些操作，具有一定的公用性，如：编码处理
        - 前置切面：目标之前执行
        - 后置切面：目标之后执行
        - 环绕切面：目标之前执行一部分，目标之后再执行一部分
      - 链对象：体现责任链模式，运行一个切面对象一部分代码在目标前执行，一部分在目标后执行，链中包含了需要执行的切面和目标。
      - 代理对象：负责调用切面对象|目标对象|链对象，n个起始对象，只需要调用代理接口。
  - struts的AOP
    - struts中的拦截器就是环绕切面
    - 目标对象：Action对象
      - ActionMapping对象，记录 <action>配置匹配对应的Action类对象，有mapping对象，表示有请求映射
    - 代理对象：ActionProxy对象,代理Action目标，包括链对象。
    - 链对象：ActionInvocation对象，里面包含interceptor,result-type,action
    - 切面对象：interceptor
  - 内部流程：
    - struts根据struts.xml文件创建ActionMapping对象
    - 根据ActionMapping创建ActionProxy对象
      - 在创建代理对象内部，创建了ActionInvocation链对象
      - 在创建链对象时，将默认19拦截器对象，目标action对象，result-type对象装入链对象
    - 执行proxy (目的是为了执行目标)
      - 会按照默认拦截器栈的配置顺序先执行每一个切面，再执行目标action
      - 获得action处理方法返回值
      - 根据返回值找到对应的reuslt-type对象完成响应

## 自定义

- 拦截器
  - 拦截器定义与应用

    - 自定义拦截器类 implements Interceptor接口 或 extends AbstractInterceptor父类

    - 重写intercept(ActionInvocation)

      - struts处理请求时会调用该方法开始拦截器功能，

      - ActionInvocation链对象，包含了其他的拦截器和目标action对象，如果需要可以使用：

        ```java
        actionInvocation.invoke(); //调用下一个拦截器或目标对象
        ```

    - 在struts.xml文件的<package>中声明拦截器(IOC 控制反转)

      ```xml
      <interceptors>
        <interceptor name="m1" class="com.util.MyInterceptor1" />
      </interceptors>
      ```

    - 在指定的<action>映射中，使用子标签<interceptor-ref>表示此次请求处理执行该拦截器

      ```xml
      <action name="test1" class="com.action.TestAction1" method="t1">
        <interceptor-ref name="m1" />
      </action>
      ```

    - 注意：一旦引用了自定义拦截器。默认拦截器将不再自动执行。可以手动引入

  - 拦截器栈配置

    - 在struts.xml文件的<package>中声明拦截器栈

      ```xml
      <interceptors>
        <interceptor name="m1" class="com.util.MyInterceptor1" />
      
        <interceptor-stack name="myStack" >
          <interceptor-ref name="m1" />
          <interceptor-ref name="defaultStack" />
        </interceptor-stack>
      </interceptors>
      ```

    - 在action的配置中引用拦截器栈

      ```xml
      <action name="test3" class="com.action.TestAction1" method="t3">
        <interceptor-ref name="myStack" />
      </action>
      ```

  - 默认拦截器引用：不需要在<action>中引用拦截器，每次请求都会自动执行

    在struts.xml文件的<package>中声明默认拦截器引用

    ```xml
    <package>
    	<default-interceptor-ref name="myStack" />
    </package>
    ```

  - 拦截器初始化参数：在拦截器声明时或拦截器引用时，使用<param>子标签指定初始化参数

    ```xml
    <interceptor name="m1" class="com.util.MyInterceptor1" >
      <param name="a">100</param>
    </interceptor>
    <!--在拦截器类中定义与参数同名的属性，并实现set方法。-->
    ```

  - 方法拦截器：通过传递初始化参数，可以指定那些方法拦截，哪些方法不拦截

    - 首先拦截器会根据引用配置，确定在哪些action请求时被调用，并根据拦截器自身的初始化参数确定哪些方法使用拦截器。

    - 自定义拦截器类 extends MethodFilterInterceptor父类,重写doIntercept方法，不考虑其他方法。

    - 在声明拦截器时，为其(父类)传参，指定可以处理的方法或不可以处理的方法。

      ```xml
      <interceptor name="m2" class="com.util.MyInterceptor2" >
        <!--排除的方法-->
        <param name="excludeMethods">t2,t4,t5</param>
        <!--包含的方法-->
        <param name="includeMethods">t1,t3</param>
      </interceptor>
      ```
- 响应类型
  - 直接响应

    如：response.getWriter().write("main.jsp")

  - 间接响应
    
    如：转发->request.getRequestDispatcher("main.jsp").forward(request,response)
    
    ​		重定向->response.sendRedirect("main.jsp")
    
    - 自定义响应类型
    
      - 定义类 implements Result 接口，重写方法
    
        ```java
        void execute(ActionInvocation var1){
          //编写具体的响应代码
        }
        ```
    
      - 在struts.xml的<package>中声明响应类型
    
        ```xml
        <result-types>
          <result-type name="result1" class="com.util.MyResult"></result-type>
        </result-types>
        ```
    
      - 在<action>响应时，<result type="result1"> 使用指定的响应类型
    
        ```xml
        <action name="test4" class="com.action.TestAction1" method="t4" >
          <result name="success" type="result1" ></result>
        </action>
        ```
    
      - 自定义响应类初始化参数
    
        ```xml
        <!--在声明或使用时，通过<param>子标签指定参数-->
        <result name="success" type="result1" >
          <param name="b" >200</param>
        </result>
        <!--在MyResult类对象中定义与参数同名的属性并实现set方法。-->
        ```
    
    - 引入响应类型插件：以将响应数据转换成json格式为例
    
      - 引入插件依赖：struts2-json-plugin.jar
    
      - 在struts.xml中设置<package extends="json-default" >
    
      - 在返回结果配置处制定类型
    
        ```xml
        <action name="test5" class="com.action.TestAction1" method="t5">
          <result name="success" type="json"></result>
        </action>
        ```
    
      转换机制：jsonResult对象在处理响应时，会（通过ActionInvocation）获得action对象，通过action对象的所有get方法，获得相应返回值，将这些返回值及其属下名称存入map集合，再讲装载好的map集合转换成json，并直接响应回去。

## 文件操作

- 文件上传：框架内部使用的是fileupload组件

  - 网页部分

    - 使用<form>发送请求
    - 设置<form>的两个属性 method="post" enctype="multipart/form-data"
    - 使用<input type="file" name="img" />子标签选择上传的文件。

  - 后台部分：框架中提供了一个拦截器(fileUpload)负责获得上传文件参数

    - 在action中定义File类型的属性,属性名即为参数名，并实现set方法来获取上传文件

      ```java
      private File img ;
      public void setImg(File img){
        this.img = img;
      }
      ```

    - 每一个文件还有2个常用信息，文件名和文件类型，可以同样通过以上方法获取

      ```java
      //定义以参数名为前缀的2个属性,实现set方法
      private String imgFileName ;  
      private String imgContentType ;
      ```

    - 如果上传的是多个参数同名的文件，在action中只需要定义File[]即可。

- 文件下载

  - 说明：其实文件下载，我们不需要做什么，在定义result的type属性值时，如果是stream，该拦截器会帮助我们下载，文件名称可以通过action中的getInputStream方法返回出去。

  - 机制：streamResult对象会先获得栈顶对象(action)，通过反射调用getInputStream方法来获取下载的文件路径及名称，然后通过输入流获取文件内容，并通过reponse将文件内容响应给浏览器实现下载。

  - 注意：响应回浏览器的文件内容默认显示在窗口上，但是可以为streamResult传递参数，指定下载内容所在的位置(形成下载的文件框)，如：

    ```xml
    <result name="success" type="stream" >
      <!--1.png是随便定义的，在弹出下载框的时候可以修改名称-->
      <param name="contentDisposition">attachment;filename=1.png</param>
    </result>
    ```

## 标签

- 逻辑标签

  struts通过转发，可以向jsp网页携带一些数据

  - 在Action中返回数据

    ```java
    int age = 18 ;
    String[]  names = new String[]{"dmc1","dmc2","dmc3"} ;
    public int getAge(){
      return age;
    }
    public String[] getNames(){
      return names;
    }
    //在session中存入值。
    ActionContext.getContext().getSession().put("age",20);
    ```

  - 显示数据：<s:property value="age" />

    注意：struts标签不需要(不能)与el联用

    ​			struts标签默认是从值栈栈顶对象获取相应的属性数据的。

    ​			如果需要获取request,session,application等对象数据，需要使用OGNL(对象图形导航语言),如：

    ​			<s:property value="#session.age" />

  - 判断数据

    ```html
    <s:if test="age>=20" >
      已成年
    </s:if>
    <s:elseif test="age>=10">
      青少年
    </s:elseif>
    <s:else>
      幼年
    </s:else>
    ```

  - 循环数据

    ```html
    <!--String[]-->
    <s:iterator value="names" >
      <s:property />
    </s:iterator>
    ------------------------------
    <!--List[] users-->
    <s:iterator value="users" >
      <s:property value="uno"/>
      <s:property value="uname"/>
    </s:iterator>
    ```

- 表单标签：表单组件自动使用table表格优化格式

  ```html
  <s:form action="test2.do" method="post">
    <s:textfield label="文本框" name="a" ></s:textfield>
    <s:password label="密码框" name="b"></s:password>
    <s:radio list="#{'1':'男','2':'女'}" label="单选按钮" name="c" value="1"></s:radio>
    <s:checkboxlist list="#{'1':'足球','2':'篮球','3':'排球'}" label="复选按钮" name="d"></s:checkboxlist>
    <s:select list="#{'1':'黑龙江','2':'吉林','3':'辽宁'}" label="下拉框" name="e"  ></s:select>
    <s:file label="文件选择框" name="f" ></s:file>
    <s:textarea label="文本域" name="g"></s:textarea>
    <s:reset value="重置"></s:reset>
    <s:submit></s:submit>
  </s:form>
  ```

- 令牌标签

  - 说明：\<s:token> ,作用是防止表单重复提交(F5)

  - 使用

    - 在网页的form中增加令牌标签

    - 需要在action配置时，增加token拦截器

      ```xml
      <action name="test2" class="com.action.TestAction2" method="t2">
        <interceptor-ref name="defaultStack" ></interceptor-ref>
        <interceptor-ref name="token" ></interceptor-ref>
      </action>
      ```

  - 机制：网页在属性时，发现存在一个令牌标签，框架就会在表单页面中加入一个令牌码，表单提交时或获取令牌码并存入session，重复提交时发现令牌码与session中的一样，会给出错误信息"invalid.token".

  - 注意：当token拦截器发现是重复提交时，不会抛出异常，而是直接返回了"invalid.token"，所以可以通过result配置，实现响应处理。

- 国际化标签

  - 特点：相同的网页，可以根据语言系统的不同呈现出不同的语言页面。

  - 应用

    - 在网页中需要国际化的文字部分使用<s:text name="a">代替

    - 在struts.xml中 设置默认属性，指定语言文件的前缀。

      ```xml
      <constant name="struts.custom.i18n.resources" value="dmc" ></constant>
      ```

    - 按照指定的前缀，在src/main/resources根目录创建语言资源文件 

      ​	dmc_zh_CN.properties
      ​	dmc_en_US.properties

    - 在不同的语言资源文件中配置，对应的key和语言翻译如：

      a:我的名称是

      a:my name is

## 注解编程

- struts注解只替代了struts.xml文件中的部分注解(action、result)

- struts注解编程需要引入插件：struts2-convention-plugin.jar

- 需要用到注解编程的Action都需要继承ActionSupport类

- 在处理请求的TestAction1类上，使用：

  ```java
  //指定继承的package
  @ParentPackage("struts-default")
  //指定处理请求的父级路径
  @Namespace("/")
  ```

- 在处理请求的方法上，使用：

  ```xml
  <package name="dmc" extends="struts-default" namespace="/">
  
    <interceptors>
      <interceptor name="m" class="com.util.MyInterceptor" ></interceptor>
  
      <interceptor-stack name="myStack">
        <interceptor-ref name="m"></interceptor-ref>
        <interceptor-ref name="defaultStack"></interceptor-ref>
      </interceptor-stack>
    </interceptors>
    <!--
           <action name="test1" class="com.action.TestAction1" method="t1">
         <interceptor-ref name="myStack"></interceptor-ref>
         <result name="success" type="dispatcher">main.jsp</result>
     </action>
     -->
  </package>
  ```

  ```java
  @ParentPackage("dmc")
  @Namespace("/")
  public class TestAction1 {
    @Action(
      value="test1",
      interceptorRefs = {@InterceptorRef("myStack")},
      results={@Result( name="success",type = "dispatcher" ,location="/main.jsp" )}
    )
    public String t1(){
      System.out.println("-------t1---------");
      return "success" ;
    }
  }
  ```

  