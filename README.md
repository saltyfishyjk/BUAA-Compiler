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

### 编码前的设计

#### 设计路线的选择与分析

所谓代码生成，就是将抽象语法树（Abstract Syntax Tree，在语法阶段生成）转化为中间代码，再由中间代码转换为目标代码的过程。由于中间代码和代码优化关系密切（代码优化的INPUT是优化前的中间代码，OUTPUT是优化后的中间代码），因此中间代码十分必要且重要。至此，我们的思路已经逐渐清晰：首先生成中间代码，然后依据中间代码生成mips代码。

对于中间代码的选择，课程网站和手册推荐并“要求”使用四元式（虽然说是未来会考察，不过根据学长这玩意好像没法考察）。然而，四元式是一种抽象的，需要个人设计并实现的中间代码形式（PCODE也具有这样的特点），对于还未进行代码生成工作的我而言，很难在一开始就想清楚如何设计各类四元式；同时，由于每个人四元式的个性化，无法对其进行自动化评测，也就是说，如果选择四元式作为中间代码，只有在完成AST到四元式与四元式到mips后才可以使用课程网站进行评测，这带来了debug方面的麻烦：难以确定是生成中间代码的bug还是生成mips的bug还是二者兼有。而对于LLVM IR而言，其优点主要有三方面：一是具有良好的范式和定义，可以注意到对于选择LLVM IR赛道而言**不需要**自己写解释器，即类似mips代码一样可以被直接自动化评测；二是其有较为完整的材料（软件学院的GitBook）和自动化评测（LLVM IR赛道评测）；三是由于其完整的规范性，我们**不需要**在生成LLVM IR时考虑和mips相关的内容，降低了我们的思维复杂度。因此，在完成LLVM IR中间码后可以直接评测自己的正确性，在正确的LLVM IR代码的基础上进行mips代码的生成。

特别地，软件学院的指导书中提供了一个开源的LLVM IR编译器仓库。~~这十分友好~~

至此，我们的设计路线已经确定：**首先实现AST生成LLVM IR，然后实现LLVM IR生成mips**。

#### 从AST到LLVM IR

