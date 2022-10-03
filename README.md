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

按照类之间的依赖关系，首先实现`expression`中的类，其中的类之存在相互依赖关系而不对其他三个包的类存在依赖关系；接着实现`declration`中的类，其中对外部类的依赖都在`expression`中；然后实现`statement`类，其中对外部类的依赖都在`expression`和`statement`中；最后实现`function`中的类。

#### 语法树结点解析器类

本部分的一个核心设计思想是：为每一个非终结符设计对应的解析器类，主要包含`Token`迭代器，对外提供`Parse<Class>`方法来解析当前内容。

### 编码实现——`parser`语法分析器package



#### `function`函数类别package



#### `declaration`变量/常量声明类别package



#### `statement`语句类别package

#### `expression`表达式声明类别package

## Part 5 错误处理设计



## Part 6 代码生成设计



## Part 7 代码优化设计

