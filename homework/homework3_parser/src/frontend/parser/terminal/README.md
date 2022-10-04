# Terminal

终结符package，包括`Ident`、`IntConst`类。

## `Ident`

标识符Identifier，实际上是`Token`的一种，其中包含一个`Token`对象，类别固定为`TokenType.Ident`

## `IntConst`

数值常量Integer-Const，实际上是`Token`的一种，其中包含一个`Token`对象，类别固定为`TokenType.IntConst`