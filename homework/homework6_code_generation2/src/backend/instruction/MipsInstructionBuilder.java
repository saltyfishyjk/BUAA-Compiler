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
            // leftSymbol = new MipsSymbol("temp", -1);
            leftSymbol = new MipsSymbol("temp", 30, false, -1, false,
                    -1, true, false);
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
            // rightSymbol = new MipsSymbol("temp", -1);
            rightSymbol = new MipsSymbol("temp", 30, false, -1, false,
                    -1, true, false);
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
        String functionName = call.getFunctionName();
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        if (functionName.equals("@putint")) {
            // putint
            Move move = new Move(3, 4);
            ret.add(move);
            Li li = new Li(2, 1);
            ret.add(li);
            String name = call.getOperand(1).getName();
            int reg = this.table.getRegIndex(name, this.father);
            move = new Move(4, reg);
            ret.add(move);
            Syscall syscall = new Syscall();
            ret.add(syscall);
            move = new Move(4, 3);
            ret.add(move);
        } else if (functionName.equals("@putch")) {
            // 不应当进入本分支，因为打印字符串已经在MipsBasicBlockBuilder中处理完了
            System.out.println("ERROR in Mips InstructionBuilder : should not reach here");
        } else if (functionName.equals("@getint")) {
            // getint
            ret = genMipsInstructionFromGetIntFunc();
        } else {
            // 普通函数调用
            ret = genMipsInstructionFromSelfDefineFunc();
        }
        return ret;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromGetIntFunc() {
        IrCall call = (IrCall)irInstruction;
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        /* 将$v0移入$v1做保护 */
        Move move = new Move(3, 2);
        ret.add(move);
        /* 将立即数5装入$v0 */
        Li li = new Li(2, 5);
        ret.add(li);
        /* 系统调用 */
        Syscall syscall = new Syscall();
        ret.add(syscall);
        /* 获取被赋值变量的寄存器编号 */
        // int reg = this.table.getRegIndex(call.getName(), this.father);
        MipsSymbol symbol = new MipsSymbol(call.getName(), 30, false, -1,
                false, -1, false, false);
        insertSymbolTable(symbol.getName(), symbol);
        int reg = this.table.getRegIndex(symbol.getName(), this.father);
        move = new Move(reg, 2);
        ret.add(move);
        /* 将原$v0的值移回 */
        move = new Move(2, 3);
        ret.add(move);
        return ret;
    }

    /* 调用自定义函数 */
    private ArrayList<MipsInstruction> genMipsInstructionFromSelfDefineFunc() {
        IrCall call = (IrCall)irInstruction;
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        /* TODO : 1. 保存现场到$sp */
        int spOffset = 0;
        for (int i = 2; i < 32; i++) {
            if (26 <= i && i <= 30) {
                continue;
            }
            if (this.registerFile.inReg(i) ||  i == 31) {
                Sw sw = new Sw(i, 29, spOffset);
                ret.add(sw);
                spOffset -= 4;
            }
        }
        /* TODO : 2. 实参存入寄存器与内存（如果有）$fp */
        ArrayList<IrValue> params = call.getParams();
        int len = params.size();
        /* 将子函数fp装入v1 */
        Addi addi = new Addi(3, 30, this.table.getFpOffset());
        ret.add(addi);
        /* TODO : 可能有风险（寄存器表） */
        for (int i = 0; i < 4 && i < len; i++) {
            IrValue param = params.get(i);
            String name = param.getName();
            int reg = this.table.getRegIndex(name, this.father);
            Move move = new Move(4 + i, reg);
            if (this.table.hasSymbol(name)) {
                this.table.getSymbol(name).setUsed(true);
            }
            ret.add(move);
        }
        int fpOffset = 0;
        for (int i = 4; i < len; i++) {
            IrValue param = params.get(i);
            String name = param.getName();
            int reg = this.table.getRegIndex(name, this.father);
            Sw sw = new Sw(reg, 3, fpOffset);
            if (this.table.hasSymbol(name)) {
                this.table.getSymbol(name).setUsed(true);
            }
            ret.add(sw);
            fpOffset += 4;
        }

        /* TODO : 3. 修改$fp, $sp */
        /* fp */
        Move move = new Move(30, 3);
        ret.add(move);
        /* sp */
        addi = new Addi(29, 29, spOffset);
        ret.add(addi);


        /* TODO : 4. jal跳转 */
        Jal jal = new Jal(call.getFunctionName().substring(1));
        ret.add(jal);

        /* TODO : 5. 恢复$fp现场，本质上是通过MipsSymbolTable的fpOffset自减 */
        addi = new Addi(30, 30, -fpOffset);
        ret.add(addi);
        /* TODO : 6. 恢复$sp现场，本质上是通过讲$sp自增至原值，将$ra和其他保存寄存器的值恢复 */
        addi = new Addi(29, 29, -spOffset);
        ret.add(addi);
        for (int i = 31; i >= 2; i--) {
            if (26 <= i && i <= 30) {
                continue;
            }
            if (this.registerFile.inReg(i) || i == 31) {
                spOffset += 4;
                Lw lw = new Lw(i, 29, spOffset);
                ret.add(lw);
            }
        }
        /* TODO : 7. 可能会有一个左值赋值 */
        if (call.getName().length() > 0) {
            /* 有赋值需求 */
            MipsSymbol leftSymbol = new MipsSymbol(call.getName(), 30, false, -1, false,
                    0, true, false);
            insertSymbolTable(leftSymbol.getName(), leftSymbol);
            int regLeft = this.table.getRegIndex(call.getName(), this.father);
            move = new Move(regLeft, 2);
            ret.add(move);
        }
        return ret;
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
        ArrayList<MipsInstruction> ans = new ArrayList<>();
        if (!ret.isVoid()) {
            /* 返回值为int的函数需要将返回值存入$v0即$2 */
            String name = ret.getOperand(0).getName();
            int reg;
            if (isConst(name)) {
                // 常数，需要从寄存器表获取一个$t并使用li将该立即数加载进去
                // 然后使用move进行赋值
                // 这里的Symbol不应当被加入符号表
                reg = this.registerFile.getReg(true, new MipsSymbol("temp", -1), this.father);
                Li li = new Li(reg, Integer.valueOf(name));
                ans.add(li);
            } else {
                // 变量
                reg = this.table.getRegIndex(name, this.father);
            }
            Move move = new Move(2, reg);
            ans.add(move);
        }
        return ans;
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
            MipsSymbol tempSymbol = new MipsSymbol("name", 30, false, -1, false,
                    -1, true, false);
            leftReg = this.registerFile.getReg(true, tempSymbol, this.father);
            Li li = new Li(leftReg, Integer.valueOf(leftName));
            ret.add(li);
        } else {
            // 变量
            leftReg = this.table.getRegIndex(leftName, this.father);
        }
        /* TODO : 需要检查 获取右操作数的寄存器 */
        rightReg = this.table.getRegIndex(rightName, this.father);
        /* TODO : 待施工 */
        Move move = new Move(rightReg, leftReg);
        this.registerFile.getSymbol(leftReg).setUsed(true);
        /*if (this.table.hasSymbol(leftName)) {
            this.table.getSymbol(leftName).setUsed(true);
        }*/
        this.registerFile.getSymbol(leftReg).setUsed(true);
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
