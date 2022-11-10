package backend.symbol;

import backend.RegisterFile;
import backend.basicblock.MipsBasicBlock;
import backend.instruction.MipsInstruction;
import backend.symbol.MipsSymbol;

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
        this.registerFile = registerFile;
    }

    /* 查询LLVM IR符号是否已加入符号表 */
    public boolean hasSymbol(String name) {
        return this.symbols.containsKey(name);
    }

    /* 将新的LLVM IR符号加入符号表 */
    public void addSymbol(String name, MipsSymbol symbol) {
        this.symbols.put(name, symbol);
    }

    /* 获取LLVM IR变量对应的符号的寄存器 */
    public int getRegIndex(String name, MipsBasicBlock basicBlock) {
        MipsSymbol symbol = this.symbols.get(name);
        /* 如果MipsSymbol直接在寄存器中，返回 */
        if (symbol.isInReg()) {
            return symbol.getRegIndex();
        } else {
            int reg = this.registerFile.getReg(symbol.isTemp(), symbol, basicBlock);
            return reg;
        }
        // return symbol.getRegIndex();
    }

    /* 查询给定LLVM IR符号是否位于寄存器 */
    /* 执行本命令前须判断该符号已位于符号表中 */
    public boolean inReg(String name) {
        MipsSymbol symbol = this.symbols.get(name);
        return symbol.isInReg();
    }

    /* 将LLVM IR符号从内存加载到寄存器中 */
    /* 执行本命令前须判断该符号位于内存且不位于寄存器中 */
    private ArrayList<MipsInstruction> loadInReg(String name) {
        MipsSymbol symbol = this.symbols.get(name);
        /* TODO : 待施工 */
        return null;
    }

    public void addOffset(int delta) {
        this.fpOffset += delta;
    }

    public int getFpOffset() {
        return fpOffset;
    }

    public RegisterFile getRegisterFile() {
        return registerFile;
    }

    public MipsSymbol getSymbol(String name) {
        if (hasSymbol(name)) {
            return this.symbols.get(name);
        } else {
            System.out.println("ERROR in MipsSymbolTable : should not reach here");
        }
        return null;
    }
}
