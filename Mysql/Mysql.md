## 介绍数据库

### 本质

数据库本质就是文件，永久性存储数据。

- 本身有很多写好的流程，可以帮助我们操作文件。
- 想要操作文件中的数据，直接给数据库发送指令。

### 分类

- 关系型数据库：SQL
- 非关系型数据库：NoSQL

### 历史

- 1970年 IBM公司  研究数据之间的关系
  - 将数据存储在表格里，表格之间的联系记录着某种关系
- 1974年 IBM公司  研究出一套规范
  - SEQUEL:Structured English Query Language
- 1976年   SEQUEL2  新版本发布
- 1980年  SQL:Structured Query Language
- 1989年  由国际标准化组织(ISO)颁布SQL正式国际标准  SQL89标准

### 语言规范

SQL语言规范不区分大小写。

SQL结构化查询语句：

- DDL(Data Definition Language)

  - 数据定义语言，用来创建，删除，修改数据库中的对象(表格,用户,索引,视图,存储过程,触发器)

  - create:创建、drop：删除、alter：修改

- DML(Data Manipulation Language)

  - 数据操作语言，用来操作数据库中具体的数据
  - insert:新增、delete:删除、update：修改、select：查询(*DQL:Data Query Language)

- DCL(Data Control Language)

  - 数据控制语言，用来控制用户权限
  - grant 权限 to 用户：赋予权限；revoke 权限，权限 from 用户：回收权限

- TPL(Transcation Process Language)

  - 事务处理语言，可以理解为多线程并发访问同一个文件资源，带来的安全问题
  - 开始事务：begin transcation、提交：commit、回滚：rollback、保存还原点：save-point A:保存A还原点

### 数据库设计范式(**Normal Form**)

- 第一范式(1NF)
  - 数据的原子性：数据库中的每一张表的每一个列都是不可分割的(行列交叉点的单元格内只存储一个数据)
  - 每一个表必须有主键约束(快速查询某一行记录)，可以用联合主键
- 第二范式(2NF)
  - 在满足1NF的前提下，不允许出现部分依赖性(所有非主键完全依赖于主键)
- 第三范式(3NF)
  - 在满足前两个范式的前提下，不允许出现传递依赖性(非主键列只依赖主键，不能受到非主键列影响)

## 目录

- bin：存储可执行文件
- data：存储数据文件
- docs：文档
- include：存储包含的头文件
- lib：存储库文件
- share：错误信息和字符集文件

## 登录

- -D， --database=name：打开指定数据库
- --delimiter=name：指定分割符
- -h，--host=name：指定服务器名称或地址
- -p，--password[=name] ：密码
- -P，--port：指定端口号
- --prompt=name：设置提示符\D:完整日期，\d:当前数据库，\h:服务器名称，\u:当前用户
- -u，--user=name：用户名
- -V，--version：输出版本信息并退出

## 数据类型

- 五种整型
  - TINYINT（1字节）、SMALLINT（2字节）、MEDIUMINT（3字节）、INT（4字节）、BIGINT（8字节）
- 两种浮点
  - float[(M,D)]: M数字总位数 D小数点后面位数；单精度浮点数精确到大约7位小数位；
  - double[(M,D)]：与float类似范围更大更精确
- 日期类型
  - DATE：1000.01.01---9999.12.31 
  - DATETIME：1000.01.01 00:00:00---9999.12.31 23:59:59
  - TIMESTAMP：1970.01.01 00点起--2037年之间的一个值
  - TIME：-8385959到8385959
  - YEAR：2位或者4位，1970到2069年
- 字符型
  -  CHAR；VARCHAR；TINYTEXT；TEXT；MEDIUMTEXT；LONGTEXT；ENUM;SET;
- utf8排序规则
  - utf8_general_ci：默认的排序规则，性能高，不太精确
  - utf8_unicode_ci：性能低，扩展性好

## DDL数据库定义语言

- 数据库操作

  - 创建数据库：

    CREATE {DATABASE|SCHEMA} [IF NOT EXISTS] db_name [DEFAULT] CHARACTER SET [=] charset_name

  - 查看错误信息：show warnings

  - 显示数据库创建的编码指令：show create database 库名

  - 修改数据库编码方式：alter database 库名 character set =编码格式

  - 删除数据库：drop {database|schema} [IF EXISTS] 库名

