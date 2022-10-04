# Parser-Expression

该package主要实现对**表达式**的语法结构分析。

## 表达式类的设计

- 由于语法作业的要求，设计顶层接口`SyntaxNode`，包含一个语法输出函数`syntaxOutput`，每个语法结构中的非终结符都实现该接口，这样可以方便地实现满足文法要求的输出
- 由于语法作业的要求，每个类需要一个`name`属性，用来支持完成`syntaxOutput`
- 对于具有左递归文法的非终结符，需要将其文法改写为非左递归文法，并在条件允许的情况下尽可能使用范式、抽象和接口
- 对于具有多种文法定义的非终结符，如果其结构简单清晰，则应当为其设计公共抽象类或接口，并由具体的非终结符实现或继承
- 对于仅为一层封装的非终结符（如`Exp->AddExp`），直接将其声明作为类的属性保存

## 表达式相关文法

### 原始表达式相关文法（未消去左递归）

表达式的解析和OO2022Unit1比较相似，以下列举表达式相关文法：

| 非终结符        | 文法                                                         | 含义         | 备注                                   |
| --------------- | ------------------------------------------------------------ | ------------ | -------------------------------------- |
| `<Exp>`         | `<AddExp>`                                                   | 表达式       | SysY表达式是int型表达式                |
| `<Cond>`        | `<LOrExp>`                                                   | 条件表达式   | 存在即可                               |
| `<LVal>`        | `<Ident>{ '[' <Exp> ']' }`                                   | 左值表达式   | 1.普通变量2.一维数组3.二维数组         |
| `<PrimaryExp>`  | `'(' <Exp> ')' | <LVal> | <Number>`                          | 基本表达式   | 覆盖三种情况：表达式、左值表达式、数值 |
| `<Number>`      | `<IntConst>`                                                 | 数值         | 存在即可                               |
| `<UnaryExp>`    | `<PrimaryExp> | <Ident> '(' [<FuncRParams>] ')' | <UnaryOp> <UnaryExp>` | 一元表达式   | 存在即可；                             |
| `<UnaryOp>`     | `'+' | '-' | '!'`                                            | 单目运算符   | `'!'`仅出现在条件表达式中              |
| `<FuncRParams>` | `<Exp> {',' <Exp>}`                                          | 函数实参表   |                                        |
| `<MulExp>`      | `<UnaryExp> | <MulExp> ('*' | '/' | '%') <UnaryExp>`         | 乘除模表达式 | <font color=red>须消除左递归</font>    |
| `<AddExp>`      | `<MulExp> | <AddExp> ('+' | '-') <MulExp>`                   | 加减表达式   | <font color=red>须消除左递归</font>    |
| `<RelExp>`      | `<AddExp> | <RelExp> ('<' | '>' | '<=' | '>=') <AddExp>`     | 关系表达式   | <font color=red>须消除左递归</font>    |
| `<EqExp>`       | `<RelExp> | <EqExp> ('==' | '!=') <RelExp>`                  | 相等性表达式 | <font color=red>须消除左递归</font>    |
| `<LAndExp>`     | `<EqExp> | <LAndExp> '&&' <EqExp>`                           | 逻辑与表达式 | <font color=red>须消除左递归</font>    |
| `<LOrExp>`      | `<LAdnExp> | <LOrExp> '||' <LAndExp>`                        | 逻辑或表达式 | <font color=red>须消除左递归</font>    |
| `<ConstExp>`    | `<AddExp>`                                                   | 常量表达式   | 使用的`Ident`必须是常量                |

### 修改后表达式相关文法（消去左递归）

| 非终结符        | 文法                                                         | 含义         | 备注                                   |
| --------------- | ------------------------------------------------------------ | ------------ | -------------------------------------- |
| `<Exp>`         | `<AddExp>`                                                   | 表达式       | SysY表达式是int型表达式                |
| `<Cond>`        | `<LOrExp>`                                                   | 条件表达式   | 存在即可                               |
| `<LVal>`        | `<Ident> { '[' <Exp> ']' }`                                  | 左值表达式   | 1.普通变量2.一维数组3.二维数组         |
| `<PrimaryExp>`  | `'(' <Exp> ')' | <LVal> | <Number>`                          | 基本表达式   | 覆盖三种情况：表达式、左值表达式、数值 |
| `<Number>`      | `<IntConst>`                                                 | 数值         | 存在即可                               |
| `<UnaryExp>`    | `<PrimaryExp> | <Ident> '(' [<FuncRParams>] ')' | <UnaryOp> <UnaryExp>` | 一元表达式   | 存在即可；                             |
| `<UnaryOp>`     | `'+' | '-' | '!'`                                            | 单目运算符   | `'!'`仅出现在条件表达式中              |
| `<FuncRParams>` | `<Exp> {',' <Exp>}`                                          | 函数实参表   |                                        |
| `<MulExp>`      | `<UnaryExp> { ('*' | '/' | '%') <UnaryExp> }`                | 乘除模表达式 | <font color=red>消除左递归</font>      |
| `<AddExp>`      | `<MulExp> { ('+' | '-') <MulExp> }`                          | 加减表达式   | <font color=red>消除左递归</font>      |
| `<RelExp>`      | `<AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }`            | 关系表达式   | <font color=red>消除左递归</font>      |
| `<EqExp>`       | `<RelExp> { ('==' | '!=') <RelExp> }`                        | 相等性表达式 | <font color=red>消除左递归</font>      |
| `<LAndExp>`     | `<EqExp> { '&&' <EqExp> }`                                   | 逻辑与表达式 | <font color=red>消除左递归</font>      |
| `<LOrExp>`      | `<LAndExp> { '||' <LAndExp> }`                               | 逻辑或表达式 | <font color=red>消除左递归</font>      |
| `<ConstExp>`    | `<AddExp>`                                                   | 常量表达式   | 使用的`Ident`必须是常量                |

