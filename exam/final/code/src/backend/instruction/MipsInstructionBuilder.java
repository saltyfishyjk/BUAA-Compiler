package backend.instruction;

import backend.RegisterFile;
import backend.basicblock.MipsBasicBlock;
import backend.symbol.MipsSymbol;
import backend.symbol.MipsSymbolTable;
import middle.llvmir.IrValue;
import middle.llvmir.value.instructions.IrBinaryInst;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.IrInstructionType;
import middle.llvmir.value.instructions.IrLabel;
import middle.llvmir.value.instructions.memory.IrAlloca;
import middle.llvmir.value.instructions.memory.IrLoad;
import middle.llvmir.value.instructions.memory.IrStore;
import middle.llvmir.value.instructions.terminator.IrBr;
import middle.llvmir.value.instructions.terminator.IrCall;
import middle.llvmir.value.instructions.terminator.IrGoto;
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
        } else if (irInstruction instanceof IrGoto) {
            return genMipsInstructionFromGoto();
        } else if (irInstruction instanceof IrLabel) {
            return genMipsInstructionFromLabel();
        } else if (irInstruction instanceof IrBr) {
            return genMipsInstructionFromBr();
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
        int dimension = alloca.getDimension();
        MipsSymbol symbol = null;
        if (dimension == 0) {
            /* 0维变量声明 */
            symbol = new MipsSymbol(name, 30);
        } else if (dimension == 1) {
            /* 1维变量声明 */
            symbol = new MipsSymbol(name, 30);
            symbol.setDimension(dimension);
            symbol.setDimension1(alloca.getDimension1());
            // 1维数组的起始位置的偏移
            int fpOffset = this.table.getFpOffset();
            symbol.setOffset(fpOffset);
            /* 标记已为其分配内存 */
            symbol.setHasRam(true);
            /* 将fp上移并保存 */
            fpOffset += 4 * symbol.getDimension1();
            this.table.setFpOffset(fpOffset);
        } else if (dimension == 2) {
            /* 2维变量声明 */
            symbol = new MipsSymbol(name, 30);
            /* 标记两层维数 */
            symbol.setDimension(dimension);
            symbol.setDimension1(alloca.getDimension1());
            symbol.setDimension2(alloca.getDimension2());
            /* 2维数组的起始位置的偏移 */
            int fpOffset = this.table.getFpOffset();
            symbol.setOffset(fpOffset);
            /* 标记已为其分配内存 */
            symbol.setHasRam(true);
            /* 将fp上移并保存 */
            fpOffset += 4 * symbol.getDimension1() * symbol.getDimension2();
            this.table.setFpOffset(fpOffset);
        }
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
            leftReg = this.table.getRegIndex(leftName, this.father, false);
            leftSymbol = this.table.getSymbol(leftName);
        }
        /* 处理右操作数*/
        IrValue right = null;
        if (inst.getInstructionType() != IrInstructionType.Not) {
            right = inst.getRight();
        }
        int rightReg = -1;
        MipsSymbol rightSymbol = null;
        /* 获取右操作数所在寄存器 */
        if (right != null) {
            String rightName = right.getName();
            if (isConst(rightName)) {
                // rightSymbol = new MipsSymbol("temp", -1);
                rightSymbol = new MipsSymbol("temp", 30, false, -1, false,
                        -1, true, false);
                rightReg = this.registerFile.getReg(true, rightSymbol, this.father);
                // 找到一个临时寄存器，用li装入
                Li li = new Li(rightReg, Integer.valueOf(rightName));
                ret.add(li);
            } else {
                rightReg = this.table.getRegIndex(rightName, this.father, false);
                rightSymbol = this.table.getSymbol(rightName);
            }
        } else {
            if (inst.getInstructionType().equals(IrInstructionType.Not)) {
                /* 右操作数为null，说明为Not语句 */
            } else {
                System.out.println("ERROR IN MipsInstructionBuilder : should not reach here");
            }
        }
        String ansName = inst.getName();
        /* 生成答案临时变量符号 */
        MipsSymbol ansSymbol = new MipsSymbol(ansName, 30, false,
                -1, false, -1, true, false);
        insertSymbolTable(ansName, ansSymbol);
        int ansReg = this.table.getRegIndex(ansName, this.father, false);
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
        } else if (inst.getInstructionType().equals(IrInstructionType.Bitand)) {
            And and = new And(ansReg, leftReg, rightReg);
            ret.add(and);
        }
        
        else if (inst.getInstructionType().equals(IrInstructionType.Lt)) {
            /* < */
            Slt slt = new Slt(ansReg, leftReg, rightReg);
            ret.add(slt);
        } else if (inst.getInstructionType().equals(IrInstructionType.Le)) {
            /* <= */
            Sle sle = new Sle(ansReg, leftReg, rightReg);
            ret.add(sle);
        } else if (inst.getInstructionType().equals(IrInstructionType.Gt)) {
            /* > */
            Sgt sgt = new Sgt(ansReg, leftReg, rightReg);
            ret.add(sgt);
        } else if (inst.getInstructionType().equals(IrInstructionType.Ge)) {
            /* >= */
            Sge sge = new Sge(ansReg, leftReg, rightReg);
            ret.add(sge);
        } else if (inst.getInstructionType().equals(IrInstructionType.Eq)) {
            /* ==*/
            Seq seq = new Seq(ansReg, leftReg, rightReg);
            ret.add(seq);
        } else if (inst.getInstructionType().equals(IrInstructionType.Ne)) {
            /* != */
            Sne sne = new Sne(ansReg, leftReg, rightReg);
            ret.add(sne);
        } else if (inst.getInstructionType().equals(IrInstructionType.Not)) {
            /* ! */
            Li li = new Li(3, 0);
            Seq seq = new Seq(ansReg, leftReg, 3);
            ret.add(li);
            ret.add(seq);
        } else {
            System.out.println("ERROR in MipsInstructionBuilder : should not reach here");
        }

        /* 将左右操作数标记为已使用，方便释放寄存器 */
        leftSymbol.setUsed(true);
        if (rightSymbol != null) {
            rightSymbol.setUsed(true);
        }
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
            MipsSymbol symbol = null;
            if (this.table.hasSymbol(name)) {
                symbol = this.table.getSymbol(name);
                int reg = this.table.getRegIndex(name, this.father, true);
                move = new Move(4, reg);
                ret.add(move);
            } else {
                /* 说明要打印的是立即数 */
                li = new Li(4, Integer.valueOf(name));
                ret.add(li);
            }
            Syscall syscall = new Syscall();
            ret.add(syscall);
            move = new Move(4, 3);
            ret.add(move);
            if (symbol != null) {
                symbol.setUsed(true);
            }
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
                false, -1, true, false);
        insertSymbolTable(symbol.getName(), symbol);
        int reg = this.table.getRegIndex(symbol.getName(), this.father, false);
        move = new Move(reg, 2);
        ret.add(move);
        /* 将原$v0的值移回 */
        move = new Move(2, 3);
        ret.add(move);
        return ret;
    }

    /* 调用自定义函数 */
    private ArrayList<MipsInstruction> genMipsInstructionFromSelfDefineFunc() {
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        ArrayList<MipsInstruction> temp = this.registerFile.writeBackAll();
        if (temp != null && temp.size() > 0) {
            ret.addAll(temp);
        }
        if (ret.size() > 0) {
            this.father.addInstruction(ret);
            ret = new ArrayList<>();
        }
        /* 1. 保存现场到$sp */
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
        
        
        /* 实参存入寄存器与内存（如果有）$fp */
        /* 将子函数fp装入v1 */
        Addi addi = new Addi(3, 30, (this.table.getFpOffset() + 32 * 4));
        ret.add(addi);
        this.father.addInstruction(ret);
        ret = new ArrayList<>();
        /* 应当建立新表 */
        /* 深拷贝 */
        RegisterFile newRegisterFile = new RegisterFile();
        MipsSymbolTable newTable = new MipsSymbolTable(newRegisterFile);
        newTable.setSymbols(this.table.cloneSymbols());
        newTable.setFpOffset(this.table.getFpOffset());
        newRegisterFile.setTable(newTable);
        newRegisterFile.setHasValues(this.registerFile.cloneHasValues());
        newRegisterFile.setRegNum(this.registerFile.getRegNum());
        newRegisterFile.setRegs(this.registerFile.cloneRegs());
        newRegisterFile.setSregUse(this.registerFile.cloneSregUse());
        IrCall call = (IrCall)irInstruction;
        ArrayList<IrValue> params = call.getParams();
        int len = params.size();
        // for (int i = 0; i < 4 && i < len; i++) {
        int newFpOffset = 0;
        for (int i = 0; i < len; i++) {
            IrValue param = params.get(i);
            String name = param.getName();
            if (newTable.hasSymbol(name)) {
                /* 分情况讨论包括符号是否为数组，传入的维数，符号是否为形参等 */
                int symbolDimension = param.getDimension();
                if (symbolDimension == 0) {
                    /* 说明符号本身是0维的，进行值传递 */
                    int reg = newTable.getRegIndex(name, this.father, true);
                    if (i < 4) {
                        /* 存入$a */
                        Move move = new Move(4 + i, reg);
                        newTable.getSymbol(name).setUsed(true);
                        ret.add(move);
                        this.father.addInstruction(ret);
                        ret = new ArrayList<>();
                    } else {
                        /* 存入内存 */
                        Sw sw = new Sw(reg, 3, newFpOffset);
                        ret.add(sw);
                        this.father.addInstruction(ret);
                        ret = new ArrayList<>();
                    }
                } else if (symbolDimension == 1) {
                    /* 说明符号本身是1维的，分情况讨论 */
                    boolean isParam = param.isParam();
                    if (isParam) {
                        /* 说明符号是形参，应当从内存访问 */
                        int dimensionValue = param.getDimensionValue();
                        if (dimensionValue == 0) {
                            /* 说明取用形参的内存单元进行值传递 */
                            /* 计算偏移 */
                            IrValue dimension1 = param.getDimension1Value();
                            String dimension1Name = dimension1.getName();
                            if (isConst(dimension1Name)) {
                                /* 说明是常数 */
                                Li li = new Li(2, Integer.valueOf(dimension1Name));
                                ret.add(li);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                /* <<2 */
                                Sll sll = new Sll(2, 2, 2);
                                ret.add(sll);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 获取该维数变量所在的寄存器 */
                                int dimension1Reg = newTable.getRegIndex(dimension1Name,
                                        this.father, true);
                                /* <<2 */
                                Sll sll = new Sll(2, dimension1Reg, 2);
                                ret.add(sll);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            /* 获取该形参数组的首地址(绝对地址) */
                            int reg = newTable.getRegIndex(name, this.father, true);
                            /* 获取内存单元的绝对地址 */
                            Add add = new Add(2, 2, reg);
                            ret.add(add);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            if (i < 4) {
                                /* 存入$a */
                                /* 将该内存单元中的值加载到$a寄存器 */
                                Lw lw = new Lw(4 + i, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                /* 首先将目标内存单元加载到2号寄存器 */
                                Lw lw = new Lw(2, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                /* 再将该内存单元转存到目标内存中 */
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else if (dimensionValue == 1) {
                            /* 说明传递形参，由于其已经是绝对地址了，因此直接加载 */
                            /* 获取形参数组首地址的绝对地址 */
                            int reg = newTable.getRegIndex(name, this.father, true);
                            if (i < 4) {
                                /* 将该首地址加载到$a寄存器 */
                                Move move = new Move(4 + i, reg);
                                ret.add(move);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                Sw sw = new Sw(reg, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else {
                            System.out.println("ERROR IN MipsInstructionBuilder : " +
                                    "should not reach here");
                        }
                    } else {
                        /* 说明符号不是形参，正常访问 */
                        int dimensionValue = param.getDimensionValue();
                        /* 获取符号 */
                        MipsSymbol symbol = this.table.getSymbol(name);
                        /* 获取相对于fp/gp的偏移 */
                        int fpOffset = symbol.getOffset();
                        /* 获取base */
                        int base = symbol.getBase();
                        if (dimensionValue == 0) {
                            /* 说明取用内存单元进行值传递 */
                            /* 计算偏移 */
                            IrValue dimension1 = param.getDimension1Value();
                            String dimension1Name = dimension1.getName();
                            if (isConst(dimension1Name)) {
                                /* 说明是常数 */
                                Li li = new Li(2, Integer.valueOf(dimension1Name));
                                ret.add(li);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                /* <<2 */
                                Sll sll = new Sll(2, 2, 2);
                                ret.add(sll);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 获取该维数变量所在的寄存器 */
                                int dimension1Reg = newTable.getRegIndex(dimension1Name, 
                                        this.father, true);
                                /* <<2 */
                                Sll sll = new Sll(2, dimension1Reg, 2);
                                ret.add(sll);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            /* 计算总偏移并装入2号寄存器 */
                            addi = new Addi(2, 2, fpOffset);
                            ret.add(addi);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 计算绝对地址 */
                            Add add = new Add(2, 2, base);
                            ret.add(add);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            if (i < 4) {
                                /* 装入$a */
                                Lw lw = new Lw(4 + i, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 装入内存 */
                                /* 首先将目标内存单元加载到2号寄存器中 */
                                Lw lw = new Lw(2, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                /* 存入内存 */
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else if (dimensionValue == 1) {
                            /* 说明进行地址传递，需要计算出数组首元素的绝对地址 */
                            if (i < 4) {
                                /* 将reg(base) + fpOffset装入$a */
                                addi = new Addi(4 + i, base, fpOffset);
                                ret.add(addi);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                addi = new Addi(2, base, fpOffset);
                                ret.add(addi);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else {
                            System.out.println("ERROR IN MipsInstructionBuilder :" +
                                    " should not reach here");
                        }
                    }    
                } else if (symbolDimension == 2) {
                    /* 说明符号本身是2维的，分情况讨论 */
                    boolean isParam = param.isParam();
                    if (isParam) {
                        /* 说明是形参，对内存单元访问要访问内存 */
                        int dimensionValue = param.getDimensionValue();
                        if (dimensionValue == 0) {
                            /* 形如a[i][j]，计算公式为i * n + j */
                            /* 说明访问一个内存单元 */
                            IrValue dimension1 = param.getDimension1Value();
                            String dimension1Name = dimension1.getName();
                            if (isConst(dimension1Name)) {
                                /* 说明是常数 */
                                Li li = new Li(2, Integer.valueOf(dimension1Name));
                                ret.add(li);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 获取该维数变量所在的寄存器 */
                                int dimension1Reg = newTable.getRegIndex(dimension1Name,
                                        this.father, true);
                                /* 将该变量移入2号寄存器 */
                                Move move = new Move(2, dimension1Reg);
                                ret.add(move);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            /* 获取n */
                            int n = param.getDimension2();
                            /* 计算i * n并装入2号寄存器 */
                            MulImm mulImm = new MulImm(2, 2, n);
                            ret.add(mulImm);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 计算i * n + j并装入2号寄存器 */
                            IrValue dimension2 = param.getDimension2Value();
                            String dimension2Name = dimension2.getName();
                            if (isConst(dimension2Name)) {
                                /* 说明是常数 */
                                addi = new Addi(2, 2, Integer.valueOf(dimension2Name));
                                ret.add(addi);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 获取该维数变量所在的寄存器 */
                                int dimension2Reg = newTable.getRegIndex(dimension2Name, 
                                        this.father, true);
                                /* 累加入2号寄存器 */
                                Add add = new Add(2, 2, dimension2Reg);
                                ret.add(add);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            /* <<2 */
                            Sll sll = new Sll(2, 2, 2);
                            ret.add(sll);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 获取数组首地址绝对地址 */
                            int reg = newTable.getRegIndex(name, this.father, true);
                            /* 将数组首地址绝对地址和偏移相加得到目标内存单元绝对地址 */
                            Add add = new Add(2, 2, reg);
                            ret.add(add);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            if (i < 4) {
                                /* 将该内存单元的值加载到$a中 */
                                Lw lw = new Lw(4 + i, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                Lw lw = new Lw(2, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else if (dimensionValue == 1) {
                            /* 说明传递其中一个1维数组的地址，需要计算出其绝对地址 */
                            /* 声明如a[m][n]，传入如a[i] */
                            /* 计算方式为abs + i * n */
                            /* 获取i */
                            IrValue dimension1 = param.getDimension1Value();
                            String dimension1Name = dimension1.getName();
                            if (isConst(dimension1Name)) {
                                Li li = new Li(2, Integer.valueOf(dimension1Name));
                                ret.add(li);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                int reg1 = newTable.getRegIndex(dimension1.getName(),
                                        this.father, true);
                                Move move = new Move(2, reg1);
                                ret.add(move);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            /* 获取n */
                            int n = param.getDimension2();
                            /* 计算i * n并装入2号寄存器 */
                            MulImm mulImm = new MulImm(2, 2, n);
                            ret.add(mulImm);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* <<2 */
                            Sll sll = new Sll(2, 2, 2);
                            ret.add(sll);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 计算绝对地址 */
                            int reg = newTable.getRegIndex(param.getName(),
                                    this.father, true);
                            if (i < 4) {
                                /* 存入$a */
                                Add add = new Add(4 + i, 2, reg);
                                ret.add(add);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                Add add = new Add(2, 2, reg);
                                ret.add(add);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else if (dimensionValue == 2) {
                            /* 说明直接传递本身即可 */
                            int reg = newTable.getRegIndex(param.getName(), this.father, true);
                            if (i < 4) {
                                /* 存入$a */
                                Move move = new Move(4 + i, reg);
                                ret.add(move);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                Sw sw = new Sw(reg, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else {
                            System.out.println("ERROR IN MipsInstructionBuilder : " +
                                    "should not reach here");
                        }
                    } else {
                        /* 说明不是形参，对内存单元直接访问 */
                        int dimensionValue = param.getDimensionValue();
                        /* 获取符号 */
                        MipsSymbol symbol = this.table.getSymbol(name);
                        /* 获取相对于fp/gp的偏移 */
                        int fpOffset = symbol.getOffset();
                        /* 获取base */
                        int base = symbol.getBase();
                        if (dimensionValue == 0) {
                            /* 说明访问一个内存单元 */
                            /* 形如a[i][j]，计算公式为i * n + j */
                            /* 说明访问一个内存单元 */
                            IrValue dimension1 = param.getDimension1Value();
                            String dimension1Name = dimension1.getName();
                            if (isConst(dimension1Name)) {
                                /* 说明是常数 */
                                Li li = new Li(2, Integer.valueOf(dimension1Name));
                                ret.add(li);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 获取该维数变量所在的寄存器 */
                                int dimension1Reg = newTable.getRegIndex(dimension1Name, 
                                        this.father, true);
                                /* 将该变量移入2号寄存器 */
                                Move move = new Move(2, dimension1Reg);
                                ret.add(move);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            /* 获取n */
                            int n = param.getDimension2();
                            /* 计算i * n并装入2号寄存器 */
                            MulImm mulImm = new MulImm(2, 2, n);
                            ret.add(mulImm);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 计算i * n + j并装入2号寄存器 */
                            IrValue dimension2 = param.getDimension2Value();
                            String dimension2Name = dimension2.getName();
                            if (isConst(dimension2Name)) {
                                /* 说明是常数 */
                                addi = new Addi(2, 2, Integer.valueOf(dimension2Name));
                                ret.add(addi);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 获取该维数变量所在的寄存器 */
                                int dimension2Reg = newTable.getRegIndex(dimension2Name, 
                                        this.father, true);
                                /* 累加入2号寄存器 */
                                Add add = new Add(2, 2, dimension2Reg);
                                ret.add(add);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            /* <<2 */
                            Sll sll = new Sll(2, 2, 2);
                            ret.add(sll);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 获取目标内存单元相对于base的偏移 */
                            addi = new Addi(2, 2, fpOffset);
                            ret.add(addi);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 获取目标内存单元绝对地址 */
                            Add add = new Add(2, 2, base);
                            ret.add(add);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            if (i < 4) {
                                /* 将该内存单元的值加载到$a中 */
                                Lw lw = new Lw(4 + i, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                Lw lw = new Lw(2, 2, 0);
                                ret.add(lw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else if (dimensionValue == 1) {
                            /* 说明传递其中一个1维数组的地址，需要计算出绝对地址 */
                            /* 计算公式为(base) + fpOffset + i * n */
                            IrValue dimension1 = param.getDimension1Value();
                            String dimension1Name = dimension1.getName();
                            /* 计算i */
                            if (isConst(dimension1Name)) {
                                Li li = new Li(2, Integer.valueOf(dimension1Name));
                                ret.add(li);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                int reg = newTable.getRegIndex(dimension1Name, this.father, true);
                                Move move = new Move(2, reg);
                                ret.add(move);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                            int n = param.getDimension2();
                            /* 计算i * n */
                            MulImm mulImm = new MulImm(2, 2, n);
                            ret.add(mulImm);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* <<2 */
                            Sll sll = new Sll(2, 2, 2);
                            ret.add(sll);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            /* 计算fpOffset + i * n */
                            addi = new Addi(2, 2, fpOffset);
                            ret.add(addi);
                            this.father.addInstruction(ret);
                            ret = new ArrayList<>();
                            if (i < 4) {
                                /* 计算绝对地址并装入$a */
                                Add add = new Add(4 + i, 2, base);
                                ret.add(add);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                Add add = new Add(2, 2, base);
                                ret.add(add);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else if (dimensionValue == 2) {
                            /* 需要计算出数组绝对地址 */
                            /* 计算公式为(base) + fpOffset */
                            if (i < 4) {
                                /* 存入$a */
                                addi = new Addi(4 + i, base, fpOffset);
                                ret.add(addi);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            } else {
                                /* 存入内存 */
                                addi = new Addi(2, base, fpOffset);
                                ret.add(addi);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                                Sw sw = new Sw(2, 3, newFpOffset);
                                ret.add(sw);
                                this.father.addInstruction(ret);
                                ret = new ArrayList<>();
                            }
                        } else {
                            System.out.println("ERROR IN MipsInstructionBuilder : " +
                                    "should not reach here");
                        }
                    }
                }
                
            } else {
                /* 进入此说明参数是一个常数*/
                Li li = new Li(4 + i, Integer.valueOf(name));
                ret.add(li);
                this.father.addInstruction(ret);
                ret = new ArrayList<>();
            }
            if (i >= 4) {
                newFpOffset += 4;
            }
        }
        // int newFpOffset = 0;
        for (int i = 4; i < len; i++) {
            IrValue param = params.get(i);
            String name = param.getName();
            if (newTable.hasSymbol(name)) {
                int reg = newTable.getRegIndex(name, this.father, true);
                Sw sw = new Sw(reg, 3, newFpOffset);
                this.table.getSymbol(name).setUsed(true);
                ret.add(sw);
                this.father.addInstruction(ret);
                ret = new ArrayList<>();
            } else {
                /* 进入此说明参数是一个常数 */
                /* 由于已经保存完了寄存器现场，因此可以直接拿一个寄存器来用 */
                Li li = new Li(8, Integer.valueOf(name));
                Sw sw = new Sw(8, 3, newFpOffset);
                ret.add(li);
                ret.add(sw);
                this.father.addInstruction(ret);
                ret = new ArrayList<>();
            }
            newFpOffset += 4;
        }

        /* 3. 修改$fp, $sp */
        /* fp */
        Move move = new Move(30, 3);
        ret.add(move);
        /* sp */
        addi = new Addi(29, 29, spOffset);
        ret.add(addi);


        /* 4. jal跳转 */
        Jal jal = new Jal(call.getFunctionName().substring(1));
        ret.add(jal);

        this.father.addInstruction(ret);
        ret = new ArrayList<>();
        
        /* 5. 恢复$fp现场，本质上是通过MipsSymbolTable的fpOffset自减 */
        // addi = new Addi(30, 30, -fpOffset);
        addi = new Addi(30, 30, -(this.table.getFpOffset() + 32 * 4));
        ret.add(addi);
        /* 6. 恢复$sp现场，本质上是通过讲$sp自增至原值，将$ra和其他保存寄存器的值恢复 */
        addi = new Addi(29, 29, -spOffset);
        ret.add(addi);
        this.father.addInstruction(ret);
        ret = new ArrayList<>();
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
        this.father.addInstruction(ret);
        ret = new ArrayList<>();
        /* 7. 可能会有一个左值赋值 */
        if (call.getName().length() > 0) {
            /* 有赋值需求 */
            MipsSymbol leftSymbol = new MipsSymbol(call.getName(), 30, false, -1, false,
                    0, true, false);
            insertSymbolTable(leftSymbol.getName(), leftSymbol);
            int regLeft = this.table.getRegIndex(call.getName(), this.father, false);
            move = new Move(regLeft, 2);
            ret.add(move);
            this.father.addInstruction(ret);
            ret = new ArrayList<>();
        }
        ret = new ArrayList<>();
        return ret;
    }

    /* IrLoad -> MipsInstruction */
    private ArrayList<MipsInstruction> genMipsInstructionFromLoad() {
        /* IrLoad左侧的变量都是新临时变量，用于取用全局变量或局部变量 */
        /* 全局变量直接从lw从内存中加载 */
        /* 局部变量若位于寄存器中，则move */
        /* 局部变量若位于内存中，则lw */
        IrLoad left = (IrLoad)irInstruction;

        String leftName = left.getName();
        /* 生成左部临时变量符号 */
        MipsSymbol leftSymbol = new MipsSymbol(leftName, 30, false,
                -1, false, -1, true, false);
        insertSymbolTable(leftName, leftSymbol);
        /* 获取右部变量 */
        IrValue right = left.getOperand(0);
        String rightName = right.getName();
        MipsSymbol rightSymbol = this.table.getSymbol(rightName);
        /* right维数 */
        int rightDimension = right.getDimension();
        int rightReg = -1;
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        if (rightDimension == 0) {
            /* 说明从0维变量中load */
            int leftReg = this.registerFile.getReg(true, leftSymbol, this.father);
            /* 标记当前变量在寄存器中 */
            leftSymbol.setInReg(true);
            /* 标记当前变量所在寄存器 */
            leftSymbol.setRegIndex(leftReg);
            rightReg = this.table.getRegIndex(rightName, this.father, true);
            Move move = new Move(leftReg, rightReg);
            ret.add(move);
            if (ret != null && ret.size() > 0) {
                this.father.addInstruction(ret);
                ret = new ArrayList<>();
            }
        } else if (rightDimension == 1) {
            /* 说明从1维变量中load */
            /* 1维变量 */
            IrValue dimension1 = left.getDimension1Value();
            String dimension1Name = dimension1.getName();
            int reg1 = -1;
            MipsSymbol dimension1Symbol = null;
            if (isConst(dimension1Name)) {
                /* 是常数 */
                dimension1Symbol = new MipsSymbol("Temp", 30, false, -1, false, 
                        -1, true, false);
                reg1 = this.registerFile.getReg(true, dimension1Symbol, this.father);
                Li li = new Li(reg1, Integer.valueOf(dimension1Name));
                ret.add(li);
                if (ret != null && ret.size() > 0) {
                    this.father.addInstruction(ret);
                    ret = new ArrayList<>();
                }
            } else {
                /* 是变量 */
                reg1 = this.table.getRegIndex(dimension1Name, this.father, true);
            }
            ArrayList<MipsInstruction> instructions = null;
            instructions = this.registerFile.readBackPublic(leftSymbol, rightSymbol, reg1,
                    -1, 1, this.father);
            if (instructions != null && instructions.size() > 0) {
                ret.addAll(instructions);
                if (ret != null && ret.size() > 0) {
                    this.father.addInstruction(ret);
                    ret = new ArrayList<>();
                }
            }
            if (dimension1Symbol != null) {
                dimension1Symbol.setUsed(true);
            }
        } else if (rightDimension == 2) {
            /* 说明从2维变量中load */
            /* 1维变量 */
            IrValue dimension1 = left.getDimension1Value();
            String dimension1Name = dimension1.getName();
            MipsSymbol temp1 = null;
            int reg1 = -1;
            if (isConst(dimension1Name)) {
                temp1 = new MipsSymbol("temp", 30);
                reg1 = this.registerFile.getReg(true, temp1, this.father);
                /* 将该常数加载入该寄存器 */
                Li li = new Li(reg1, Integer.valueOf(dimension1Name));
                ret.add(li);
                if (ret != null && ret.size() > 0) {
                    this.father.addInstruction(ret);
                    ret = new ArrayList<>();
                }
            } else {
                reg1 = this.table.getRegIndex(dimension1Name, this.father, true);
            }
            /* 2维变量 */
            IrValue dimension2 = left.getDimension2Value();
            String dimension2Name = dimension2.getName();
            MipsSymbol temp2 = null;
            int reg2 = -1;
            if (isConst(dimension2Name)) {
                temp2 = new MipsSymbol("temp", 30);
                reg2 = this.registerFile.getReg(true, temp2, this.father);
                /* 将该常数加载入该寄存器 */
                Li li = new Li(reg2, Integer.valueOf(dimension2Name));
                ret.add(li);
                if (ret != null && ret.size() > 0) {
                    this.father.addInstruction(ret);
                    ret = new ArrayList<>();
                }
            } else {
                reg2 = this.table.getRegIndex(dimension2Name, this.father, true);
            }
            ArrayList<MipsInstruction> instructions = null;
            instructions = this.registerFile.readBackPublic(leftSymbol, rightSymbol, reg1,
                    reg2, 2, this.father);
            if (instructions != null && instructions.size() > 0) {
                ret.addAll(instructions);
                if (ret != null && ret.size() > 0) {
                    this.father.addInstruction(ret);
                    ret = new ArrayList<>();
                }
            } else {
                System.out.println("ERROR IN MipsInstructionBuilder : should not reach here");
            }
            if (temp1 != null) {
                temp1.setTemp(true);
                temp1.setUsed(true);
            }
            if (temp2 != null) {
                temp2.setTemp(true);
                temp2.setUsed(true);
            }
        } else {
            System.out.println("ERROR IN MipsInstructionBuilder : should not reach here");
        }
        
        
        if (rightName.contains("Global")) {
            /* 将全局变量标记为不在寄存器中 */
            rightSymbol.setInReg(false);
        }
        if (ret != null && ret.size() > 0) {
            this.father.addInstruction(ret);
            ret = new ArrayList<>();
        }
        return ret;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromRet() {
        IrRet ret = (IrRet)irInstruction;
        ArrayList<MipsInstruction> ans = new ArrayList<>();
        if (!ret.isVoid()) {
            /* 返回值为int的函数需要将返回值存入$v0即$2 */
            String name = ret.getOperand(0).getName();
            int reg;
            MipsSymbol temp = null;
            if (isConst(name)) {
                // 常数，需要从寄存器表获取一个$t并使用li将该立即数加载进去
                // 然后使用move进行赋值
                // 这里的Symbol不应当被加入符号表
                temp = new MipsSymbol("temp", 30);
                reg = this.registerFile.getReg(true, temp, this.father);
                Li li = new Li(reg, Integer.valueOf(name));
                ans.add(li);
                temp.setUsed(true);
            } else {
                // 变量
                reg = this.table.getRegIndex(name, this.father, true);
            }
            Move move = new Move(2, reg);
            if (temp != null) {
                temp.setUsed(true);
            }
            ans.add(move);
            if (!this.father.getFather().getIsMain()) {
                Jr jr = new Jr(31);
                ans.add(jr);
            } else {
                Li li = new Li(2, 0xa);
                ans.add(li);
                Syscall syscall = new Syscall();
                ans.add(syscall);
            }
        } else {
            Jr jr = new Jr(31);
            ans.add(jr);
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
        MipsSymbol tempSymbol = null;
        if (isConst(leftName)) {
            // 常数，需要从寄存器表获取一个$t并使用li将该立即数加载进去
            // 然后使用move进行赋值
            // 这里的Symbol不应当被加入符号表
            tempSymbol = new MipsSymbol("name", 30, false, -1, false,
                    -1, true, false);
            leftReg = this.registerFile.getReg(true, tempSymbol, this.father);
            Li li = new Li(leftReg, Integer.valueOf(leftName));
            ret.add(li);
        } else {
            // 变量
            // 这里不用处理数组等情况，因为生成中间代码时会提前生成Load将数组中的值加载到变量中
            leftReg = this.table.getRegIndex(leftName, this.father, true);
        }
        MipsSymbol rightSymbol = this.table.getSymbol(rightName);
        /* Right的维数 */
        int dimensionPointer = store.getDimensionPointer();
        if (dimensionPointer == 0) {
            rightReg = this.table.getRegIndex(rightName, this.father, false);
            /* 如果右侧是无维度变量，则直接赋值即可 */
            Move move = new Move(rightReg, leftReg);
            this.registerFile.getSymbol(leftReg).setUsed(true);
            ret.add(move);
        }
        boolean handleIrValue = store.getHandleIrValue();
        if (dimensionPointer == 1) {
            /* 需要将值保存到1维变量的内存中 */
            if (handleIrValue) {
                /* 说明维度数值是变量，需要加载 */
                IrValue dimension1PointerValue = store.getDimension1PointerValue();
                String dimension1PointerValueName = dimension1PointerValue.getName();
                if (isConst(dimension1PointerValueName)) {
                    /* 说明维度变量是常数 */
                    ArrayList<MipsInstruction> temp = this.registerFile.writeBackPublic(
                            leftReg, rightSymbol, 
                            Integer.valueOf(dimension1PointerValueName) * 4, this.father);
                    // rightSymbol.setInReg(false);
                    rightSymbol.setUsed(true);
                    if (temp != null && temp.size() > 0) {
                        ret.addAll(temp);
                    }
                } else {
                    /* 说明维度是变量 */
                    /* 获取维度变量值所在寄存器 */
                    int dimension1PointerReg = this.table.getRegIndex(
                            dimension1PointerValueName, this.father, true);
                    //ArrayList<MipsInstruction> temp = this.registerFile.writeBackPublic(
                    //        rightReg, rightSymbol, dimension1PointerReg, -1, 1, this.father);
                    ArrayList<MipsInstruction> temp = this.registerFile.writeBackPublic(
                            leftReg, rightSymbol, dimension1PointerReg, -1, 1, this.father);
                    if (temp != null && temp.size() > 0) {
                        ret.addAll(temp);
                    } else {
                        System.out.println(
                                "ERROR IN MipsInstructionBuilder : should not reach here");
                    }
                }
            } else {
                /* 说明维度数值是常数，在编译时已知 */
                ArrayList<MipsInstruction> temp = this.registerFile.writeBackPublic(leftReg,
                        rightSymbol, store.getDimension1Pointer() * 4, this.father);
                // rightSymbol.setInReg(false);
                rightSymbol.setUsed(true);
                if (temp != null && temp.size() > 0) {
                    ret.addAll(temp);
                }
            }
        } else if (dimensionPointer == 2) {
            /* 需要将值保存到2维变量的内存中 */
            if (handleIrValue) {
                /* 说明维度数值是变量，需要加载 */
                IrValue dimension1PointerValue = store.getDimension1PointerValue();
                String dimension1PointerValueName = dimension1PointerValue.getName();
                MipsSymbol temp1 = null; // 用于1维变量是常数的时候使用
                int reg1 = -1;
                if (isConst(dimension1PointerValueName)) {
                    /* 说明是一个常数，需要找到一个空闲寄存器将其装入 */
                    temp1 = new MipsSymbol("temp", 30);
                    reg1 = this.registerFile.getReg(true, temp1, this.father);
                    /* 将立即数装入寄存器 */
                    Li li = new Li(reg1, Integer.valueOf(dimension1PointerValueName));
                    ret.add(li);
                } else {
                    reg1 = this.table.getRegIndex(dimension1PointerValueName, this.father, true);
                }
                IrValue dimension2PointerValue = store.getDimension2PointerValue();
                String dimension2PointerValueName = dimension2PointerValue.getName();
                MipsSymbol temp2 = null;
                int reg2 = -1;
                if (isConst(dimension2PointerValueName)) {
                    /* 说明是一个常数，需要找到一个空闲寄存器将其装入 */
                    temp2 = new MipsSymbol("temp", 30);
                    reg2 = this.registerFile.getReg(true, temp2, this.father);
                    /* 将立即数装入寄存器 */
                    Li li = new Li(reg2, Integer.valueOf(dimension2PointerValueName));
                    ret.add(li);
                } else {
                    reg2 = this.table.getRegIndex(dimension2PointerValueName, this.father, true);
                }
                // ArrayList<MipsInstruction> instructions = this.registerFile.writeBackPublic(
                //        rightReg, rightSymbol, reg1, reg2, 2, this.father);
                ArrayList<MipsInstruction> instructions = this.registerFile.writeBackPublic(
                        leftReg, rightSymbol, reg1, reg2, 2, this.father);
                if (instructions != null && instructions.size() > 0) {
                    ret.addAll(instructions);
                } else {
                    System.out.println("ERROR IN MipsInstructionBuilder : should not reach here");
                }
                if (temp1 != null) {
                    temp1.setTemp(true);
                    temp1.setUsed(true);
                }
                if (temp2 != null) {
                    temp2.setTemp(true);
                    temp2.setUsed(true);
                }
            } else {
                /* 说明维度数值是常数，在编译时已知 */
                /* 对于数组a[m][n]，访问a[i][j]即访问a(i * n + j) */
                int n = rightSymbol.getDimension2();
                int i = store.getDimension1Pointer();
                int j = store.getDimension2Pointer();
                int offset = i * n + j;
                offset *= 4;
                ArrayList<MipsInstruction> temp = this.registerFile.writeBackPublic(
                        leftReg, rightSymbol, offset, this.father);
                rightSymbol.setUsed(true);
                // rightSymbol.setInReg(false);
                if (temp != null && temp.size() > 0) {
                    ret.addAll(temp);
                }
            }
        } else if (rightName.contains("Global")) {
            /* 如果是全局变量，则需要立刻写回内存 */
            MipsInstruction temp = this.registerFile.writeBackPublic(rightSymbol);
            rightSymbol.setUsed(true);
            rightSymbol.setInReg(false);
            ret.add(temp);
        }
        if (tempSymbol != null) {
            tempSymbol.setUsed(true);
        }
        return ret;
    }

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

    /* IrGoto -> Mips j */
    private ArrayList<MipsInstruction> genMipsInstructionFromGoto() {
        IrGoto irGoto = (IrGoto)irInstruction;
        IrLabel irLabel = (IrLabel) irGoto.getOperand(0);
        J j = new J(irLabel.getName().substring(1));
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        /* 写回内存 */
        ArrayList<MipsInstruction> sws = this.registerFile.writeBackAll();
        if (sws != null && sws.size() > 0) {
            ret.addAll(sws);
        }
        ret.add(j);
        return ret;
    }

    /* IrLabel -> Mips Label */
    private ArrayList<MipsInstruction> genMipsInstructionFromLabel() {
        /* 写回内存 */
        ArrayList<MipsInstruction> ret = new ArrayList<>();
        ArrayList<MipsInstruction> sws = this.registerFile.writeBackAll();
        if (sws != null && sws.size() > 0) {
            ret.addAll(sws);
        }
        IrLabel irLabel = (IrLabel)irInstruction;
        Label mipsLabel = new Label(irLabel.getName().substring(1));
        ret.add(mipsLabel);
        return ret;
    }

    private ArrayList<MipsInstruction> genMipsInstructionFromBr() {
        IrBr inst = (IrBr)irInstruction;
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
            leftReg = this.table.getRegIndex(leftName, this.father, true);
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
            rightReg = this.table.getRegIndex(rightName, this.father, true);
            rightSymbol = this.table.getSymbol(rightName);
        }
        IrLabel label = (IrLabel)inst.getLabel();
        /* 将左右操作数标记为已使用，方便释放寄存器 */
        leftSymbol.setUsed(true);
        rightSymbol.setUsed(true);
        /* 写回内存 */
        ArrayList<MipsInstruction> sws = this.registerFile.writeBackAll();
        if (sws != null && sws.size() > 0) {
            ret.addAll(sws);
        }
        if (inst.getInstructionType().equals(IrInstructionType.Beq)) {
            Beq beq = new Beq(leftReg, rightReg, label.getName().substring(1));
            ret.add(beq);
        } else {
            Bne bne = new Bne(leftReg, rightReg, label.getName().substring(1));
            ret.add(bne);
        }

        return ret;
    }
}
