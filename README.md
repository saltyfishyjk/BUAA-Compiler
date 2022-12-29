# BUAA-Compiler-2022 实验源码



![license: MIT](https://img.shields.io/badge/license-MIT-green)

## 课程简介

《编译技术》是北航计算机学院大三上学期核心专业课之一。除了理论课程之外，还包括一样课程设计（下称实验），要求使用C/C++或Java实现一个源码为SysY，目标代码为PCODE或LLVM IR或MIPS的编译器；其中，生成PCODE需要同时编写一个解释器，生成MIPS的需要进行优化并参加竞速排名（2022年要求）。

需要注意的是，区别于OS，编译实验**查重**，因此请确保不会出现学术不端的情况。

## 课程作业简介

实验部分分为六次作业，分别是文法解读、词法分析、语法分析、错误处理、代码生成一、代码生成二，以下简介每次作业的要求：

- 第一次作业 文法解读：根据SysY文法编写4-6个**符合文法**的测试程序，要求实现100%覆盖
- 第二次作业 词法分析：实现词法分析部分，对给定SysY源码进行词法分析并按作业要求输出
- 第三次作业 语法分析：**在词法分析基础上**实现语法分析部分，对给定SysY源码进行语法分析并按作业要求输出
- 第四次作业 错误处理：在**词法分析和语法分析基础上**实现错误处理，对给定SysY源码（可能不含错误或含若干个给定错误类型错误）进行错误处理并按作业要求输出
- 第五次作业 代码生成一：在**词法分析和语法分析基础上**实现**部分**代码生成，对作业要求的文法范围能够进行代码生成
- 第六次作业 代码生成二：在**词法分析、语法分析和代码生成一基础上**实现**全部**代码生成

## 仓库简介

### 实验完成情况

笔者选择了MIPS赛道~~但没有实现任何代码优化~~，通过了课下所有作业的辅助测试库和所有正式作业评测，通过了期中期末上机测试的代码题目。

笔者水平有限，代码仅供参考。

### Releases介绍

本仓库共发布了6个Release，分别对应六次作业的终版代码，方便读者按需获取某一特定节点的编译器，每个Release带有一份简短的英文介绍。

需要指出的是，第四次作业错误处理和第五、六次作业的代码生成是**两个独立的方向**，也就是说，第五、六次作业的输入**不会**存在错误，不需要进行错误处理，后续的作业和考试也不会将二者耦合在一起，因此一个设计流程是：

```
词法分析->语法分析->错误处理
词法分析->语法分析->代码生成一->代码生成二
```

但是，笔者的设计流程是：

```
词法分析->语法分析->错误处理->代码生成一->代码生成二
```

这里需要读者稍加留意。

### 仓库结构介绍

#### README.md

即本文档，前面是课程简介、课程作业简介和仓库简介，后面是笔者的设计文档。

#### homework

下含6个文件夹，分别对应6次作业，每个文件夹内是对应作业的终版编译器，可直接IDEA运行。

#### exam

下含2个文件夹，分别对应期中考试和期末考试，包含考试题目、解题思路和笔者的代码（均已AC课上评测）。

#### docs

下含两篇markdown文章，分别是优化和感想。

***


## 一、参考编译器介绍

本编译器主要参考的编译器为Pascal编译器，具体如下。

### 1. 总体结构

该编译器总体结构为经典的词法分析、语法分析、错误处理、语义分析、代码生成和代码优化六个部分。

### 2. 接口设计

#### 2.1 `nextch`读取下一个字符

```pascal
procedure nextch;  { read next character; process line end }
  begin
    if cc = ll {* 如果读到了当前行行尾 *}
    then begin
           if eof( psin ) {* 要读入的文件已经读完 *}
           then begin
                  writeln( psout ); {* 输出空行 *}
                  writeln( psout, 'program incomplete' ); {* 输出'program incomplete' *}
                  errormsg; {* 输出错误信息 *}
                  exit;
                end;
           if errpos <> 0 {* errpos不等于0，有错误出现 *}
           then begin
                  if skipflag then endskip; {* 调用endskip过程加下划线 *}
                  writeln( psout );
                  errpos := 0
                end;
           write( psout, lc: 5, ' '); {* 没有错误执行的操作，在list文件中输出当前PCODE的行号以及一个空格，不换。lc:5表示输出长度大于等于5,不足5前面用空格补齐*}
           ll := 0; {* 将行长度和字符指针置零 *}
           cc := 0;
           while not eoln( psin ) do {* 因为在读字符之前当前行已经读完了，所以psin的指针指向的是下一行代码,于是进行循环将psin所在行的代码装入line中。循环读字符直到读到行末，能进入此循环说明之前处理了错误或进入新行 *}
             begin
               ll := ll + 1; {* 统计当前行长度 *}
               read( psin, ch ); {* 读入一个字符 *}
               write( psout, ch ); {* 输出该字符 *}
               line[ll] := ch {* 将ch保存到line中,循环结束line保存到当前行末的所有字符 *}
             end;
           ll := ll + 1;
           readln( psin ); {* 从psin读一行空行，让psin指向下一行代码 *}
           line[ll] := ' '; {* 将行末置为空格 *}
           writeln( psout );
         end;
    cc := cc + 1; {* 字符指针后移 *}
    ch := line[cc]; {* 读取下一个字符，ch = 取出来的字符 *}
  end { nextch };
```

#### 2.2 `error`打印错误信息

```pascal
{* 打印出错位置和错误编号，并将错误编号加入errs中 *}
procedure error( n: integer ); {* n为错误号 *}
begin
  if errpos = 0
  then write ( psout, '****' );
  if cc > errpos {* 确认字符计数指针在当前errpos之后，避免重复报错 *}
  then begin
         write( psout, ' ': cc-errpos, '^', n:2);
         errpos := cc + 3;
         errs := errs +[n] {* 将错误号加入errs集合 *}
       end
end { error };
```

#### 2.3 `adjustscale`处理实数

```pascal
{* 根据小数位数和指数大小求出实数尾部值的大小，并附在rnum后面得到最后的实数 *}
  procedure adjustscale;
    var s : integer;
        d, t : real;
    begin
      if k + e > emax {* 当前位数加上指数超过指数上限则报错 *}
      then error(21)
      else if k + e < emin {* 低于最小值则直接将实数设置为0 *}
      then rnum := 0
      else begin
        s := abs(e); {* 将指数转为正数方便处理 *}
        t := 1.0; {* 指数部分转换后的结果 *}
        d := 10.0; {* 底数 *}
        repeat {* 将实数的指数部分变为普通数字 *}
          while not odd(s) do {* 循环处理偶次幂直到指数为奇数 *}
            begin
              s := s div 2; {* 指数除以二 *}
              d := sqr(d) {* 把平方直接转到d上，d = d的平方 *}
            end;
          s := s - 1;
          t := d * t
        until s = 0;
        if e >= 0 {* 判断指数正负，决定是该除以t还是乘以t。把指数e转换成2N+1或者2N的形式, t此时为10的e次方*}
        then rnum := rnum * t
        else rnum := rnum / t
      end
    end { adjustscale };
```

#### 2.4 `enter`登记符号表

```pascal
{* 把标准类型、过程、函数名登到符号表(tab)中。
x0为标识符名，x1为标识符种类，x2为标识符类型，x3为地址或大小（大小只针对类型） *}
procedure enter(x0:alfa; x1:objecttyp; x2:types; x3:integer );
  begin
    t := t + 1;    { enter standard identifier }
    with tab[t] do
      begin
        name := x0;
        link := t - 1;
        obj := x1;
        typ := x2;
        ref := 0;
        normal := true;
        lev := 0; {*主程序开始之前就登录了一些自带的,所以级别最高*}
        adr := x3;
      end
  end; { enter }
```

#### 2.5 `enterarray`登记数组符号表

```pascal
{* 将数组下标信息录入数组表atab，
tp为数组下标类型，可为ints,bools或者chars
l,h分别为数组下上界 *}
procedure enterarray( tp: types; l,h: integer );
  begin
    if l > h {* 界限出错 *}
    then error(27);
    if( abs(l) > xmax ) or ( abs(h) > xmax ) {* 超范围下标，报错 *}
    then begin
           error(27);
           l := 0;
           h := 0;
         end;
    if a = amax {* 数组表已满，报错 *}
    then fatal(4)
    else begin
           a := a + 1;
           with atab[a] do {* 正常设置数组的三个域 *}
             begin
               inxtyp := tp;
               low := l;
               high := h
             end
         end
  end { enterarray };
```

#### 2.6 `enterreal`登记实常量表

```pascal
{* 录入实常量表rconst *}
procedure enterreal( x: real );
  begin
    if c2 = c2max - 1
    then fatal(3)
    else begin
           rconst[c2+1] := x;
           c1 := 1; {*循环用的局部变量*}
           while rconst[c1] <> x do
             c1 := c1 + 1;
           if c1 > c2
           then  c2 := c1 {*如果在c2之前就有该常量,则c1<=c2,于是c2不更新,否则c2更新*}
         end
  end { enterreal };
```



#### 2.7 `enterblock`登记分程序信息入分程序表

```pascal
{* 将分程序信息录入分程序表btab *}
procedure enterblock;
  begin
    if b = bmax {* 分程序表满了 *}
    then fatal(2)
    else begin
           b := b + 1;
           btab[b].last := 0; {* 指向过程或函数最后一个符号在表中的位置,建表用 *}
           btab[b].lastpar := 0; {* 指向过程或者函数的最后一个'参数'符号在tab中的位置,退栈用 *}
         end
  end { enterblock };
```

#### 2.8 `emit`生成中间代码PCODE

```pascal
{* emit和下面两个过程都是用来生成PCODE的，后个过程接的参数是操作数
fct为操作码 *}
procedure emit( fct: integer ); {*这几个emit是用来生成pcode的,fct为操作码,这里为无操作数*}
  begin
    if lc = cmax   {*lc为code表的索引变量*}
    then fatal(6);
    code[lc].f := fct;
    lc := lc + 1
  end { emit };

procedure emit1( fct, b: integer );  {*一个操作数*}
  begin
    if lc = cmax
    then fatal(6);
    with code[lc] do
      begin
        f := fct;
        y := b;
      end;
    lc := lc + 1
  end { emit1 };

procedure emit2( fct, a, b: integer );  {*两个操作数*}
  begin
    if lc = cmax then fatal(6);
    with code[lc] do
      begin
        f := fct;
        x := a;
        y := b
      end;
    lc := lc + 1;
  end { emit2 };
```

#### 2.9 `test`检查符号合法性

```pascal
  {* 检查当前sym是否合法,若不合法,打印出错标志并进行跳读 *}
  procedure test( s1,s2: symset; n:integer );
    begin
      if not( sy in s1 )
      then skip( s1 + s2, n )
    end { test };
  {* 检查分号是否合法 *}
  procedure testsemicolon;
    begin
      if sy = semicolon
      then insymbol
      else begin
             error(14);
             if sy in [comma, colon]
             then insymbol
           end;
      test( [ident] + blockbegsys, fsys, 6 )
    end { testsemicolon };
```

#### 2.10 `enter`登记符号表

```pascal
  {* 在分程序中将标识符id填入tab,k为标识符种类 *}
  procedure enter( id: alfa; k:objecttyp );
    var j,l : integer;
    begin
      if t = tmax {* tab已满，报错 *}
      then fatal(1)
      else begin
             tab[0].name := id;
             j := btab[display[level]].last; {* 得到对应level的分程序的最后一个标识符位置 *}
             l := j;
             while tab[j].name <> id do {* 从分程序尾部一直向前遍历，看是否存在与id重名的标识符 *}
               j := tab[j].link;
             if j <> 0 {* 有重名则报错。已经在符号表中有记载了,重复定义 *}
             then error(1)
             else begin {* 不重名则正常入栈 。在tab中登记信息*}
                    t := t + 1;
                    with tab[t] do
                      begin
                        name := id;
                        link := l;
                        obj := k;
                        typ := notyp;
                        ref := 0;
                        lev := level;
                        adr := 0;
                        normal := false { initial value }
                      end;
                    btab[display[level]].last := t{*更新当前分程序层最后一个标识符在tab表中的位置*}
                  end
           end
    end { enter };

  {* 查找分程序中标识符id在符号表中位置并返回 *}
  function loc( id: alfa ):integer;
    var i,j : integer;        { locate if in table }
    begin
      i := level; {*i为当前分程序层*}
      tab[0].name := id;  { sentinel }
      repeat
        j := btab[display[i]].last;
        while tab[j].name <> id do
        j := tab[j].link;
        i := i - 1;
      until ( i < 0 ) or ( j <> 0 );{*当前层没有则往前再找一层,直到找到(j<>0)或者没有(i<0)*}
      if j = 0
      then error(0);{*没找到,报错*}
      loc := j
    end { loc } ;

  {* 将变量加入到tab，若sy不是标识符则报错 *}
  procedure entervariable;
    begin
      if sy = ident
      then begin
             enter( id, vvariable );
             insymbol
           end
      else error(2)
    end { entervariable };
```

#### 2.11 `constant`处理常量

```pascal
{* 处理分程序中常量，由c返回常量的类型与值 *}
  procedure constant( fsys: symset; var c: conrec );
    var x, sign : integer;
    begin
      c.tp := notyp;
      c.i := 0;
      test( constbegsys, fsys, 50 );
      if sy in constbegsys {* 如果sy是常量开始的符号,才往下继续分析 *}
      then begin
             if sy = charcon
             then begin {* sy是字符常量 *}
                    c.tp := chars;
                    c.i := inum; {* inum存储该字符的ascii码值 *}
                    insymbol
                  end
             else begin
                  sign := 1; {* sy不是字符常量,默认符号为正 *}
                  if sy in [plus, minus] {* sy是正负号 *}
                  then begin
                         if sy = minus {*是负号*}
                         then sign := -1;
                         insymbol
                       end;
                  if sy = ident {* sy是标识符常量 *}
                  then begin
                         x := loc(id); {* 找到id在表中位置 *}
                         if x <> 0
                         then
                           if tab[x].obj <> konstant {* id对应符号种类不是常量，报错 *}
                           then error(25)
                           else begin {* 得到的tab[x]为标识符常量，对c进行相应处理 *}
                                  c.tp := tab[x].typ; {* 得到类型 *}
                                  if c.tp = reals {* 根据类型是整数还是实数进行处理 *}
                                  then c.r := sign*rconst[tab[x].adr]
                                  else c.i := sign*tab[x].adr
                                end;
                         insymbol
                       end
                  else if sy = intcon {* sy是整数常量 *}
                       then begin
                              c.tp := ints;
                              c.i := sign*inum; {* 在i域中存入带符号的整数值 *}
                              insymbol
                            end
                 else if sy = realcon {* sy是实数常量 *}
                      then begin
                             c.tp := reals;
                             c.r := sign*rnum; {* 在r域中存入带符号的整数值 *}
                             insymbol
                           end
                 else skip(fsys,50) {* sy不是任何类型常量，报错并跳过部分代码 *}
                end;
                test(fsys,[],6)
           end
    end { constant };
```

#### 2.12 `typ`处理类型

```pascal
  {* 处理类型说明,返回当前关键词的类型tp,在符号表中的位置rf,以及需要占用存储空间的大小sz *}
  procedure typ( fsys: symset; var tp: types; var rf,sz:integer );
    var eltp : types;
        elrf, x : integer;
        elsz, offset, t0, t1 : integer;

    {* 数组类型的处理比较特殊，做单独处理.登录数组类型到atab并返回数组的登录位置和数组大小 *}
    procedure arraytyp( var aref, arsz: integer );
      var eltp : types; {* 数组元素类型 *}
          low, high : conrec; {* 上下界 *}
          elrf, elsz: integer; {* 记录ref和size方便返回 *}
      begin
        constant( [colon, rbrack, rparent, ofsy] + fsys, low ); {* 获得数组编号下界 *}
        if low.tp = reals {* 下标类型不是整数，报错并切换为整数，数值为0}
        then begin
               error(27);
               low.tp := ints;
               low.i := 0
             end;
        if sy = colon {* 下一个符号是..或者:都可继续执行 *}
        then insymbol
        else error(13);
        constant( [rbrack, comma, rparent, ofsy ] + fsys, high ); {* 获得数组编号上界 *}
        if high.tp <> low.tp {* 上下界需保持类型一致，否则报错，并将上界大小调为与下界一致 *}
        then begin
               error(27);
               high.i := low.i
             end;
        enterarray( low.tp, low.i, high.i ); {* 将数组下标信息录入atab *}
        aref := a; {* 将数组在atab中的位置存在aref中 *}
        if sy = comma
        then begin {* 若读到逗号，说明需要建立多维数组 *}
               insymbol;
               eltp := arrays; {* 数组元素类型为arrays *}
               arraytyp( elrf, elsz ) {* 递归处理数组内的数组 *}
             end
        else begin {* 不是逗号的情况（右方括号或非法） *}
               if sy = rbrack {* 读到右方括号，说明数组下标部分声明完毕 *}
               then insymbol
               else begin {* 非法 *}
                      error(12);
                      if sy = rparent {* 若为右大括号则容错 *}
                      then insymbol
                    end;
               if sy = ofsy {* 读到of关键字则继续，否则报错 *}
               then insymbol
               else error(8);
               typ( fsys, eltp, elrf, elsz ) {* 处理数组元素的类型 *}
             end;
        with atab[aref] do {* 处理完多维数组或数组元素信息则将这些信息存入atab[aref]中}
          begin
            arsz := (high-low+1) * elsz;
            size := arsz;
            eltyp := eltp;
            elref := elrf;
            elsize := elsz
          end
      end { arraytyp };
    begin { typ  }
      tp := notyp;
      rf := 0;
      sz := 0;
      test( typebegsys, fsys, 10 ); {* 检查当前符号是否为类型声明的开始符 *}
      if sy in typebegsys {* 如果是类型声明的开始符 *}
      then begin
              if sy = ident {* 当前符号为标识符 *}
              then begin
                    x := loc(id); {* 查找id在符号表中的位置 *}
                    if x <> 0
                    then with tab[x] do
                           if obj <> typel {* 符号表中标识符种类不是类型标识符(typel)（非用户定义或编译器自带），报错 *}
                           then error(29)
                           else begin {* 正常获取符号类型 *}
                                  tp := typ;
                                  rf := ref;
                                  sz := adr;{*获得其在运行栈中分配的存储空间*}
                                  if tp = notyp {* 未定义类型，报错 *}
                                  then error(30)
                                end;
                    insymbol
                  end
              else if sy = arraysy {* 当前符号为array关键字 *}
              then begin
                    insymbol;
                    if sy = lbrack {* 当前符号为[，则处理下一符号}
                    then insymbol
                    else begin {* 否则报错并容错( *}
                          error(11);
                          if sy = lparent 
                          then insymbol
                         end;
                    tp := arrays; {*类型为数组类型*}
                    arraytyp(rf,sz) {*登录数组类型*}
                   end
             else begin { records } {* 不是标识符也不是数组类型，则只可能为记录 *}
                    insymbol;
                    enterblock; {* 记录被看做一个分程序，故需在btab中记录它的信息 *}
                    tp := records;
                    rf := b; {* rf指向btab中记录的位置 *}
                    if level = lmax
                    then fatal(5);{* 当前嵌套层次已经是最大层次了,即不能产生更深的嵌套，报错并终止程序 *}
                    level := level + 1;
                    display[level] := b; {* 建立分层次索引 *}
                    offset := 0;
                    while not ( sy in fsys - [semicolon,comma,ident]+ [endsy] ) do
                      {* end之前都是记录类型变量内的变量声明 *}
                      begin { field section } {* 处理记录内部成员变量 *}
                        if sy = ident
                        then begin {* 当前符号为标识符 *}
                                t0 := t; {* 将当前tab指针存入t0 *}
                                entervariable; {* 变量表 *}
                                while sy = comma do {* 同类型变量同时申明，
                                通过逗号分隔,未遇到冒号则继续读入并入表 *}
                                  begin
                                    insymbol;
                                    entervariable
                                  end;
                                if sy = colon
                                then insymbol{* 遇到了冒号,说明这类的变量声明结束了,冒号后面跟变量的类型 *}
                                else error(5);
                                t1 := t; {* 将当前tab指针存入t1 *}
                                typ( fsys + [semicolon, endsy, comma,ident], eltp, elrf, elsz );
                                {* 递归调用typ来处理记录类型的成员变量,确定各成员的类型,ref和adr *}
                                while t0 < t1 do {* 填写t0到t1中信息缺失的部分,
                                由于t0~t1都是同一类型的变量,因此size大小是相同 *}
                                begin
                                  t0 := t0 + 1;
                                  with tab[t0] do {* 用获取到的成员变量信息补充表项内容 *}
                                    begin
                                      typ := eltp;{*eltp来自上面的typ递归调用*}
                                      ref := elrf; {* 此处ref为记录在btab中的位置 *}
                                      normal := true;{*所有normal的初值都为false*}
                                      adr := offset; {* 变量地址位移 *}
                                      offset := offset + elsz {* 下一变量真实位置 *}
                                    end
                                end
                             end; { sy = ident }
                        if sy <> endsy
                        then begin {* 当前符号不是end *}
                               if sy = semicolon
                               then insymbol {* 若为分号，则正常读取后续符号 *}
                               else begin {* 否则报错并容错，容逗号 *}
                                      error(14);
                                      if sy = comma
                                      then insymbol
                                    end;
                                    test( [ident,endsy, semicolon],fsys,6 )
                                    {* 检测当前符号是否合法。开启下一行处理时检验当前符号是否合法*}
                             end
                      end; { field section }
                    btab[rf].vsize := offset; {* vsize为记录所需存储单元数目 *}
                    sz := offset;
                    btab[rf].psize := 0; {* 记录存储单元不需要psize。该程序块的参数占用空间为0,因为record类型并不是真正的过程,没有参数 *}
                    insymbol;
                    level := level - 1{*record处理结束后level降一层*}
                  end; { record }
             test( fsys, [],6 ) {*检查当前sym是否合法*}
           end;
      end { typ };
```

#### 2.13 `parameterlist`处理形参

```pascal
  {* 处理过程或函数说明中的形参，将形参信息填入符号表 *}
  procedure parameterlist; { formal parameter list  }
    var tp : types;
        valpar : boolean; {* value parameter *}
        rf, sz, x, t0 : integer;
    begin
      insymbol;
      tp := notyp;
      rf := 0;{*初始化符号表的位置*}
      sz := 0; {*初始化元素的大小*}
      test( [ident, varsy], fsys+[rparent], 7 );
      while sy in [ident, varsy] do {* 处理所有是标识符或var关键字的形参 *}
        begin
          if sy <> varsy {* 不是var（指针）参数，将valpar设置为true *}
          then valpar := true {*值形参*}
          else begin {* 是指针参数，将valpar设置为false *}
                 insymbol;
                 valpar := false
               end;
          t0 := t; {* 存第一个参数在tab中地址到t0 *}
          entervariable;
          while sy = comma do {* 循环给多个同类型参数入表 *}
            begin
              insymbol;
              entervariable;
            end;

          {* 以下代码与typ处理记录同类成员变量的代码近似，只做不同部分的分析 *}
          if sy = colon{*等于冒号则开始处理类型*}
          then begin
                  insymbol;
                  if sy <> ident {* 非标识符一定不是数据类型，报错 *}
                  then error(2)
                  else begin
                         x := loc(id);
                         insymbol;
                         if x <> 0 
                         then with tab[x] do
                           if obj <> typel {*是标识符中的类型*}
                           then error(29)
                           else begin
                                  tp := typ;
                                  rf := ref;
                                  if valpar {* 值形参。针对指针参数与值参数对sz做不同处理 *}
                                    then sz := adr {*获得当前形参在符号表的位置*}
                                    else sz := 1 {*不是值形参则将其置为1(在这篇代码中,变量形参的大小都设置为了1)*}
                                end;
                       end;
                  test( [semicolon, rparent], [comma,ident]+fsys, 14 )
                 end
          else error(5); {* 不是冒号则报错（不支持数据类型的自动解释） *}
          while t0 < t do
            begin
              t0 := t0 + 1;
              with tab[t0] do
                begin
                  typ := tp;
                  ref := rf; {* ref = 0 *}
                  adr := dx; {* adr = 运行栈中存储分配单元的相对地址dx *}
                  lev := level;
                  normal := valpar;
                  dx := dx + sz{*block中的变量存储索引更新*}
                end
            end;
            if sy <> rparent {* 不是右括号代表还有参数或出错 *}
            then begin
                   if sy = semicolon
                   then insymbol
                   else begin
                          error(14);
                          if sy = comma
                          then insymbol
                        end;
                        test( [ident, varsy],[rparent]+fsys,6)
                 end
        end { while };
      if sy = rparent {* 参数声明结束后应当用右括号结尾 *}
      then begin
             insymbol;
             test( [semicolon, colon],fsys,6 )
           end
      else error(4)
    end { parameterlist };
```

#### 2.14 `typedeclaration`处理自定义类型

```pascal
  {* 处理类型自定义，将自定义的类型信息填入tab，与constdec过程几乎一样 *}
  procedure typedeclaration;
    var tp: types;
        rf, sz, t1 : integer;
    begin
      insymbol;
      test([ident], blockbegsys,2 );{*检查获取到的是不是标识符*}
      while sy = ident do
        begin
          enter(id, typel);
          t1 := t;
          insymbol;
          if sy = eql {*赋值符号识别与容错*}
          then insymbol
          else begin
                 error(16);
                 if sy = becomes
                 then insymbol
               end;
          typ( [semicolon,comma,ident]+fsys, tp,rf,sz ); {* 通过调用typ过程获取类型填表所需域 *}
          with tab[t1] do {*将typ的返回值填表*}
            begin
              typ := tp;
              ref := rf;
              adr := sz {* 类型的adr存的是类型所需大小 *}
            end;
          testsemicolon
        end
    end { typedeclaration };
```

#### 2.15 `variabledeclaration`处理普通变量

```pascal
  {* 处理普通变量申明，将变量填入tab，与parameterlist的值形参处理过程几乎一样 *}
{*处理变量类型,并将变量名及其相应信息填入符号表*}
  procedure variabledeclaration;
    var tp : types;
        t0, t1, rf, sz : integer;
    begin
      insymbol;
      while sy = ident do {*是标识符*}
        begin
          t0 := t;
          entervariable; {*将变量名登录到符号表中*}
          while sy = comma do {*多个变量名逗号分隔*}
            begin
              insymbol;
              entervariable;
            end;
          if sy = colon {*冒号*}
          then insymbol
          else error(5);
          t1 := t;
          typ([semicolon,comma,ident]+fsys, tp,rf,sz );{*获得类型地址和大小*}
          while t0 < t1 do {*对一行的变量循环填表*}
            begin
              t0 := t0 + 1;
              with tab[t0] do
                begin
                  typ := tp;
                  ref := rf;
                  lev := level;
                  adr := dx;
                  normal := true;
                  dx := dx + sz
                end
            end;
          testsemicolon
        end
    end { variabledeclaration };
```

#### 2.16 `procdeclaration`处理过程和函数

```pascal
  {* 处理过程与函数声明，将函数名或过程名填入tab *}
  procedure procdeclaration;
    var isfun : boolean;  {*是否是function*}
    begin
      isfun := sy = funcsy; {* sy是function就是方法，否则是过程 *}
      insymbol; 
      if sy <> ident {* sy不是标识符则报错，用十个空格代替 *}
      then begin
             error(2);
             id :='          '
           end;
      if isfun {* 函数或过程入tab *}
      then enter(id,funktion)
      else enter(id,prozedure);
      tab[t].normal := true;
      insymbol;
      block([semicolon]+fsys, isfun, level+1 ); {* 递归调用block处理分程序 *}
      if sy = semicolon {* 读到分号才算过程正常结束，否则报错 *}
      then insymbol
      else error(14);
      emit(32+ord(isfun)) {exit} {* 生成退出分程序的PCODE *}
    end { proceduredeclaration };
```

#### 2.17 `statement`处理各种语句

```pascal
  {* 分析处理各种语句 *}
  procedure statement( fsys:symset );
    var i : integer;

    {* 处理表达式子程序，提前声明供selector调用，避免蛋生鸡问题。由参数(x)返回求值结果的类型 *}
    procedure expression(fsys:symset; var x:item); forward;

    {* 处理结构变量v（数组下标或记录的成员变量）的使用 *}
    procedure selector(fsys:symset; var v:item);
      var x : item;
          a,j : integer;
      begin { sy in [lparent, lbrack, period] } {* 只要sy是(或者[或者.，就一直处理 *}
        repeat 
          if sy = period {* sy是.，后续内容作为成员变量处理 *}
          then begin
                 insymbol; { field selector }
                 if sy <> ident {* 域的类型必为标识符，否则报错 *}
                 then error(2)
                 else begin
                        if v.typ <> records {* v不是records类型，报错 *}
                        then error(31)
                        else begin { search field identifier }
                               j := btab[v.ref].last; {* 获得该记录在tab中最后一个标识符的位置 *}
                               tab[0].name := id; {* 暂存id *}
                               while tab[j].name <> id do {* 在tab中，
                               从j的位置（记录最后一项）向前查找id *}
                                 j := tab[j].link;
                               if j = 0 {* 未找到该标识符，说明该域在记录中不存在，报错 *}
                               then error(0);
                               v.typ := tab[j].typ;
                               v.ref := tab[j].ref;  {*记录其在btab中的指针值*}
                               a := tab[j].adr;  {*记录该变量对record变量起始地址的相对位移*}
                               if a <> 0 {*如果相对位移不为0则生成一条指令来计算此位移*}
                               then emit1(9,a) {* 输出PCODE:INT a，将a放在栈顶 *}
                             end;
                        insymbol
                      end
               end
          else begin { array selector } {*处理数组下标。sy是[或(或其他，后续内容作为数组下标处理或报错 *}
                 if sy <> lbrack {* 只认[作为取数组操作符，对{做隐式容错处理 *}
                 then error(11);
                 repeat {* 循环读取方括号中所有内容， 此处循环是为了满足处理多维数组的需要*}
                   insymbol;
                   expression( fsys+[comma,rbrack],x); {* 递归调用expression处理下标 *}{*处理表达式,例如array[6+i]*}
                   if v.typ <> arrays
                   then error(28)
                   else begin
                          a := v.ref; {*从v中获得该数组在atab中的位置*}
                          if atab[a].inxtyp <> x.typ {*如果传入的下标类型和atab中记录的数组下标类型不符则报错*}
                          then error(26)
                          else if atab[a].elsize = 1 {* 输出PCODE去取下标变量地址，如果是变量形参(指针)*}
                               then emit1(20,a)  {*IDX,取下标变量地址,长度为1*}
                               else emit1(21,a);  {*IXX,取下标变量地址*}
                          v.typ := atab[a].eltyp;
                          v.ref := atab[a].elref
                        end
                 until sy <> comma; {*直到没有遇到逗号终止循环*}
                 if sy = rbrack {* 遇到右括号，正常结束，否则报错 *}
                 then insymbol
                 else begin
                        error(12);
                        if sy = rparent   {*对右括号容错处理*}
                        then insymbol
                      end
               end
        until not( sy in[lbrack, lparent, period]);
        test( fsys,[],6)
      end { selector };

    {* 处理非标准的过程或函数调用
       i表示需要调用的过程或函数名在符号表中的位置 *}
    procedure call( fsys: symset; i:integer );
      var x : item; {*代表传进来的参数结构体*}
      lastp,cp,k : integer;
      begin
        emit1(18,i); { mark stack } {* 生成标记栈指令,传入被调用过程或函数在tab表中的位置,建立新的内务信息区 *}
        lastp := btab[tab[i].ref].lastpar; {* 记录当前过程或函数最后一个参数在符号表中的位置 *}
        cp := i; {*记录当前被调用函数或者过程在符号表中的位置*}    {*cp 到 lastp 之间是tab表中形参列表,若是在循环中cp > lastp则表示实参与形参个数不一致*}
        if sy = lparent  {*如果识别到左括号则开始进行被调用过程的参数处理*}
        then begin { actual parameter list }
               repeat
                 insymbol;
                 if cp >= lastp {* 如果当前符号的位置小于最后一个符号的位置，说明还有参数没有处理,反之报错 *}
                 then error(39)
                 else begin {* 开始处理参数 *}
                        cp := cp + 1;
                        if tab[cp].normal {* 如果normal的值为真，即如果传入的是值形参或者其他参数 *}
                        then begin { value parameter } 
                               expression( fsys+[comma, colon,rparent],x);
                               {* 调用expression处理参数 *}
                               if x.typ = tab[cp].typ {*表达式的类型和分程序要求输入的类型一致*}
                               then begin
                                      if x.ref <> tab[cp].ref {* btab中地址不同，报错 *}
                                      then error(36)
                                      else if x.typ = arrays {* x为数组类型，生成装入块指令，将实参表达式的值或地址放到预留的参数单元中 *}
                                      then emit1(22,atab[x.ref].size)
                                      else if x.typ = records {* x为记录类型，同样生成装入块指令完成操作，只是细节有所不同 *}
                                       then emit1(22,btab[x.ref].vsize)
                                    end
                               else if ( x.typ = ints ) and ( tab[cp].typ = reals )
                                  {* 如果表达式的类型是整型，但是分程序要求输入的是实数型参数 *}
                                then emit1(26,0) {* 生成强制转换指令 *}
                              else if x.typ <> notyp
                                         then error(36);
                             end
                        else begin { variable parameter }
                               if sy <> ident {*如果传进来的不是标识符则报错*}
                               then error(2)
                               else begin
                                      k := loc(id);{*找到其在tab中的位置*}
                                      insymbol;
                                      if k <> 0
                                      then begin
                                             if tab[k].obj <> vvariable {* 获取到的形参种类不是变量，报错 *}
                                             then error(37);
                                             x.typ := tab[k].typ;
                                             x.ref := tab[k].ref;
                                             if tab[k].normal {* tab[k]是值形参，将变量地址装入栈顶 *}
                                             then emit2(0,tab[k].lev,tab[k].adr)
                                             else emit2(1,tab[k].lev,tab[k].adr);
                                               {* 是变量形参，将变量的值装入栈顶 *}
                                             if sy in [lbrack, lparent, period]{*后面跟的可以是左中括号(数组下标),左括号(容错)或.(对应记录)。若有则调用分析子结构的过程来处理 *}
                                             then selector(fsys+[comma,colon,rparent],x);
                                             if ( x.typ <> tab[cp].typ ) or ( x.ref <> tab[cp].ref )
                                             then error(36)
                                           end
                                    end
                             end {variable parameter }
                      end;
                 test( [comma, rparent],fsys,6)
               until sy <> comma; {*循环直到处理完逗号*}
               if sy = rparent {* 以括号结束 *}
               then insymbol
               else error(4)
             end;
        if cp < lastp {* 实参数量不够，报错 *}
        then error(39); { too few actual parameters }
        emit1(19,btab[tab[i].ref].psize-1 ); {* 生成CAL指令，正式开始过程或函数调用 *}
        if tab[i].lev < level
        then emit2(3,tab[i].lev, level ) {* 生成DIS指令，更新display区 *}
      end { call };

    {* 处理整型或实数型两个操作数运算时的类型转换。(a + b = c, 根据a 和 b 判断c的类型)*}
    function resulttype( a, b : types) :types;
      begin
        if ( a > reals ) or ( b > reals ) {* 操作数不是整型也不是实数型，报错，返回notyp *}
        then begin
               error(33);
               resulttype := notyp
             end
        else if ( a = notyp ) or ( b = notyp ) {* 操作数都是notyp，返回notyp *}
             then resulttype := notyp
        else if a = ints {* 操作数a是整型 *}
             then if b = ints {* b是整型则不转换，b是实型则转实型 *}
                  then resulttype := ints
                  else begin
                         resulttype := reals;
                         emit1(26,1) {* 生成指令FLT进行类型转化 *}
                       end
        else begin {* a不是整型，返回结果必是实型 *}
             resulttype := reals;
             if b = ints {* b是整型，生成FLT进行类型转化 *}
             then emit1(26,0)
             end
      end { resulttype } ;

    procedure expression( fsys: symset; var x: item );  {*处理表达式,由x返回求值结果的类型*}
      var y : item;
          op : symbol;

      procedure simpleexpression( fsys: symset; var x: item );  {*处理简单表达式(+、-、or),由参数x返回求值结果的类型*}
        var y : item;
            op : symbol;

        procedure term( fsys: symset; var x: item );  {*处理项,由参数返回结果类型*}
          var y : item;
              op : symbol;

          procedure factor( fsys: symset; var x: item );  {*处理因子,由参数返回结果类型*}
            var i,f : integer;

            procedure standfct( n: integer ); {*处理标准函数调用,传入标准函数的编号n,执行不同的操作*}
              var ts : typset;  {*类型集合*}
              begin  { standard function no. n }
                if sy = lparent {*如果当前符号不是左括号则报错*}
                then insymbol
                else error(9);
                if n < 17 {*如果标准函数编号小于17*}
                then begin
                       expression( fsys+[rparent], x );   {*递归调用处理表达式的过程来处理参数*}
                       case n of
                       { abs, sqr } 0,2: begin
                                           ts := [ints, reals];
                                           tab[i].typ := x.typ;
                                           if x.typ = reals   {*如果参数类型为实数则对应的函数标号+1*}
                                           then n := n + 1
                                         end;

                       { odd, chr } 4,5: ts := [ints]; {* 判定奇偶与ascii码转字符串只涉及整型 *}
                       { odr }        6: ts := [ints,bools,chars]; {* 6号操作涉及整型,布尔型或者字符型 *}
                       { succ,pred } 7,8 : begin {* 得到前、后一个元素涉及整型、布尔型或者字符型 *}
                                             ts := [ints, bools,chars];
                                             tab[i].typ := x.typ
                                           end;
                       { round,trunc } 9,10,11,12,13,14,15,16: {* 其他17以前数学运算涉及ints与reals *}
                       { sin,cos,... }     begin
                                             ts := [ints,reals];
                                             if x.typ = ints {* 若为整型，则转为实型并生成PCODE *}
                                             then emit1(26,0)
                                           end;
                     end; { case }
                     if x.typ in ts
                     then emit1(8,n) {* x满足涉及类型，生成FCT n，表示生成n号标准函数 *}
                     else if x.typ <> notyp
                          then error(48);  {*该标准函数的参数类型不正确*}
                   end
                else begin { n in [17,18] }  {*17、18号，eof 和 eoln 函数*}
                       if sy <> ident {*不是标识符*}
                       then error(2)
                       else if id <> 'input    ' {* 只有‘input  ’合法。报0号错误,未知id*}
                            then error(0)
                            else insymbol;
                       emit1(8,n); {* 生成对应标准函数 *}
                     end;
                x.typ := tab[i].typ;
                if sy = rparent {* 右括号 标准函数调用结束*}
                then insymbol
                else error(4)
              end { standfct } ;
            begin { factor } {* 因子分析程序开始 *}
              x.typ := notyp;
              x.ref := 0;
              test( facbegsys, fsys,58 );
              while sy in facbegsys do {* 从所有合法因子开始符号开始循环处理因子 *}
                begin
                  if sy = ident {* sy是普通标识符 *}
                  then begin
                         i := loc(id);
                         insymbol;
                         with tab[i] do
                           case obj of
                             konstant: begin {* 处理常量标识符并生成PCODE *}
                                         x.typ := typ;
                                         x.ref := 0;
                                         if x.typ = reals {* x是否实常数 *}
                                         then emit1(25,adr)	{* LDR adr：将实数装入数据栈,
                                         注意实数常量的adr对应着其在rconst实常量表中的位置 *}
                                         else emit1(24,adr) {* LDC adr：装入字面常量，
                                          如果是整型直接存入栈顶即可 *}
                                       end;
                             vvariable:begin {* 处理变量标识符并生成PCODE *}
                                         x.typ := typ;
                                         x.ref := ref;
                                         if sy in [lbrack, lparent,period] {* x是数组或记录 *} {*如果该标识符变量后面紧跟'('、']'、'.'则说明该变量存在子结构*}
                                         then begin
                                                if normal
                                                then f := 0 {*值形参标准名或其他变量*}
                                                else f := 1; {*变量形参*}
                                                emit2(f,lev,adr); {*变量形参装载地址而值形参装载值*}
                                                selector(fsys,x); {*处理子结构得到真正的变量*}
                                                if x.typ in stantyps
                                                then emit(34)
                                              end
                                         else begin {* x是普通变量 *}
                                                if x.typ in stantyps 
                                                then if normal 
                                                     then f := 1 {*标准类型的值形参进行取值操作*}
                                                     else f := 2 {*标准类型的变量形参进行间接取值操作*}
                                                else if normal
                                                     then f := 0 {*非标准类型的值形参取地址*}
                                                     else f := 1; {*非标准类型的变量形参取值  ？？？？*}
                                                emit2(f,lev,adr)
                                              end
                                       end;
                             typel,prozedure: error(44); {* 因子不能为类型名或过程。表达式中不能出现的类型或者过程标识符*}
                             funktion: begin  {*如果是函数符号 处理函数并生成PCODE *}
                                         x.typ := typ;
                                         if lev <> 0 {* 通过lev是否为0判断处理的是
                                           标准函数还是非标准函数。层次为0则是标准函数,层次不为零则不是标准函数*}
                                         then call(fsys,i)
                                         else standfct(adr)
                                       end
                           end { case,with }
                       end
                  else if sy in [ charcon,intcon,realcon ] {* sy是常量 *}
                       then begin
                              if sy = realcon {* 处理实常量 *}
                              then begin
                                     x.typ := reals;
                                     enterreal(rnum); {*将该元素放入实常量表*}
                                     emit1(25,c1) {* LDR c1，
                                     将实常量表中第c1个(也就是刚刚放进去的)元素放入栈顶 *}
                                   end
                              else begin  {*整数和字符型常量*}
                                     if sy = charcon
                                     then x.typ := chars
                                     else x.typ := ints;
                                     emit1(24,inum) {* LDC inum：装入字面常量inum *}
                                   end;
                              x.ref := 0;
                              insymbol
                            end
                  else if sy = lparent {* sy是左括号，处理括号中表达式 *}
                       then begin
                              insymbol;
                              expression(fsys + [rparent],x); {* expression递归处理括号中表达式 *}
                              if sy = rparent
                              then insymbol
                              else error(4)
                            end
                  else if sy = notsy {* sy是逻辑非关键字 *}
                       then begin
                              insymbol;
                              factor(fsys,x); {* 递归调用factor *}
                              if x.typ = bools {* x的type为布尔型，生成NOT:逻辑非指令 *}
                              then emit(35)
                              else if x.typ <> notyp {* x是其他type，报错 *}
                                   then error(32)
                            end;
                  test(fsys,facbegsys,6)
                end { while }
            end { factor };
          begin { term   } {*项*}
            factor( fsys + [times,rdiv,idiv,imod,andsy],x); {* 利用factor分析[]中因子 *}
            while sy in [times,rdiv,idiv,imod,andsy] do{*因子和因子之间用* /  DIV MOD AND*}
              begin
                op := sy;
                insymbol;
                factor(fsys+[times,rdiv,idiv,imod,andsy],y ); {* 处理二元操作的第二个操作数 *}
                if op = times {* 操作符为乘号 *}
                then begin
                       x.typ := resulttype(x.typ, y.typ);
                       case x.typ of
                         notyp: ;
                         ints : emit(57); {* MUL：整形乘 *}
                         reals: emit(60); {* MUR：实型乘 *}
                       end
                     end
                else if op = rdiv {* 操作符为实型除 *}
                     then begin
                            if x.typ = ints {* x强制转换为实型 *}
                            then begin
                                   emit1(26,1);
                                   x.typ := reals;
                                 end;
                            if y.typ = ints {* y强制转换为实型 *}
                            then begin
                                   emit1(26,0);
                                   y.typ := reals;
                                 end;
                            if (x.typ = reals) and (y.typ = reals)
                            then emit(61) {* DIR: 实型除 *}
                            else begin {* x或y为notyp或出错 *}
                                   if( x.typ <> notyp ) and (y.typ <> notyp)
                                   then error(33);
                                   x.typ := notyp
                                 end
                          end
                else if op = andsy {* 操作符为与操作 *}
                     then begin
                            if( x.typ = bools )and(y.typ = bools) {* x与y必须为bools *}
                            then emit(56) {* AND：逻辑与 *}
                            else begin {* x或y为notyp或出错 *}
                                   if( x.typ <> notyp ) and (y.typ <> notyp)
                                   then error(32);
                                   x.typ := notyp
                                 end
                          end
                else begin { op in [idiv,imod] } {* 操作符为整型除或整型取模 *}
                       if (x.typ = ints) and (y.typ = ints)
                       then if op = idiv
                            then emit(58) {* DIV：整数除 *}
                            else emit(59) {* MOD: 取模 *}
                       else begin {* x或y为notyp或出错 *}
                              if ( x.typ <> notyp ) and (y.typ <> notyp)
                              then error(34);
                              x.typ := notyp
                            end
                     end
              end { while }
          end { term };
        begin { simpleexpression } {* 开始处理简单表达式 *}
          if sy in [plus,minus] {* sy是正负号 *}
          then begin
                 op := sy;
                 insymbol;
                 term( fsys+[plus,minus],x); {* 处理项 *}
                 if x.typ > reals
                 then error(33)
                 else if op = minus {* 减号取相反数，MUS：求负 *}
                      then emit(36)
               end
          else term(fsys+[plus,minus,orsy],x);
          while sy in [plus,minus,orsy] do {* 循环处理出现的+，-，or *}
            begin
              op := sy;
              insymbol;
              term(fsys+[plus,minus,orsy],y);
              if op = orsy {* 处理or *}
              then begin
                     if ( x.typ = bools )and(y.typ = bools)
                     then emit(51) {* ORR：逻辑或 *}
                     else begin
                            if( x.typ <> notyp) and (y.typ <> notyp)
                            then error(32);
                            x.typ := notyp
                          end
                   end
              else begin {* 处理算术运算符+- *}
                     x.typ := resulttype(x.typ,y.typ); {* 得到类型转换后的值 *}
                     case x.typ of
                       notyp: ;
                       ints: if op = plus
                             then emit(52) {* ADD *}
                             else emit(53); {* SUB *}
                       reals:if op = plus
                             then emit(54) {* ADR *}
                             else emit(55) {* SUR *}
                     end { case }
                   end
            end { while }
          end { simpleexpression };
        begin { expression  }
        simpleexpression(fsys+[eql,neq,lss,leq,gtr,geq],x);
        if sy in [ eql,neq,lss,leq,gtr,geq] {* sy是数值比较符 *}
        then begin
               op := sy;
               insymbol;
               simpleexpression(fsys,y);
               if(x.typ in [notyp,ints,bools,chars]) and (x.typ = y.typ)
               then case op of {* 操作数类型一致且不为实型才开始比较，
               以下所有PCODE都只针对整型 *}
                      eql: emit(45); {* EQL *}
                      neq: emit(46); {* NEQ *}
                      lss: emit(47); {* LSS *}
                      leq: emit(48); {* LEQ *}
                      gtr: emit(49); {* GRT *}
                      geq: emit(50); {* GEQ *}
                    end
                else begin {* 操作数类型不一致则做转换 *}
                       if x.typ = ints {* 转换x为reals *}
                       then begin
                              x.typ := reals;
                              emit1(26,1)
                            end
                       else if y.typ = ints {* 转换y为reals *}
                            then begin
                                   y.typ := reals;
                                   emit1(26,0)
                                 end;
                       if ( x.typ = reals)and(y.typ=reals)
                       then case op of {* 针对实型数值比较运算的PCODE *}
                              eql: emit(39); {* EQR *}
                              neq: emit(40); {* NER *}
                              lss: emit(41); {* LSR *}
                              leq: emit(42); {* LER *}
                              gtr: emit(43); {* GTR *}
                              geq: emit(44); {* GER *}
                            end
                       else error(35)
                     end;
               x.typ := bools {* 将返回的x值变成bools}
             end
        end { expression };

    {* 处理赋值语句 *}
    procedure assignment( lv, ad: integer );
      var x,y: item;
          f  : integer;
      begin   { tab[i].obj in [variable,prozedure] } {*当且仅当当前符号表的目标类型为变量或者过程型时*}
        x.typ := tab[i].typ;
        x.ref := tab[i].ref;
        if tab[i].normal
        then f := 0
        else f := 1;
        emit2(f,lv,ad);
        if sy in [lbrack,lparent,period] {* 出现左括号和.，变量为数组或记录 *}
        then selector([becomes,eql]+fsys,x);
        if sy = becomes {* 读到赋值符号，之后开始赋值 *}{*赋值符号并容错*}
        then insymbol 
        else begin
               error(51);
               if sy = eql {* 等号容错 *}
               then insymbol
             end;
        expression(fsys,y); {* 获得赋值符号右侧值 *}
        if x.typ = y.typ {* 左右类型相等的处理 *}
        then if x.typ in stantyps {* x不是数组变量也不是记录变量，就是普通的变量 *}
             then emit(38) {* STO：栈顶赋值到次栈顶 *}
             else if x.ref <> y.ref {* x，y是记录变量但ref与y不等，说明不是同一个记录类型 *}
                  then error(46)
             else if x.typ = arrays {* x是数组 *}
                  then emit1(23,atab[x.ref].size) {* CPB atab[x.ref].size：拷贝atab中的项 *}
             else emit1(23,btab[x.ref].vsize) {* x是记录且记录类型与y一样 CPB atab[x.ref].size：拷贝btab中的项 *}
        else if(x.typ = reals )and (y.typ = ints) 
        then begin
               emit1(26,0); {* FLT 0 *}{* y类型需转换为reals *}
               emit(38) {* STO *} {*赋值*}
             end
        else if ( x.typ <> notyp ) and ( y.typ <> notyp ) 
             then error(46)
      end { assignment };

    {* 处理带begin end的混合语句 *}
    procedure compoundstatement;
      begin
        insymbol;
        statement([semicolon,endsy]+fsys); {* 处理一句语句 *}
        while sy in [semicolon]+statbegsys do {* sy是分号或者statbegsys就循环处理混合语句 *}
          begin
            if sy = semicolon
            then insymbol
            else error(14);
            statement([semicolon,endsy]+fsys) {* 处理一句语句 *}
          end;
        if sy = endsy {* 遇到end，说明当前层正常结束 *}
        then insymbol
        else error(57)
      end { compoundstatement };

    {* 处理if语句 *}
    procedure ifstatement;
      var x : item;
          lc1,lc2: integer;
      begin
        insymbol;
        expression( fsys+[thensy,dosy],x); {* 将if到then或到do之前的内容丢进expression处理 *}
        if not ( x.typ in [bools,notyp]) {* 得到的应该是布尔值，否则报错 *}
        then error(17);
        lc1 := lc;
        emit(11);  { jmpc } {* JPC:若x为假，跳转到y域对应地址 *}
        if sy = thensy {* 处理then之后内容 *}
        then insymbol
        else begin {* 没加then，报错，对do进行容错 *}
               error(52);
               if sy = dosy
               then insymbol
             end;
        statement( fsys+[elsesy]); {* 处理到else之前ifthen的代码 *}
        if sy = elsesy {* 处理else内代码 *}
        then begin
               insymbol;
               lc2 := lc;
               emit(10); {* JMP:无条件跳转到y域对应地址 *}
               code[lc1].y := lc; {*设置if指令跳转的位置*}
               statement(fsys); {*处理else条件下的分析语句*}
               code[lc2].y := lc  {*设置else指令跳转的位置*}
             end
        else code[lc1].y := lc {*设置if指令跳转的位置*}
      end { ifstatement };{*设置if指令跳转的位置*}

    {* 处理case语句 *}
    procedure casestatement;
      var x : item;
          i,j,k,lc1 : integer;
          casetab : array[1..csmax]of {* 限制case表个数为csmax *}
                     packed record
                       val,lc : index
                     end;
          exittab : array[1..csmax] of integer;

      {* 处理case语句中的标号，将各标号对应的目标代码入口地址填入casetab中，并检查标号有无重复定义 *}
      procedure caselabel;
        var lab : conrec;
            k : integer;
        begin
          constant( fsys+[comma,colon],lab );{*处理常量(因为标签都是常量)*}
          if lab.tp <> x.typ {*如果获得的标签类型和变量的类型不符*}
          then error(47){*case语句中标号必须是与case子句表达式类型相同的常量*}
          else if i = csmax {*如果可以声明的case达到了最大限度。爆栈报错并终止程序 *}
               then fatal(6)
          else begin
                 i := i+1; {casestatement的变量i为case总数。得到新case，移动case指针 *}
                 k := 0; {* 用来检查标号是否重复定义的变量 *}
                 casetab[i].val := lab.i; {* 保存新case值（即i指针） *}
                 casetab[i].lc := lc; {* 记录新case生成代码的位置 *}
                 repeat
                   k := k+1
                 until casetab[k].val = lab.i; {* 扫一遍已经声明的label,看有没有重复声明 *}
                 if k < i {* 出现重复声明，报错 *}
                 then error(1); { multiple definition }
               end
        end { caselabel };

      {* 处理case语句的一个分支 *}
      procedure onecase;
        begin
          if sy in constbegsys {* case之后必须是常量 *}
          then begin
                 caselabel; {* 获取一个标签 *}
                 while sy = comma do {* 如果有逗号说明是一个case对应多个标签的情况，则继续处理下个标签 *}
                   begin
                     insymbol;
                     caselabel
                   end;
                 if sy = colon {* 读到冒号说明label声明结束，读到其他符号，报错 *}
                 then insymbol
                 else error(5);
                 statement([semicolon,endsy]+fsys); {* 处理冒号到分号间内容 *}
                 j := j+1; {* 用来记录当前case对应exittab的位置 *}
                 exittab[j] := lc; {* 记录当前case分支结束的代码位置,即下面将要生成的跳转指令的位置 *}
                 emit(10) {* JMP: 生成一条跳转指令来结束这一case分支 *}
               end
          end { onecase };

      begin  { casestatement  }
        insymbol;
        i := 0;
        j := 0;
        expression( fsys + [ofsy,comma,colon],x ); {* 处理到of *}
        if not( x.typ in [ints,bools,chars,notyp ]) {* 若结果不是以上四种类型则报错 *}
        then error(23);
        lc1 := lc; {* 记录当前PCODE代码的位置指针 *}
        emit(12); {jmpx} {* SWT:查找情况表,注意这里暂时没有给定跳转的地址 *}
        if sy = ofsy
        then insymbol
        else error(8);
        onecase; {* 处理一个分支 *}
        while sy = semicolon do {* 循环处理之后所有分支 *}
          begin
            insymbol;
            onecase
          end;
        code[lc1].y := lc;
        for k := 1 to i do {* 遍历所有分支，建立情况表 *}
          begin
            emit1( 13,casetab[k].val); {* CAS：设置查找的值 *}
            emit1( 13,casetab[k].lc); {* CAS：给出对应的跳转地址 *}
          end;
        emit1(10,0); {* JMP：说明情况表结束 *}
        for k := 1 to j do {* 给定每个case分支退出之后的跳转地址 *}
          code[exittab[k]].y := lc; {* 现在的lc指向情况表结束之后的位置，
          将各分支的结束跳转地址指向这里 *}
        if sy = endsy {* endsy以示结束 *}
        then insymbol
        else error(57)
      end { casestatement };

    {* 处理repeat语句 *}
    procedure repeatstatement;
      var x : item;
          lc1: integer; {* 用来记录repeat的开始位置 *}
      begin
        lc1 := lc; {* 保存repeat当开始时的代码地址 *}
        insymbol;
        statement( [semicolon,untilsy]+fsys); {* 调用statement递归子程序来处理循环体中的语句 *}
        while sy in [semicolon]+statbegsys do
        {* 如果遇到了分号或者statement的开始符号,则说明循环体中还有语句没有处理完 *}
          begin
            if sy = semicolon
            then insymbol
            else error(14);
            statement([semicolon,untilsy]+fsys) {* 处理循环体下一条语句 *}
          end;
        if sy = untilsy {* 遇到until，处理until内内容 *}
        then begin
               insymbol;
               expression(fsys,x);
               if not(x.typ in [bools,notyp] ) {* until内内容必须为bools，否则报错 *}
               then error(17);
               emit1(11,lc1);
             end
        else error(53)
      end { repeatstatement };

    {* 处理while语句 *}
    procedure whilestatement;
      var x : item;
          lc1,lc2 : integer;
      begin
        insymbol;
        lc1 := lc;
        expression( fsys+[dosy],x); {* 处理do之前内容 *}
        if not( x.typ in [bools, notyp] ) {* 内容必须为bools *}
        then error(17);
        lc2 := lc;
        emit(11); {* JPC *}
        if sy = dosy {* do之后正常处理 *}
        then insymbol
        else error(54);
        statement(fsys); {* 正常处理 *}
        emit1(10,lc1); {* JMP lc1：往回跳转 *}
        code[lc2].y := lc
      end { whilestatement };

    {* 处理for语句 *}
    procedure forstatement;
      var cvt : types; {*循环变量的类型*}
          x :  item;
          i,f,lc1,lc2 : integer;
      begin
        insymbol;
        if sy = ident {* for语句开头是标识符 *}
        then begin
               i := loc(id);  {* 从tabs中获得计数变量标识符 *}
               insymbol;
               if i = 0 {* 找不到标识符则计数变量，将计数变量类型默认处理为整形 *}
               then cvt := ints
               else if tab[i].obj = vvariable {* 对应的这个标识符对应符号是变量类型，正常处理 *}
                    then begin
                           cvt := tab[i].typ;
                           if not tab[i].normal {* 如果是变量形参，则报错 *}
                           then error(37)
                           else emit2(0,tab[i].lev, tab[i].adr ); {* 如果不是变量形参，
                              获取该符号的地址 *}
                           if not ( cvt in [notyp, ints, bools, chars])
                           then error(18)
                         end
              else begin {* 符号的类型也不是变量，报错并将计数变量类型设置为整型 *}
                     error(37);  {*应该是变量类型*}
                     cvt := ints
                     end
             end
        else skip([becomes,tosy,downtosy,dosy]+fsys,2); {* for语句开头不是标识符或什么也没有，
          跳过出错内容 *}
        if sy = becomes {* 读到赋值符号，给计数器赋初值 *}
        then begin
               insymbol;
               expression( [tosy, downtosy,dosy]+fsys,x); {*for a:= 6 + 1 to 10 do ... 此处处理6+1并将结果的类型返回*}
               if x.typ <> cvt {*如果和a 的类型不符,则报错*}
               then error(19);
             end
        else skip([tosy, downtosy,dosy]+fsys,51); {* 没出现赋值符号，跳过出错内容 *}
        f := 14; {* F1U *}
        if sy in [tosy,downtosy] {* 读到to或downto，加步伐大小 *}
        then begin
               if sy = downtosy {* downto就 F1D：减步伐大小 *}
               then f := 16;
               insymbol;
               expression([dosy]+fsys,x); {* 处理到do之前 *}
               if x.typ a<> cat
               then error(19)
             end
        else skip([dosy]+fsys,55); {* 没出现to或downto，跳过出错内容 *}
        lc1 := lc; {* 记录下句F1U指令的位置 *}
        emit(f); {* 生成f对应PCODE *}
        if sy = dosy {* 找到do *}
        then insymbol
        else error(54);
        lc2 := lc; {* 获取循环体开始代码的位置 *}
        statement(fsys); {* 处理循环体语句 *}
        emit1(f+1,lc2); {* 结束时生成F2U或F2D指令 *}
        code[lc1].y := lc {* 将之前产生的F1U的跳转地址回传回去 *}
      end { forstatement };

    {* 处理标准过程（输入、输出） *}
    procedure standproc( n: integer );
      var i,f : integer;
          x,y : item;
      begin
        case n of
          1,2 : begin { read }
                  if not iflag {* input flag为true才开始读入 *}
                  then begin
                         error(20); {*程序头部未包含参数“output”或“input”*}
                         iflag := true
                       end;
                  if sy = lparent {* 左括号后为读入内容 *}
                  then begin
                         repeat
                           insymbol;
                           if sy <> ident {* 读入参数应该为标识符 *}
                           then error(2)
                           else begin
                                  i := loc(id);
                                  insymbol;
                                  if i <> 0
                                  then if tab[i].obj <> vvariable
                                       then error(37)
                                       else begin
                                              x.typ := tab[i].typ;
                                              x.ref := tab[i].ref;
                                              if tab[i].normal {*指令处理过程中其操作的都是地址,装载值则直接将地址赋值,加载地址则根据地址再去找值*}
                                                then f := 0 {*对值形参加载地址,根据地址索引到S中的值,所以为加载地址*}
                                              else f := 1; {*对变量形参加载值,即 a=adr,所以加载的地址直接赋值就好了,即指令为加载值*}
                                              emit2(f,tab[i].lev,tab[i].adr);
                                              if sy in [lbrack,lparent,period]
                                              then selector( fsys+[comma,rparent],x);
                                              if x.typ in [ints,reals,chars,notyp]
                                                then emit1(27,ord(x.typ)) {* 若n = 1，读入1个字符 *}
                                                else error(41) {*read或write的参数类型不正确*}
                                            end
                                end;
                           test([comma,rparent],fsys,6);
                         until sy <> comma;
                         if sy = rparent {* 右括号表示结束 *}
                         then insymbol
                         else error(4)
                       end;
                  if n = 2
                  then emit(62) {* 读入一行 *}
                end;
          3,4 : begin { write }
                  if sy = lparent {* 左括号后为写入内容 *}
                  then begin
                         repeat {* 循环读字符串，可用逗号分割 *}
                           insymbol;
                           if sy = stringcon  {*如果是字符串类型*}
                           then begin
                                  emit1(24,sleng); {* LDC sleng：装入sleng字面常量 *}
                                  emit1(28,inum); {* WRS inum：写字符 *}
                                  insymbol
                                end
                           else begin {* 写入内容带表达式 *}
                                  expression(fsys+[comma,colon,rparent],x); {* 处理到逗号、冒号或右括号 *}
                                  if not( x.typ in stantyps ) {* x不可为数组或记录 *}
                                  then error(41);
                                  if sy = colon {*如果出现冒号继续处理,每一个输出参数后面有冒号的话,说明有格式化操作*}
                                  then begin
                                         insymbol;
                                         expression( fsys+[comma,colon,rparent],y);
                                         if y.typ <> ints  {*一个冒号后面代表输出几个字符*}
                                         then error(43); {*write语句的域宽应该是整型*}

                                         if sy = colon
                                         then begin
                                                if x.typ <> reals {*该表达式应该是实型*}
                                                then error(42);
                                                insymbol;
                                                expression(fsys+[comma,rparent],y);
                                                if y.typ <> ints
                                                then error(43);
                                                emit(37) {* 写实数，给定位宽 *}
                                              end
                                         else emit1(30,ord(x.typ)) {* 写，给定位宽 *}
                                       end
                                  else emit1(29,ord(x.typ)) {* 写，隐含位宽 *}
                           end
                         until sy <> comma;
                         if sy = rparent {* 右括号代表结束 *}
                         then insymbol
                         else error(4)
                       end;
                  if n = 4
                  then emit(63) {* 写一行 *}
                end; { write }
        end { case };
      end { standproc } ;
{*处理分析语句*}
    begin { statement }
      if sy in statbegsys+[ident]
      then case sy of {* 根据不同sy类型决定使用哪种statement子过程处理 *}
             ident : begin
                       i := loc(id);
                       insymbol;
                       if i <> 0
                       then case tab[i].obj of
                              konstant,typel : error(45);  {*无法对常量进行赋值操作*}
                              vvariable:     assignment( tab[i].lev,tab[i].adr); {*对变量则处理赋值语句*}
                              prozedure:       if tab[i].lev <> 0
                                               then call(fsys,i)
                                               else standproc(tab[i].adr);
                              funktion:        if tab[i].ref = display[level]
                                               then assignment(tab[i].lev+1,0)
                                               else error(45) {*对函数标识符也进行赋值操作？？？？*}
                            end { case }
                     end;
             beginsy : compoundstatement;
             ifsy    : ifstatement;
             casesy  : casestatement;
             whilesy : whilestatement;
             repeatsy: repeatstatement;
             forsy   : forstatement;
           end;  { case }
      test( fsys, [],14);
    end { statement };
```



### 3. 文件组织

课程组所给Pascal编译器代码均处在同一个word文件中，文件组织上较为耦合。事实上，可以为每一个过程/函数置入新的文件，以更清晰地了解编译器的结构和组织。大体来说，该编译器遵循文法分析、语法分析、错误处理、语义分析、代码生成和代码优化的模块化设计理念。

## 二、编译器总体设计

### 1. 总体结构

本编译器的总贴结构分为前端(frontend)，中端(middle)和后端(backend)三个部分。

其中，前端负责词法分析和语法分析，并在语法分析时进行错误处理以及部分语义分析；

中端负责符号表生成、中间代码生成、语义分析和部分代码优化；

后端负责最终代码（mips）生成以及部分代码优化。

### 2. 接口设计

#### 2.1 前端

- `TokenList`：经词法分析后得到的源码单词串
- `TokenListIterator`：单词迭代器，对`TokenList`的迭代器，用于语法分析
- `%Parser`：各种`parser`如`ConseDeclParser`等，负责各个语法成分的解析。
- 各种为了词法分析和语法分析处理方便而设计的类，主要是对文法成分进行模拟

#### 2.2 中端

- `ErrorTable`：错误表
- `Symbol`：符号
- `SymbolTable`：符号表，用于语义分析
- `IrBuilder`：中间代码生成器，用于对词法分析和语法分析处理后利用语法树生成中间代码
- `Ir%Builder`：各种`builder`，如`IrFunctionBuilder`，用于生成中间代码的各种组成部分
- `IrUse`：用于进行数据流分析
- 各种为了填表/错误处理/中间代码生成而设计的类，主要是对中间代码的结构的模拟

#### 2.3 后端

- `MipsBuilder`：Mips代码生成器，用于将中间代码翻译为Mips代码
- `RegisterFile`：寄存器表，用于保存编译时的寄存器分配情况
- `MipsSymbolTable`：Mips符号表，用于保存中间代码和mips的映射关系
- `Add`等各种Mips指令
- `MipsFunctionBuilder`等各种`builder`，用于按照树状结构翻译中间代码
- `MipsBasicBlock`：Mips基本块，用于进行数据流分析和代码优化

### 3. 文件组织

#### 3.1 `frontend`前端

```
├─lexer 词法分析
└─parser 语法分析
    ├─declaration 声明
    │  ├─constant 常量
    │  │  └─constinitval 常量初值
    │  └─variable 变量
    │      ├─initval 变量初值
    │      └─vardef 变量声明
    ├─expression 表达式
    │  ├─multiexp 多项式表达式
    │  ├─primaryexp 基本表达式
    │  └─unaryexp 一元表达式
    ├─function 函数
    │  └─functype 函数类型
    ├─statement 语句
    │  ├─blockitem 基本块元素
    │  └─stmt 语句
    └─terminal 终结符
```

#### 3.2 `middle`中端

```
├─error 错误处理
├─llvmir LLVM IR中间代码
│  ├─type 中间代码类型
│  └─value 中间代码类
│      ├─basicblock 基本块
│      ├─constant 常量
│      ├─function 函数
│      ├─globalvariable 全局常变量
│      └─instructions 中间代码指令
│          ├─memory 内存指令
│          └─terminator 终结指令
└─symbol 符号
```

#### 3.3 `backend`后端

```
├─basicblock 基本块
├─function 函数
├─instruction Mips命令
└─symbol Mips符号
```

## 三、词法分析设计

> 会随着项目的部分或全部重构而更新

### 1. 编码前的设计

在项目结构方面，词法分析属于前端内容，将其目录`lexer`置于`src/frontend`下，为后续架构预留空间；一共设计了五个类`SourceFileLexer`、`Token`、`TokenType`、`TokenList`和`TokenType`，其中，`SourceFileLexer`置于`src/frontend`目录下，其余四个类置于`src/frontend/lexer`下。

整体上，`SourceFileLexer`提供对源文件的文法分析工具；`lexer`目录提供对输入文件的解析，最终生成一个`TokenList`对象并包含必要信息，为后续语法分析等工作做好准备，具体地，`TokenType`是一个枚举类，包括了所有的单词类别及对应正则表达式，`Token`设计了单词类，包括单词类别`TokenType`属性，`conotent`单词内容属性和所在行属性，`TokenList`事实上封装了一个`ArrayList<Token>`容器对象，`TokenLexer`类接收`SourceFileLexer`并解析出一系列`Token`，最终生成`TokenList`对象提供给外界调用。以下按照目录级别从浅到深依次具体介绍。

### 2. 编码实现

#### 2.1 `SourceFileLexer`源文件词法分析器

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

#### 2.2 `lexer`词法分析器package

`lexer`是词法分析器的核心所在目录，其内部四个类相互配合，完成了对源文件的词法解析。以下介绍以低依赖向高依赖的顺序。

##### 2.2.1 `TokenType`单词类别枚举类

###### 2.2.1.1 设计思路

在SysY语言中有有限种类的单词类别，他们是常量但应用广泛，Java为此提供了绝佳的实现方案：枚举类，这是一个完备的类，有诸如构造器、属性、方法等类的要素。本编译器充分结合了枚举类的特性与SysY语言的特性。具体而言，为不同单词种类设计了不同的枚举对象，并区分了是否需要贪婪匹配，对外提供查询方法和打印方法。

对于保留字的尝试匹配应该在第一优先级，如`main`等；对于双字符运算符的匹配应当优先于单字符运算符，特别是具有前缀依赖关系的（如`==`和`=`）

###### 2.2.1.2 属性

- `isGreed`：是否贪婪匹配，对于保留关键字如`main`等，此值为真，其余为假
- `patternString`：正则表达式字符串
- `pattern`：`Pattern`对象，为按照上述两个属性编译后得到的对象

###### 2.2.1.3 实现功能

- `getPattern`：获取`Pattern`对象
- `toString`：打印对象名（如`MAINTK`），为词法分析作业服务

##### 2.2.2 `Token`单词对象

###### 2.2.2.1 设计思路

为了将单词这一重要基本元素恰当地表达，本编译器将其封装为一个类，包含单词类别、所在行、内容等属性，并对外提供`get`方法。

###### 2.2.2.2 属性

- `type`：`TokenType`对象，记录本单词属于的单词类别
- `lineNum`：记录本单词所在行号，从1开始
- `content`：单词内容

###### 2.2.2.3 方法

主要为`get`和`set`。

##### 2.2.3 `TokenList`单词表

事实上该类属性只有`ArrayList<Token>`容器对象，但是选择将其封装为一个整体，一方面在含义上正确且低耦合，另一方面拜托了对Java容器类别的依赖，如果后续更改真实的容器也不需要更改外部代码，只需要更改内部代码，符合SOLID原则。

###### 2.2.3.1 属性

- `tokens`：单词表容器对象

###### 2.2.3.2 方法

主要为`get`，`set`和`toString`

##### 2.2.4`TokenLexer`词法分析器

###### 2.2.4.1设计思路

本类是词法分析器的核心逻辑。具体地，传入`SourceFileLexer`对象，通过调用其提供的各种特权指令，解析出每个有效单词并最终生成`TokenList`对象并对外提供，整体接口较为简洁。

实现上有几个需要注意的点：

- 循环节开始，应当首先跳过空白字符和注释，跳过一次注释后应当`continue`，因为存在连续注释的情况。
- 需要正确计算调用特权指令时传入的参数，具体地，对于遇到注释时调用`moveForward`，需要仔细计算应当跳过几个字符。

###### 2.2.4.2 属性

- `sourceFileLexer`：`SourceFileLexer`对象，提供对抽象的源文件的特权解析指令
- `tokenList`：单词表对象

###### 2.2.4.3 方法

主要为内部使用方法，对外提供方法主要为`getTokenList`

- `tokenize`：解析函数，核心逻辑
- `skipWhhiteSpace`：跳过空白字符
- `skipComment`：如果下一部分为注释则跳过并返回真，否则返回假
- `addToken`：遍历`TokenType`枚举类的对象，尝试匹配下一个有效单词（这里需要注意枚举类对象的先后顺序，如`==`应当出现在`=`前）

## 四、语法分析设计

### 1. 编码前的设计

在项目结构方面，语法分析属于前端内容，将其目录`parser`置于`src/frontend`下，为后续架构预留空间。在其下设计了四个package：`function`、`declaration`、`statement`和`expression`，分别对应文法定义说明文档中的函数、变量/常量声明、语句和表达式四个类别，完成了文法的初步解耦。

#### 1.1 语法树结点类

本部分的一个核心设计思想是：每一个非终结符都有其对应的类/接口并对外提供方法，自己实现的优化/解耦不能取代这些类/接口。

##### 1.1.1 实现顺序

按照类之间的依赖关系，首先实现`expression`中的类，其中的类之存在相互依赖关系而不对其他三个包的类存在依赖关系；接着实现`declration`中的类，其中对外部类的依赖都在`expression`中；然后实现`statement`类，其中对外部类的依赖都在`expression`和`statement`中；最后实现`function`中的类。

##### 1.1.2 实现技巧

- 对于`Expression`中的`AddExp`等结构高度相似且包含左递归的非终结符，通过改写其文法去除左递归使得可以使用递归下降法进行解析；同时，利用其结构上的高度相似，为其设计统一超类`MultiExp`，提高代码复用率。
- 对于`Stmt`、`UnaryExp`等文法右侧声明种类较多的非终结符，为其设计统一的`<Class>Ele`接口，并为每一种声明设计单独的非终结符类，统一实现该接口，并在原非终结符中通过保存其`Ele`对象来实现架构的完整性。

#### 1.2 语法树结点解析器类

本部分的一个核心设计思想是：为每一个非终结符设计对应的解析器类，也对部分自行设计的“非终结符”（主要是文法声明右侧种类较多时）设计对应的解析器类，主要包含`Token`迭代器`TokenListIterator`，非终结符的各种组成对象。

`<Class>Parser`类对外提供`Parse<Class>`方法来解析当前内容，同时提供`syntaxOutput`方法来实现满足语法要求的解析。

对于每个`Parser`类的`parse<Class>`方法，统一规定其进入时通过`TokenListIterator`获得的下一个`Token`为其组成元素的第一个。一方面，这简化了我们进入`parse<Class>`方法时的思维复杂度，但另一方面，将该部分代价转移到每个`parse<Class>`方法的实现中，有舍有得。

### 2. 编码实现——`parser`语法分析器package

#### 2.1 `expression`表达式声明类别package

##### 2.1.1.1 `multiexp`

包含`AddExp,EqExp,LAndExp,LOrExp,MulExp,MultiExp,RelExp`类及其对应的`Parser`类，其中除`MultiExp`外的`_Exp`类均为继承`MultiExp`。

在`MultiExp`中实现了消除左递归文法后的语法树结构，具体地，通过`first`对象和`operators`与`operands`列表来表达一个语法树节点。在输出上，由于输出的特点，可以通过依次打印`first`与后续`operator`和`operand`来实现满足文法要求的输出。

##### 2.1.1.2 `pimaryexp`

包含`LVal, Number, PrimaryExp`这三个文法中的非终结符以及为了架构所人为构建的语法节点类`PrimaryExpExp`和他们对应的`Parser`类，其实现了`PrimaryExp`文法声明右侧`'('<Exp>')'`的部分，并且其中`Number, LVal, PrimaryExpExp`三个类都继承了`PrimaryExpEle`接口，表明其分别可以独立地成为`PrimaryExp`的一个声明。

##### 2.1.1.3 `unaryexp`

包含`UnaryExp, UnaryOp`这两个文法中的非终结符以及为了架构所人为构建`UnaryExpFunc, UnaryExpOp`类和他们对应的`Parser`类。两个人为构造的类实现了`UnaryExpEle`接口，表明其分别可以独立地成为`UnaryExp`的一个声明。特别地，在上面`primaryexp`中的`PrimaryExp`也实现了这一接口，表明其也是`UnaryExp`的一个声明。`UnaryOp`十分简单，但为了保证架构的完整性和鲁棒性，我们依旧将其设计为了单独的类。

##### 2.1.1.4 `Cond, ConstExp, Exp, FuncRParams`

这些非终结符结构相对单一（文法声明右侧只有一种可能性），因此没有将其单独置入package。

#### 2.2 `declaration`变量/常量声明类别package

##### 2.2.1 `constant`

包含`constinitval`package和`ConstDecl, ConstDef`类。

在`constinitval`package中，人为构造了`ConstInitValMulti`类，并使其实现`ConstInitValEle`接口。同时，由于文法表达，上面实现过的`ConseExp`也实现这一接口。从这里也可以一窥我们实现顺序的优越之处。

在`ConstDef, ConstDecl`类的文法声明结构较为单一，因此没有为其设置单独的package。

##### 2.2.2 `variable`

包含`initval, vardef`两个package和`VarDecl`及其对应的`Parser`类。

在`initval`package中，人为设计了`InitVals`类并实现`InitValEle`接口，对应`InitVal`文法中多初值的情况。其中，由`Exp`实现了`InitValEle`接口，因为其也是`InitVal`文法声明右侧的元素之一。

在`vardef`中，人为设计了`VarDefInit, VarDefNull`两个类并实现`VarDefEle`接口，分别对应有初值声明和无初值声明两种情况。

`VarDecl`类结构较为简单，并未对其单独设置`package`

##### 2.2.3 `BType, Decl`

这两个非终结符同样文法声明较为简单，未为其单独设置类。

#### 2.3 `statement`语句类别package

##### 2.3.1 `blockitem`

设置接口`BlockItemEle`，并使`Decl`和`Stmt`实现这个接口。

##### 2.3.2 `stmt`

这部分代码量很大，但是逻辑却并不复杂。具体地，人为实现了`StmtAssign, StmtBlock, StmtBreak, StmtCond, StmtContinue, StmtExp, StmtGetint, StmtNull, StmtPrint, StmtReturn, StmtWhile`这几种类，分别对应`Stmt`非终结符的一种文法声明，并使其实现`StmtEle`接口来将其作为`Stmt`的元素统一管理。特别地，`Block`也因其是`Stmt`文法声明的一种可能而实现该接口。

##### 2.3.3 `Block`

实现了`Block`类和其对应的`Parser`

#### 2.4 `function`函数类别package

##### 2.4.1 `functype`

人为实现了`FuncTypeInt, FuncTypeVoid`类并实现`FuncTypeEle`接口，为两种函数类型。

##### 2.4.2 `FuncDef, FuncFParam, FuncFParams, MainFuncDef`

其文法声明结构较为单一，未为其单独设置package。

#### 2.5 `terminal`终结符号类别

在官方文法说明手册中，`FormatString, Ident, IntConst`并非作为非终结符进行解释，而是通过自然语言进行描述。然而，其在文法中的地位不可或缺，因此为其单独设置package，以保证架构的完整性和鲁棒性。

#### 2.6 `CompUnit`类

作为整体编译单元，该类是一个外部包裹类。

#### 2.7 `SyntaxNode`接口

由于语法输出的要求，为每一个非终结符都实现了该接口。具体地，该接口中包含一个抽象方法，即`syntaxOutput`方法，每个直接或简介实现该接口的类都需要实现该方法。这一方法返回一个`String`对象，这使得我们将语法解析与语法作业输出相解耦。具体地，流程是首先进行语法解析，在这过程中同时建立语法树，最后可以通过调用`CompUnit`的`syntaxOutput`方法实现递归获得语法输出。虽然这使得我们的代码量上升了一个数量级，但是考虑到解耦带来的结构的架构的清晰性，我们认为这是值得的。

## 五、错误处理设计

### 1. 编码前的设计

首先，考虑到错误处理的两大类型：语义不相关和语义相关，我们采用将其耦合入递归下降语法分析的过程中的解决方案。同时，由于其中语义相关的错误需要符号表的支持（如名字重定义等错误），我们在这一阶段部分地实现项目中端，即符号和符号表。

#### 1.1 错误处理

##### 1.1.1 语义不相关错误

| 错误类型     | 错误类别码 | 解释                                                         |
| ------------ | ---------- | ------------------------------------------------------------ |
| 非法符号     | a          | 格式字符串中出现非法字符，报错行号为**<FormatString>**所在行数 |
| 缺少分号     | i          | 报错行号为分号**前一个非终结符**所在行号                     |
| 缺少右小括号 | j          | 报错行号为右小括号**前一个非终结符**所在行号                 |
| 缺少右中括号 | k          | 报错行号为右中括号**前一个非终结符**所在行号                 |

以上四类报错的特点是他们不和语义相关，只是违背了文法或语义不相关的约束。

##### 1.1.2 语义相关错误

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

#### 1.2. 符号

在词法和语法分析中，我们更多地将输入文件的源代码作为割裂的字符串来处理，但在这个阶段，我们开始设计符号类，并在非终结符语法分析过程中生成符号对象并加入符号表进行统一管理。

具体地，每个符号（符号表表项）由名字和属性组成。

#### 1.3. 符号表

是符号的封装管理容器

### 2. 编码实现

#### 2.1 `Error`类

设计实现`Error`类，主要记录错误类别`ErrorType`、行号`lineNume`这两个评测时需要打印输出的内容。

#### 2.2 `ErrorType`类

错误类型类，主要记录错误所属不同类别，由`a`到`m`。

#### 2.3 `ErrorTable`

错误表格类，主要记录发生的各种错误，用于输出评测。

#### 2.4 语法分析中耦合具体错误处理语句

这一部分比较零碎，主要根据文法结构进行处理和判断，举例而言，在`MainFuncDefParser`解析`main`函数时，可能遇到`j`类和`g`类错误，在合适的位置分别插入处理函数。

```java
    private void handleGError() {
        if (this.blockParser.checkReturn() != 2) {
            Error error = new Error(this.blockParser.getRightBraceLineNum(),
                    ErrorType.MISSING_RETURN);
            ErrorTable.addError(error);
        }
    }
```

```java
    private void handleJError(Token token) {
        if (!token.getType().equals(TokenType.RPARENT)) {
            this.iterator.unReadToken(2);
            Error error = new Error(this.iterator.readNextToken().getLineNum(),
                    ErrorType.MISSING_R_PARENT);
            ErrorTable.addError(error);
        }
    }
```

```java
    public MainFuncDef parseMainFuncDef() {
        this.intTk = this.iterator.readNextToken();
        this.mainTk = this.iterator.readNextToken();
        /* 添加新符号 & 处理b类错误：名字重定义 */
        addFuncSymbol();
        this.leftParent = this.iterator.readNextToken();
        this.rightParent = this.iterator.readNextToken();
        /* 处理j类错误：缺失 ) */
        handleJError(this.rightParent);
        this.blockParser = new BlockParser(this.iterator, this.curSymbolTable, 2);
        // this.blockParser = new BlockParser(this.iterator);
        this.block = this.blockParser.parseBlock();
        /* 处理g类错误：缺失有返回值的return语句 */
        handleGError();
        MainFuncDef mainFuncDef = new MainFuncDef(this.intTk, this.mainTk,
                this.leftParent, this.rightParent, this.block);
        return mainFuncDef;
    }

    private void addFuncSymbol() {
        /* 生成新符号 */
        SymbolType symbolType = SymbolType.FUNC;
        int dimension = 0;
        this.symbolFunc = new SymbolFunc(this.mainTk.getLineNum(),
                "main", symbolType);
        this.symbolFunc.setDimension(0); // int
        /* 检查b类错误 */
        if (this.curSymbolTable.checkBTypeError(symbolFunc)) {
            Error error = new Error(symbolFunc.getLineNum(), ErrorType.DUPLICATED_IDENT);
            ErrorTable.addError(error);
        }
        this.curSymbolTable = new SymbolTable(this.curSymbolTable);
    }
```

## 六、代码生成设计

### 1. 编码前的设计

#### 1.1 设计路线的选择与分析

所谓代码生成，就是将抽象语法树（Abstract Syntax Tree，在语法阶段生成）转化为中间代码，再由中间代码转换为目标代码的过程。由于中间代码和代码优化关系密切（代码优化的INPUT是优化前的中间代码，OUTPUT是优化后的中间代码），因此中间代码十分必要且重要。至此，我们的思路已经逐渐清晰：首先生成中间代码，然后依据中间代码生成mips代码。

对于中间代码的选择，课程网站和手册推荐并“要求”使用四元式（虽然说是未来会考察，不过根据学长这玩意好像没法考察）。然而，四元式是一种抽象的，需要个人设计并实现的中间代码形式（PCODE也具有这样的特点），对于还未进行代码生成工作的我而言，很难在一开始就想清楚如何设计各类四元式；同时，由于每个人四元式的个性化，无法对其进行自动化评测，也就是说，如果选择四元式作为中间代码，只有在完成AST到四元式与四元式到mips后才可以使用课程网站进行评测，这带来了debug方面的麻烦：难以确定是生成中间代码的bug还是生成mips的bug还是二者兼有。而对于LLVM IR而言，其优点主要有三方面：一是具有良好的范式和定义，可以注意到对于选择LLVM IR赛道而言**不需要**自己写解释器，即类似mips代码一样可以被直接自动化评测；二是其有较为完整的材料（软件学院的GitBook）和自动化评测（LLVM IR赛道评测）；三是由于其完整的规范性，我们**不需要**在生成LLVM IR时考虑和mips相关的内容，降低了我们的思维复杂度。因此，在完成LLVM IR中间码后可以直接评测自己的正确性，在正确的LLVM IR代码的基础上进行mips代码的生成。

特别地，软件学院的指导书中提供了一个开源的LLVM IR编译器仓库。~~这十分友好~~

至此，我们的设计路线已经确定：**首先实现AST生成LLVM IR，然后实现LLVM IR生成mips**。

#### 1.2 从AST到LLVM IR

首先，我们了解并设计LLVM IR对应的类和数据结构，以快速获得对其的基本印象，主要参考资料是[软件学院编译实验指导书](https://buaa-se-compiling.github.io)。

##### 1.2.1 LLVM IR 总体结构

- LLVM IR文件的基本单位称为`module`，由于本实验只涉及单文件编译，因此只有一个module
- 一个`module`中有多个顶层实体，如`function`和`global variable`，这可以分别对应SysY中编译单元`CompUnit`文法定义中的`{Decl}`和`{FuncDef}`
- 一个`function def`中至少有一个`basicblock`，这意味着函数体声明内至少有一个基本块（这里的基本块的含义更多是编译原理中的“基本块与流图”中的基本块）
- 一个`basicblock`中有若干`instruction`，并且都以`terminator instruction`结尾，这和我们对基本块的定义是一致的。

###### 1.2.1.1 基本块

在编译技术原理课中，我们已经详细地学习了基本块和流图的基本概念，这里结合实验再次阐述。

一个基本块是包含了若干个指令以及一个终结指令的代码序列。

基本块只会从终结指令退出，并且基本块的执行是原子性的，也就是说，如果基本块中的一条指令执行了，那么块内其他所有的指令也都会执行。这个约束**是通过代码的语义实现的**。基本块内部没有控制流，控制流是由多个基本块直接通过跳转指令实现的。

形象地讲，一个基本块中的代码是顺序执行的，且顺序执行的代码都属于一个基本块。

例如你有一份不含跳转（没有分支、循环）也没有函数调用的、只会顺序执行的代码，那么这份代码只有一个基本块。

然而，一旦在中间加入一个 `if-else` 语句，那么代码就会变成四个基本块：`if` 上面的代码仍然是顺序执行的，在一个基本块中；`then` 和 `else` 各自部分的代码也都是顺序执行的，因此各有一个基本块；`if` 之后的代码也是顺序执行的，也在一个基本块中。所以总共四个基本块。

###### 1.2.1.2 指令（Instruction）

指令指的是 LLVM IR 中的非分支指令（non-branching Instruction），通常用来进行某种计算或者是访存（比如上面例子中的 `add`、`load`），这些指令并不会改变程序的控制流。

值得一提的是，`call` 指令也是非分支指令，因为在使用 `call` 调用函数时，我们并不关系被调用函数内部的具体情况（即使被调用函数内部存在的控制流），而是只关心我们传入的参数以及被调用函数的返回值，因此这并不会影响我们当前程序的控制流。

###### 1.2.1.3 推荐指令

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

###### 1.2.1.4 类型系统

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

##### 1.2.2 处理流程

###### 1.2.2.1 设计模式

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

###### 1.2.2.2 接口交互

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



#### 1.3 从LLVM IR 到mips

完成LLVM IR后，接下来要做的就是将LLVM IR翻译为mips。

##### 1.3.1 mips框架

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

###### 1.3.1.1 寄存器约定

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

##### 1.3.2 架构设计

整体上，按照`MipsModule -> MipsFunc -> MipsBasicBlock -> MipsInstruction`的结构来设计。

######  1.3.2.1 `MipsModule`

和`IrModule`相对应。具体地，包括`.data`段的字符串常量，`.text`段的`MipsFunc`函数，mips代码的主题结构就是在`.text`段进行执行和跳转。

###### 1.3.2.2 `MipsFunc`

和`IrFunc`相对应。具体地，`MipsFunc`包括一组指令，对于非`main`函数而言，这组指令会以`jr`结束，代表跳回函数调用处，而`main`函数则以`li $v0, 10 <br/>syscall`结束表示终止程序。

######  1.3.2.3 `MipsBasicBlock`

和`IrBasicBlock`相对应。具体地，`MipBasicBlock`一般会以`label`开始，表示如何跳转入自己。

/* TODO : 这里没有想得很清楚，等实现了if和while再做修订 */

###### 1.3.2.4 `MipsInstruction`

和`IrInstruction`相对应，具体地，一条`IrInstruction`可能会被翻译为一条或多条`MipsInstruction`；多条`IrInstruction`也可能被翻译为一条`MipsInstruction`（典型案例如连续的putch会被合并为字符串打印）

### 2. 编码实现

#### 2.1 从AST到LLVM IR

LLVM IR的结构如下：

```
- Module 
	- Function
		- BasicBlock
			- Instruction
```

由于SysY的文法和LLVM IR并不完全对应，因此以下详细讨论生成器`IrBuilder`的设计。

其中对SysY的术语和LLVM IR的术语会混合使用，为了和代码保持一致，也为了区分彼此，对于SysY的文法名词，我们采用文法

##### 2.1.1 `genIrModule`

对给定`CompUnit`解析并生成`IrModule`。

其中，`IrModule`包含`List<IrGlobalVariable>`和`List<IrFunction>`，分别调用`genIrInstruction`方法（`IrGlobalVariable`的来源本质上是SysY中的`Decl`，后续将简述重载在本方法中的使用）和`genIrFunction`方法。

##### 2.1.2 `genIrFunction`

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

##### 2.1.3 `genIrBasicBlock`

对给定的部分`Stmt`返回`List<IrBasicBlock>`。

这里，我们突出利用了Java提供的重载功能，在`genIrFunction`中已经详细列举了所有可能的传入参数，即`Block, StmtCond, StmtWhile`，对三种情况分别重载传入参数。

##### 2.1.4 `genIrInstruction`

对给定的`Decl`和部分`Stmt`返回`List<IrInstruction>`。

类似地，我们也着重采用了Java的重载功能，对于所有的可能情况分别重载传入参数。

##### 2.1.5 条件语句`if`







#### 2.2 从LLVM IR到MIPS

到这里，我们已经（部分正确）实现了LLVM IR的翻译，接下来，我们的目标工作是推进最后一公里：将LLVM IR翻译为MIPS。类似地，我们继续采用自顶向下的设计思路。

##### 2.2.1 输出接口

设计`MipsNode`接口并声明`mipsOutput`方法（该方法返回一个字符串数组），使`MipsModule`、`MipsFunc`、`MipsBasicBlock`和`MipsInstruction`实现接口。具体地，每个模块的该输出方法将依次调用自己子模块的方法，整合后合并输出。

##### 2.2.2 字符串常量声明

在mips中，打印字符串和打印数字是两种不同的系统调用，同时不支持单个字符的打印（如果你想要把单个字符转化为长度为1的字符串当然可以，但是性能代价过高）；而在LLVM IR中，我们的实现采取了一个一个字符打印的方式（原因是LLVM的字符串输出过于麻烦）。对此，我们的处理方法是遍历每个`IrBasicBlock`（基本块内语句的执行是连续的，选取此为单位避免了两个基本块的相邻putch被误判为一条字符串）时将连续的`putch`语句的输出内容连缀为一个字符串并加入`MipsModule`中作为`.data`段输出（这需要我们设计一个`MipsData`类来保存这些字符串和他们的名字，并设计一个相应的计数器以保证在遍历`IrModule`的时候可以正确地为字符串命名）。

##### 2.2.3 `$gp`28

对于全局变量，其需要对任何时刻的任何函数都可以正确访问。因此，我们需要`$gp`来保存全局变量指针，并记录每个全局变量具体的偏移。

##### 2.2.4 `$fp` 30

函数栈指针，从`$fp`向上依次是参数区（为了规范，对于`$a0-$a4`，我们会在进入函数后立刻将其保存到内存中）、局部变量区和临时变量区。其中局部变量区和临时变量区目前不做区分，一致处理。

操作时，我们需要遵守如下范式约束：

- 本函数调用结束时，`$fp`的值仍应当等于进入本函数时的值。
- `$fp`的增长方向是`+`，恢复方向是`-`。具体地，当我们在父函数内部执行调用某一个子函数的时候，编译器内部应当保存当前父函数`$fp`栈顶事实上的位置，然后将`$fp`寄存器的值自增至此，然后以`+`的方向压入函数实参；调用`jal`进入子函数；在`jal`语句后，由于我们遵守了第一条的`$fp`范式，因此按照编译器内部保存的事实偏移即可通过`-`恢复`$fp`到父函数的正确位置。事实上，对于实参压栈还有更复杂的细节问题（比如参数个数），这将在**函数调用**部分进一步介绍。

##### 2.2.5 `$sp`29

函数帧指针，从`$sp`向下依次是`$ra`和其他需要保存的寄存器。

类似地，我们在操作时遵守如下范式：

- 本函数调用结束时，`$sp`的值应当等于进入本函数时的值。
- `$sp`的增长方向是`-`，恢复方向是`+`。具体地，函数调用时，将`$ra`和其他需要保存的寄存器以`-`的方向压入栈；调用`jal`进入子函数；在`jal`语句后，按照编译器保存的数据恢复`$ra`和其他寄存器的值。

##### 2.2.6 函数调用

###### 2.2.6.1 `main`函数

对于`main`函数，我们会在将全局变量压入`$gp`后即调用`j main`进入`main`函数（使用`j`是因为这是程序入口，无条件转入）；在函数结束时，我们调用`li $v0, 10 <br/>syscall`来结束整个程序。

###### 2.2.6.2 其他函数

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

##### 2.2.7 符号表与符号

在翻译为MIPS时，需要将LLVM IR中的符号映射到一个`MipsSymbol`，其中记录包括是否在寄存器中，是否`dirty`，是否已经分配对应的内存空间，内存空间相对于`$fp`的偏移，是否被改写等。

##### 2.2.8 寄存器表

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

## 七、代码优化设计

### （1）词法分析

#### 1. 正则表达式识别Token

在词法分析识别Token时，可以自己按照文法构造有穷自动机，但另一种更优秀的办法是利用正则表达式和贪婪规则进行分析，示例如下：

```java
public enum TokenType {
    /* ---------- specific elements begin ---------- */
    MAINTK(true, "main"),
    CONSTTK(true, "const"),
    INTTK(true, "int"),
    BREAKTK(true, "break"),
    CONTINUETK(true, "continue"),
    IFTK(true, "if"),
    ELSETK(true, "else"),
    WHILETK(true, "while"),
    GETINTTK(true, "getint"),
    PRINTFTK(true, "printf"),
    RETURNTK(true, "return"),
    VOIDTK(true, "void"),
    /* ---------- specific elements end ---------- */
    /* ----------- begin ---------- */
    IDENFR(false, "[_A-Za-z][_A-Za-z0-9]*"),
    INTCON(false, "[0-9]+"),
    STRCON(false, "\\\"[^\\\"]*\\\""),
    /* ---------- end ---------- */
    /* ----------- comparison operation begin ---------- */
    LEQ(false, "<="),
    LSS(false, "<"),
    GEQ(false, ">="),
    GRE(false, ">"),
    EQL(false, "=="),
    NEQ(false, "!="),
    /* ---------- comparison operation end ---------- */
    /* ---------- arithmetic operation begin ---------- */
    PLUS(false, "\\+"),
    MINU(false, "-"),
    MULT(false, "\\*"),
    DIV(false, "/"),
    /* ---------- arithmetic operation end ---------- */
    /* ---------- logical operation begin ---------- */
    NOT(false, "!"),
    AND(false, "&&"),
    OR(false, "\\|\\|"),
    MOD(false, "%"),
    /* ---------- logical operation end ---------- */
    ASSIGN(false, "="),
    SEMICN(false, ";"),
    COMMA(false, ","),
    /* ---------- brackets begin ---------- */
    LPARENT(false, "\\("),
    RPARENT(false, "\\)"),
    LBRACK(false, "\\["),
    RBRACK(false, "]"),
    LBRACE(false, "\\{"),
    RBRACE(false, "}");
    /* ---------- brackets end ---------- */
}
```

#### 2. 单词串

为了便于语法分析的进行，在词法分析阶段将分析得到的单词存入自定义类`TokenList`单词串中，示例如下：

```java
public class TokenList {
    private ArrayList<Token> tokens;

    public TokenList() {
        this.tokens = new ArrayList<>();
    }

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }
}
```

#### 3. 单词迭代器

为了在语法分析时便于单词的预读与回退，设计了单词迭代器`TokenListIterator`：

```java
public class TokenListIterator {
    private TokenList tokenList;
    private ListIterator<Token> iterator;
    private Token last;

    public TokenListIterator(TokenList tokenList) {
        this.tokenList = tokenList;
        this.iterator = this.tokenList.getTokens().listIterator();
    }

    public ListIterator<Token> getIterator() {
        return iterator;
    }

    public Token readNextToken() {
        return last = this.iterator.next();
    }

    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    public void unReadToken(int k) {
        int cnt = k;
        while (cnt > 0) {
            cnt--;
            if (this.iterator.hasPrevious()) {
                last = this.iterator.previous();
            } else {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return last.toString();
    }
}
```

### （2）中间代码生成

本编译器的中间代码选择了LLVM IR。

#### 1. SSA（静态单一赋值）

在生成LLVM IR时，采用了SSA的方法以便于后续寄存器分配和优化等。每个变量仅被赋值一次，简化了优化的实现和优化程序的效率。

在编译器的设计中，静态单赋值形式（static single assignment form，通常简写为SSA form或是
SSA）是中间代码（IR，intermediate representation)的特性，每个变量仅被赋值一次。在原始的IR中，已存在的变数可被分割成许多不同的版本，在许多教科书当中通常会将旧的变数名称加上一个下标而成为新的变数名称，以至于标明每个变数及其不同版本。在SSA，UD链（use-define chain，赋值代表define，使用变数代表use）是非常明确，而且每个仅包含单一元素。
SSA架构下每个变量仅被赋值一次从而数据流分析更加方便，因此数据流分析相关的优化也可更高效高质量的完成，也可以高效率的完成GVN(全局值编号)从而完成相关的代码优化

在`SSA_book`中，介绍了如下SSA生成算法：

![image-20221225223356347](优化文章/image-20221225223356347.png)

在本编译器中，实现效果如下：

- SysY源码：

  ```C
  int main() {
  	int a = 1, b = 2, c = 3;
  	int x = a + b * c - a / c;
  	return 0;
  }
  ```

- LLVM IR中间代码

  ```
  declare i32 @getint()
  declare void @putint(i32)
  declare void @putch(i32)
  
  
  define dso_local i32 @main() #0 {
      %_LocalVariable0 = alloca i32
      %_LocalVariable1 = alloca i32
      %_LocalVariable2 = alloca i32
      %_LocalVariable3 = alloca i32
      store i32 1, i32* %_LocalVariable0
      store i32 2, i32* %_LocalVariable1
      store i32 3, i32* %_LocalVariable2
      %_LocalVariable4 = load i32, i32* %_LocalVariable0
      %_LocalVariable5 = load i32, i32* %_LocalVariable1
      %_LocalVariable6 = load i32, i32* %_LocalVariable2
      %_LocalVariable7 = mul i32 %_LocalVariable5, %_LocalVariable6
      %_LocalVariable8 = add i32 %_LocalVariable4, %_LocalVariable7
      %_LocalVariable9 = load i32, i32* %_LocalVariable0
      %_LocalVariable10 = load i32, i32* %_LocalVariable2
      %_LocalVariable11 = sdiv i32 %_LocalVariable9, %_LocalVariable10
      %_LocalVariable12 = sub i32 %_LocalVariable8, %_LocalVariable11
      store i32 %_LocalVariable12, i32* %_LocalVariable3
      ret i32 0
  }
  ```

可以注意到，其中的变量会被`alloca`申请空间，后续的每一次引用都会使用`load`将其加载到新的局部变量中，从而完成了SSA。

#### 2. 死代码删除

##### 2.1 跳转代码删除

对于 `branch` 语句，当判断跳转的变量取固定值时，则只有一种跳转的可能，因此可删去无法跳转到的分支。

##### 2.2 一般代码删除

对于SSA形式生成的代码，删除定义了但未被使用的变量的相关语句。

### （3）中间代码优化

#### 1. 基本块划分和流图

后续的多数优化方案依赖于数据流分析，因此先将中间代码划分为基本块并构建流图。

在本编译器中，`middle/llvmir/value/basicblock`下保存了中间代码基本块类和中间代码基本块生成器。具体地，如下划分基本块并构造流图：

- 基本块属于某一个具体函数的内部，不跨函数
- 最开始初始化一个大的基本块，向下解析来拆解新的基本块
  - 遇到无条件跳转语句结束该基本块，生成一个新基本块对象并将当前基本块和即将跳转的基本块连接
  - 遇到条件跳转语句结束该基本块，并于即将跳转的基本块相连接，生成一个新基本块并将当前基本块和新基本块相连接。
  - 遇到标签Label时，结束当前基本块并生成一个新的基本块，将当前基本块与新基本块连接。
- 连接基本块时，通过将其加入基本块链表来维护和保存，以便于后续数据流分析。

#### 2. 活跃变量分析

活跃变量分析针对的目标是变量，其与后续寄存器分配息息相关。在本编译器中，具体实现如下：

- 计算每个基本块的`use`和`def`集合
- 由公式$out[B] = \cup_{(B的每个后继基本块P)}in[p]$和$in[B] = use[b] \cup (out[B] - def[B])$来计算每个基本块的IN和OUT集合
- 检查IN集合是否有变化，若无变化则停止，若有变化则继续重复循环。

#### 3. 常量合并与常量传播

##### 3.1 基本块内

对于基本块内部的常量传播，计算方法如下：

- 新建符号表，符号表中变量和值（常量或变量）一一对应
- 顺序扫描，中间代码如果满足操作数的值都是常数，则以常数替换变量

##### 3.2 跨基本块

对于跨基本块的常量传播，由于我们之前计算得到了数据流分析，因此可以计算每个基本块的IN集合。对于某个变量，如果基本块的IN集合中只有一条中间代码对变量进行赋值操作，则将该中间代码作为该基本块的前去语句，按照基本块内的复写传播进行操作。

#### 4. 删除无用循环

由于前面进行了活跃变量分析，因此在循环内部的变量在循环外不再使用时可以删去循环。

#### 5. 到达定义分析

根据编译理论课讲述的内容，由公式$in[B] = \cup_{(B的每个前驱基本块P)out[P}$和$out[B] = gen[B] \cup (in[B] - kill[B])$计算IN和OUT集合，循环计算直至OUT集合稳定不变。

### （4）Mips代码生成与优化

#### 1. 尽可能使用寄存器

由于前述SSA的特性，对于一些临时变量不需要给其分配内存，直接标记为临时变量，在使用后即可释放其寄存器。

#### 2. 乘除优化

- 乘法优化为对操作数为常数的指令进行优化，方法是乘法转换为左移和加法操作
- 除法优化为将指令转化为乘法指令再进行乘法指令优化
- 取模运算转化为乘除指令再优化：$a \ \% \ b = a -a/b$

#### 3. 窥孔优化

- 删除无用的`j,beq,bne`等跳转指令
- 删除无用的`move`指令
- 删除无用的`lw,sw`指令

### （5）寄存器分配

#### 1. 寄存器使用约束

本编译器遵循mips规范使用`$a,$a,$t`等寄存器。具体地，对于一些寄存器进行如下定义和约束：

- 全局寄存器：保存跨函数变量，不需要写回内存。
- 局部寄存器：保存跨基本块变量，再函数内不需要写回内存，但是函数调用时需要根据保护现场来写回内存。
- 临时寄存器：保存基本块内变量，需要写回内存。

#### 2. 图着色

由前述活跃变量分析得到变量之间的冲突图，根据编译理论课讲述的启发式算法找到一个寄存器分配方案进行分配。

