package backend;

import backend.function.MipsFunction;
import backend.instruction.Asciiz;
import backend.instruction.J;
import backend.instruction.Li;
import backend.instruction.MipsInstruction;
import backend.instruction.Nop;

import java.util.ArrayList;

/**
 * Mips Module
 * Mips代码的顶层模块
 */
public class MipsModule implements MipsNode {
    /* 由于是在遍历LLVM IR的过程中检索并获得.asciiz常量字符串，因此提供public添加方法 */
    private ArrayList<Asciiz> asciizs;
    // 加载全局变量到内存的指令
    private ArrayList<MipsInstruction> globals;
    private ArrayList<MipsFunction> functions;
    /* 加载函数运行栈 */
    private Li li;
    /* 跳转到main函数 */
    private J jmain;
    /* 插入一条nop */
    private Nop nop = new Nop();
    /* 插入一条换行 */
    private String enter = "\n";

    public MipsModule() {
        init();
    }

    private void init() {
        this.asciizs = new ArrayList<>();
        this.functions = new ArrayList<>();
        this.globals = new ArrayList<>();
        initLi();
        initjMain();
    }

    private void initLi() {
        /* $30 = $fp */
        this.li = new Li(30, 0x10040000);
    }

    private void initjMain() {
        this.jmain = new J("main");
    }

    public void addAsciiz(Asciiz asciiz) {
        this.asciizs.add(asciiz);
    }

    public void addFunction(MipsFunction function) {
        this.functions.add(function);
    }

    public void addGlobal(MipsInstruction instruction) {
        this.globals.add(instruction);
    }

    @Override
    public ArrayList<String> mipsOutput() {
        ArrayList<String> ret = new ArrayList<>();
        /* 添加.data段，没有声明内容也可以标注.data */
        ret.add("# 字符串常量段\n.data\n");
        for (Asciiz asciiz : this.asciizs) {
            ArrayList<String> temp = asciiz.mipsOutput();
            /* 每条字符串常量asciiz只导出一条mips语句 */
            String s = temp.get(0);
            ret.add(s);
        }
        /* 添加.text段 */
        ret.add("\n# text代码段\n.text\n");
        ArrayList<String> temp;
        /* 1. 写入函数运行栈基地址 */
        ret.add("\n# 写入函数运行栈基地址\n");
        temp = this.li.mipsOutput();
        for (String index : temp) {
            ret.add(index);
        }
        /* 2. 将全局变量sw到内存 */
        ret.add("\n# 写入全局变量\n");
        for (MipsInstruction instruction : globals) {
            temp = instruction.mipsOutput();
            for (String index : temp) {
                ret.add(index);
            }
        }
        /* 3. 跳转到main函数 */
        ret.add("\n# 跳转到main函数\n");
        temp = this.jmain.mipsOutput();
        for (String index : temp) {
            ret.add(index);
        }
        /* 4. 添加nop */
        temp = this.nop.mipsOutput();
        for (String index : temp) {
            ret.add(index);
        }
        /* 5. 导出main在内的函数 */
        ret.add(enter);
        for (MipsFunction function : this.functions) {
            temp =  function.mipsOutput();
            if (temp != null && temp.size() > 0) {
                for (String index : temp) {
                    ret.add(index);
                }
            }
            // 函数声明后加一个换行符以看的更清楚
            ret.add(enter);
        }
        return ret;
    }
}
