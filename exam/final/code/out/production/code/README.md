# 错误处理

## 编码前的设计

首先，考虑到错误处理的两大类型：语义不相关和语义相关，我们采用将其耦合入递归下降语法分析的过程中的解决方案。同时，由于其中语义相关的错误需要符号表的支持（如名字重定义等错误），我们在这一阶段部分地实现项目中端，即符号和符号表。

### 1. 错误处理

#### 1.1 语义不相关错误

| 错误类型     | 错误类别码 | 解释                                                         |
| ------------ | ---------- | ------------------------------------------------------------ |
| 非法符号     | a          | 格式字符串中出现非法字符，报错行号为**<FormatString>**所在行数 |
| 缺少分号     | i          | 报错行号为分号**前一个非终结符**所在行号                     |
| 缺少右小括号 | j          | 报错行号为右小括号**前一个非终结符**所在行号                 |
| 缺少右中括号 | k          | 报错行号为右中括号**前一个非终结符**所在行号                 |

以上四类报错的特点是他们不和语义相关，只是违背了文法或语义不相关的约束。

#### 1.2 语义相关错误

| 错误类型                                | 错误类别码 | 解释                                                         |
| --------------------------------------- | ---------- | ------------------------------------------------------------ |
| 名字重定义                              | b          | 函数名或变量名在**当前作用域**下重复定义<br/>注意，变量一定是同一级作用域下才会判定出错，不同作用域下，内层会覆盖外层定义。<br/>报错行号为**`<Ident>`**所在行数 |
| 未定义的名字                            | c          | 使用了未定义的标识符<br/>报错行号为**`<Ident>`**所在行数<br/>此处包括函数未定义（在函数调用前没有函数声明）和变量/常量未定义 |
| 函数参数个数不匹配                      | d          | 函数调用语句中，参数个数与函数定义中的参数个数不匹配<br/>报错行号为函数名调用语句的**函数名**所在行数 |
| 函数参数类型匹配                        | e          | 函数调用语句中，参数个数与函数定义中的参数个数不匹配。<br>报错行号为函数名调用语句的**函数名**所在行数 |
| 无返回值的函数存在不匹配的`return`语句  | f          | 报错行号为`return`所在行号                                   |
| 有返回值的函数缺少`return`语句          | g          | 只需要考虑函数末尾是否存在`return`语句即可，**无需考虑**数据流 |
| 不能改变常量的值                        | h          | `<LVal>`为常量时，不能对其修改<br/>报错行号为`<LVal>`所在行号 |
| `print`中格式字符与表达式个数不匹配     | l          | 报错行号为`printf`所在行号                                   |
| 在非循环块中使用`break`和`continue`语句 | m          | 报错行号为`break`和`continue`所在行号                        |

以上九类错误的特点是和语义相关，需要结合符号表分别进行处理。

#### 1.3 错误类别枚举类`ErrorType`

记录当前保存上述13种错误类型，作为`Error`对象的一个属性。

#### 1.4 错误类`Error`

为各类错误建立整体类`Error`，作为错误表类`ErrorTable`对象的组成元素，同时实现`Comparable`接口，便于排序。

#### 1.5 错误表类`ErrorType`

封装保存错误的容器，为后续输出做准备。对于这个容器的选择，有以下两点要求：

- 不重复。一行至多只有一个错误，同一个错误不应当多次输出。
- 有序。这一条要求较为宽松，事实上，结果输出时有序即可，在插入时可以乱序。
- 支持排序。尽管在建立过程中可以不有序，但是容器应当支持排序方法。

基于以上考虑，我们最终选用`TreeSet`作为容器，其满足有序和唯一，但代价是时间复杂度略有上升（从常数到`log`），但这可以接受。

由于错误表是共享的，在实现时可以考虑使用单例模式，避免到处传递其对象的繁琐。

### 2. 符号`Symbol`

在词法和语法分析中，我们更多地将输入文件的源代码作为割裂的字符串来处理。但在这个阶段，我们开始设计符号类，记录源程序中**各种名字（即标识符）**的特性信息，并在语法分析过程中生成符号对象并加入符号表进行统一管理。

具体地，每个符号（符号表表项）由名字和属性组成。属性较为琐碎复杂，以下分别介绍

#### 2.0 公共属性

对于每一个符号，其都具有以下属性：

- `lineNum:int`
- `name:String`
- `symbolType:SymbolType`
- `dimension:int`

#### 2.1 符号种类枚举类

根据符号种类的不同，可以分为变量VAR，一维变量数组VAR1，二维变量数组VAR2，常量CON，一维常量数组CON1，二维常量数组CON2，函数（名）FUNC

#### 2.2 维数

对于变量、常量和函数，其本身总有值，具体地，`VAR, CON`以及`int`的`FUNC`是0维的，`VAR1, CON1`是1维的，`VAR2, CON2`是2维的。特别地，`void`的`FUNC`是-1维的

#### 2.3 函数符号

对于函数符号而言，其还具有形式参数符号，因此，增加一个容器属性保存其形式参数





### 3. 符号表`SymbolTable`

符号表，也叫名字特性表，是符号的封装管理容器，使用`HashMap<String, Symbol>`具体保存。

SysY不支持在函数内嵌套函数，但支持`Block`之间的嵌套，因此，每当进入一个新的`Block`或函数，就建立新的子符号表，将当前的符号表指针指向这个新的表，并将该新表的父表指针指向上一个表。

- `a`类：对每一处`FormatString`做检查，检测非法符号

- `b`类和`c`类：建立符号表，对于`b`类重定义，每次调用标识符的时候检查当前作用域下是否已经定义了相同名字的变量或函数；对于`c`类未定义，检查表中所有数据，判断是否有未定义的情况
- `d`类和`e`类：都属于函数调用错误，分别具体处理如下：
  - 对于`d`类参数个数不匹配的情况，首先在函数声明时在符号表中记录该函数名对应的参数及其类型、个数等属性，在参数调用时对参数个数进行计数，如果不匹配则抛出异常
  - 对于`e`类参数类型不匹配的情况，对每一个传入参数检查其维度，仅有二维数组、一维数组、整数、void和数组的部分维度情况，因此仅需考参数的维度是否匹配
- `f`类和`g`类：都属于函数返回值错误问题，分别具体处理如下：
  - 对于`f`类无返回值函数存在不匹配的`return`语句，即`void`函数不能有返回值。值得注意的是，如果是`return ;`，也是合法的，因此不能在`void`函数中遇到`return`就判断该错误
  - 对于`g`类有返回值函数缺少`return`问题，需要检测函数`block`结构体最后一句是否为`return`类型语句，若不是`return`或`return ;`，则产生该错误。
- `h`类：每当对`Lval`进行赋值时，检查是否该标识符为`const`常量，若是，则产生该错误。
- `i,j,k`类：缺少分号`;`小括号`)`中括号`]`的情况，在语法分析过程中顺便处理即可。
- `l`类：`printf`中格式字符与表达式个数不匹配，类似`a`类，检查逻辑是在分别计数格式字符数量和表达式个数，当二者不相等时产生该错误。
- `m`类：在非循环块中使用`break`和`continue`语句，检查方案为在符号表中设置整数`cycleDepth`标记当前的循环体深度，每进入一个循环体该变量`+1`，退出则`-1`，不为`0`表示当前为函数循环函数体，否则不在，应当产生该错误。