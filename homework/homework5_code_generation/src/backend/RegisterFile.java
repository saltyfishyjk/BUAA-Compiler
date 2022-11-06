package backend;

import backend.basicblock.MipsBasicBlock;
import backend.instruction.Sw;
import backend.symbol.MipsSymbol;
import backend.symbol.MipsSymbolTable;

import java.util.HashMap;
import java.util.Stack;

/**
 * Mips Register File : Mips 寄存器表
 * 每个函数有一个对应的符号表，每一个符号表保存一张寄存器表，表示当前寄存器的分配情况
 */
public class RegisterFile {
    /* 该寄存器表所在符号表 */
    MipsSymbolTable table;
    /* 寄存器编号与是否有值的映射，如果为false说明没有有效值，可以直接用 */
    private HashMap<Integer, Boolean> hasValues;
    /* 寄存器编号与MipsSymbol的映射，获取对应符号的具体情况 */
    private HashMap<Integer, MipsSymbol> regs;
    private int regNum = 32;
    private Stack<Integer> sRegUse = new Stack<>(); // 记录s寄存器的使用先后顺序

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

    /* 初始化 */
    private void init() {
        for (int i = 0; i < this.regNum; i++) {
            if (isConReg(i) || isTempReg(i)) {
                // 可以被程序分配的寄存器
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
            /* 找到了空闲寄存器，将寄存器编号压入use栈 */
            this.sRegUse.push(freeReg);
            /* 修改MipsSymbol状态 */
            symbol.setInReg(true); // 标记该Symbol已经在寄存器中
            symbol.setRegIndex(freeReg); // 记录该Symbol所在寄存器编号
            /* 修改寄存器表状态 */
            this.hasValues.put(freeReg, true);
            this.regs.put(freeReg, symbol);
            symbol.setInReg(true);
            symbol.setRegIndex(freeReg);
            return freeReg;
        } else {
            /* 没有找到空闲寄存器，说明需要将某个寄存器写入内存或做其他操作 */
            if (isTemp) {
                System.out.println("ERROR in RegisterFile : UNEXPECTED!");
                return -1;
            }
            /* 弹出最旧的$s寄存器 */
            int oldReg = this.sRegUse.pop();
            MipsSymbol oldSymbol = this.regs.get(oldReg);
            if (oldSymbol.hasRam()) {
                /* 已经分配内存 */
                writeBack(oldSymbol, basicBlock);
            } else {
                /* 须分配内存 */
                allocRam(oldSymbol);
                writeBack(oldSymbol, basicBlock);
            }
            /* 修改被弹出符号状态 */
            oldSymbol.setInReg(false);
            oldSymbol.setRegIndex(-1);
            /* 修改寄存器表状态 */
            this.regs.put(oldReg, symbol);
            this.sRegUse.push(oldReg);
            /* 修改新加入符号状态 */
            symbol.setInReg(true);
            symbol.setRegIndex(oldReg);
            return oldReg;
        }
    }

    /* 申请空间只可能是局部变量 */
    private void allocRam(MipsSymbol symbol) {
        int fpOffset = this.table.getFpOffset();
        /* TODO : 这里默认申请4字节，可能需要修改 */
        symbol.setOffset(fpOffset + 4);
        this.table.addOffset(4);
        symbol.setHasRam(true);
    }

    /* 写回可能是全局变量或局部变量 */
    private void writeBack(MipsSymbol symbol, MipsBasicBlock basicBlock) {
        int rt = symbol.getRegIndex();
        int base = symbol.getBase(); // 默认以fp为base
        int offset = symbol.getOffset();
        Sw sw = new Sw(rt, base, offset);
        basicBlock.addInstruction(sw);
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
                }
            }
        }
        return -1; // 说明没有找到可以被直接使用的寄存器
    }


}
