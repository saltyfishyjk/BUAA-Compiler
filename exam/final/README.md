# BUAA-Compiler-final-exam-2022

本次考试一共8道题目，但只需根据自己所选的目标代码赛道完成指定题目即可，每道题目的详细题面已经在本目录下的`T1-T8`中介绍，以下主要介绍笔者的考试体验和经验。`code`文件夹下为代码生成题目的AC代码。

## Part 0 考试要求

本次考试共八道题目，请按说明完成并提交部分题目。
（1）通用评测题：所有同学均需完成，请根据自己编译器的目标代码提交到相应题目中；其中代码生成题目需修改自己的编译器代码再提交，代码生成及代码优化题目可以直接提交之前的编译器代码，目标代码为MIPS的同学需提交带优化的版本，会进行竞速排序（现场不公布排名）。
（2）文件上传题：仅目标代码为MIPS的同学需提交，如果没有完成优化，只提交优化前的中间代码和目标代码。提交的代码用于代码生成和优化的验证，请务必确保是编译器直接产生的版本。
（3）解答题：所有同学均需完成，请结合自己的编译器源代码详细说明题目的解决方案。
（4）简答题：根据目标代码选择一道完成，请结合测试程序和自己的编译器源代码详细说明实现方案。

## Part 1 代码生成题面

### 【问题描述】

（1）小译同学在编写testfile时希望能写出 int i = getint(); 这类输入语句。

（2）课程组提供的 SysY 文法中没有位运算操作，现在需实现一个“按位与”的运算操作，并用关键字 bitand 来表示该操作，如表达式 `b bitand c` 表示 变量 `b` 与 变量 `c` 的数值做“按位与”运算得到的结果。

 本次考核中，要求同学们在代码生成作业的基础上实现上述两项需求。

### 【题目要求】

（1）增加的语法成分如下所示：

  (i) 变量定义 

```C
VarDef → Ident { '[' ConstExp ']' } 
            | Ident { '[' ConstExp ']' } '=' InitVal
            | Ident '=' 'getint' '(' ')'
```

  (ii) 乘除模表达式 `MulExp → UnaryExp | MulExp ('*' | '/' | '%' | 'bitand' ) UnaryExp`

（2）为了降低难度，使用输入语句进行变量定义时，变量**只可能**为一个普通 `int` 类型的变量，**不会**出现对数组变量进行赋值，如 `int arr[2] = getint();` 这是不合法的

（3）为了降低难度，按位与运算符号 `&` 被关键字 `bitand` 代替，运算效果与 `C/Java` 语言程序相同，但运算优先级与**乘除模**`('*','/','%')`为同一优先级，与 `C/Java` 语言程序规定的位运算优先级**不同**，如 `a + b bitand c * d;` 按照运算优先级翻译成中间代码为

```C
t1 = b bitand c
t2 = t1 * d
t3 = a + t2
```

（4）为了降低难度，常量表达式(`ConstExp`)的计算中**不会**出现按位与(`bitand`)操作，如 `const int p = N bitand M;` 和 `int a[N bitand M];` （其中 `M` 和 `N` 为常量），这些都是不合法的

（5）在新增的语法规则中，`bitand` 为保留关键字，测试样例中**不会**出现标识符 `Ident` 为 `bitand` 的情况

（6）`int i = getint();` 等价于 `int i;` 与 `i = getint();` 两条语句

（7）`a bitand b` 的运算效果等价于 `C/Java` 语言程序中的 `a & b`

（8）提示：按位与运算可选用 `and` 指令，其格式与 `add`、`sub`、`mul` 等指令相同。



### 【输入形式】testfile.txt为符合文法要求的测试程序。另外可能存在来自于标准输入的数据。

### 【输出形式】按照选择的不同目标码分为三类：

 1）生成MIPS的编译器

   按如上要求将目标代码生成结果输出至mips.txt中。

 2）生成LLVM IR的编译器

   按如上要求将目标代码生成结果输出至llvm_ir.txt中。

 3）生成PCODE的编译器

   按如上要求生成PCODE并解释执行，在pcoderesult.txt中记录解释执行结果。

### 【测试样例】

```
int main()
{
	int i = getint(), j = getint();
	printf("%d", i bitand j);
	return 0;
}
```

### 【样例输入】

```
5
9
```

### 【样例输出】

```
1
```

### 【样例说明】

```
// i = 5(00000101), j = 9(00001001)
// 按位与结果为 1(00000001)
```



### 【评分标准】按与预期结果不一致的行数扣分，每项扣10%。

### 【目标代码说明】

 （1）PCODE代码的定义可参见教材P458, Pascal-S指令代码集，可以进行修改，解释执行程序也可以借鉴Pascal-S编译器源码中的解释执行程序，若PCODE代码修改了请相应修改解释执行程序

 （2）MIPS代码可以选择基础指令及伪指令，不能选择宏指令； MARS 使用 4.5 版本（课程组修改版本），请在教学平台的“课件下载”中获取课程组修改过的版本Mars-for-Compiler-2022.jar以及“竞速排序及仿真器使用说明2022”文档查看具体要求



### 【测试样例说明】

  本次考核共有15个测试点，均 Accept 即可拿到满分。

## Part 2 实现`int a = getint();`

### 思路

首先，题面中明确`int a = getint();`与`int a; a = getint();`是等价的，因此核心思路即转化成这样的形式。

其次，考虑改动的代码所处位置（前/中/后端？词法分析/语法分析/中间代码生成/目标代码生成？）。考虑到实现难度/debug难度/作答时间等，将目标定在词法分析阶段实现这一转化。

然后，考虑实现方法，由于词法分析阶段还没有语法结构（即文法中的各种非终结符结构），因此需要从代码表面特征入手分析。注意到所给样例已经提示可以在一行声明语句中出现形如多个类似结构的赋值语句，因此思路如下：

- 找到一个`getint`标识符，标注其位置为`i`
- 从`i`向前找到一个最近的`;`，标注其位置为`j`，如果没找到就令`j = 0`
- 在`[j,i]`之间寻找`int`标识符，如果找到，说明其是形如`int ... var = getint() ...;`，其中`...`是可能有其他内容
- 将其拆解为`int ...var...;var = getint();`，源码的其余部分保持不动
- 重复上述循环直至`[j,i]`之间找不到`int`标识符

### 坑点与debug

- ArrayList不能一边遍历一边删除
- 由于实现位置是单词串，因此每次单词串的长度会发生变化，应当每次循环都更新
- 上述寻找`getint`标识符应当从后向前找，否则会发生`int a = getint(), b = getint();`被错误转化成`int a,b;b = getint();a = getint();`的问题

### 后记

虽然笔者通过了课上测评（15/15），但是感觉这一实现还有潜在的问题，这里简单记录一下：

- 在`[j,i]`之间寻找`int`标识符时，可能存在的一种情况是这个`int`标识符是函数声明的返回类型，如以下代码段：

  ```C
  int a;
  int main() {
      a = getint();
  }
  ```

  对于这种情况，笔者的实现应该是会错误处理为如下代码：

  ```C
  int a;
  int main() {
  	int a;
  	a = getint();
  }
  ```

## Part 3 实现`bitand`

### 思路

由题意知，只需添加一种和乘除模运算级别相同的`bitand`运算即可，需要仿照对乘除模的处理方式依次修改词法分析、语法分析和代码生成部分，但改动的代码量不大且较为机械。