首先，我们了解并设计LLVM IR对应的类和数据结构，以快速获得对其的基本印象，主要参考资料是[软件学院编译实验指导书](https://buaa-se-compiling.github.io)。

##### LLVM IR 总体结构

- LLVM IR文件的基本单位称为`module`，由于本实验只涉及单文件编译，因此只有一个module
- 一个`module`中有多个顶层实体，如`function`和`global variable`，这可以分别对应SysY中编译单元`CompUnit`文法定义中的`{Decl}`和`{FuncDef}`
- 一个`function def`中至少有一个`basicblock`，这意味着函数体声明内至少有一个基本块（这里的基本块的含义更多是编译原理中的“基本块与流图”中的基本块）
- 一个`basicblock`中有若干`instruction`，并且都以`terminator instruction`结尾，这和我们对基本块的定义是一致的。

###### 基本块

在编译技术原理课中，我们已经详细地学习了基本块和流图的基本概念，这里结合实验再次阐述。

一个基本块是包含了若干个指令以及一个终结指令的代码序列。

基本块只会从终结指令退出，并且基本块的执行是原子性的，也就是说，如果基本块中的一条指令执行了，那么块内其他所有的指令也都会执行。这个约束**是通过代码的语义实现的**。基本块内部没有控制流，控制流是由多个基本块直接通过跳转指令实现的。

形象地讲，一个基本块中的代码是顺序执行的，且顺序执行的代码都属于一个基本块。

例如你有一份不含跳转（没有分支、循环）也没有函数调用的、只会顺序执行的代码，那么这份代码只有一个基本块。

然而，一旦在中间加入一个 `if-else` 语句，那么代码就会变成四个基本块：`if` 上面的代码仍然是顺序执行的，在一个基本块中；`then` 和 `else` 各自部分的代码也都是顺序执行的，因此各有一个基本块；`if` 之后的代码也是顺序执行的，也在一个基本块中。所以总共四个基本块。

###### 指令（Instruction）

指令指的是 LLVM IR 中的非分支指令（non-branching Instruction），通常用来进行某种计算或者是访存（比如上面例子中的 `add`、`load`），这些指令并不会改变程序的控制流。

值得一提的是，`call` 指令也是非分支指令，因为在使用 `call` 调用函数时，我们并不关系被调用函数内部的具体情况（即使被调用函数内部存在的控制流），而是只关心我们传入的参数以及被调用函数的返回值，因此这并不会影响我们当前程序的控制流。

###### 推荐指令

在上面的总体结构中引用的部分术语将在下面进行更详细的介绍，同时列出所有推荐指令以让读者有更清晰的认识。

**instruction**指令

| llvm ir       | usage                                                        | intro                                       |
| ------------- | ------------------------------------------------------------ | ------------------------------------------- |
| add           | `<result> = add <ty> <op1>, <op2>`                           | `+`                                         |
| sub           | `<result> = sub <ty> <op1>, <op2>`                           | `-`                                         |
| mul           | `<result> = mul <ty> <op1>, <op2>`                           | `*`                                         |
| sdiv          | `<result> = sdiv <ty> <op1>, <op2>`                          | `/`有符号除法                               |
| icmp          | `<result> = icmp <cond> <ty> <op1>, <op2>`                   | `<,<=,>,<=`比较指令                         |
| and           | `<result> = and <ty> <op1>, <op2>`                           | `&`与                                       |
| or            | `<result> = or <ty> <op1>, <op2>`                            | `|`或                                       |
| call          | `<result> = call [ret attrs] <ty> <fnptrval>(<function args>)` | 函数调用                                    |
| alloca        | `<result> = alloca <type>`                                   | 分配内存                                    |
| load          | `<result> = load <ty>, <ty>* <pointer>`                      | 读取内存                                    |
| store         | `store <ty> <value>, <ty>* <pointer>`                        | 写内存                                      |
| getelementptr | `<result> = getelementptr <ty>, * {, [inrange] <ty> <idx>}*` `<result> = getelementptr inbounds <ty>, <ty>* <ptrval>{, [inrange] <ty> <idx>}*` | 计算目标元素的位置（仅计算）                |
| phi           | `<result> = phi [fast-math-flags] <ty> [ <val0>, <label0>], ...` |                                             |
| zext..to      | `<result> = zext <ty> <value> to <ty2>`                      | 类型转换，将 `ty`的`value`的type转换为`ty2` |

**terminator instruction**终结指令

终结指令**一定**位于某个基本块的末尾（否则中间就改变了基本块内的控制流）；反过来，每个基本块的末尾也**一定**是一条终结指令（否则仍然是顺序执行的，基本块不应该结束）。终结指令决定了程序控制流的执行方向。例如，`ret` 指令会使程序的控制流返回到当前函数的调用者（可以理解为 `return`），`br` 指令表示根据标识符选择一个控制流的方向（可以理解为 `if`）。

| llvm ir | usage                                                        | intro                                                |
| ------- | ------------------------------------------------------------ | ---------------------------------------------------- |
| br      | `br i1 <cond>, label <iftrue>, label <iffalse>` `br label <dest>` | 改变控制流，具体行为是检测标志位`i1`，若为`1`，跳转` |
| ret     | `ret <type> <value>` ,`ret void`                             | 退出当前函数，并返回值（可选）                       |

###### 类型系统

**Void Type**

占位用，不代表值也不占据空间。

```
define void @foo() {
	ret void
}
```

**Integer Type**

`i1`表示`1bit`长的integer（在本实验中被当作`bool`使用）；`i32`表示`32bit`长的integer。这是本实验中唯二需要的类型。

```
ret i32 0
br i1 %2,label %3,label
```

**Label Type**

标签类型，用于代码标签。

**Array Type**

数组类型，用于保存

**Pointer Type**

变量类型

##### 处理流程

###### 设计模式

> 参考文献：[访问者模式一篇就够了-简书](https://www.jianshu.com/p/1f1049d0a0f4)

首先，归纳本部分的迭代开发特点：

- 需要遍历AST。具体地，在本项目中为从`CompUnit`开始，依次向下遍历语法树，返回*LLVM IR*（分析至此时还未完善定义返回的具体数据结构形式，先以抽象形式表达）。
- *LLVM IR*的*顺序列表*。需要得到得益于AST的树的特点，通过对其递归遍历可以得到*LLVM IR*的*顺序列表*。

基于此，我们发现以下特点：

- 不希望更改语法结点及其内部的实现。在前端部分，我们已经完善了语法分析的流程以及语法结点的保存。在这一阶段，我们不对语法分析阶段做任何变动，也不希望修改语法节点类：因为没有任何新的信息增加。
- 希望为语法结点增加新的方法。具体地，由于我们要根据AST生成*LLVM IR*的*顺序列表*，不可避免地需要类似在语法分析作业中的输出那样，在每个语法节点中实现新的方法，其可以返回一个*LLVM IR*的*顺序列表*，向上提供这一被调用方法，向下调用其子语法结点的对应方法。

因此，我们选用久经考验的Visitor访问者模式，其特点较符合我们本次迭代开发的需求，实现思路如下：

- 设计`Visitor`接口，存储上文中提供生成*LLVM IR*的方法，并声明访问各种语法结点类型的函数签名
- 设计`IrVisitor`类，实现`Visitor`接口，并具体实现其中的各个访问语法结点的函数签名
- 设计`AcceptVisit`接口，其中声明方法`void accept(Visitor visitor)`，并使`SyntaxNode`接口继承本接口，并在各个语法结点类中实现该方法，方法体为`visitor.visit(this)`

###### 接口交互

在上述设计过程中，我们还没有明确规定以什么形式的*LLVM IR*来进行方法与接口之间的交互。以下进行简要分析：

- 首先，参考经过验证的架构，我们对LLVM IR的类设计分为四个层次：`Module, Function, BasicBlock, Instruction`。各部分的主要属性如下：
  - `Module`：模块，是LLVM IR中的顶层单元
    - `List<Function>`：函数列表，包括四个IO用的函数签名`i32 @getint(), void @putint(i32), void putch(i32), void @putstr(i8*)`，分别实现读取一个整数，输出一个整数，输出一个字符，输出字符串
    - `List<GlobalVariable>`：全局变量列表
  - `Function`：函数，是`Module`的基本组成部分之一
    - `List<BasicBlock>`：基本块列表，将一个函数分为若干个代码顺序上相连的基本块
    - `Module parent`：指向该`Function`的父`Module`
  - `BasicBlock`：基本块，一个`BasicBlock`由若干个基本块组成
    - `List<Instruction>`：指令列表，将一个基本块划分为若干个顺序的指令
    - `Function parent`：指向该`BasicBlock`的父`Function`
  - `Instructon`：指令，是LLVM IR的最基本元素，若干种具体的指令类继承了该顶层类
    - `BasicBlock parent`：指向该`Instruction`的父`BasicBlock`，每个`Instruction`都属于一个`BasicBlock`
- 在上述架构的基础上，我们设计`IrNode`接口并在其中设计`irOutput`方法，并使这四个类都实现该接口，以便于我们递归下降调用顶层`Module`即可得到LLVM IR组成的字符串。在这里，我们这次声明其类型为`List<String>`，以方便在调试时观察以及避免再次使用一堆`StringBuilder`累加在一起。
- 另外，类似语法分析中的递归下降，我们不必强行统一`visit`方法的类型接口：因为每个具体的`visit`的重载都明确知道自己的传入类型以及需要的下层接口。



#### 从LLVM IR 到mips

完成LLVM IR后，接下来要做的就是将LLVM IR翻译为mips。

##### mips框架

对于一个基础例子：

```c
int a=1;

int foo(int b){

    return b;
}


int main(){
    int b=2;

    printf("hello world");
    b=foo(a);
    return 0;
}

```

LLVM IR如下：

```assembly
declare i32 @getint()
declare void @putint(i32)
declare void @putch(i32)

@_GlobalVariable0 = dso_local global i32 1

define dso_local i32 @foo(i32 %_LocalVariable0 ) #0 {
ret i32 %_LocalVariable0
}

