package backend;

import backend.instruction.MipsInstruction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mips Symbol Table : Mips符号表
 * 用于在LLVM IR ->时将LLVM IR的变量和Mips的寄存器/内存映射起来
 *
 */
public class MipsSymbolTable {
    /* 寄存器表，保存当前寄存器的使用情况 */
    private RegisterFile registerFile;
    /* LLVM IR -> Mips Symbol */
    /* 对于MipsSymbol，其应当与RegisterFile中的保持一致，我们利用Java的特性，让引用指向同一个对象来实现 */
    private HashMap<String, MipsSymbol> symbols;
    private int fpOffset; // 表明当前已经使用的内存的顶部相对fp的偏移

    public MipsSymbolTable(RegisterFile registerFile) {
        this.symbols = new HashMap<>();
        this.registerFile = new RegisterFile();
    }

    /* 查询LLVM IR变量是否已加入符号表 */
    public boolean hasSymbol(String name) {
        return this.symbols.containsKey(name);
    }

    /* 将新的LLVM IR符号加入符号表 */
    public void addSymbol(String name) {

    }

    /* 查询给定LLVM IR符号是否位于寄存器 */
    /* 执行本命令前须判断该符号已位于符号表中 */
    public boolean inReg(String name) {
        MipsSymbol symbol = this.symbols.get(name);
        return symbol.isInReg();
    }

    /* 获取一个可用的寄存器编号，可能涉及内存交换等 */
    public int getFreeRegIndex(boolean isTemp) {
        /* 如果isTemp，应当在t0-t9即8-15和24-25号寄存器中查找 */
        if (isTemp) {
            /* TODO : 待施工 */
        } else {
            /* 如果非isTemp，应当在s0-s7即16-23号寄存器中查找 */
            /* TODO : 待施工 */
        }
        return -1; // TODO : 待施工
    }

    /* 将LLVM IR符号从内存加载到寄存器中 */
    /* 执行本命令前须判断该符号位于内存且不位于寄存器中 */
    public ArrayList<MipsInstruction> loadInReg(String name) {
        MipsSymbol symbol = this.symbols.get(name);
        /* TODO : 待施工 */
        return null;
    }

    /* 获取LLVM IR变量对应的符号的寄存器 */
    public int getRegIndex(String name) {
        MipsSymbol symbol = this.symbols.get(name);
        return symbol.getRegIndex();
    }
}
