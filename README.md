## BUAA-Compiler 编译器设计文档



## Part 0 前言



## Part 1 参考编译器介绍



## Part 2 编译器总体设计



## Part 3 词法分析设计

> 会随着项目的部分或全部重构而更新

### 编码前的设计

在项目结构方面，词法分析属于前端内容，将其目录`lexer`置于`src/frontend`下，为后续架构预留空间；一共设计了五个类`SourceFileLexer`、`Token`、`TokenType`、`TokenList`和`TokenType`，其中，`SourceFileLexer`置于`src/frontend`目录下，其余四个类置于`src/frontend/lexer`下。

整体上，`SourceFileLexer`提供对源文件的文法分析工具；`lexer`目录提供对输入文件的解析，最终生成一个`TokenList`对象并包含必要信息，为后续语法分析等工作做好准备，具体地，`TokenType`是一个枚举类，包括了所有的单词类别及对应正则表达式，`Token`设计了单词类，包括单词类别`TokenType`属性，`conotent`单词内容属性和所在行属性，`TokenList`事实上封装了一个`ArrayList<Token>`容器对象，`TokenLexer`类接收`SourceFileLexer`并解析出一系列`Token`，最终生成`TokenList`对象提供给外界调用。以下按照目录级别从浅到深依次具体介绍。

### 编码实现

#### `SourceFileLexer`源文件词法分析器

##### 设计思路

整体而言，`SourceFileLexer`接收一个`InputStream`对象作为输入流，提供各种精细的特权级指令（包括光标移动指定位、跳行等），主要为`lexer/TokenLexer`提供服务，实现了输入与源文件词法解析、源文件词法解析与词法解析的解耦。

##### 属性

- `inputStream`：输入流对象，需要构造器传入
- `ArrayList<String> lines`：行容器，在`readLines`中初始化
- `lineNum`：当前光标所在行号，从0开始
- `columnNum`：当前光标所在列号，从0开始

##### 实现功能

- `readLines`：读入`InputStream`输入流对象的所有行并存储
- `endOfLine`：判断是否到达行末尾
- `endOfFile`：判断是否到达文件末尾，保证读入可结束
- `peekLine`：当前光标所在行
- `peekChar`：当前光标指向的字符
- `peekSubStr`：当前光标后指定长度的子字符串
- `isWhiteSpace`：判断字符是否为空白字符
- `skipWhiteSpace`：跳过一组连续的空白字符（如果有）
- `moveForward`：将光标前移指定长度
- `nextLine`：跳过当前光标所在行
- `getLeftLine`：获取当前光标所在行剩余字符串
- `hitSubStr`：尝试从光标处开始匹配给定正则表达式
- `getLineNum`：获取当前光标所在行号，**从1开始**

#### `lexer`词法分析器package

`lexer`是词法分析器的核心所在目录，其内部四个类相互配合，完成了对源文件的词法解析。以下介绍以低依赖向高依赖的顺序。

##### `TokenType`单词类别枚举类

###### 设计思路

在SysY语言中有有限种类的单词类别，他们是常量但应用广泛，Java为此提供了绝佳的实现方案：枚举类，这是一个完备的类，有诸如构造器、属性、方法等类的要素。本编译器充分结合了枚举类的特性与SysY语言的特性。具体而言，为不同单词种类设计了不同的枚举对象，并区分了是否需要贪婪匹配，对外提供查询方法和打印方法。

对于保留字的尝试匹配应该在第一优先级，如`main`等；对于双字符运算符的匹配应当优先于单字符运算符，特别是具有前缀依赖关系的（如`==`和`=`）

###### 属性

- `isGreed`：是否贪婪匹配，对于保留关键字如`main`等，此值为真，其余为假
- `patternString`：正则表达式字符串
- `pattern`：`Pattern`对象，为按照上述两个属性编译后得到的对象

###### 实现功能

- `getPattern`：获取`Pattern`对象
- `toString`：打印对象名（如`MAINTK`），为词法分析作业服务

##### `Token`单词对象

###### 设计思路

为了将单词这一重要基本元素恰当地表达，本编译器将其封装为一个类，包含单词类别、所在行、内容等属性，并对外提供`get`方法。

###### 属性

- `type`：`TokenType`对象，记录本单词属于的单词类别
- `lineNum`：记录本单词所在行号，从1开始
- `content`：单词内容

###### 方法

主要为`get`和`set`。

##### `TokenList`单词表

事实上该类属性只有`ArrayList<Token>`容器对象，但是选择将其封装为一个整体，一方面在含义上正确且低耦合，另一方面拜托了对Java容器类别的依赖，如果后续更改真实的容器也不需要更改外部代码，只需要更改内部代码，符合SOLID原则。

###### 属性

- `tokens`：单词表容器对象

###### 方法

主要为`get`，`set`和`toString`

##### `TokenLexer`词法分析器

###### 设计思路

