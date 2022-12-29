package backend;

import backend.basicblock.MipsBasicBlock;
import backend.instruction.Add;
import backend.instruction.Addi;
import backend.instruction.Lw;
import backend.instruction.MipsInstruction;
import backend.instruction.MulImm;
import backend.instruction.Sll;
import backend.instruction.Sw;
import backend.symbol.MipsSymbol;
import backend.symbol.MipsSymbolTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Mips Register File : Mips 寄存器表
 * 每个函数有一个对应的符号表，每一个符号表保存一张寄存器表，表示当前寄存器的分配情况
 */
public class RegisterFile {
    /* 该寄存器表所在符号表 */
    private MipsSymbolTable table;
    /* 寄存器编号与是否有值的映射，如果为false说明没有有效值，可以直接用 */
    private HashMap<Integer, Boolean> hasValues;
    /* 寄存器编号与MipsSymbol的映射，获取对应符号的具体情况 */
    private HashMap<Integer, MipsSymbol> regs;
    private int regNum = 32;
    private Stack<Integer> sregUse = new Stack<>(); // 记录s寄存器的使用先后顺序
    private int tempPtr = 8; //指向最旧的寄存器的指针

    public Stack<Integer> cloneSregUse() {
        Stack<Integer> ret = new Stack<>();
        for (Integer index : sregUse) {
            ret.push(index);
        }
        return ret;
    }

    public void setSregUse(Stack<Integer> sregUse) {
        this.sregUse = sregUse;
    }

    public HashMap<Integer, Boolean> cloneHasValues() {
        HashMap<Integer, Boolean> newHasValues = new HashMap<>();
        for (Integer index : this.hasValues.keySet()) {
            newHasValues.put(index, this.hasValues.get(index));
        }
        return newHasValues;
    }

    public HashMap<Integer, MipsSymbol> cloneRegs() {
        HashMap<Integer, MipsSymbol> newRegs = new HashMap<>();
        for (Integer index : this.regs.keySet()) {
            String name = this.regs.get(index).getName();
            // newRegs.put(index, this.regs.get(index).cloneMipsSymbol());
            MipsSymbol symbol = this.table.getSymbol(name);
            if (symbol == null) {
                newRegs.put(index, this.regs.get(index).cloneMipsSymbol());
            } else {
                newRegs.put(index, this.table.getSymbol(name));
            }
            
        }
        return newRegs;
    }

    public int getRegNum() {
        return this.regNum;
    }

    public void setHasValues(HashMap<Integer, Boolean> hasValues) {
        this.hasValues = hasValues;
    }

    public void setRegs(HashMap<Integer, MipsSymbol> regs) {
        this.regs = regs;
    }

    public void setRegNum(int regNum) {
        this.regNum = regNum;
    }

    public RegisterFile() {
        init();
    }

    public void setTable(MipsSymbolTable table) {
        this.table = table;
    }

    public RegisterFile(MipsSymbolTable table) {
        this.table = table;
        init();
    }

    /* 判断是否是$t寄存器 */
    private boolean isTempReg(int index) {
        if ((8 <= index && index <= 15) || // t0-t7
                (24 <= index && index <= 25)) { // t8-t9
            return true;
        } else {
            return false;
        }
    }

    /* 判断是否为$s寄存器 */
    private boolean isConReg(int index) {
        if (16 <= index && index <= 23) { // s0-s7
            return true;
        } else {
            return false;
        }
    }

    /* 判断是否是$v寄存器 */
    private boolean isVreg(int index) {
        if (2 <= index && index <= 3) {
            return true;
        } else {
            return false;
        }
    }

    /* 判断是否是$a寄存器 */
    private boolean isAreg(int index) {
        if (4 <= index && index <= 7) {
            return  true;
        } else {
            return false;
        }
    }

    /* 判断是否为$ra寄存器 */
    private boolean isRareg(int index) {
        return index == 31;
    }

