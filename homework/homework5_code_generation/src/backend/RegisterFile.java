package backend;

import java.util.HashMap;

/**
 * Mips Register File : Mips 寄存器表
 * 每个函数有一个对应的符号表，每一个符号表保存一张寄存器表，表示当前寄存器的分配情况
 */
public class RegisterFile {
    /* 寄存器编号与是否有值的映射，如果为false说明没有有效值，可以直接用 */
    private HashMap<Integer, Boolean> hasValues;
    /* 寄存器编号与MipsSymbol的映射，获取对应符号的具体情况 */
    private HashMap<Integer, MipsSymbol> regs;
    private int regNum = 32;

    public RegisterFile() {
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

    /* 获取一个空闲寄存器编号 */
    public int hasFreeReg(boolean isTemp) {
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