define dso_local i32 @main() #0 {
%_LocalVariable0 = alloca i32
store i32 2, i32* %_LocalVariable0
call void @putch(i32 104)
call void @putch(i32 101)
call void @putch(i32 108)
call void @putch(i32 108)
call void @putch(i32 111)
call void @putch(i32 32)
call void @putch(i32 119)
call void @putch(i32 111)
call void @putch(i32 114)
call void @putch(i32 108)
call void @putch(i32 100)
%_LocalVariable1 = load i32, i32* @_GlobalVariable0
%_LocalVariable2 = call i32 @foo(i32 %_LocalVariable1)
store i32 %_LocalVariable2, i32* %_LocalVariable0
ret i32 0
}
```

一个mips的框架如下：

```assembly
.data
str_0: .asciiz "hello world"
str_2: .asciiz "hello mips"
/** .data段 formatString中被%d分隔的字符串 **/


.text
li $fp, 0x10040000
/** 把函数运行栈的基地址写入$fp **/
/** 对于 $gp,$fp,$sp 的论述见(四)存储结构 **/


li $8,1
sw $8,0($gp)
/** 在 jal main 前sw全局变量到$gp。由于全局变量是可以计算的常量，需要先li再sw **/



j main
nop
/** 进入main函数 **/



/******* 函数定义 开始 *******/
foo:
/** 关于加载参数，个人设计为前四个参数使用$a0-$a3，其余参数sw进当前运行栈，需要时再lw调取。写实参的工作由调用方执行，所以函数开始处，没有关于处理实参的mips代码 **/

