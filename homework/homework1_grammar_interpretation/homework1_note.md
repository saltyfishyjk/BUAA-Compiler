## homework1 note

## Part 1 SysY和C语言不同之处

尽管SysY文法的形式化定义给出了明确的规则，但是细节之处难免忘记，因此这里记录常见的不同之处，用以自查。

- 没有`#include`
- 有返回值的函数Block必须`return`
- 没有`freopen`
- 没有`scanf`
- `int a = getint()`不合法，需要分开写成`int a`和`a = getint()`
- 没有`+=, -=, *=, /=, %=`
- 没有`& | ^`
- 没有`<< >>`
- 没有`for`，有`while`
- 常量声明必须赋值，如`const int x = 10`，`const int;`是非法的

## Part 2 SysY测试用例全覆盖需要包含的情况

- 