    /* 初始化 */
    private void init() {
        this.hasValues = new HashMap<>();
        this.regs = new HashMap<>();
        for (int i = 0; i < this.regNum; i++) {
            if (isConReg(i) || isTempReg(i) || isAreg(i) || isVreg(i) || isRareg(i)) {
                // 可以被程序分配的寄存器 or 不知道有没有值的寄存器
                this.hasValues.put(i, false);
            } else {
                // 不可以被分配的寄存器
                this.hasValues.put(i, true);
            }
        }
    }

    /* TODO : 临时变量使用后的标记 */
    /* isTemp = true ->从t0-t7找 */
    public int getReg(boolean isTemp,
                      MipsSymbol symbol,
                      MipsBasicBlock basicBlock) {
        int freeReg = hasFreeReg(isTemp);
        if (freeReg != -1) {
            if (!isTemp) {
                /* 找到了空闲寄存器，将寄存器编号压入use栈 */
                this.sregUse.push(freeReg);
            }
            /* 修改MipsSymbol状态 */
            symbol.setInReg(true); // 标记该Symbol已经在寄存器中
            symbol.setRegIndex(freeReg); // 记录该Symbol所在寄存器编号
            /* 修改寄存器表状态 */
            this.hasValues.put(freeReg, true);
            this.regs.put(freeReg, symbol);
            symbol.setInReg(true);
            symbol.setRegIndex(freeReg);
            /* 写入寄存器 */
            if (symbol.hasRam()) {
                readBack(freeReg, symbol, basicBlock);
            }
            if (freeReg == this.tempPtr) {
                this.tempPtr = (this.tempPtr + 1 + 32) % 32;
            }
            return freeReg;
        } else {
            /* 没有找到空闲寄存器，说明需要将某个寄存器写入内存或做其他操作 */
            if (isTemp) {
                // System.out.println("ERROR in RegisterFile : UNEXPECTED!");
                /* 找到一个最旧的t寄存器 */
                int ret = getOldTempReg(basicBlock);
                /* 修改MipsSymbol状态 */
                symbol.setInReg(true); // 标记该Symbol已经在寄存器中
                symbol.setRegIndex(ret); // 记录该Symbol所在寄存器编号
                /* 修改寄存器表状态 */
                this.hasValues.put(ret, true);
                this.regs.put(ret, symbol);
                symbol.setInReg(true);
                symbol.setRegIndex(ret);
                /* 写入寄存器 */
                /*if (symbol.hasRam()) {
                    readBack(ret, symbol, basicBlock);
                }*/
                return ret;
            } else {
                /* 弹出最旧的$s寄存器 */
                int oldReg = this.sregUse.pop();
                MipsSymbol oldSymbol = this.regs.get(oldReg);
                if (oldSymbol.hasRam()) {
                    /* 已经分配内存 */
                    if (oldSymbol.isInReg()) {
                        writeBack(oldSymbol, basicBlock);
                    }
                } else {
                    /* 须分配内存 */
                    if (oldSymbol.isInReg()) {
                        allocRam(oldSymbol);
                        writeBack(oldSymbol, basicBlock);
                    }
                }
                /* 修改被弹出符号状态 */
                oldSymbol.setInReg(false);
                oldSymbol.setRegIndex(-1);
                /* 修改寄存器表状态 */
                this.regs.put(oldReg, symbol);
                this.sregUse.push(oldReg);
                /* 修改新加入符号状态 */
                symbol.setInReg(true);
                symbol.setRegIndex(oldReg);
                /* 如果新加入符号在内存有数据，则应读回 */
                if (symbol.hasRam()) {
                    readBack(oldReg, symbol, basicBlock);
                }
                if (oldReg == this.tempPtr) {
                    this.tempPtr = (this.tempPtr + 1 + 32) % 32;
                }
                return oldReg;
            }
        }
    }