/** 一些函数体mips语句，此处为空 **/

move $v0,$4  
/** 把存储返回值的寄存器的寄存器move到$v0，此处把$a0也就是$4作为返回值 **/
/** 对于int型返回值的函数，把返回值写进$v0 **/
jr $ra
/** 返回到调用处地址 **/

/******* 函数定义 结束 *******/



main:
li $8,2		/** 局部变量 int b = 2 **/

/******* printf 开始,具体见后续(二)部分 *******/
move $9, $4
li $v0, 4
la $a0,str_0
syscall
move $4,$9
/******* printf结束 *******/


/******* 函数调用开始，具体见后续(五) *******/
lw $9,0($gp)
move $4,$9
sw $9,0($gp)
add $sp,$sp,-12
sw $8,4($sp)
sw $ra,8($sp)

add $fp,$fp,4
jal foo
add $fp,$fp,-4
lw $8,4($sp)
lw $ra,8($sp)

add $sp,$sp,12
move $9,$2
/******* 函数调用结束。把函数调用foo(a)的结果$2也就是$v0 写进临时寄存器$10 *******/



move $8,$9
/** 把函数调用foo(a)的结果赋值给临时变量b **/



li $2,0
/** main 函数的返回值 **/

li $v0, 10
syscall
/** 结束mips程序的命令 **/

