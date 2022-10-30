package middle.symbol;

import java.util.ArrayList;

public class SymbolVar extends Symbol {
    private int initVal; // 普通变量初值
    private ArrayList<Integer> initVal1;
    private ArrayList<ArrayList<Integer>> initVal2;

    public SymbolVar(int lineNum, String name, SymbolType symbolType) {
        super(lineNum, name, symbolType);
    }

    public SymbolVar(int lineNum, String name, SymbolType symbolType, int dimension) {
        this(lineNum, name, symbolType);
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