- 数据表操作

  - 创建表

    ```mysql
    CREATE TABLE [IF NOT EXISTS] table_name(
      column_name data_type,
      ...
    ) [ENGING = Innodb DEFAULT CHARSET = UTF8 collate utf8_general_ci];
    ```

  - 修改表名：alter table 原表名 rename [to] 新表名

  - 修改原有列(列名、长度、类型..)：alter table 表名 change 原列名 新列名 新类型 新长度

  - 修改列属性：alter table 表名 modify 列名 新类型 新长度

  - 新增列：alter table 表名 add 新列名 新类型 新长度 [after 列]

  - 删除原列：alter table 表名 drop 列名

  -  查看数据表结构：show columns from 表名 | desc 表名

  -  查看表状态：show table status from 数据库名 like '表名'

  - 查看表中的主键约束：show keys from tb_name

  - 从文件导入数据到数据表：load data local infile "mytable.txt" into 表名

  - 修改表的存储引擎：alter table 表名 ENGINE=存储引擎名
  
  - 删除索引：ALTER TABLE table_name DROP index  index_name;
  
  - 查看索引：SHOW INDEX FROM table_name;
  
  - 清空表：truncate table table_name;

## DML数据操作语言

- 插入数据：insert [into] 表名 [(col_name,…)] values (val,…)[,(val,...)]
- 删除数据：delete from 表名 [where …]
- 查询数据：select expr,… from 表名 [where ...]
- 修改数据：update 表名 set 列=值[,列=值,… where …]

### 函数

**mysql中所有函数都有返回值**

- 比较函数：isnull(字段)：空值返回1，不是空值返回0

- 数学函数：abs绝对值、floor向下取整、mod(5,2)取余数、pow(3,2)求次方、round(X,D)将x保留d为小数，四舍五入、rand()随机数

- 日期和时间：now()、year()、month()、day()、week()

- 流程函数：if(条件,值1，值2)，ifnull(字段，值)

- 分组函数(聚合函数)：count()、avg()、min()、max()、sum()

- 字符串函数

  | mysql字符串函数                     | String类方法  | 功能                               |
  | ----------------------------------- | ------------- | ---------------------------------- |
  | length()                            | length()      | 字符串长度                         |
  | concat()                            | concat()      | 字符串拼接                         |
  | substr()                            | substring()   | 字符串截取                         |
  | instr()                             | indexOf()     | 指定字符串位置                     |
  | replace()                           | replace()     | 替换字符串                         |
  | upper()                             | toUpperCase() | 字符串转大写                       |
  | lower()                             | toLowerCase() | 字符串转小写                       |
  | ltrim() rtrim()                     | trim()        | 去除字符串前后空格                 |
  | lpad('字符串',长度,补充字符) rpad() |               | 显示多少个字符，不够补充，超出截取 |
  | reverce()                           |               | 字符串反转                         |

### 嵌套查询

一个完整的查询sql中，嵌套了另一个完整的sql语句

### 关键字

- where：条件表达式
  - 比较运算符：>   >=   <   <=   !=   =
  - 算数运算符：+   -   *   /
  - 逻辑运算符：and   or   not   如果and和or同时出现，and优先级跟高
  - [not] between A and B：[不]在A到B之前的值
  - [not] in ():括号内的条件满足一个就行，后面也可以是一个查询出来的结果
  - [not] like:模糊查询，%用户代替0-n个字符，_用来代替一个字符(有且只有一个)
- order by：order by 字段 排序规则，字段 排序规则....
  - asc 升序 默认，desc 降序
- distnict：去重，一般用法 distinct 列,列
- group by：分组关键字
- having：与where使用相同
- any && same：满足查询子集中的某一个即可，如：>any、<any、=any(与in一致)、!=any
  - select * from student where classid >any (select classid from myclass where calssid>2);
- all：需要满足查询子集中的全部才可以。!=all(与not in一致)

**注意：**

- 优先级：where>group by >having>order by
- any、some、all使用起来与in类似，查询是否满足后面的子集中的条件，但是后面不允许写固定值，只能写sql语句

### 集合操作