    /* 申请空间只可能是局部变量 */
    private void allocRam(MipsSymbol symbol) {
        int fpOffset = this.table.getFpOffset();
        /* TODO : 这里默认申请4字节，可能需要修改 */
        symbol.setOffset(fpOffset + 4);
        // this.table.addOffset(4);
        this.table.addOffset(8);
        symbol.setHasRam(true);
    }

    /* 写回可能是全局变量或局部变量 */
    private void writeBack(MipsSymbol symbol, MipsBasicBlock basicBlock) {
        int rt = symbol.getRegIndex();
        int base = symbol.getBase(); // base可能为gp或fp
        int offset = symbol.getOffset();
        Sw sw = new Sw(rt, base, offset);
        ArrayList<MipsInstruction> temp = new ArrayList<>();
        temp.add(sw);
        basicBlock.addInstruction(temp);
    }

    public MipsInstruction writeBackPublic(MipsSymbol symbol) {
        int rt = symbol.getRegIndex();
        int base = symbol.getBase(); // base可能为gp或fp
        int offset = symbol.getOffset();
        Sw sw = new Sw(rt, base, offset);
        return sw;
    }
    
    /* 对于编译时能确定的相对偏移的writeBack */
    public ArrayList<MipsInstruction> writeBackPublic(int leftReg,
                                        MipsSymbol symbol,
                                        int deltaOffset,
                                        MipsBasicBlock basicBlock) {
        String name = symbol.getName();
        boolean isParam = symbol.getIsParam();
        Sw sw = null;
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        if (isParam) {
            /* 获取函数形参数组首元素的绝对地址 */
            int reg = this.table.getRegIndex(name, basicBlock, true);
            /* 计算目标内存单元的绝对地址 */
            Addi addi = new Addi(3, reg, deltaOffset);
            ret.add(addi);
            /* 存储到目标单元 */
            sw = new Sw(leftReg, 3, 0);
            ret.add(sw);
        } else {
            /* 说明不是数组形参，应当直接根据base的偏移进行储存 */
            int base = symbol.getBase();
            int offset = symbol.getOffset() + deltaOffset;
            sw = new Sw(leftReg, base, offset);
            ret.add(sw);
        }
        
        
        return ret;
    }