本类是词法分析器的核心逻辑。具体地，传入`SourceFileLexer`对象，通过调用其提供的各种特权指令，解析出每个有效单词并最终生成`TokenList`对象并对外提供，整体接口较为简洁。

实现上有几个需要注意的点：

- 循环节开始，应当首先跳过空白字符和注释，跳过一次注释后应当`continue`，因为存在连续注释的情况。
- 需要正确计算调用特权指令时传入的参数，具体地，对于遇到注释时调用`moveForward`，需要仔细计算应当跳过几个字符。

###### 属性

- `sourceFileLexer`：`SourceFileLexer`对象，提供对抽象的源文件的特权解析指令
- `tokenList`：单词表对象

###### 方法

主要为内部使用方法，对外提供方法主要为`getTokenList`

- `tokenize`：解析函数，核心逻辑
- `skipWhhiteSpace`：跳过空白字符
- `skipComment`：如果下一部分为注释则跳过并返回真，否则返回假
- `addToken`：遍历`TokenType`枚举类的对象，尝试匹配下一个有效单词（这里需要注意枚举类对象的先后顺序，如`==`应当出现在`=`前）

## Part 4 语法分析设计

### 编码前的设计

在项目结构方面，语法分析属于前端内容，将其目录`parser`置于`src/frontend`下，为后续架构预留空间。在其下设计了四个package：`function`、`declaration`、`statement`和`expression`，分别对应文法定义说明文档中的函数、变量/常量声明、语句和表达式四个类别，完成了文法的初步解耦。

#### 语法树结点类

本部分的一个核心设计思想是：每一个非终结符都有其对应的类/接口并对外提供方法，自己实现的优化/解耦不能取代这些类/接口。

##### 实现顺序

按照类之间的依赖关系，首先实现`expression`中的类，其中的类之存在相互依赖关系而不对其他三个包的类存在依赖关系；接着实现`declration`中的类，其中对外部类的依赖都在`expression`中；然后实现`statement`类，其中对外部类的依赖都在`expression`和`statement`中；最后实现`function`中的类。

##### 实现技巧

- 对于`Expression`中的`AddExp`等结构高度相似且包含左递归的非终结符，通过改写其文法去除左递归使得可以使用递归下降法进行解析；同时，利用其结构上的高度相似，为其设计统一超类`MultiExp`，提高代码复用率。
- 对于`Stmt`、`UnaryExp`等文法右侧声明种类较多的非终结符，为其设计统一的`<Class>Ele`接口，并为每一种声明设计单独的非终结符类，统一实现该接口，并在原非终结符中通过保存其`Ele`对象来实现架构的完整性。

#### 语法树结点解析器类

本部分的一个核心设计思想是：为每一个非终结符设计对应的解析器类，也对部分自行设计的“非终结符”（主要是文法声明右侧种类较多时）设计对应的解析器类，主要包含`Token`迭代器`TokenListIterator`，非终结符的各种组成对象。

`<Class>Parser`类对外提供`Parse<Class>`方法来解析当前内容，同时提供`syntaxOutput`方法来实现满足语法要求的解析。

对于每个`Parser`类的`parse<Class>`方法，统一规定其进入时通过`TokenListIterator`获得的下一个`Token`为其组成元素的第一个。一方面，这简化了我们进入`parse<Class>`方法时的思维复杂度，但另一方面，将该部分代价转移到每个`parse<Class>`方法的实现中，有舍有得。

### 编码实现——`parser`语法分析器package

#### `expression`表达式声明类别package

##### `multiexp`

包含`AddExp,EqExp,LAndExp,LOrExp,MulExp,MultiExp,RelExp`类及其对应的`Parser`类，其中除`MultiExp`外的`_Exp`类均为继承`MultiExp`。

在`MultiExp`中实现了消除左递归文法后的语法树结构，具体地，通过`first`对象和`operators`与`operands`列表来表达一个语法树节点。在输出上，由于输出的特点，可以通过依次打印`first`与后续`operator`和`operand`来实现满足文法要求的输出。

##### `pimaryexp`

包含`LVal, Number, PrimaryExp`这三个文法中的非终结符以及为了架构所人为构建的语法节点类`PrimaryExpExp`和他们对应的`Parser`类，其实现了`PrimaryExp`文法声明右侧`'('<Exp>')'`的部分，并且其中`Number, LVal, PrimaryExpExp`三个类都继承了`PrimaryExpEle`接口，表明其分别可以独立地成为`PrimaryExp`的一个声明。

##### `unaryexp`

包含`UnaryExp, UnaryOp`这两个文法中的非终结符以及为了架构所人为构建`UnaryExpFunc, UnaryExpOp`类和他们对应的`Parser`类。两个人为构造的类实现了`UnaryExpEle`接口，表明其分别可以独立地成为`UnaryExp`的一个声明。特别地，在上面`primaryexp`中的`PrimaryExp`也实现了这一接口，表明其也是`UnaryExp`的一个声明。`UnaryOp`十分简单，但为了保证架构的完整性和鲁棒性，我们依旧将其设计为了单独的类。