- union：并集
  - select tid,tname,tsex fron teacher union select sid,sname,ssex from student;
  - 前后两个查询子集的列数必须一致；类型可以不一致；默认显示第一个子集的字段；拼接时还会去重
- union all：并集
- union与union all的区别
  - union在进行表连接查询后会去除掉重复的结果，union all不会去重
  - union将会按照字段的顺序排序，union all只是简单的将两个结果合并后返回

### 列的约束

- 主键约束(Primary Key)

  - 每个表中只能有一个主键约束，用来标记数据的唯一性，并且主键列不能为空。

  - alter table 表名 add constraint [约束名称] primary key(列名)

    - alter table user add constraint primary key(user_id);

      简写：alter table myclass add primary key(classid)

  - 添加主键后，可以设置让主键ID自增，如果没有设置起始值，默认从1开始。

    - alter table user modify user_id int(4) auto_increment
    - alter table user change user_id user_id int(4) auto_increment
    - 设置起始值：alter table user auto_increment=10
    - 删除主键：alter table user drop primary key

- 唯一约束(Unique [Key])

  - 为表中的某一个字段条件唯一约束，列的值不能重复，但可以为空，表中可以存在多个唯一约束。

  - alter table 表名 add constraint [约束名字] unique(列)

    - alter table user add constraint uk_user_id unique \[key](user_id);

      简写：alter table user add unique \[key](user_id)，约束名默认是列名

  - 删除唯一约束：alter table user drop index 约束名

- 非空约束(not null)

  - 表格中的某一个或多个列上添加非空约束，这些列的值不能为空。
  - alter table 表名 change 原列名 原列名 类型(长度) [not] null [default 值]
    - alter table user modify name varcahr(20) not null default ""

- 外键约束(Foreign Key)

  - 表中的信息受到另一张表信息的限制，值必须是另一张表某列的值，另一张表的该列的值必须是唯一约束

  - 表中可以有多个列被设置成外键约束，并且列的值可以为空，也可以重复

  - alter table 表名 add constraint fk\_当前表\_关联表 foreign key(当前表列) references 另一个表(key)

    - 简写：alter table 表名 foreign key(当前表列) references 另一个表(key)

  - 删除外键约束：

    - alter table 表名 drop foreign key 约束名称(删除后会添加一个新key)

    - 再执行：alter table 表名 drop key 新key名称

      (一般与原名称一样,可以使用show keys from 表名查看约束名称)

- 检查约束

  - 列在存值得时候做一个细致的检查，范围是否合理等情况
  - alter table 表名 add constraint [约束名字] check(条件)
    - 简写：alter table tb_name add check(条件)
    - 如：alter table user add constraint ck_age check(age>15 and age<30)
  - 删除检查约束：alter table user drop constraint ck_age;

### 联合查询

- 表之间的关系
  - 一对一：两种表的关系是唯一的，一般建成一张表。
  - 一对多：多端存在一个外键列，与一端的主键ID关联
  - 多对多：存在一个中间表存储两种表的关系，中间表有两个字段分别关联两张表的主键，是联合主键。
- 表的联合查询
  - 等值连接：广义笛卡尔积(select * from tb1,tb2)->进行条件筛选--->等值连接
    - 任何两张表都可以进行笛卡尔积查询，性能比较低：select * from A,B where 条件
  - 外连接
    - select * from A left/right [outer] join B on 条件
    - A,B的出现先后，控制A或B表的列的先后显示
    - left/right 决定以哪个表为基本，作为基准的表数据会全部显示，非基准的表按on条件筛选，满足则显示，不满足则显示null
  - 内(自)连接：select * from A inner join B on 条件
    - 两张表都满足关联条件的数据才会显示

### 行列互换

- select 分组字段,max(if(字段='值',字段,默认值)) as '值',max(if(字段='值',字段,默认值)) as '值'.... from tb_name group by 分组字段
- 其中聚合函数max,可以是avg，min等

### 分页查询

- 查询 limit start,length

## DCL数据控制语言

- 创建用户：create user '用户名'@'ip' identified by '密码

  - 用户创建成功后，只有一个默认的Usage权限，只允许登录，不能做其他事情。

- 查看用户权限：show grants for '用户名'@'IP'

- 查看当前用户：select user()

- 给用户赋权：grant 权限1,权限2... on 数据库.表 to '用户名'@'IP'

  - 如：grant all on *.* to 'just'@'localhost'