    /**
     * 写回维度数值为变量的数组元素
     * 对于数组形参，其首元素绝对地址存放在fp内存中，通过相对首元素的偏移加上该绝对地址（abs + offset(ele)）获取访问的绝对地址
     * 对于全局/局部数组，其存放在fp或gp中，其相对于fp的偏移在**编译**时已知，通过计算fp/gp + offset(arr) + offset(ele)来访问
     * @param symbol : 被写回的元素
     * @param reg1 : 1维变量所在的寄存器
     * @param reg2 : 2维变量所在的寄存器
     * @param dimension : 当前应当计算1维还是2维
     * @return : 返回新增的所有指令
     */
    public ArrayList<MipsInstruction> writeBackPublic(int leftReg,
                                                      MipsSymbol symbol,
                                                      int reg1,
                                                      int reg2,
                                                      int dimension,
                                                      MipsBasicBlock basicBlock) {
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        if (dimension == 1) {
            /* 1维变量 */
            /* 对形如a[reg1]计算 */
            boolean isParam = symbol.getIsParam();
            if (isParam) {
                /* Step 1 计算相对于数组首地址的偏移 */
                /* 说明数组是函数形参，其在对应内存中保存的信息是数组首元素的绝对地址 */
                String name = symbol.getName();
                /* 计算目标内存单元相对于数组首元素的偏移 */
                Sll sll = new Sll(3, reg1, 2);
                ret.add(sll);
                /* 获取函数形参数组首元素的绝对地址 */
                int reg = this.table.getRegIndex(name, basicBlock, true);
                /* Step 2 计算数组首元素绝对地址加上目标内存单元相对于数组首元素的偏移 */
                Add add = new Add(3, 3, reg); 
                ret.add(add);
                /* 生成Sw */
                Sw sw = new Sw(leftReg, 3, 0);
                ret.add(sw);
            } else {
                /* 说明数组是全局变量或局部变量，可以以当前的gp或fp作为base */
                int base = symbol.getBase();
                int fpOffset = symbol.getOffset();
                /* 偏移量需要*4即左移2位 */
                Sll sll = new Sll(3, reg1, 2);
                ret.add(sll);
                /* 偏移量和数组的基fpOffset相加得到相对base的偏移 */
                Addi addi = new Addi(3, 3, fpOffset);
                ret.add(addi);
                /* 相对base的偏移量和base相加得到绝对偏移 */
                Add add = new Add(3, 3, base);
                ret.add(add);
                /* 获取被写回的元素所在的寄存器 */
                // int rt = table.getRegIndex(symbol.getName(), basicBlock, true);
                // int rt = symbol.getRegIndex();
                Sw sw = new Sw(leftReg, 3, 0);
                ret.add(sw);
            }
        } else if (dimension == 2) {
            /* 2维变量 */
            /* 对形如a[reg1][reg2]的计算 */
            /* 函数传入参数形如a[][n]的计算 */
            /* 相对**数组首地址**的偏移是reg1 * n + reg2 */
            boolean isParam = symbol.getIsParam();
            if (isParam) {
                /* 说明数组是函数形参，其在对应内存中保存的信息是数组首元素的绝对地址 */
                /* Step 1 计算相对于数组首地址的偏移 */
                /* 获取第2维的长度n */
                int n = symbol.getDimension2();
                /* 计算reg1 * n并装入3号寄存器 */
                MulImm mulImm = new MulImm(3, reg1, n);
                ret.add(mulImm);
                /* 将reg1 * n + reg2装入3号寄存器 */
                Add add = new Add(3, 3, reg2);
                ret.add(add);
                /* 将偏移<<2 */
                Sll sll = new Sll(3, 3, 2);
                ret.add(sll);
                /* Step 2 计算数组首元素绝对地址加上目标内存单元相对于数组首元素的偏移 */
                int reg = this.table.getRegIndex(symbol.getName(), basicBlock, true);
                add = new Add(3, 3, reg);
                ret.add(add);
                Sw sw = new Sw(leftReg, 3, 0);
                ret.add(sw);
            } else {
                /* 说明数组是全局变量或局部变量，可以以当前的gp或fp作为base */
                int n = symbol.getDimension2(); // 获取第二维的长度，是编译时确定的整数
                // 将reg1 * n 装入3号寄存器
                MulImm mulImm = new MulImm(3, reg1, n);
                ret.add(mulImm);
                // 将reg1 * n + reg2装入3号寄存器
                Add add = new Add(3, 3, reg2);
                ret.add(add);
                // 将偏移 << 2
                Sll sll = new Sll(3, 3, 2);
                ret.add(sll);
                // 将相对数组首地址偏移和数组相对fp的偏移相加
                int fpOffset = symbol.getOffset();
                if (fpOffset != 0) {
                    Addi addi = new Addi(3, 3, fpOffset);
                    ret.add(addi);
                }
                // fp + offset获取绝对地址
                int base = symbol.getBase();
                add = new Add(3, 3, base);
                ret.add(add);
                // 计算Sw
                /* 获取被写回的元素所在的寄存器 */
                // int rt = table.getRegIndex(symbol.getName(), basicBlock, true);
                // int rt = symbol.getRegIndex();
                Sw sw = new Sw(leftReg, 3, 0);
                ret.add(sw);
            }
        } else {
            System.out.printf("ERROR IN RegisterFile : should not reach here");
        }
        return ret;
    }

    /* 从内存中读取变量 */
    private void readBack(int targetReg, MipsSymbol symbol, MipsBasicBlock basicBlock) {
        Lw lw = new Lw(targetReg, symbol.getBase(), symbol.getOffset());
        ArrayList<MipsInstruction> temp = new ArrayList<>();
        temp.add(lw);
        basicBlock.addInstruction(temp);
    }

