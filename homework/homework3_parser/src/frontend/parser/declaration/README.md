# Parser-Declaration

该package主要实现对变量/常量声明的语法结构分析。

## 声明类的设计

## 声明类相关文法

| 非终结符         | 文法                                                         | 含义     | 备注                         |
| ---------------- | ------------------------------------------------------------ | -------- | ---------------------------- |
| `<Decl>`         | `<ConstDecl> | <VarDecl>`                                    | 声明     | 变量声明、常量声明两种       |
| `<ConstDecl>`    | `'Const' <BType> <ConstDef> { ',' <ConstDef> } ';'`          | 常量声明 |                              |
| `<BType>`        | `'int'`                                                      | 基本类型 |                              |
| `<ConstDef>`     | `<Ident> { '[' <ConstExp> ']' } '=' <ConstInitVal>`          | 常数定义 | 普通变量、一维数组、二维数组 |
| `<ConstInitVal>` | `<ConstExp> | '{' [ <ConstInitVal> { ',' <ConstInitVal> } ] '}'` | 常量初值 |                              |
| `<VarDecl>`      | `<BType> <VarDef> { ',' <VarDef> } ';'`                      | 变量声明 |                              |
| `<VarDef>`       | `<Ident> { '[' <ConstExp> ']'} | <Ident> { '[' <ConstExp> ']' } '=' <InitVal>` | 变量定义 |                              |
| `<InitVal>`      | `<Exp> | '{' [ <InitVal> { ',' <InitVal> } ] '}'`            | 变量初值 |                              |