- 刷新权限：flush privileges

- 收回用户权限：revoke 权限 on 数据库.表 from '用户名'@'IP'

- 修改用户密码：update user set authentication_string=值

  - 如：update user set authentication_string=password('1234') where user='用户名'

- 删除用户：drop user '用户名'@'IP'或者drop user '用户名'@'*'

- **扩展**：常用mysql权限

  - 数据库、数据表、数据列权限

    | 权限           | 权限说明                                       |
    | -------------- | ---------------------------------------------- |
    | Create         | 建立新的数据库或数据表                         |
    | Alter          | 修改已存在的数据表(例如增加/删除列)            |
    | Drop           | 删除数据表或数据库                             |
    | Insert         | 增加表的记录                                   |
    | Delete         | 删除表的记录                                   |
    | Update         | 修改表中已存在的记录                           |
    | Select         | 显示/搜索表的记录                              |
    | References     | 允许创建外键                                   |
    | Index          | 建立或删除索引                                 |
    | Create View    | 允许创建视图                                   |
    | Create Routine | 允许创建存储过程和包                           |
    | Execute        | 允许执行存储过程和包                           |
    | Trigger        | 允许操作触发器                                 |
    | Create User    | 允许更改、创建、删除、重命名用户和收回所有权限 |

  - 全局管理mysql权限

    | 权限           | 权限说明                                  |
    | -------------- | ----------------------------------------- |
    | Grant Option   | 允许向其他用户授予或移除权限              |
    | Show View      | 允许执行SHOW CREATE VIEW语句              |
    | Show Databases | 允许账户执行SHOW DATABASE语句来查看数据库 |
    | Lock Table     | 允许执行LOCK TABLES语句来锁定表           |
    | File           | 在MySQL服务器上读写文件                   |
    | Process        | 显示或杀死属于其它用户的服务线程          |
    | Reload         | 重载访问控制表，刷新日志等                |
    | ShutDown       | 关闭MySQL服务                             |

  - 特别的权限

    - All：允许做任何事(和root一样)
    - Usage：只允许登录，其它什么也不允许做

## TPL事务处理语言

- mysql数据库处理事务管理默认效果可以更改，默认自动控制提交事务。
  - set autocommit = 1；设置事务自动提交(1)，手动提交(0)
- 查看mysql变量值：show variables like '%commit%'

### 本质

- 多线程并发操作同一张表可能带来的安全问题

### 四大特性

- A：Atomicity

  - 原子性：一个事务中的所有操作是一个整体，不可再分;事务中的所有操作要么都成功，要么都失败

- C：Consistency

  - 一致性：一个用户操作了数据，提交后另一个用户看到的数据与前一个用户看到的效果一致

- I：Isolation

  - 隔离性：指多个用户同时操作数据库，一个用户操作数据库时，另外的用户不能有所干扰，多个用户间的数据事务操作要相互隔离

  - 事务的隔离级别：

    - Serializable：最高级别，可避免所有可能出现的问题，性能很慢
    - Repeatable Read：可重复读(避免’脏读’,’不可重复读’)
    - Read Committed：读已提交(避免’脏读’)
    - Read UnCommitted：读取未提交(所有安全隐患均存在)

    mysql默认隔离级别：Repeatable Read，Oracle默认隔离级别：Read Committed

  - 可能带来的安全隐患：

    - 脏读：一个人读到了另外一个人还没有提交的数据—脏数据
    - 不可重复读：相同条件第一次读取数据与后面读取数据不一致，被另外的人执行了修改/删除操作
    - 幻读(虚读)：相同条件第一次读取数据与后面读取数据不一致，被另外的人执行了新增操作

- D：Durability

  - 持久性：用户操作数据的事务一旦被提交(缓存—>文件)，对数据库底层的真实改变是永久的，不可返回

### 执行流程

- 开启事务：每一次执行一条sql语句之前，默认会开启事务
  - 手动开启事务：begin/start transcation
- 执行操作：inser update delete select 可能不止一条sql语句
- 事务的处理
  - 提交(commit)/回滚(rollback)/保存还原点(save point A)，mysql数据库会默认执行提交事务

### 事务级别操作