    public ArrayList<MipsInstruction> readBackPublic(MipsSymbol leftSymbol,
                                                     MipsSymbol symbol,
                                                     int reg1,
                                                     int reg2,
                                                     int dimension,
                                                     MipsBasicBlock basicBlock) {
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        boolean isParam = symbol.getIsParam();
        /* Step 1 计算被加载的内存单元的位置并装入3号寄存器 */
        if (dimension == 1) {
            /* 1维变量，形如a[i] */
            /* Step 1.1 计算偏移：将偏移<<2 */
            Sll sll = new Sll(3, reg1, 2);
            ret.add(sll);
            if (isParam) {
                /* 是函数形参 */
                /* Step 1.2 获取函数形参数组首元素的绝对地址 */
                int reg = this.table.getRegIndex(symbol.getName(), basicBlock, true);
                /* 计算目标内存单元的绝对地址并装入3号寄存器 */
                Add add = new Add(3, 3, reg);
                ret.add(add);
            } else {
                /* 不是函数形参 */
                int base = symbol.getBase();
                int fpOffset = symbol.getOffset();
                /* Step 1.2 计算相对于fp/gp的偏移并装入3号寄存器 */
                Addi addi = new Addi(3, 3, fpOffset);
                ret.add(addi);
                /* Step 1.3 将fp/gp和相对fp/gp的偏移相加并装入3号寄存器 */
                Add add = new Add(3, 3, base);
                ret.add(add);
            }
        } else if (dimension == 2) {
            /* 2维变量，形如a[i][j] */
            /* Step 2.1 计算偏移 */
            /* 获取第2维长度n */
            int n = symbol.getDimension2();
            /* $3 = reg1(i) * n */
            MulImm mulImm = new MulImm(3, reg1, n);
            ret.add(mulImm);
            /* $3 = reg1(i) * n + m */
            Add add = new Add(3, 3, reg2);
            ret.add(add);
            /* 偏移<<2 */
            Sll sll = new Sll(3, 3, 2);
            ret.add(sll);
            isParam = symbol.getIsParam();
            if (isParam) {
                /* 是函数形参 */
                int reg = this.table.getRegIndex(symbol.getName(), basicBlock, true);
                /* 计算目标内存单元并装入3号寄存器 */
                add = new Add(3, 3, reg);
                ret.add(add);
            } else {
                /* 不是函数形参 */
                int base = symbol.getBase();
                int fpOffset = symbol.getOffset();
                /* 计算相对于fp/gp的偏移 */
                Addi addi = new Addi(3, 3, fpOffset);
                ret.add(addi);
                /* 计算绝对地址 */
                add = new Add(3, 3, base);
                ret.add(add);
            }
        } else {
            System.out.println("ERROR IN RegisterFile : should not reach here");
        }
        /* Step 2 将被加载的内存单元读取到leftSymbol所在的寄存器 */
        int leftReg = this.getReg(true, leftSymbol, basicBlock);
        leftSymbol.setInReg(true);
        leftSymbol.setRegIndex(leftReg);
        Lw lw = new Lw(leftReg, 3, 0);
        ret.add(lw);
        return ret;
    }

    /* 获取一个空闲寄存器编号 */
    private int hasFreeReg(boolean isTemp) {
        for (int i = 0; i < this.regNum; i++) {
            /* 为临时变量寻找寄存器 */
            if (this.isTempReg(i) && isTemp) {
                /* 如果有未赋值的寄存器可以直接使用 */
                if (!this.hasValues.get(i)) {
                    return i;
                } else {
                    /* 如果有已经被use的临时变量的寄存器也可以直接使用 */
                    MipsSymbol symbol = this.regs.get(i);
                    if (symbol.isTemp() && symbol.isUsed()) {
                        return i;
                    }
                }
            } else if (this.isConReg(i) && !isTemp) {
                /* 为局部变量寻找寄存器 */
                /* 如果有未赋值的寄存器可以直接使用 */
                if (!this.hasValues.get(i)) {
                    return i;
                } else if (!this.regs.get(i).isInReg()) {
                    return i;
                }
            }
        }
        return -1; // 说明没有找到可以被直接使用的寄存器
    }

