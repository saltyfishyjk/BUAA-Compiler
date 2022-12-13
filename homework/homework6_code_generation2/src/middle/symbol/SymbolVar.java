package middle.symbol;

import middle.llvmir.IrValue;

import java.util.ArrayList;

public class SymbolVar extends Symbol {
    private int initVal; // 普通变量初值
    private ArrayList<Integer> initVal1;
    private ArrayList<ArrayList<Integer>> initVal2;
    
    private boolean all0 = false; // 标记是否初始值全部为0

    public void setAll0(boolean all0) {
        this.all0 = all0;
    }
    
    public boolean getAll0() {
        return this.all0;
    }

    public SymbolVar(int lineNum, String name, SymbolType symbolType) {
        super(lineNum, name, symbolType);
    }

    public SymbolVar(int lineNum, String name, SymbolType symbolType, int dimension) {
        this(lineNum, name, symbolType);
        this.setDimension(dimension);
    }

    /* 生成中间代码时不关心lineNum */
    public SymbolVar(String name, SymbolType symbolType) {
        super(name, symbolType, null);
    }

    /* 生成中间代码时不关心lineNum */
    public SymbolVar(String name, SymbolType symbolType, int dimension, IrValue value) {
        this(name, symbolType);
        this.setValue(value);
        this.setDimension(dimension);
    }

    public void setInitVal(int initVal) {
        this.initVal = initVal;
    }

    public void setInitVal1(ArrayList<Integer> initVal1) {
        this.initVal1 = initVal1;
    }

    public void setInitVal2(ArrayList<ArrayList<Integer>> initVal2) {
        this.initVal2 = initVal2;
    }

    public int getInitVal() {
        return initVal;
    }

    public ArrayList<Integer> getInitVal1() {
        return initVal1;
    }

    public ArrayList<ArrayList<Integer>> getInitVal2() {
        return initVal2;
    }
}