- 修改事务级别：set session transaction isolation level 级别；
- 查看事务级别：select @@tx_isolation;

## 查询可能产生问题

### SQL注入

- 产生原因

  - 判断不严谨
  - SQL语句问题(允许拼接字符串)

- 解决方法：在JDBC创建状态流是将Statement换成PreparedStatement，创建时就需要预先加载SQL语句，sql中不确定的值可以利用动态化进行参数的处理 利用?代替数据类型及值

  ```java
  String sql = "SELECT * FROM ATM WHERE USERNAME=?";
  PreparedStatement statement = connection.prepareStatement(sql);
  statement.setString(1, username);
  statement.executeQuery();
  ```

- 好处：

  - 增强SQL可读性
  - 可以参数动态化
  - 防止SQL注入
  - 提高执行性能

### 模糊查询

- 主要是使用：like % _

- 解决占位符的方式：

  - Sql语句正常写,在给占位符赋值时将%拼接在值上，如：

    ```java
    String sql = "select * from emp where ename like ?";
    pstat.setString(1,"%"+letter+"%");
    ```

  - 将%或者_写在预处理sql中，再给占位符赋值时正常赋值，如：

    ```
    String sql = "select * from emp where ename like \"%\"?\"%\"";
    ```

### 联合查询

- 一对多的关系,在多的端的domain实体中存在一个属性,这个属性就是一端这个对象,查询结果用ArrayList<多端类型>接收
- 查询结果不是原domain实体中的属性，需要用Map接收，类似一个对象，返回结果ArrayList<HashMap<String,Object>>

## 存储引擎

### Innodb

Innodb引擎提供了对数据库ACID事务的支持。并且还提供了行级锁和外键的约束。它的设计的目标就是处理大数据容量的数据库系统

应用场景：更新（删除）操作频率高，或者要保证数据的完整性；并发量高，支持事务和外键。比如OA自动化办公系统

四大特性：

- 插入缓冲（insert/change buffer)：提高数据操作效率
- 二次写(double write)
- 自适应哈希索引(ahi)
- 预读(read ahead)

### MyISAM

原Mysql的默认引擎，不提供事务的支持，也不支持行级锁和外键

引用场景：以读写插入为主的应用程序，比如博客系统、新闻门户网站。

### MEMORY

所有的数据都在内存中，数据的处理速度快，但是安全性不高

MyISAM与Innodb区别：

|                                                              | MyISAM                                                       | Innodb                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 存储结构                                                     | 每张表被存放在三个文件：frm-表格定义、MYD(MYData)-数据文件、MYI(MYIndex)-索引文件 | 所有的表都保存在同一个数据文件中（也可能是多个文件，或者是独立的表空间文件），InnoDB表的大小只受限于操作系统文件的大小，一般为2GB |
| 存储空间                                                     | MyISAM可被压缩，存储空间较小                                 | InnoDB的表需要更多的内存和存储，它会在主内存中建立其专用的缓冲池用于高速缓冲数据和索引 |
| 可移植性、备份及恢复                                         | 由于MyISAM的数据是以文件的形式存储，所以在跨平台的数据转移中会很方便。在备份和恢复时可单独针对某个表进行操作 | 免费的方案可以是拷贝数据文件、备份 binlog，或者用 mysqldump，在数据量达到几十G的时候就相对痛苦了 |
| 文件格式                                                     | 数据和索引是分别存储的，数据`.MYD`，索引`.MYI`               | 数据和索引是集中存储的，`.ibd`                               |
| 记录存储顺序                                                 | 按记录插入顺序保存                                           | 按主键大小有序插入                                           |
| 外键                                                         | 不支持                                                       | 支持                                                         |
| 事务                                                         | 不支持                                                       | 支持                                                         |
| 锁支持（锁是避免资源争用的一个机制，MySQL锁对用户几乎是透明的） | 表级锁定                                                     | 行级锁定、表级锁定，锁定力度小并发能力高                     |
| SELECT                                                       | MyISAM更优                                                   |                                                              |
| INSERT、UPDATE、DELETE                                       |                                                              | InnoDB更优                                                   |
| select count(*)                                              | myisam更快，因为myisam内部维护了一个计数器，可以直接调取。   |                                                              |
| 索引的实现方式                                               | B+树索引，myisam 是堆表                                      | B+树索引，Innodb 是索引组织表                                |
| 哈希索引                                                     | 不支持                                                       | 支持                                                         |
| 全文索引                                                     | 支持                                                         | 支持(5.6+)                                                   |