    public void addSymbol(int reg, MipsSymbol symbol) {
        /* 添加寄存器号到符号的映射 */
        this.regs.put(reg, symbol);
        this.hasValues.put(reg, true);
    }

    public boolean inReg(int regNum) {
        return this.hasValues.get(regNum);
    }

    public MipsSymbol getSymbol(int reg) {
        return this.regs.get(reg);
    }

    private int getOldTempReg(MipsBasicBlock basicBlock) {
        /* 进入该方法说明已经没有闲置寄存器了 */
        while (true) {
            /* 为临时变量寻找寄存器 */
            if (this.isTempReg(this.tempPtr)) {
                MipsSymbol symbol = this.regs.get(this.tempPtr);
                if (!symbol.hasRam()) {
                    /* 没有对应内存空间需要先申请 */
                    allocRam(symbol);
                }
                writeBack(symbol, basicBlock);
                symbol.setInReg(false);
                this.tempPtr = (this.tempPtr + 1 + 32) % 32;
                return symbol.getRegIndex();
            } else {
                this.tempPtr = (this.tempPtr + 1 + 32) % 32;
            }
        }
    }

    /**
     * 将寄存器中的值全部存回内存
     * 主要用于跳转语句的跳转行为不一定执行带来的寄存器视图的差别
     */
    public ArrayList<MipsInstruction> writeBackAll() {
        ArrayList<MipsInstruction> instructions = new ArrayList<>();
        for (int i = 0; i < this.regNum; i++) {
            /* t寄存器 */
            if (this.isTempReg(i)) {
                if (this.hasValues.get(i)) {
                    /* t寄存器内有值 */
                    MipsSymbol symbol = this.regs.get(i);
                    if (!symbol.isTemp() ||
                            (symbol.isTemp() && !symbol.isUsed())) {
                        /* t寄存器内的值不是临时变量 */
                        /* 或t寄存器内的值是临时变量但还没用过 */
                        /* 此时应当写回内存 */
                        if (!symbol.hasRam()) {
                            /* 如果该变量没有内存中对应的空间，应当先为其申请 */
                            allocRam(symbol);
                        }
                        Sw sw = new Sw(i, symbol.getBase(), symbol.getOffset());
                        symbol.setInReg(false);
                        symbol.setRegIndex(-1);
                        instructions.add(sw);
                    }
                    this.hasValues.put(i, false);
                }
            } else if (this.isConReg(i)) {
                /* s寄存器 */
                if (this.hasValues.get(i)) {
                    /* s寄存器内有值 */
                    MipsSymbol symbol = this.regs.get(i);
                    if (!symbol.isInReg()) {
                        /* 不在寄存器中则不写回 */
                        continue;
                    }
                    if (!symbol.hasRam()) {
                        allocRam(symbol);
                    }
                    Sw sw = new Sw(i, symbol.getBase(), symbol.getOffset());
                    symbol.setInReg(false);
                    symbol.setRegIndex(-1);
                    instructions.add(sw);
                }
                this.hasValues.put(i, false);
            } else if (this.isVreg(i) ||
                    this.isRareg(i) ||
                    this.isAreg(i)) {
                if (this.hasValues.get(i)) {
                    MipsSymbol symbol = this.regs.get(i);
                    if (!symbol.hasRam()) {
                        allocRam(symbol);
                    }
                    Sw sw = new Sw(i, symbol.getBase(), symbol.getOffset());
                    symbol.setInReg(false);
                    symbol.setRegIndex(-1);
                    instructions.add(sw);
                }
                this.hasValues.put(i, false);
            }
        }
        while (!this.sregUse.empty()) {
            this.sregUse.pop();
        }
        this.tempPtr = 8;
        return instructions;
    }

}
