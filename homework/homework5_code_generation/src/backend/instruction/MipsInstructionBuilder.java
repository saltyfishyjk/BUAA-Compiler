package backend.instruction;

import backend.basicblock.MipsBasicBlock;
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

    public MipsInstructionBuilder(MipsBasicBlock father, IrInstruction irInstruction) {
        this.irInstruction = irInstruction;
        this.father = father;
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
        IrAlloca alloca = (IrAlloca)irInstruction;
        /* TODO : 待施工 */
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
}