## 索引

### 定义

- 索引是一种特殊的文件(InnoDB数据表上的索引是表空间的一个组成部分)，它们包含着对数据表里所有记录的引用指针，相当于目录，为了方便查找内容；索引是一个文件，它是要占据物理空间的。

### 特点

- 优点
  - 可以大大加快数据的检索速度，这也是创建索引的最主要的原因
  - 通过使用索引，可以在查询的过程中，使用优化隐藏器，提高系统的性能。
- 缺点
  - 时间方面：创建索引和维护索引要耗费时间，具体地，当对表中的数据进行增加、删除和修改的时候，索引也要动态的维护，会降低增/改/删的执行效率
  - 空间方面：索引需要占物理空间。

### 使用场景

- where：可以将经常作为查询条件的字段添加索引。
- order by：如果排序字段没有索引，将会把所有数据读取待内存中，然后再排序，非常影响性能。
- join：在on的关联条件字段添加索引，不过一般关联条件字段都是主键索引，不用额外添加索引。
- 索引覆盖：如果要查询的字段都建立过索引，引擎会直接在索引表中查询而不会访问原始数据；然而只要有一个字段没有建立索引时，就会做全表扫描，因此在select后面尽量写必要的查询字段。

### 类型

- **主键索引**：数据列不允许重复，不允许为NULL，一个表只能有一个主键。

  创建[组合主键索引]：ALTER TABLE table_name ADD  CONSTRAINT index_name PRIMARY KEY  (column1[,column2,...]);

  删除：ALTER TABLE table_name DROP primary key;

- **唯一索引**：数据列不允许重复，允许为NULL值，一个表允许多个列创建唯一索引。

  创建[组合]唯一索引：ALTER TABLE table_name ADD UNIQUE (column1[,column2,...]);

  删除：ALTER TABLE table_name  drop index index_name;

- **普通索引**：基本的索引类型，没有唯一性的限制，允许为NULL值。

  创建[组合]普通索引：ALTER TABLE table_name ADD INDEX index_name(column1[, column2, column3,...]);

  删除：ALTER TABLE table_name  drop index index_name;

- **全文索引**：是目前搜索引擎使用的一种关键技术，只支持字符类型数据

  创建[组合]全文索引：ALTER TABLE table_name ADD FULLTEXT INDEX index_name (column[,column1,...])；

  删除：ALTER TABLE table_name  drop index index_name;

  使用：SELECT column,... FROM table_name WHERE match(content,tag) against('xxx xxx');

  注意：match() 函数中指定的列必须和全文索引中指定的列完全相同，否则就会报错，无法使用全文索引，这是因为全文索引不会记录关键字来自哪一列。如果想要对某一列使用全文索引，请单独为该列创建全文索引

### 原理

- 就是把无序的数据变成有序的查询。
- 将创建了索引的列的内容进行排序，然后将排序结果生成倒排表，再在倒排表内容上拼上数据地址链。
- 在查询的时候，先拿到倒排表的内容，再取出数据地址链，从而拿到具体数据。

### 创建索引原则

1. 最左前缀匹配原则

   组合索引非常重要的原则，mysql会一直向右匹配直到遇到范围查询(>、<、between、like)就停止匹配，比如a = 1 and b = 2 and c > 3 and d = 4 如果建立(a,b,c,d)顺序的索引，d是用不到索引的，如果建立(a,b,d,c)的索引则都可以用到，a,b,d的顺序可以任意调整。

2. 较频繁作为查询条件的字段才去创建索引

3. 更新频繁字段不适合创建索引

4. 若是不能有效区分数据的列不适合做索引列(如性别，男女未知，最多也就三种，区分度实在太低)

5. 尽量的扩展索引，不要新建索引。比如表中已经有a的索引，现在要加(a,b)的索引，那么只需要修改原来的索引即可

6. 定义有外键的数据列一定要建立索引

7. 对于那些查询中很少涉及的列，重复值比较多的列不要建立索引

8. 对于定义为text、image和bit的数据类型的列不要建立索引