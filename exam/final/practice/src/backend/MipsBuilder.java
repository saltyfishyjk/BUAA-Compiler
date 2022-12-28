package backend;

import backend.function.MipsFunctionBuilder;
import backend.instruction.Li;
import backend.instruction.Sw;
import backend.symbol.MipsSymbol;
import middle.llvmir.IrModule;
import middle.llvmir.value.function.IrFunction;
import middle.llvmir.value.globalvariable.IrGlobalVariable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Mips代码生成器
 * LLVM IR -> MIPS
 */
public class MipsBuilder {
    private IrModule irModule;

    public MipsBuilder(IrModule irModule) {
        this.irModule = irModule;
    }

    /* 生成MipsModule */
    public MipsModule genMipsModule() {
        MipsModule mipsModule = new MipsModule();
        /* 加载全局变量 */
        /* 对于每个符号表而言，其初始状态都应当将全局变量加载进去并可以访问 */
        /* 根据我们的寄存器约定，使用$24即$t8不断li和sw */
        HashMap<String, MipsSymbol> globalVariable = new HashMap<>();
        ArrayList<IrGlobalVariable> variables = irModule.getGlobalVariables();
        /* 记录gp的偏移 */
        int gpOffset = 0;
        for (IrGlobalVariable variable : variables) {
            if (variable.getDimension() == 0) {
                /* 0维全局变量 */
                int value = variable.getIntInit();
                if (value != 0) {
                    /* 加载到$24 */
                    Li li = new Li(24, value);
                    mipsModule.addGlobal(li);
                    /* 保存到内存 */
                    Sw sw = new Sw(24, 28, gpOffset);
                    mipsModule.addGlobal(sw);
                }
                MipsSymbol symbol = new MipsSymbol(variable.getName(),
                        28, gpOffset);
                globalVariable.put(symbol.getName(), symbol);
                gpOffset += 4;
            } else if (variable.getDimension() == 1) {
                /* 1维数组 */
                ArrayList<Integer> inits = variable.getIntInit1();
                int dimension1 = variable.getDimension1();
                MipsSymbol symbol = new MipsSymbol(variable.getName(),
                        28, false, -1, true,
                        gpOffset, false, false, 1,
                        variable.getDimension1());
                /* 添加维数 */
                symbol.setDimension1(variable.getDimension1());
                globalVariable.put(symbol.getName(), symbol);
                /* 将初值装载到内存中 */
                for (int i = 0; i < dimension1; i++) {
                    if (i < inits.size()) {
                        int num = inits.get(i);
                        if (num != 0) {
                            Li li = new Li(24, num);
                            mipsModule.addGlobal(li);
                            Sw sw = new Sw(24, 28, gpOffset);
                            mipsModule.addGlobal(sw);
                        }
                    }
                    gpOffset += 4;
                }
            } else if (variable.getDimension() == 2) {
                /* 2维数组 */
                ArrayList<ArrayList<Integer>> inits = variable.getIntInit2();
                int dimension1 = variable.getDimension1();
                int dimension2 = variable.getDimension2();
                MipsSymbol symbol = new MipsSymbol(variable.getName(),
                        28, false, -1, true,
                        gpOffset, false, false, 2,
                        dimension1, dimension2);
                /* 添加维数 */
                symbol.setDimension1(variable.getDimension1());
                symbol.setDimension2(variable.getDimension2());
                globalVariable.put(symbol.getName(), symbol);
                for (int i = 0; i < dimension1; i++) {
                    for (int j = 0; j < dimension2; j++) {
                        if (i < inits.size()) {
                            int num = inits.get(i).get(j);
                            if (num != 0) {
                                Li li = new Li(24, num);
                                mipsModule.addGlobal(li);
                                Sw sw = new Sw(24, 28, gpOffset);
                                mipsModule.addGlobal(sw);
                            }
                        }
                        gpOffset += 4;
                    }
                }
            } else {
                System.out.println("ERROR IN MipsBuilder : should not reach here");
            }
        }
        /* 生成函数 */
        ArrayList<IrFunction> irFunctions = this.irModule.getFunctions();
        for (IrFunction function : irFunctions) {
            MipsFunctionBuilder mipsFunctionBuilder = new MipsFunctionBuilder(function,
                    mipsModule, globalVariable);
            mipsModule.addFunction(mipsFunctionBuilder.genMipsFunction());
        }
        return mipsModule;
    }

}
