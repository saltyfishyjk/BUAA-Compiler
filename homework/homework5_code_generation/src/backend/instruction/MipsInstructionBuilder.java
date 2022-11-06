package backend.instruction;

import backend.basicblock.MipsBasicBlock;
import backend.symbol.MipsSymbol;
import backend.symbol.MipsSymbolTable;
import middle.llvmir.value.instructions.IrBinaryInst;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.memory.IrAlloca;
import middle.llvmir.value.instructions.memory.IrLoad;
import middle.llvmir.value.instructions.memory.IrStore;
import middle.llvmir.value.instructions.terminator.IrCall;
import middle.llvmir.value.instructions.terminator.IrRet;

import java.util.ArrayList;

/**
 * Mips Instruction Builder : Mips指令生成器
 */
public class MipsInstructionBuilder {
    private IrInstruction irInstruction;
    private MipsBasicBlock father; // 父BasicBlock
    private MipsSymbolTable table;

    public MipsInstructionBuilder(MipsBasicBlock father, IrInstruction irInstruction) {
        this.irInstruction = irInstruction;
        this.father = father;
        this.table = this.father.getTable();
    }

    public ArrayList<MipsInstruction> genMipsInstruction() {
        if (irInstruction instanceof IrAlloca) {
            return genMipsInstructionFromAlloca();
        } else if (irInstruction instanceof IrBinaryInst) {
            return genMipsInstructionFromBinary();
        } else if (irInstruction instanceof IrCall) {
            return genMipsInstructionFromCall();
        } else if (irInstruction instanceof IrLoad) {
            return genMipsInstructionFromLoad();
        } else if (irInstruction instanceof IrRet) {
            return genMipsInstructionFromRet();
        } else if (irInstruction instanceof IrStore) {
            return genMipsInstructionFromStore();
        } else {
            System.out.println("ERROR in MipsInstructionBuilder : should not reach here");
        }
        return null;
    }

    /* IrAlloca -> MipsInstruction */
    private ArrayList<MipsInstruction> genMipsInstructionFromAlloca() {
        /* alloca是LLVM IR中的变量声明语句，其本意是申请内存空间
         * 在这里，我们为了提高性能，在alloca时仅将其加入符号表，暂时不为其分配寄存器和内存 */
        IrAlloca alloca = (IrAlloca)irInstruction;
        String name = alloca.getName();
        MipsSymbol symbol = new MipsSymbol(name, 30);
        insertSymbolTable(name, symbol);
        return null;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromBinary() {
        IrBinaryInst inst = (IrBinaryInst)irInstruction;
        /* TODO : 待施工 */
        return null;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromCall() {
        IrCall call = (IrCall)irInstruction;
        /* TODO : 待施工 */
        return null;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromLoad() {
        IrLoad load = (IrLoad)irInstruction;
        /* TODO : 待施工 */
        return null;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromRet() {
        IrRet ret = (IrRet)irInstruction;
        /* TODO : 待施工 */
        return null;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromStore() {
        IrStore store = (IrStore)irInstruction;
        /* TODO : 待施工 */
        return null;
    }

    private void insertSymbolTable(String name, MipsSymbol symbol) {
        this.table.addSymbol(name, symbol);
    }
}
