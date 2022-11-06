package backend.instruction;

import backend.RegisterFile;
import backend.basicblock.MipsBasicBlock;
import backend.symbol.MipsSymbol;
import backend.symbol.MipsSymbolTable;
import middle.llvmir.IrValue;
import middle.llvmir.value.instructions.IrBinaryInst;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;
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
    private RegisterFile registerFile;

    public MipsInstructionBuilder(MipsBasicBlock father, IrInstruction irInstruction) {
        this.irInstruction = irInstruction;
        this.father = father;
        this.table = this.father.getTable();
        this.registerFile = this.table.getRegisterFile();
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
        /* TODO : 这里未处理数组 */
        IrAlloca alloca = (IrAlloca)irInstruction;
        String name = alloca.getName();
        MipsSymbol symbol = new MipsSymbol(name, 30);
        insertSymbolTable(name, symbol);
        return null;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromBinary() {
        IrBinaryInst inst = (IrBinaryInst)irInstruction;
        /* 获取左操作数所在寄存器 */
        IrValue left = inst.getLeft();
        String leftName = left.getName();
        int leftReg = -1;
        MipsSymbol leftSymbol;
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        if (isConst(leftName)) {
            // 是常数
            leftSymbol = new MipsSymbol("temp", -1);
            leftReg = this.registerFile.getReg(true, leftSymbol, this.father);
            // 找到一个临时寄存器，用li装入
            Li li = new Li(leftReg, Integer.valueOf(leftName));
            ret.add(li);
        } else {
            // 是变量
            leftReg = this.table.getRegIndex(leftName, this.father);
            leftSymbol = this.table.getSymbol(leftName);
        }
        /* 获取右操作数所在寄存器 */
        IrValue right = inst.getRight();
        String rightName = right.getName();
        MipsSymbol rightSymbol;
        int rightReg = -1;
        if (isConst(rightName)) {
            rightSymbol = new MipsSymbol("temp", -1);
            rightReg = this.registerFile.getReg(true, rightSymbol, this.father);
            // 找到一个临时寄存器，用li装入
            Li li = new Li(rightReg, Integer.valueOf(rightName));
            ret.add(li);
        } else {
            rightReg = this.table.getRegIndex(rightName, this.father);
            rightSymbol = this.table.getSymbol(rightName);
        }
        String ansName = inst.getName();
        /* 生成答案临时变量符号 */
        MipsSymbol ansSymbol = new MipsSymbol(ansName, 30, false,
                -1, false, -1, true, false);
        insertSymbolTable(ansName, ansSymbol);
        int ansReg = this.table.getRegIndex(ansName, this.father);
        if (inst.getInstructionType().equals(IrInstructionType.Add)) {
            // +
            Add add = new Add(ansReg, leftReg, rightReg);
            ret.add(add);
        } else if (inst.getInstructionType().equals(IrInstructionType.Sub)) {
            // -
            Sub sub = new Sub(ansReg, leftReg, rightReg);
            ret.add(sub);
        } else if (inst.getInstructionType().equals(IrInstructionType.Mul)) {
            // *
            Mul mul = new Mul(ansReg, leftReg, rightReg);
            ret.add(mul);
        } else if (inst.getInstructionType().equals(IrInstructionType.Div)) {
            // /
            Div div = new Div(ansReg, leftReg, rightReg);
            ret.add(div);
        } else if (inst.getInstructionType().equals(IrInstructionType.Mod)) {
            // %
            Div div = new Div(-1, leftReg, rightReg);
            ret.add(div);
            Mfhi mfhi = new Mfhi(ansReg);
            ret.add(mfhi);
        } else {
            System.out.println("ERROR in MipsInstructionBuilder : should not reach here");
        }

        /* 将左右操作数标记为已使用，方便释放寄存器 */
        leftSymbol.setUsed(true);
        rightSymbol.setUsed(true);
        return ret;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromCall() {
        IrCall call = (IrCall)irInstruction;
        /* TODO : 待施工 */
        return null;
    }

    /* IrLoad -> MipsInstruction */
    private ArrayList<MipsInstruction> genMipsInstructionFromLoad() {
        /* IrLoad左侧的变量都是新临时变量，用于取用全局变量或局部变量 */
        /* 全局变量直接从lw从内存中加载 */
        /* 局部变量若位于寄存器中，则move*/
        /* 局部变量若位于内存中，则lw */
        IrLoad left = (IrLoad)irInstruction;
        /* TODO : 待施工 */
        String leftName = left.getName();
        IrValue right = left.getOperand(0);
        String rightName = right.getName();
        /* 生成左部临时变量符号 */
        MipsSymbol leftSymbol = new MipsSymbol(leftName, 30, false,
                -1, false, -1, true, false);
        insertSymbolTable(leftName, leftSymbol);
        int leftReg = this.registerFile.getReg(true, leftSymbol, this.father);
        /* 获取右部变量 */
        MipsSymbol rightSymbol = this.table.getSymbol(rightName);
        // int rightReg = this.registerFile.getReg(rightSymbol.isTemp(), rightSymbol, this.father);
        int rightReg = this.table.getRegIndex(rightName, this.father);
        Move move = new Move(leftReg, rightReg);
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        ret.add(move);
        return ret;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromRet() {
        IrRet ret = (IrRet)irInstruction;
        /* TODO : 待施工 */
        return null;
    }

    /* IrStore -> MipsInstruction */
    private ArrayList<MipsInstruction> genMipsInstructionFromStore() {
        /* 将某个常数/变量的值赋给某个变量 */
        IrStore store = (IrStore)irInstruction;
        /* store left to right */
        IrValue left = store.getOperand(0);
        IrValue right = store.getOperand(1);
        String leftName = left.getName();
        String rightName = right.getName();
        int leftReg = -1; // 左操作数寄存器
        int rightReg = -1;
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        /* 获取左操作数的寄存器 */
        if (isConst(leftName)) {
            // 常数，需要从寄存器表获取一个$t并使用li将该立即数加载进去
            // 然后使用move进行赋值
            // 这里的Symbol不应当被加入符号表
            leftReg = this.registerFile.getReg(true, new MipsSymbol("temp", -1), this.father);
            Li li = new Li(leftReg, Integer.valueOf(leftName));
            ret.add(li);
        } else {
            // 变量
            leftReg = this.table.getRegIndex(rightName, this.father);
        }
        /* TODO : 获取右操作数的寄存器 */
        rightReg = this.table.getRegIndex(rightName, this.father);
        /* TODO : 待施工 */
        Move move = new Move(rightReg, leftReg);
        ret.add(move);
        return ret;
    }
    /* TODO : 有bug还没改完 */

    private void insertSymbolTable(String name, MipsSymbol symbol) {
        this.table.addSymbol(name, symbol);
    }

    private boolean isConst(String name) {
        if (!(name.contains("@") || name.contains("%"))) {
            return true;
        } else {
            return false;
        }
    }
}
