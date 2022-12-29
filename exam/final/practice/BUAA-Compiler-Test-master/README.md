# 自动测试程序使用说明

## GitHub链接

https://github.com/ChenMao20374042/BUAA-Compiler-Test

## 更新

2022.10.31 更新了代码生成一的测试数据和测试程序

## 使用说明

### 参数说明

在test.py中，可以设置不同的参数来实现不同测试。

```
TEST_TYPE 【修改】测试类型 如error
TEST_ID_RANGE 【修改】测试样例id范围，必须连续 如[0,9]
TESTCASE_DIR 【不用修改】测试数据路径
TEST_INPUT 【修改】是否提供标准输入（getint）如True
RUN_MARS 【修改】是否要运行mars 如False
```

然后可以通过运行test.py来进行测试。

### 测试数据

自动测试程序实现了部分错误处理测试（error），代码生成一（code）的测试数据，你可以在test/data中增加自己的测试数据。

测试数据放置于test/data下，每种测试类型都需要创建一个目录（test/data/xx_test），并将测试样例放置其中。

每一组测试样例需要包括

- 待编译源码文件 `xx_test_id.txt`
- 期望输出文件 `xx_ans_id.txt`
- 如果有标准输入（`getint()`），还需要提供标准输入文件 `xx_input_id.txt`。

### 注意事项

- 请确保你的程序打包成jar包，命名为`"Compiler.jar"`，并放置在test/目录下
- 请确保你的程序从`testfile.txt`读入待编译源码
- 如果你的程序需要读入标准输入（例如要进行中间代码测试），请确保从`input.txt`读入
- 如果你的程序需要输出（例如要进行错误处理测试和中间代码测试），请确保输出到`output.txt`
- 如果你的程序需要生成mips汇编并运行mars（例如要进行目标代码测试），请确保输出mips汇编到`mips.txt`。
- 你可以在控制台或test/testlog中查看测试结果。
- 如果某一测试样例出现错误，则测试程序将会输出相应信息并**停止后续测试**，你可以：
  - 在test/testfile.txt中查看出现错误的待编译源码
  - 在test/input.txt中查看对应的标准输入（如果有的话）
  - 在test/ansfile.txt中查看对应的期望输出
  - 在test/output.txt中查看对应的实际输出
  - 在test/mips.txt中查看生成的mips汇编（如果有的话）

## 中间代码测试

进行这部分测试需要**实现运行中间代码的虚拟机**，并在编译器程序中运行虚拟机。（其他注意事项见上）

`test.py`参数设置如下

```
TEST_TYPE = "code" #【修改】测试类型
TEST_ID_RANGE = [0,5] #【修改】测试样例id范围
TESTCASE_DIR = "./data/" + TEST_TYPE + "_test/"
TEST_INPUT = True # 【修改】是否提供输入
RUN_MARS = False #【修改】是否运行mars
```

## 错误处理部分测试

进行这部分测试需要你的编译器程序将错误处理结果输出到`output.txt`。

`test.py`参数设置如下

```
TEST_TYPE = "error" #【修改】测试类型
TEST_ID_RANGE = [0,14] #【修改】测试样例id范围
TESTCASE_DIR = "./data/" + TEST_TYPE + "_test/"
TEST_INPUT = False # 【修改】是否提供输入
RUN_MARS = False #【修改】是否运行mars
```

## mips测试

进行这部分测试需要你的编译程序生成mips汇编到`mips.txt`中。

`test.py`参数设置如下

```
TEST_TYPE = "code" #【修改】测试类型
TEST_ID_RANGE = [0,5] #【修改】测试样例id范围
TESTCASE_DIR = "./data/" + TEST_TYPE + "_test/"
TEST_INPUT = True # 【修改】是否提供输入
RUN_MARS = True #【修改】是否运行mars
```

## 其他部分测试

你可以在test/data/中补充自己的测试数据，并根据相应的测试类型选择合适的参数。