##### `Cond, ConstExp, Exp, FuncRParams`

这些非终结符结构相对单一（文法声明右侧只有一种可能性），因此没有将其单独置入package。

#### `declaration`变量/常量声明类别package

##### `constant`

包含`constinitval`package和`ConstDecl, ConstDef`类。

在`constinitval`package中，人为构造了`ConstInitValMulti`类，并使其实现`ConstInitValEle`接口。同时，由于文法表达，上面实现过的`ConseExp`也实现这一接口。从这里也可以一窥我们实现顺序的优越之处。

在`ConstDef, ConstDecl`类的文法声明结构较为单一，因此没有为其设置单独的package。

##### `variable`

包含`initval, vardef`两个package和`VarDecl`及其对应的`Parser`类。

在`initval`package中，人为设计了`InitVals`类并实现`InitValEle`接口，对应`InitVal`文法中多初值的情况。其中，由`Exp`实现了`InitValEle`接口，因为其也是`InitVal`文法声明右侧的元素之一。

在`vardef`中，人为设计了`VarDefInit, VarDefNull`两个类并实现`VarDefEle`接口，分别对应有初值声明和无初值声明两种情况。

`VarDecl`类结构较为简单，并未对其单独设置`package`

##### `BType, Decl`

这两个非终结符同样文法声明较为简单，未为其单独设置类。

#### `statement`语句类别package

##### `blockitem`

设置接口`BlockItemEle`，并使`Decl`和`Stmt`实现这个接口。

##### `stmt`

这部分代码量很大，但是逻辑却并不复杂。具体地，人为实现了`StmtAssign, StmtBlock, StmtBreak, StmtCond, StmtContinue, StmtExp, StmtGetint, StmtNull, StmtPrint, StmtReturn, StmtWhile`这几种类，分别对应`Stmt`非终结符的一种文法声明，并使其实现`StmtEle`接口来将其作为`Stmt`的元素统一管理。特别地，`Block`也因其是`Stmt`文法声明的一种可能而实现该接口。

##### `Block`

实现了`Block`类和其对应的`Parser`

#### `function`函数类别package

##### `functype`

人为实现了`FuncTypeInt, FuncTypeVoid`类并实现`FuncTypeEle`接口，为两种函数类型。

##### `FuncDef, FuncFParam, FuncFParams, MainFuncDef`

其文法声明结构较为单一，未为其单独设置package。

#### `terminal`终结符号类别

在官方文法说明手册中，`FormatString, Ident, IntConst`并非作为非终结符进行解释，而是通过自然语言进行描述。然而，其在文法中的地位不可或缺，因此为其单独设置package，以保证架构的完整性和鲁棒性。

#### `CompUnit`类

作为整体编译单元，该类是一个外部包裹类。

#### `SyntaxNode`接口

由于语法输出的要求，为每一个非终结符都实现了该接口。具体地，该接口中包含一个抽象方法，即`syntaxOutput`方法，每个直接或简介实现该接口的类都需要实现该方法。这一方法返回一个`String`对象，这使得我们将语法解析与语法作业输出相解耦。具体地，流程是首先进行语法解析，在这过程中同时建立语法树，最后可以通过调用`CompUnit`的`syntaxOutput`方法实现递归获得语法输出。虽然这使得我们的代码量上升了一个数量级，但是考虑到解耦带来的结构的架构的清晰性，我们认为这是值得的。

## Part 5 错误处理设计

### 编码前的设计

首先，考虑到错误处理的两大类型：语义不相关和语义相关，我们采用将其耦合入递归下降语法分析的过程中的解决方案。同时，由于其中语义相关的错误需要符号表的支持（如名字重定义等错误），我们在这一阶段部分地实现项目中端，即符号和符号表。

#### 1. 错误处理

##### 1.1 语义不相关错误

| 错误类型     | 错误类别码 | 解释                                                         |
| ------------ | ---------- | ------------------------------------------------------------ |
| 非法符号     | a          | 格式字符串中出现非法字符，报错行号为**<FormatString>**所在行数 |
| 缺少分号     | i          | 报错行号为分号**前一个非终结符**所在行号                     |
| 缺少右小括号 | j          | 报错行号为右小括号**前一个非终结符**所在行号                 |
| 缺少右中括号 | k          | 报错行号为右中括号**前一个非终结符**所在行号                 |

以上四类报错的特点是他们不和语义相关，只是违背了文法或语义不相关的约束。

##### 1.2 语义相关错误

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

#### 2. 符号

在词法和语法分析中，我们更多地将输入文件的源代码作为割裂的字符串来处理，但在这个阶段，我们开始设计符号类，并在非终结符语法分析过程中生成符号对象并加入符号表进行统一管理。

具体地，每个符号（符号表表项）由名字和属性组成。

#### 3. 符号表

是符号的封装管理容器



## Part 6 代码生成设计



## Part 7 代码优化设计

