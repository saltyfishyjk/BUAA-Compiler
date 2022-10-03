# Parser-Function

该package主要实现对**函数**的语法结构分析。

## 函数类的设计

### 函数类相关文法

| 非终结符        | 文法                                                 | 含义       | 备注           |
| --------------- | ---------------------------------------------------- | ---------- | -------------- |
| `<FuncDef>`     | `<FuncType> <Ident> '(' [<FuncFParams>] ')' <Block>` | 函数定义   | 无形参/有形参  |
| `<MainFuncDef>` | `'int' 'main' '(' ')' <Block>`                       | 主函数定义 | 存在`main`函数 |
| `<FuncType>`    | `'void' | 'int'`                                     | 函数类型   |                |
| `<FuncFParams>` | `<FuncFParam> { ',' <FuncFParam> }`                  | 函数形参表 |                |
| `<FuncFParam>`  | `<BType> <Ident> [ '[' ']' { '[' <ConstExp> ']' } ]` | 函数形参   |                |

