# Parser-Expression

该package主要实现对表达式的语法结构分析。

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
| `<UnaryExp>`    | `<PrimaryExp> | <Ident> '(' [<FuncParams>] ')' | <UnaryOp> <UnaryExp>` | 一元表达式   | 存在即可；                             |
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
| `<UnaryExp>`    | `<PrimaryExp> | <Ident> '(' [<FuncParams>] ')' | <UnaryOp> <UnaryExp>` | 一元表达式   | 存在即可；                             |
| `<UnaryOp>`     | `'+' | '-' | '!'`                                            | 单目运算符   | `'!'`仅出现在条件表达式中              |
| `<FuncRParams>` | `<Exp> {',' <Exp>}`                                          | 函数实参表   |                                        |
| `<MulExp>`      | `<UnaryExp> | { ('*' | '/' | '%') <UnaryExp> }`              | 乘除模表达式 | <font color=red>消除左递归</font>      |
| `<AddExp>`      | `<MulExp> { ('+' | '-') <MulExp> }`                          | 加减表达式   | <font color=red>消除左递归</font>      |
| `<RelExp>`      | `<AddExp> { ('<' | '>' | '<=' | '>=') <AddExp> }`            | 关系表达式   | <font color=red>消除左递归</font>      |
| `<EqExp>`       | `<RelExp> | { ('==' | '!=') <RelExp> }`                      | 相等性表达式 | <font color=red>消除左递归</font>      |
| `<LAndExp>`     | `<EqExp> { '&&' <EqExp> }`                                   | 逻辑与表达式 | <font color=red>消除左递归</font>      |
| `<LOrExp>`      | `<LAdnExp> { '||' <LAndExp> }`                               | 逻辑或表达式 | <font color=red>消除左递归</font>      |
| `<ConstExp>`    | `<AddExp>`                                                   | 常量表达式   | 使用的`Ident`必须是常量                |