```

###### 寄存器约定

| Register  | Name      | Uasge                                 |
| --------- | --------- | ------------------------------------- |
| `$0`      | `$zero`   | 常量0                                 |
| `$1`      | `$at`     | 保留给汇编器                          |
| `$2-$3`   | `$v0-$v1` | 函数调用返回值                        |
| `$4-$7`   | `$a0-$a3` | 函数调用参数（参数数量多于4时会压栈） |
| `$8-$15`  | `$t0-$t7` | 临时变量寄存器                        |
| `$16-$23` | `$s0-$s7` | 子函数变量寄存器                      |
| `$24-$25` | `$t8-$t9` | 更多临时变量寄存器                    |
| `$26-$27` | `$k0-$k1` | 保留给中断或异常处理程序              |
| `$28`     | `$gp`     | 全局指针                              |
| `$29`     | `$sp`     | 栈指针                                |
| `$30`     | `$fp`     | 帧指针                                |
| `$31`     | `$ra`     | 函数调用返回地址                      |

##### 架构设计

整体上，按照`MipsModule -> MipsFunc -> MipsBasicBlock -> MipsInstruction`的结构来设计。

######  `MipsModule`

和`IrModule`相对应。具体地，包括`.data`段的字符串常量，`.text`段的`MipsFunc`函数，mips代码的主题结构就是在`.text`段进行执行和跳转。

###### `MipsFunc`

和`IrFunc`相对应。具体地，`MipsFunc`包括一组指令，对于非`main`函数而言，这组指令会以`jr`结束，代表跳回函数调用处，而`main`函数则以`li $v0, 10 <br/>syscall`结束表示终止程序。

######  `MipsBasicBlock`

和`IrBasicBlock`相对应。具体地，`MipBasicBlock`一般会以`label`开始，表示如何跳转入自己。

/* TODO : 这里没有想得很清楚，等实现了if和while再做修订 */

###### `MipsInstruction`

和`IrInstruction`相对应，具体地，一条`IrInstruction`可能会被翻译为一条或多条`MipsInstruction`；多条`IrInstruction`也可能被翻译为一条`MipsInstruction`（典型案例如连续的putch会被合并为字符串打印）

### 编码实现

#### 从AST到LLVM IR

LLVM IR的结构如下：

```
- Module 
	- Function
		- BasicBlock
			- Instruction
