package backend.symbol;

import backend.RegisterFile;
import backend.basicblock.MipsBasicBlock;
import backend.instruction.Lw;
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
        this.registerFile = registerFile;
    }

    public HashMap<String, MipsSymbol> cloneSymbols() {
        HashMap<String, MipsSymbol> newSymbols = new HashMap<>();
        for (String index : this.symbols.keySet()) {
            newSymbols.put(index, this.symbols.get(index).cloneMipsSymbol());
        }
        return newSymbols;
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
    public int getRegIndex(String name, MipsBasicBlock basicBlock, boolean load) {
        MipsSymbol symbol = this.symbols.get(name);
        /* 如果MipsSymbol直接在寄存器中，返回 */
        if (symbol.isInReg()) {
            return symbol.getRegIndex();
        } else {
            int reg = this.registerFile.getReg(symbol.isTemp(), symbol, basicBlock);
            if (load) {
                Lw lw = new Lw(reg, symbol.getBase(), symbol.getOffset());
                ArrayList<MipsInstruction> temp = new ArrayList<>();
                temp.add(lw);
                basicBlock.addInstruction(temp);
            }
            return reg;
        }
        // return symbol.getRegIndex();
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
            // System.out.println("ERROR in MipsSymbolTable : should not reach here");
        }
        return null;
    }

    public HashMap<String, MipsSymbol> getSymbols() {
        return symbols;
    }

    public void setFpOffset(int fpOffset) {
        this.fpOffset = fpOffset;
    }

    public void setSymbols(HashMap<String, MipsSymbol> symbols) {
        this.symbols = symbols;
    }
}
