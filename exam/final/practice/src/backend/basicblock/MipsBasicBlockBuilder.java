package backend.basicblock;

import backend.function.MipsFunction;
import backend.instruction.Asciiz;
import backend.instruction.AsciizCnt;
import backend.instruction.La;
import backend.instruction.Li;
import backend.instruction.MipsInstruction;
import backend.instruction.MipsInstructionBuilder;
import backend.instruction.Move;
import backend.instruction.Syscall;
import middle.llvmir.IrValue;
import middle.llvmir.value.basicblock.IrBasicBlock;
import middle.llvmir.value.instructions.IrInstruction;
import middle.llvmir.value.instructions.terminator.IrCall;

import java.util.ArrayList;

/**
 * Mips Basic Block Builder : Mips基本块生成器
 */
public class MipsBasicBlockBuilder {
    private IrBasicBlock basicBlock;
    private MipsFunction father; // 父Function

    public MipsBasicBlockBuilder(MipsFunction father, IrBasicBlock basicBlock) {
        this.basicBlock = basicBlock;
        this.father = father;
    }

    public MipsBasicBlock genMipsBasicBlock() {
        MipsBasicBlock block = new MipsBasicBlock(father);
        ArrayList<IrInstruction> instructions = basicBlock.getInstructions();
        int len = instructions.size();
        // for (IrInstruction instruction : instructions) {
        for (int i = 0; i < len; i++) {
            IrInstruction instruction = instructions.get(i);
            // 需要特殊处理putch，将多个字符打印合并为字符串打印
            if (instruction instanceof IrCall) {
                IrCall irCall = (IrCall)instruction;
                String functionName = irCall.getFunctionName();
                if (functionName.equals("@putch")) {
                    StringBuilder sb = new StringBuilder();
                    IrInstruction temp = instruction;
                    while (temp instanceof IrCall &&
                            ((IrCall) temp).getFunctionName().equals("@putch")) {
                        IrValue value = temp.getOperand(1);
                        sb.append(String.valueOf((char)Integer.valueOf(value.getName()).
                                    intValue()));
                        i += 1;
                        if (i >= len) {
                            break;
                        }
                        temp = instructions.get(i);
                    }
                    i -= 1;
                    int cnt = AsciizCnt.getCnt();
                    Asciiz asciiz = new Asciiz("str_" + cnt, sb.toString());
                    asciiz.setCnt(cnt);
                    /* 需要li $v0, 4和la $a0, label，为了安全，将$a0挪到$v1，结束后再挪回来 */
                    this.father.getFather().addAsciiz(asciiz);
                    ArrayList<MipsInstruction> output = new ArrayList<>();
                    Move move = new Move(3, 4);
                    output.add(move);
                    Li li = new Li(2, 4);
                    output.add(li);
                    La la = new La(4, asciiz.getName());
                    output.add(la);
                    Syscall syscall = new Syscall();
                    output.add(syscall);
                    move = new Move(4, 3);
                    output.add(move);
                    block.addInstruction(output);
                    continue;
                }

            }
            MipsInstructionBuilder builder = new MipsInstructionBuilder(block, instruction);
            block.addInstruction(builder.genMipsInstruction());
        }
        return block;
    }

}