```

由于SysY的文法和LLVM IR并不完全对应，因此以下详细讨论生成器`IrBuilder`的设计。

其中对SysY的术语和LLVM IR的术语会混合使用，为了和代码保持一致，也为了区分彼此，对于SysY的文法名词，我们采用文法

##### `genIrModule`

对给定`CompUnit`解析并生成`IrModule`。

其中，`IrModule`包含`List<IrGlobalVariable>`和`List<IrFunction>`，分别调用`genIrInstruction`方法（`IrGlobalVariable`的来源本质上是SysY中的`Decl`，后续将简述重载在本方法中的使用）和`genIrFunction`方法。

##### `genIrFunction`

对给定的`Function`解析并生成`IrFunction`。值得注意的是，SysY并不支持嵌套`Function`，这给我们提供了便利：本方法返回的是一个`IrFunction`对象。

其中，`IrFunction`包含`List<IrParam>`和`List<IrBasicBlock>`。其中，`List<IrParam>`通过本质上是`IrFunctionType`的`IrValueType`中的`List<IrValueType>`来表达；`List<IrBasicBlock>`通过`genIrBasicBlock`方法来获取。

特别地，分析LLVM IR中`IrFunction`和SysY中`Function`部分的区别：

- 在LLVM IR中，`IrFunction`的属性是`List<IrBasicBlock>`，其中`IrBasicBlock`是**基本块**，其内部的属性为`List<IrInstruction>`，**不再嵌套`IrBasicBlcok`**
- 在SysY中，文法是递归下降的，包含了更丰富的语义。具体地，`Function`的属性`Block`中有若干个`BlockItem`，每个`BlockItem`可能是`ConstDecl, VarDecl, StmtAssign, Block, StmtCond, StmtWhile, StmtBreak, StmtContinue, StmtReturn, StmtGetint, StmtPrint`中的某一个。这里，我们从LLVM IR的角度考虑，将其分类如下：
  - 调用`genIrBasicBlock`，获得`List<IrBasicBlock>`返回`List`的主要原因是可能存在嵌套结构，但嵌套结构会被“展开铺平”，因此返回的是一个列表。
    - `Block`
    - `StmtCond` 
    - `StmtWhile`
  - 调用`genInstruction`，获得`List<IrBasicBlock>`中的一组指令。
    - `ConstDecl`
    - `VarDecl`
    - `StmtAssign`
    - `StmtBreak`
    - `StmtContinue`
    - `StmtReturn`
    - `StmtGetint`
    - `StmtPrint`
    - `StmtExp`
  - 特别地，`StmtNull`仅有分号，没有实际语义，不翻译为中间代码，直接忽略

##### `genIrBasicBlock`

对给定的部分`Stmt`返回`List<IrBasicBlock>`。

这里，我们突出利用了Java提供的重载功能，在`genIrFunction`中已经详细列举了所有可能的传入参数，即`Block, StmtCond, StmtWhile`，对三种情况分别重载传入参数。

##### `genIrInstruction`

对给定的`Decl`和部分`Stmt`返回`List<IrInstruction>`。

类似地，我们也着重采用了Java的重载功能，对于所有的可能情况分别重载传入参数。

##### 条件语句`if`







#### 从LLVM IR到MIPS

到这里，我们已经（部分正确）实现了LLVM IR的翻译，接下来，我们的目标工作是推进最后一公里：将LLVM IR翻译为MIPS。类似地，我们继续采用自顶向下的设计思路。

##### 输出接口

设计`MipsNode`接口并声明`mipsOutput`方法（该方法返回一个字符串数组），使`MipsModule`、`MipsFunc`、`MipsBasicBlock`和`MipsInstruction`实现接口。具体地，每个模块的该输出方法将依次调用自己子模块的方法，整合后合并输出。

##### 字符串常量声明

在mips中，打印字符串和打印数字是两种不同的系统调用，同时不支持单个字符的打印（如果你想要把单个字符转化为长度为1的字符串当然可以，但是性能代价过高）；而在LLVM IR中，我们的实现采取了一个一个字符打印的方式（原因是LLVM的字符串输出过于麻烦）。对此，我们的处理方法是遍历每个`IrBasicBlock`（基本块内语句的执行是连续的，选取此为单位避免了两个基本块的相邻putch被误判为一条字符串）时将连续的`putch`语句的输出内容连缀为一个字符串并加入`MipsModule`中作为`.data`段输出（这需要我们设计一个`MipsData`类来保存这些字符串和他们的名字，并设计一个相应的计数器以保证在遍历`IrModule`的时候可以正确地为字符串命名）。

##### `$gp`28

对于全局变量，其需要对任何时刻的任何函数都可以正确访问。因此，我们需要`$gp`来保存全局变量指针，并记录每个全局变量具体的偏移。

##### `$fp` 30

函数栈指针，从`$fp`向上依次是参数区（为了规范，对于`$a0-$a4`，我们会在进入函数后立刻将其保存到内存中）、局部变量区和临时变量区。其中局部变量区和临时变量区目前不做区分，一致处理。

操作时，我们需要遵守如下范式约束：

- 本函数调用结束时，`$fp`的值仍应当等于进入本函数时的值。
- `$fp`的增长方向是`+`，恢复方向是`-`。具体地，当我们在父函数内部执行调用某一个子函数的时候，编译器内部应当保存当前父函数`$fp`栈顶事实上的位置，然后将`$fp`寄存器的值自增至此，然后以`+`的方向压入函数实参；调用`jal`进入子函数；在`jal`语句后，由于我们遵守了第一条的`$fp`范式，因此按照编译器内部保存的事实偏移即可通过`-`恢复`$fp`到父函数的正确位置。事实上，对于实参压栈还有更复杂的细节问题（比如参数个数），这将在**函数调用**部分进一步介绍。

##### `$sp`29

函数帧指针，从`$sp`向下依次是`$ra`和其他需要保存的寄存器。

类似地，我们在操作时遵守如下范式：

- 本函数调用结束时，`$sp`的值应当等于进入本函数时的值。
- `$sp`的增长方向是`-`，恢复方向是`+`。具体地，函数调用时，将`$ra`和其他需要保存的寄存器以`-`的方向压入栈；调用`jal`进入子函数；在`jal`语句后，按照编译器保存的数据恢复`$ra`和其他寄存器的值。

##### 函数调用

###### `main`函数

对于`main`函数，我们会在将全局变量压入`$gp`后即调用`j main`进入`main`函数（使用`j`是因为这是程序入口，无条件转入）；在函数结束时，我们调用`li $v0, 10 <br/>syscall`来结束整个程序。

###### 其他函数

**调用函数**

在调用函数时，我们依次进行如下操作：

- 保存`$sp`现场。我们首先应当将`$ra`和其他需要保存的临时寄存器的值压入`$sp`栈，这一方面保证了安全性，一方面解开我们的束缚，可以在后续操作中方便地使用临时寄存器而不必担心错误地修改了不该修改的值。

- 保存`$fp`现场。根据编译器内保存信息，将`$fp`自增至栈顶，然后依次将实参压入被调用函数的`$fp`内。值得注意的是，当执行将实参压栈的操作时候，本质细节是使用`sw`指令将某一个寄存器的值存入指定内存，也就是说，这时需要我们将实参提前放入寄存器；当面临参数过多的情况，我们依然需要父函数的`$fp`来结合索引找到正确的内存块以实现读取并存入子函数的`$fp`。因此，我们采用的办法是首先指定一个特定寄存器作为子函数的`$fp`，比如`$t9`；接着，将实参逐个存入`$a`寄存器或存入`$t9`栈；最后，将`$fp`的值更改为`$t9`的值。
- `jal`
- 恢复`$fp`现场，本质上是通过编译器内部保存的数据将`$fp`自减至父函数原值
- 恢复`$sp`现场，本质上是将`$sp`自增至父函数原值，并将`$ra`和其他被保存的临时寄存器的值恢复。

**声明函数**

得益于我们在上述调用函数的约束，在声明函数时我们进行如下操作：

- 将`$a`寄存器保存的参数（如果有）压入`$fp`并记录
- 进行计算
- 将返回值保存入`$v0`
- `jr`

##### 符号表与符号

在翻译为MIPS时，需要将LLVM IR中的符号映射到一个`MipsSymbol`，其中记录包括是否在寄存器中，是否`dirty`，是否已经分配对应的内存空间，内存空间相对于`$fp`的偏移，是否被改写等。

##### 寄存器表

存储管理是十分重要，在代码生成一中，我们采用如下设计：

- 使用的寄存器范围为`$t0-$t9, $s0-$s7`
- 对于`alloca`的变量，将其存入`$s0-$s5`寄存器
- 对于临时变量，将其存入`$t0-t7`寄存器
- 对于全局变量，将其存入内存，每次使用`$t8-t9`寄存器临时取用，每次写入后必写回并释放寄存器，以避免子函数访问到错误的值
- 对于函数调用时被调用函数的新`$fp`，使用`$s6`保存
- 在寄存器有空余时，直接分配寄存器
- 在`$t`寄存器没有空余时，寻找一个已经`use`的变量所在的`$t`寄存器并将其与`MipsSymbol`映射关系清除，将寄存器分配给新符号
- 在`$s`寄存器没有空余时，取一个最旧的`$s`寄存器，将其`MipsSymbol`写回内存
  - 如果`dirty`为`false`，说明该值不需要特别写回内存
  - 如果`dirty`为`true`，检查该符号是否已经分配内存，如果没有分配内存，则首先分配内存并记录在`MipsSymbol`中，然后生成一条`sw`指令，并将该符号的值写回。

## Part 7 代码优化设计

